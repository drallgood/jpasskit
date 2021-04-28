/*
 * Copyright (C) 2019 Patrice Brend'amour <patrice@brendamour.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.brendamour.jpasskit.signing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.personalization.PKPersonalization;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class PKInMemorySigningUtil extends PKAbstractSigningUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public PKInMemorySigningUtil() {
        super(new ObjectMapper());
    }

    public PKInMemorySigningUtil(ObjectWriter objectWriter) {
        super(objectWriter);
    }

    @Deprecated
    public PKInMemorySigningUtil(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.brendamour.jpasskit.signing.IPKSigningUtil#createSignedAndZippedPkPassArchive(de.brendamour.jpasskit.PKPass,
     * de.brendamour.jpasskit.signing.IPKPassTemplate, de.brendamour.jpasskit.signing.PKSigningInformation)
     */

    @Override
    public byte[] createSignedAndZippedPkPassArchive(PKPass pass, IPKPassTemplate passTemplate, PKSigningInformation signingInformation)
            throws PKSigningException {
        return createSignedAndZippedPersonalizedPkPassArchive(pass, null, passTemplate, signingInformation);
    }

    @Override
    public byte[] createSignedAndZippedPersonalizedPkPassArchive(PKPass pass, PKPersonalization personalization, IPKPassTemplate passTemplate,
            PKSigningInformation signingInformation) throws PKSigningException {
        Map<String, ByteBuffer> allFiles;
        try {
            allFiles = passTemplate.getAllFiles();
        } catch (IOException e) {
            throw new PKSigningException("Error when getting files from template", e);
        }

        ByteBuffer passJSONFile = createPassJSONFile(pass);
        allFiles.put(PASS_JSON_FILE_NAME, passJSONFile);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passJSONFile: {}", new String(passJSONFile.array(), Charset.forName("UTF-8")));
        }

        if (personalization != null) {
            ByteBuffer personalizationJSONFile = createPersonalizationJSONFile(personalization);
            allFiles.put(PERSONALIZATION_JSON_FILE_NAME, personalizationJSONFile);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("personalizationJSONFile: {}", new String(personalizationJSONFile.array(), Charset.forName("UTF-8")));
            }
        }

        ByteBuffer manifestJSONFile = createManifestJSONFile(allFiles);
        allFiles.put(MANIFEST_JSON_FILE_NAME, manifestJSONFile);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("manifestJSONFile: {}", new String(manifestJSONFile.array(), Charset.forName("UTF-8")));
        }

        ByteBuffer signature = ByteBuffer.wrap(signManifestFile(manifestJSONFile.array(), signingInformation));
        allFiles.put(SIGNATURE_FILE_NAME, signature);

        return createZippedPassAndReturnAsByteArray(allFiles);
    }

    private ByteBuffer createPassJSONFile(final PKPass pass) throws PKSigningException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            objectWriter.writeValue(byteArrayOutputStream, pass);
            return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new PKSigningException("Error when writing " + PASS_JSON_FILE_NAME, e);
        }
    }

    private ByteBuffer createPersonalizationJSONFile(final PKPersonalization personalization) throws PKSigningException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            objectWriter.writeValue(byteArrayOutputStream, personalization);
            return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new PKSigningException("Error when writing " + PERSONALIZATION_JSON_FILE_NAME, e);
        }
    }

    private ByteBuffer createManifestJSONFile(Map<String, ByteBuffer> allFiles) throws PKSigningException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Map<String, String> fileWithHashMap = hashFiles(allFiles, Hashing.sha1());
            objectWriter.writeValue(byteArrayOutputStream, fileWithHashMap);
            return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new PKSigningException("Error when writing " + MANIFEST_JSON_FILE_NAME, e);
        }
    }

    private Map<String, String> hashFiles(Map<String, ByteBuffer> files, final HashFunction hashFunction) {
        Map<String, String> fileWithHashMap = new HashMap<>();
        for (Entry<String, ByteBuffer> passResourceFile : files.entrySet()) {
            HashCode hash = hashFunction.hashBytes(passResourceFile.getValue().array());
            fileWithHashMap.put(getRelativePathOfZipEntry(passResourceFile.getKey(), ""), Hex.encodeHexString(hash.asBytes()));
        }
        return fileWithHashMap;
    }

    private byte[] createZippedPassAndReturnAsByteArray(final Map<String, ByteBuffer> files) throws PKSigningException {
        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass)) {
            for (Entry<String, ByteBuffer> passResourceFile : files.entrySet()) {
                ZipEntry entry = new ZipEntry(getRelativePathOfZipEntry(passResourceFile.getKey(), ""));
                zipOutputStream.putNextEntry(entry);
                IOUtils.copy(new ByteArrayInputStream(passResourceFile.getValue().array()), zipOutputStream);
            }
        } catch (IOException e) {
            throw new PKSigningException("Error while creating a zip package", e);
        }
        return byteArrayOutputStreamForZippedPass.toByteArray();
    }
}
