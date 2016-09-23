/**
 * Copyright (C) 2016 Patrice Brend'amour <patrice@brendamour.net>
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
import com.google.common.collect.ImmutableMap;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.enums.PKPassPersonalizationField;
import de.brendamour.jpasskit.personalization.PKPersonalization;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

public class PKFileBasedSigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = "StoreCard.raw";
    private static final String appleWWDRCA = "passbook/ca-chain.cert.pem";
    private static final String keyStorePath = "passbook/jpasskittest.p12";
    private static final String keyStorePassword = "password";

    @Test
    public void testManifest() throws Exception {

        File temporaryPassDir = new File("target/");
        File manifestJSONFile = new File(getPathFromClasspath("pass2.json"));

        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                keyStorePath, keyStorePassword, appleWWDRCA);
        PKFileBasedSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
        pkSigningUtil.signManifestFileAndWriteToDirectory(temporaryPassDir, manifestJSONFile, pkSigningInformation);
    }

    @Test
    public void testFileBasedSigningWithLoadedPass() throws Exception {
        PKPass pass = new ObjectMapper().readValue(new File(getPathFromClasspath("pass2.json")), PKPass.class);
        pass.setRelevantDate(new Date());
        pass.getBarcode().setMessageEncoding(Charset.forName("utf-8"));
        pass.setUserInfo(ImmutableMap.<String, Object> of("name", "John Doe"));

        createZipAndAssert(pass, "target/passFileBasedLoaded.zip");
    }

    @Test
    public void testFileBasedSigningWithGeneratedPass() throws Exception {
        PKBarcode barcode = new PKBarcode();
        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("abcdefg");
        barcode.setMessageEncoding(Charset.forName("UTF-8"));

        PKPass pass = new PKPass();
        pass.setBarcodes(Arrays.asList(barcode));
        pass.setPassTypeIdentifier("pti");
        pass.setTeamIdentifier("ti");

        createZipAndAssert(pass, "target/passFileBasedGenerated.zip");
    }

    @Test
    public void testFileBasedSigningWithGeneratedPassAndPersonalization() throws Exception {
        PKBarcode barcode = new PKBarcode();
        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("abcdefg");
        barcode.setMessageEncoding(Charset.forName("UTF-8"));

        PKPass pass = new PKPass();
        pass.setBarcodes(Arrays.asList(barcode));
        pass.setPassTypeIdentifier("pti");
        pass.setTeamIdentifier("ti");

        PKPersonalization personalization = new PKPersonalization();
        personalization.setDescription("desc");
        personalization.setTermsAndConditions("T&C");
        personalization.addRequiredPersonalizationField(PKPassPersonalizationField.PKPassPersonalizationFieldName);

        createZipAndAssert(pass, personalization, "target/passFileBasedGenerated.zip");
    }

    private void createZipAndAssert(PKPass pkPass, String fileName) throws Exception {
        createZipAndAssert(pkPass, null, fileName);
    }

    private void createZipAndAssert(PKPass pkPass, PKPersonalization personalization, String fileName) throws Exception {
        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                keyStorePath, keyStorePassword, appleWWDRCA);
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
    }

    private String getPathFromClasspath(String path) throws Exception {
        return Paths.get(ClassLoader.getSystemResource(path).toURI()).toString();
    }
}
