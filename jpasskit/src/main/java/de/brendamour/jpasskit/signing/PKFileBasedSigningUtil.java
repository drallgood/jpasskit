/**
 * Copyright (C) 2015 Patrice Brend'amour <patrice@brendamour.net>
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
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.CMSProcessableFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class PKFileBasedSigningUtil extends PKAbstractSigningUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PKFileBasedSigningUtil.class);
    private static final String FILE_SEPARATOR_UNIX = "/";
    private static final String MANIFEST_JSON_FILE_NAME = "manifest.json";
    private static final String PASS_JSON_FILE_NAME = "pass.json";

    public PKFileBasedSigningUtil() {
        super(new ObjectMapper());
    }

    @Inject
    public PKFileBasedSigningUtil(ObjectWriter objectWriter) {
        super(objectWriter);
    }

    @Deprecated
    @Inject
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
        File tempPassDir = Files.createTempDir();
        try {
            passTemplate.provisionPassAtDirectory(tempPassDir);
        } catch (IOException e) {
            throw new PKSigningException("Error when provisioning template", e);
        }

        createPassJSONFile(pass, tempPassDir);

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
            final PKSigningInformation signingInformation) throws Exception {
        return createSignedAndZippedPkPassArchive(pass, new PKPassTemplateFolder(fileUrlOfTemplateDirectory), signingInformation);
    }

    public byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final String pathToTemplateDirectory,
            final PKSigningInformation signingInformation) throws Exception {
        return createSignedAndZippedPkPassArchive(pass, new PKPassTemplateFolder(pathToTemplateDirectory), signingInformation);
    }

    public void signManifestFileAndWriteToDirectory(final File temporaryPassDirectory, final File manifestJSONFile,
            final PKSigningInformation signingInformation) throws PKSigningException {

        if (temporaryPassDirectory == null || manifestJSONFile == null) {
            throw new IllegalArgumentException("Temporary directory or manifest file not provided");
        }

        File signatureFile = new File(temporaryPassDirectory.getAbsolutePath() + File.separator + "signature");
        try (FileOutputStream signatureOutputStream = new FileOutputStream(signatureFile)) {
            CMSProcessableFile content = new CMSProcessableFile(manifestJSONFile);
            signatureOutputStream.write(signManifestUsingContent(signingInformation, content));
        } catch (IOException e) {
            throw new PKSigningException("Error when writing signature to folder", e);
        }
    }

    private void createPassJSONFile(final PKPass pass, final File tempPassDir) throws PKSigningException {
        File passJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + PASS_JSON_FILE_NAME);

        try {
            objectWriter.writeValue(passJSONFile, pass);
        } catch (IOException e) {
            throw new PKSigningException("Error when writing pass.json", e);
        }
    }

    private File createManifestJSONFile(final File tempPassDir) throws PKSigningException {
        Map<String, String> fileWithHashMap = new HashMap<String, String>();

        HashFunction hashFunction = Hashing.sha1();
        File[] filesInTempDir = tempPassDir.listFiles();
        hashFilesInDirectory(filesInTempDir, fileWithHashMap, hashFunction, null);
        File manifestJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + MANIFEST_JSON_FILE_NAME);

        try {
            objectWriter.writeValue(manifestJSONFile, fileWithHashMap);
        } catch (IOException e) {
            throw new PKSigningException("Error when writing manifest.json", e);
        }
        return manifestJSONFile;
    }

    /* Windows OS separators did not work */
    private void hashFilesInDirectory(final File[] files, final Map<String, String> fileWithHashMap, final HashFunction hashFunction,
            final String parentName) throws PKSigningException {
        StringBuilder name;
        HashCode hash;
        for (File passResourceFile : files) {
            name = new StringBuilder();
            if (passResourceFile.isFile()) {
                try {
                    hash = Files.hash(passResourceFile, hashFunction);
                } catch (IOException e) {
                    throw new PKSigningException("Error when hashing files", e);
                }
                if (StringUtils.isEmpty(parentName)) {
                    // direct call
                    name.append(passResourceFile.getName());
                } else {
                    // recursive call (appending parent directory)
                    name.append(parentName);
                    name.append(FILE_SEPARATOR_UNIX);
                    name.append(passResourceFile.getName());
                }
                fileWithHashMap.put(name.toString(), Hex.encodeHexString(hash.asBytes()));
            } else if (passResourceFile.isDirectory()) {
                if (StringUtils.isEmpty(parentName)) {
                    // direct call
                    name.append(passResourceFile.getName());
                } else {
                    // recursive call (appending parent directory)
                    name.append(parentName);
                    name.append(FILE_SEPARATOR_UNIX);
                    name.append(passResourceFile.getName());
                }
                hashFilesInDirectory(passResourceFile.listFiles(), fileWithHashMap, hashFunction, name.toString());
            }
        }
    }

    private byte[] createZippedPassAndReturnAsByteArray(final File tempPassDir) throws PKSigningException {
        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream(); // closed with the parent ZipOutputStream
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass)) {
            zip(tempPassDir, tempPassDir, zipOutputStream);
            return byteArrayOutputStreamForZippedPass.toByteArray();
        } catch (IOException e) {
            throw new PKSigningException("Error while creating a zip package", e);
        }
    }

    private final void zip(final File directory, final File base, final ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zipOutputStream);
            } else {
                try (FileInputStream fileInputStream = new FileInputStream(files[i])) {
                    ZipEntry entry = new ZipEntry(getRelativePathOfZipEntry(files[i].getPath(), base.getPath()));
                    zipOutputStream.putNextEntry(entry);
                    IOUtils.copy(fileInputStream, zipOutputStream);
                }
            }
        }
    }
}
