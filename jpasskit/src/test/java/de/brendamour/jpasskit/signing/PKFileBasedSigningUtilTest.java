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

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.PKPassBuilder;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.enums.PKPassPersonalizationField;
import de.brendamour.jpasskit.personalization.PKPersonalization;

import de.brendamour.jpasskit.personalization.PKPersonalizationBuilder;
import java.time.Instant;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class PKFileBasedSigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = "StoreCard.raw";
    private static final String APPLE_WWDRCA = "passbook/ca-chain.cert.pem";
    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final String KEYSTORE_PASSWORD = "password";

    @Test
    public void testManifest() throws Exception {

        File temporaryPassDir = new File("target/");
        File manifestJSONFile = new File(getPathFromClasspath("pass.json"));

        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                KEYSTORE_PATH, KEYSTORE_PASSWORD, APPLE_WWDRCA);
        PKFileBasedSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
        pkSigningUtil.signManifestFileAndWriteToDirectory(temporaryPassDir, manifestJSONFile, pkSigningInformation);
    }

    @Test
    public void testFileBasedSigningWithLoadedPass() throws Exception {
        PKPass pass = new ObjectMapper().readValue(new File(getPathFromClasspath("pass.json")), PKPass.class);
        PKPassBuilder passBuilder = PKPass.builder(pass)
                .relevantDate(Instant.now())
                .userInfo(Collections.<String, Object>singletonMap("name", "John Doe"));

        passBuilder.getBarcodeBuilders().get(0)
                .messageEncoding(Charset.forName("utf-8"));

        createZipAndAssert(passBuilder.build(), "target/passFileBasedLoaded.zip");
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

        createZipAndAssert(passBuilder.build(), "target/passFileBasedGenerated.zip");
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
        
        createZipAndAssert(passBuilder.build(), "target/passFileBasedGenerated_andiOS8Fallback.zip");
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

        createZipAndAssert(passBuilder.build(), personalization.build(), "target/passFileBasedGenerated.zip");
    }

    private void createZipAndAssert(PKPass pkPass, String fileName) throws Exception {
        createZipAndAssert(pkPass, null, fileName);
    }

    private void createZipAndAssert(PKPass pkPass, PKPersonalization personalization, String fileName) throws Exception {
        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                KEYSTORE_PATH, KEYSTORE_PASSWORD, APPLE_WWDRCA);
        PKPassTemplateFolder pkPassTemplate = new PKPassTemplateFolder(getPathFromClasspath(PASS_TEMPLATE_FOLDER));
        IPKSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
        byte[] signedAndZippedPkPassArchive;
        if (personalization != null) {
            signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPersonalizedPkPassArchive(pkPass, personalization, pkPassTemplate,
                    pkSigningInformation);
        } else {
            signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPkPassArchive(pkPass, pkPassTemplate, pkSigningInformation);
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
        
        Path pkpassFile = Paths.get(fileName);
        FileSystem fs = FileSystems.newFileSystem(pkpassFile, null);
        Path bgFilePath = fs.getPath(PKPassTemplateInMemory.PK_ICON);
        Assert.assertTrue(Files.exists(bgFilePath));
        Path ignoredFilePath = fs.getPath(".ignored_file");
        Assert.assertFalse(Files.exists(ignoredFilePath));
    }

    private String getPathFromClasspath(String path) throws Exception {
        return Paths.get(ClassLoader.getSystemResource(path).toURI()).toString();
    }
}
