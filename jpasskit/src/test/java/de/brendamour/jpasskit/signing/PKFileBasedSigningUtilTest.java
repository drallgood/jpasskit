/**
 * Copyright (C) 2024 Patrice Brend'amour <patrice@brendamour.net>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.brendamour.jpasskit.signing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.PKPassBuilder;
import de.brendamour.jpasskit.PKRelevantDate;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.enums.PKPassPersonalizationField;
import de.brendamour.jpasskit.personalization.PKPersonalization;
import de.brendamour.jpasskit.personalization.PKPersonalizationBuilder;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PKFileBasedSigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = "StoreCard.raw";
    private static final String APPLE_WWDRCA = "passbook/ca-chain.cert.pem";
    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final String KEYSTORE_PASSWORD = "password";

    @Test
    public void testManifest() throws Exception {

        File temporaryPassDir = Files.createTempDirectory("pass").toFile();
        File manifestJSONFile = new File(getPathFromClasspath("pass.json"));

        PKSigningInformation pkSigningInformation =
                new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                        KEYSTORE_PATH, KEYSTORE_PASSWORD, APPLE_WWDRCA);
        PKFileBasedSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
        pkSigningUtil.signManifestFileAndWriteToDirectory(temporaryPassDir, manifestJSONFile, pkSigningInformation);
    }

    @Test
    public void testFileBasedSigningWithLoadedPass() throws Exception {
        ObjectMapper objectMapper = getObjectMapper();
        PKPass pass = objectMapper.readValue(new File(getPathFromClasspath("pass.json")), PKPass.class);
        PKPassBuilder passBuilder = PKPass.builder(pass)
                .relevantDate(Instant.now())
                .userInfo(Collections.<String, Object>singletonMap("name", "John Doe"));

        passBuilder.getBarcodeBuilders().get(0)
                .messageEncoding(Charset.forName("utf-8"));

        File passfile = File.createTempFile("passFileBasedLoaded", ".zip");
        createZipAndAssert(passBuilder.build(), passfile);
    }

    @Test
    public void testFileBasedSigningWithGeneratedPass() throws Exception {
        PKPassBuilder passBuilder = PKPass.builder()
                .barcodeBuilder(
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatQR)
                                .message("abcdefg")
                                .messageEncoding(Charset.forName("UTF-8"))
                )
                .passTypeIdentifier("pti")
                .teamIdentifier("ti");

        File passfile = File.createTempFile("passFileBasedGenerated", ".zip");
        createZipAndAssert(passBuilder.build(), passfile);
    }

    @Test
    public void testFileBasedSigningWithGeneratedPass_andiOS8Fallback() throws Exception {
        PKPassBuilder passBuilder = PKPass.builder()
                .barcodes(Arrays.asList(
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatCode128)
                                .message("abcdefg")
                                .messageEncoding(Charset.forName("UTF-8"))
                                .build(),
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatQR)
                                .message("abcdefg")
                                .messageEncoding(Charset.forName("UTF-8"))
                                .build()
                ))
                .passTypeIdentifier("pti")
                .teamIdentifier("ti");

        File passfile = File.createTempFile("passFileBasedGenerated_andiOS8Fallback", ".zip");
        createZipAndAssert(passBuilder.build(), passfile);
    }

    @Test
    public void testFileBasedSigningWithGeneratedPassAndPersonalization() throws Exception {
        PKPassBuilder passBuilder = PKPass.builder()
                .barcodeBuilder(
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatQR)
                                .message("abcdefg")
                                .messageEncoding(Charset.forName("UTF-8"))
                )
                .passTypeIdentifier("pti")
                .teamIdentifier("ti");

        PKPersonalizationBuilder personalization = PKPersonalization.builder()
                .description("desc")
                .termsAndConditions("T&C")
                .requiredPersonalizationField(PKPassPersonalizationField.PKPassPersonalizationFieldName);

        File passfile = File.createTempFile("passFileBasedGenerated", ".zip");
        createZipAndAssert(passBuilder.build(), personalization.build(), passfile);
    }

    private void createZipAndAssert(PKPass pkPass, File fileName) throws Exception {
        createZipAndAssert(pkPass, null, fileName);
    }

    private void createZipAndAssert(PKPass pkPass, PKPersonalization personalization, File passZipFile) throws Exception {
        PKSigningInformation pkSigningInformation =
                new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                        KEYSTORE_PATH, KEYSTORE_PASSWORD, APPLE_WWDRCA);
        PKPassTemplateFolder pkPassTemplate = new PKPassTemplateFolder(getPathFromClasspath(PASS_TEMPLATE_FOLDER));
        IPKSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
        byte[] signedAndZippedPkPassArchive;
        if (personalization != null) {
            signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPersonalizedPkPassArchive(pkPass,
                    personalization, pkPassTemplate,
                    pkSigningInformation);
        } else {
            signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPkPassArchive(pkPass, pkPassTemplate,
                    pkSigningInformation);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);

        if (passZipFile.exists()) {
            passZipFile.delete();
        }
        IOUtils.copy(inputStream, new FileOutputStream(passZipFile));
        Assert.assertTrue(passZipFile.exists());
        Assert.assertTrue(passZipFile.length() > 0);
        AssertZip.assertValid(passZipFile);

        Path pkpassFile = passZipFile.toPath();
        FileSystem fs = FileSystems.newFileSystem(pkpassFile, (ClassLoader) null);
        Path bgFilePath = fs.getPath(PKPassTemplateInMemory.PK_ICON);
        Assert.assertTrue(Files.exists(bgFilePath));
        Path ignoredFilePath = fs.getPath(".ignored_file");
        Assert.assertFalse(Files.exists(ignoredFilePath));
    }

    @Test
    public void testRelevantDatesJsonSerialization() throws Exception {
        Instant date = Instant.parse("2025-01-15T10:00:00Z");
        Instant startDate = Instant.parse("2025-01-15T09:00:00Z");
        Instant endDate = Instant.parse("2025-01-15T11:00:00Z");

        PKRelevantDate relevantDate = PKRelevantDate.builder()
                .date(date)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        PKPassBuilder passBuilder = PKPass.builder()
                .relevantDates(List.of(relevantDate))
                .barcodeBuilder(
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatQR)
                                .message("abcdefg")
                                .messageEncoding(StandardCharsets.UTF_8)
                )
                .passTypeIdentifier("pti")
                .teamIdentifier("ti");

        PKPass pass = passBuilder.build();

        ObjectMapper objectMapper = getObjectMapper();

        String json = objectMapper.writeValueAsString(pass);

        // Verify the relevantDates object is serialized correctly
        Assert.assertTrue(json.contains("\"relevantDates\":[{"));
        Assert.assertTrue(json.contains("\"date\":\"2025-01-15T10:00:00Z\""));
        Assert.assertTrue(json.contains("\"startDate\":\"2025-01-15T09:00:00Z\""));
        Assert.assertTrue(json.contains("\"endDate\":\"2025-01-15T11:00:00Z\""));

        // Test deserialization back to object
        PKPass deserializedPass = objectMapper.readValue(json, PKPass.class);
        Assert.assertNotNull(deserializedPass.getRelevantDates());
        Assert.assertEquals(deserializedPass.getRelevantDates().get(0).getDate(), date);
        Assert.assertEquals(deserializedPass.getRelevantDates().get(0).getStartDate(), startDate);
        Assert.assertEquals(deserializedPass.getRelevantDates().get(0).getEndDate(), endDate);
    }

    private String getPathFromClasspath(String path) throws Exception {
        return Paths.get(ClassLoader.getSystemResource(path).toURI()).toString();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new StdDateFormat());
        objectMapper.configOverride(Date.class).setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
