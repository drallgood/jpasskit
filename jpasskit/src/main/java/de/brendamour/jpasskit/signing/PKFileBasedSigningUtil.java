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
import com.google.common.io.Files;

import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.personalization.PKPersonalization;

import de.brendamour.jpasskit.util.Assert;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bouncycastle.cms.CMSProcessableFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class PKFileBasedSigningUtil extends PKAbstractSigningUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public PKFileBasedSigningUtil() {
        super(new ObjectMapper());
    }

    public PKFileBasedSigningUtil(ObjectWriter objectWriter) {
        super(objectWriter);
    }

    /**
     * @deprecated Please use PKFileBasedSigningUtil(ObjectWriter objectWriter) instead
     * @param objectMapper
     */
    @Deprecated
    public PKFileBasedSigningUtil(ObjectMapper objectMapper) {
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
        return this.createSignedAndZippedPersonalizedPkPassArchive(pass, null, passTemplate, signingInformation);
    }

    @Override
    public byte[] createSignedAndZippedPersonalizedPkPassArchive(PKPass pass, PKPersonalization personalization, IPKPassTemplate passTemplate,
            PKSigningInformation signingInformation) throws PKSigningException {

        File tempPassDir = Files.createTempDir();
        try {
            passTemplate.provisionPassAtDirectory(tempPassDir);
        } catch (IOException e) {
            throw new PKSigningException("Error when provisioning template", e);
        }

        createPassJSONFile(pass, tempPassDir);

        if(personalization != null) {
            createPersonalizationJSONFile(personalization, tempPassDir);
        }

        File manifestJSONFile = createManifestJSONFile(tempPassDir);
        signManifestFileAndWriteToDirectory(tempPassDir, manifestJSONFile, signingInformation);

        byte[] zippedPass = createZippedPassAndReturnAsByteArray(tempPassDir);

        try {
            FileUtils.deleteDirectory(tempPassDir);
        } catch (IOException e) {
            LOGGER.warn("Removing the temporary directory failed", e);
        }
        return zippedPass;
    }

    public byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final URL fileUrlOfTemplateDirectory,
            final PKSigningInformation signingInformation) throws PKSigningException {
        try {
            return createSignedAndZippedPkPassArchive(pass, new PKPassTemplateFolder(fileUrlOfTemplateDirectory), signingInformation);
        } catch (UnsupportedEncodingException e) {
            throw new PKSigningException(e);
        }
    }

    public byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final String pathToTemplateDirectory,
            final PKSigningInformation signingInformation) throws PKSigningException {
        return createSignedAndZippedPkPassArchive(pass, new PKPassTemplateFolder(pathToTemplateDirectory), signingInformation);
    }

    public void signManifestFileAndWriteToDirectory(final File temporaryPassDirectory, final File manifestJSONFile,
            final PKSigningInformation signingInformation) throws PKSigningException {

        Assert.notNull(temporaryPassDirectory, "Temporary directory is mandatory");
        Assert.notNull(manifestJSONFile, "Manifest JSON file is mandatory");

        File signatureFile = new File(temporaryPassDirectory.getAbsolutePath() + File.separator + SIGNATURE_FILE_NAME);
        try (FileOutputStream signatureOutputStream = new FileOutputStream(signatureFile)) {
            CMSProcessableFile content = new CMSProcessableFile(manifestJSONFile);
            signatureOutputStream.write(signManifestUsingContent(signingInformation, content));
        } catch (IOException e) {
            throw new PKSigningException("Error when writing signature to folder", e);
        }
    }

    private void createPassJSONFile(final PKPass pass, final File tempPassDir) throws PKSigningException {
        try {
            File passJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + PASS_JSON_FILE_NAME);
            objectWriter.writeValue(passJSONFile, pass);
        } catch (IOException e) {
            throw new PKSigningException("Error when writing pass.json", e);
        }
    }

    private void createPersonalizationJSONFile(PKPersonalization personalization, File tempPassDir) throws PKSigningException {
        try {
            File personalizationJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + PERSONALIZATION_JSON_FILE_NAME);
            objectWriter.writeValue(personalizationJSONFile, personalization);
        } catch (IOException e) {
            throw new PKSigningException("Error when writing personalization.json", e);
        }
    }

    private File createManifestJSONFile(final File tempPassDir) throws PKSigningException {
        try {
            File manifestJSONFile = new File(tempPassDir.getCanonicalPath() + File.separator + MANIFEST_JSON_FILE_NAME);
            Map<String, String> fileWithHashMap = hashFiles(tempPassDir, Hashing.sha1());
            objectWriter.writeValue(manifestJSONFile, fileWithHashMap);
            return manifestJSONFile;
        } catch (IOException e) {
            throw new PKSigningException("Error when writing manifest.json", e);
        }
    }

    private Map<String, String> hashFiles(final File tempPassDir, final HashFunction hashFunction)
            throws PKSigningException {
        Map<String, String> fileWithHashMap = new HashMap<>();
        try {
            String base = tempPassDir.getCanonicalPath() + File.separator;
            HashCode hash;
            for (File file : FileUtils.listFiles(tempPassDir, new RegexFileFilter("^(?!\\.).*"), TrueFileFilter.TRUE)) {
                hash = Files.hash(file, hashFunction);
                fileWithHashMap.put(getRelativePathOfZipEntry(file.getCanonicalPath(), base), Hex.encodeHexString(hash.asBytes()));
            }
        } catch (IOException e) {
            throw new PKSigningException("Error when hashing files", e);
        }
        return fileWithHashMap;
    }

    private byte[] createZippedPassAndReturnAsByteArray(final File tempPassDir) throws PKSigningException {
        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream(); // closed with the parent ZipOutputStream
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass)) {
            String base = tempPassDir.getCanonicalPath() + File.separator;
            ZipEntry entry;
            for (File file : FileUtils.listFiles(tempPassDir, new RegexFileFilter("^(?!\\.).*"), TrueFileFilter.TRUE)) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    entry = new ZipEntry(getRelativePathOfZipEntry(file.getCanonicalPath(), base));
                    zipOutputStream.putNextEntry(entry);
                    IOUtils.copy(fileInputStream, zipOutputStream);
                }
            }
        } catch (IOException e) {
            throw new PKSigningException("Error when creating a zip package", e);
        }
        return byteArrayOutputStreamForZippedPass.toByteArray();
    }
}
