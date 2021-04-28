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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPassBuilder;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.personalization.PKPersonalizationBuilder;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKPassPersonalizationField;
import de.brendamour.jpasskit.personalization.PKPersonalization;

public class PKInMemorySigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = PKPassTemplateFolderTest.class.getClassLoader().getResource("StoreCard.raw").getPath();
    private static final String APPLE_WWDRCA = "passbook/ca-chain.cert.pem";
    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final String KEYSTORE_PASSWORD = "password";

    private PKInMemorySigningUtil pkInMemorySigningUtil;

    @BeforeMethod
    public void prepare() {
        pkInMemorySigningUtil = new PKInMemorySigningUtil();
    }

    @Test
    public void testWithFolderBasedTemplate() throws Exception {
        PKPassTemplateFolder pkPassTemplateFolder = new PKPassTemplateFolder(PASS_TEMPLATE_FOLDER);

        PKPass pass = new ObjectMapper().readValue(new File(getPathFromClasspath("pass.json")), PKPass.class);

        createZipAndAssert(pkPassTemplateFolder, pass, "target/passInMemoryFolder.zip");
    }

    @Test
    public void testWithInMemoryTemplate() throws Exception {
        PKPassTemplateInMemory pkPassTemplateInMemory = new PKPassTemplateInMemory();

        // icon
        URL iconFileURL = PKInMemorySigningUtilTest.class.getClassLoader().getResource("StoreCard.raw/icon@2x.png");
        File iconFile = new File(iconFileURL.getFile());
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, iconFile);
        // icon for language
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, iconFile);

        PKPass pass = new ObjectMapper().readValue(new File(getPathFromClasspath("pass.json")), PKPass.class);

        createZipAndAssert(pkPassTemplateInMemory, pass, "target/passInMemoryStream.zip");
    }

    @Test
    public void testWithInMemoryTemplateAndPersonalization() throws Exception {
        PKPassTemplateInMemory pkPassTemplateInMemory = new PKPassTemplateInMemory();

        // icon
        URL iconFileURL = PKInMemorySigningUtilTest.class.getClassLoader().getResource("StoreCard.raw/icon@2x.png");
        File iconFile = new File(iconFileURL.getFile());
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, iconFile);
        // icon for language
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, iconFile);

        PKPass pass = new ObjectMapper().readValue(new File(getPathFromClasspath("pass.json")), PKPass.class);

        PKPersonalizationBuilder personalization = PKPersonalization.builder()
                .description("desc")
                .termsAndConditions("T&C")
                .requiredPersonalizationField(PKPassPersonalizationField.PKPassPersonalizationFieldName);

        createZipAndAssert(pkPassTemplateInMemory, pass, personalization.build(), "target/passInMemoryStream.zip");
    }

    @Test
    public void testJSONCreation() throws Exception {
        Instant expirationDate = LocalDate.of(2020, 3, 5).atStartOfDay(ZoneId.of("America/Phoenix"))
                .toInstant();
        PKPassBuilder passBuilder = PKPass.builder()
                .barcodeBuilder(
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatQR)
                                .message("abcdefg")
                                .messageEncoding(Charset.forName("UTF-8"))
                )
                .passTypeIdentifier("pti")
                .teamIdentifier("ti")
                .expirationDate(expirationDate);

        String passJsonString = pkInMemorySigningUtil.objectWriter.writeValueAsString(passBuilder.build());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode passJsonNode = mapper.readTree(passJsonString);
        Assert.assertEquals(passJsonNode.at("/expirationDate").asText(), "2020-03-05T07:00:00Z");

    }

    private void createZipAndAssert(IPKPassTemplate pkPassTemplate, PKPass pkPass, String fileName) throws Exception {
        createZipAndAssert(pkPassTemplate, pkPass, null, fileName);
    }

    private void createZipAndAssert(IPKPassTemplate pkPassTemplate, PKPass pkPass, PKPersonalization personalization, String fileName)
            throws Exception {
        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                KEYSTORE_PATH, KEYSTORE_PASSWORD, APPLE_WWDRCA);
        byte[] signedAndZippedPkPassArchive;

        if (personalization != null) {
            signedAndZippedPkPassArchive = pkInMemorySigningUtil.createSignedAndZippedPersonalizedPkPassArchive(pkPass, personalization,
                    pkPassTemplate, pkSigningInformation);
        } else {
            signedAndZippedPkPassArchive = pkInMemorySigningUtil
                    .createSignedAndZippedPkPassArchive(pkPass, pkPassTemplate, pkSigningInformation);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);

        File passZipFile = new File(fileName);
        if (passZipFile.exists()) {
            passZipFile.delete();
        }
        IOUtils.copy(inputStream, new FileOutputStream(passZipFile));
        Assert.assertTrue(passZipFile.exists());
        Assert.assertTrue(passZipFile.length() > 0);
        AssertZip.assertValid(passZipFile);
    }

    private String getPathFromClasspath(String path) throws Exception {
        return Paths.get(ClassLoader.getSystemResource(path).toURI()).toString();
    }
}
