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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;

public final class PKFileBasedSigningUtil extends PKAbstractSIgningUtil {

    private static final int ZIP_BUFFER_SIZE = 8192;
    private static final String FILE_SEPARATOR_UNIX = "/";
    private static final String MANIFEST_JSON_FILE_NAME = "manifest.json";
    private static final String PASS_JSON_FILE_NAME = "pass.json";

    private ObjectMapper jsonObjectMapper;

    @Inject
    public PKFileBasedSigningUtil(ObjectMapper objectMapper) {
        addBCProvider();
        jsonObjectMapper = objectMapper;
        jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonObjectMapper.setDateFormat(new ISO8601DateFormat());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.brendamour.jpasskit.signing.IPKSigningUtil#createSignedAndZippedPkPassArchive(de.brendamour.jpasskit.PKPass,
     * de.brendamour.jpasskit.signing.IPKPassTemplate, de.brendamour.jpasskit.signing.PKSigningInformation)
     */

    @Override
    public byte[] createSignedAndZippedPkPassArchive(PKPass pass, IPKPassTemplate passTemplate, PKSigningInformation signingInformation)
            throws Exception {
        File tempPassDir = Files.createTempDir();
        passTemplate.provisionPassAtDirectory(tempPassDir);

        createPassJSONFile(pass, tempPassDir, jsonObjectMapper);

        File manifestJSONFile = createManifestJSONFile(tempPassDir, jsonObjectMapper);

        signManifestFileAndWriteToDirectory(tempPassDir, manifestJSONFile, signingInformation);

        byte[] zippedPass = createZippedPassAndReturnAsByteArray(tempPassDir);

        FileUtils.deleteDirectory(tempPassDir);
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

   
    public void signManifestFileAndWriteToDirectory(final File temporaryPassDirectory, final File manifestJSONFile, final PKSigningInformation signingInformation)
            throws Exception {

        if (temporaryPassDirectory == null || manifestJSONFile == null) {
            throw new IllegalArgumentException("Temporary directory or manifest file not provided");
        }

       CMSProcessableFile content = new CMSProcessableFile(manifestJSONFile);
        byte[] signedDataBytes = signManifestUsingContent(signingInformation, content);

        File signatureFile = new File(temporaryPassDirectory.getAbsolutePath() + File.separator + "signature");
        FileOutputStream signatureOutputStream = new FileOutputStream(signatureFile);
        signatureOutputStream.write(signedDataBytes);
        signatureOutputStream.close();
    }

    private void createPassJSONFile(final PKPass pass, final File tempPassDir, final ObjectMapper jsonObjectMapper) throws IOException,
            JsonGenerationException, JsonMappingException {
        File passJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + PASS_JSON_FILE_NAME);

        ObjectWriter objectWriter = getObjectWriterWithFilters(jsonObjectMapper);
        objectWriter.writeValue(passJSONFile, pass);
    }

    private File createManifestJSONFile(final File tempPassDir, final ObjectMapper jsonObjectMapper) throws IOException,
            JsonGenerationException, JsonMappingException {
        Map<String, String> fileWithHashMap = new HashMap<String, String>();

        HashFunction hashFunction = Hashing.sha1();
        File[] filesInTempDir = tempPassDir.listFiles();
        hashFilesInDirectory(filesInTempDir, fileWithHashMap, hashFunction, null);
        File manifestJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + MANIFEST_JSON_FILE_NAME);
        ObjectWriter objectWriter = getObjectWriterWithFilters(jsonObjectMapper);
        objectWriter.writeValue(manifestJSONFile, fileWithHashMap);
        return manifestJSONFile;
    }

    private ObjectWriter getObjectWriterWithFilters(final ObjectMapper jsonObjectMapper) {
        SimpleFilterProvider filters = new SimpleFilterProvider();

        // haven't found out, how to stack filters. Copying the validation one for now.
        filters.addFilter("validateFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors"));
        filters.addFilter("pkPassFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors", "foregroundColorAsObject",
                "backgroundColorAsObject", "labelColorAsObject", "passThatWasSet"));
        filters.addFilter("barcodeFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors", "messageEncodingAsString"));
        filters.addFilter("charsetFilter", SimpleBeanPropertyFilter.filterOutAllExcept("name"));
        jsonObjectMapper.setSerializationInclusion(Include.NON_NULL);
        jsonObjectMapper.addMixIn(Object.class, ValidateFilterMixIn.class);
        jsonObjectMapper.addMixIn(PKPass.class, PkPassFilterMixIn.class);
        jsonObjectMapper.addMixIn(PKBarcode.class, BarcodeFilterMixIn.class);
        jsonObjectMapper.addMixIn(Charset.class, CharsetFilterMixIn.class);

        ObjectWriter objectWriter = jsonObjectMapper.writer(filters);
        return objectWriter;
    }

    /* Windows OS separators did not work */
    private void hashFilesInDirectory(final File[] files, final Map<String, String> fileWithHashMap, final HashFunction hashFunction,
            final String parentName) throws IOException {
        StringBuilder name;
        HashCode hash;
        for (File passResourceFile : files) {
            name = new StringBuilder();
            if (passResourceFile.isFile()) {
                hash = Files.hash(passResourceFile, hashFunction);
                if (StringUtils.isEmpty(parentName)) {
                    // direct call
                    name.append(passResourceFile.getName());
                } else {
                    // recursive call (apeending parent directory)
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
                    // recursive call (apeending parent directory)
                    name.append(parentName);
                    name.append(FILE_SEPARATOR_UNIX);
                    name.append(passResourceFile.getName());
                }
                hashFilesInDirectory(passResourceFile.listFiles(), fileWithHashMap, hashFunction, name.toString());
            }
        }
    }

    private byte[] createZippedPassAndReturnAsByteArray(final File tempPassDir) throws IOException {
        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass);
        zip(tempPassDir, tempPassDir, zipOutputStream);
        zipOutputStream.close();
        return byteArrayOutputStreamForZippedPass.toByteArray();
    }

    private final void zip(final File directory, final File base, final ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[ZIP_BUFFER_SIZE];
        int read = 0;
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zipOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(getRelativePathOfZipEntry(files[i], base));
                zipOutputStream.putNextEntry(entry);
                while (-1 != (read = fileInputStream.read(buffer))) {
                    zipOutputStream.write(buffer, 0, read);
                }
                fileInputStream.close();
            }
        }
    }

    private String getRelativePathOfZipEntry(final File fileToZip, final File base) {
        String relativePathOfFile = fileToZip.getPath().substring(base.getPath().length() + 1);
        if (File.separatorChar != '/') {
            relativePathOfFile = relativePathOfFile.replace(File.separatorChar, '/');
        }

        return relativePathOfFile;
    }

    @JsonFilter("pkPassFilter")
    private class PkPassFilterMixIn {
        // just a dummy
    }

    @JsonFilter("validateFilter")
    private class ValidateFilterMixIn {
        // just a dummy
    }

    @JsonFilter("barcodeFilter")
    private class BarcodeFilterMixIn {
        // just a dummy
    }

    @JsonFilter("charsetFilter")
    private class CharsetFilterMixIn {
        // just a dummy
    }

    private void addBCProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

    }
}
