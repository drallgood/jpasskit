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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;

public class PKFileBasedSigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = "StoreCard.raw";
    private static final String appleWWDRCA = "passbook/ca-chain.cert.pem";
    private static final String keyStorePath = "passbook/jpasskittest.p12";
    private static final String keyStorePassword = "password";

    @Test
    public void testManifest() throws IOException, Exception {

        Security.addProvider(new BouncyCastleProvider());
        File temporaryPassDir = new File("target/");
        File manifestJSONFile = new File(getPathFromClasspath("pass2.json"));

        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                keyStorePath, keyStorePassword, appleWWDRCA);
        PKFileBasedSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
        pkSigningUtil.signManifestFileAndWriteToDirectory(temporaryPassDir, manifestJSONFile, pkSigningInformation);
    }

    @Test
    public void testPassZipGeneration() throws IOException, Exception {

        Security.addProvider(new BouncyCastleProvider());
        IPKSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        PKPass pass = jsonObjectMapper.readValue(new File(getPathFromClasspath("pass2.json")), PKPass.class);
        pass.setRelevantDate(new Date());
        pass.getBarcodes().get(0).setMessageEncoding(Charset.forName("utf-8"));
        pass.setUserInfo(ImmutableMap.<String, Object>of("name", "John Doe"));
        
        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                keyStorePath, keyStorePassword, appleWWDRCA);
        PKPassTemplateFolder passTemplate = new PKPassTemplateFolder(getPassFolderPath());
        byte[] signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPkPassArchive(pass, passTemplate, pkSigningInformation);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);
        
        File passZipFile = new File("target/passZIP.zip");
        if (passZipFile.exists()) {
            passZipFile.delete();
        }
        IOUtils.copy(inputStream, new FileOutputStream(passZipFile));
        Assert.assertTrue(passZipFile.exists());
        Assert.assertTrue(passZipFile.length() > 0);
    }

    @Test
    public void testJson() throws IOException, Exception {
        Security.addProvider(new BouncyCastleProvider());

        PKBarcode barcode = new PKBarcode();
        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("abcdefg");
        barcode.setMessageEncoding(Charset.forName("UTF-8"));

        PKPass pass = new PKPass();
        pass.setBarcodes(Arrays.asList(barcode));
        pass.setPassTypeIdentifier("pti");
        pass.setTeamIdentifier("ti");

        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                keyStorePath, keyStorePassword, appleWWDRCA);
        byte[] signedAndZippedPkPassArchive = new PKFileBasedSigningUtil().createSignedAndZippedPkPassArchive(pass,
                new PKPassTemplateFolder(getPassFolderPath()), pkSigningInformation);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);
        
        File passJsonFile = new File("target/passJson.zip");
        if (passJsonFile.exists()) {
            passJsonFile.delete();
        }
        IOUtils.copy(inputStream, new FileOutputStream(passJsonFile));
        Assert.assertTrue(passJsonFile.exists());
        Assert.assertTrue(passJsonFile.length() > 0);
    }

    private String getPassFolderPath() throws URISyntaxException {
        return getPathFromClasspath(PASS_TEMPLATE_FOLDER);
    }

    private String getPathFromClasspath(String path) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(path).toURI()).toString();
    }
}
