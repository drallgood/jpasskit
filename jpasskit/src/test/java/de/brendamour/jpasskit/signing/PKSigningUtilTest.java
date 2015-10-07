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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;

public class PKSigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = "StoreCard.raw";
    private static final String appleWWDRCA = "passbook/AppleWWDRCA.pem";
    private static final String keyStorePath = "passbook/Certificates.p12";
    private static final String keyStorePassword = "cert";

    @Test
    public void testManifest() throws IOException, Exception {

        Security.addProvider(new BouncyCastleProvider());
        File temporaryPassDir = new File("target/");
        File manifestJSONFile = new File(getPathFromClasspath("pass2.json"));

        PKSigningUtil pkSigningUtil = PKSigningUtil.getInstance();
        PKSigningInformation pkSigningInformation = pkSigningUtil.loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(
                keyStorePath, keyStorePassword, appleWWDRCA);
        pkSigningUtil.signManifestFile(temporaryPassDir, manifestJSONFile, pkSigningInformation);
    }

    @Test
    public void testPassZipGeneration() throws IOException, Exception {

        Security.addProvider(new BouncyCastleProvider());
        PKSigningUtil pkSigningUtil = PKSigningUtil.getInstance();

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        PKPass pass = jsonObjectMapper.readValue(new File(getPathFromClasspath("pass2.json")), PKPass.class);
        pass.setRelevantDate(new Date());
        pass.getBarcode().setMessageEncoding(Charset.forName("utf-8"));
        PKSigningInformation pkSigningInformation = pkSigningUtil.loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(
                keyStorePath, keyStorePassword, appleWWDRCA);
        byte[] signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPkPassArchive(pass, getPassFolderPath(), pkSigningInformation);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);
        IOUtils.copy(inputStream, new FileOutputStream("target/passZIP.zip"));
    }

    @Test
    public void testLoadFiles() throws IOException, Exception {
        PKSigningInformation pkSigningInformation = PKSigningUtil.getInstance().loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(
                keyStorePath, keyStorePassword, appleWWDRCA);
        Assert.assertNotNull(pkSigningInformation);
    }

    @Test
    public void testLoadStreams() throws IOException, Exception {
        InputStream keyStoreFIS = null;
        InputStream appleWWDRCAFIS = null;

        try {
            keyStoreFIS = this.getClass().getResourceAsStream("/" + keyStorePath);
            Assert.assertNotNull(keyStoreFIS, "Could not find key store file");

            appleWWDRCAFIS = this.getClass().getResourceAsStream("/" + appleWWDRCA);
            Assert.assertNotNull(appleWWDRCAFIS, "Could not find certificate file");

            PKSigningInformation pkSigningInformation = PKSigningUtil.getInstance().loadSigningInformationFromPKCS12AndIntermediateCertificateStreams(
                    keyStoreFIS, keyStorePassword, appleWWDRCAFIS);
            Assert.assertNotNull(pkSigningInformation);
        } finally {
            if (keyStoreFIS != null) {
                keyStoreFIS.close();
            }
            if (appleWWDRCAFIS != null) {
                appleWWDRCAFIS.close();
            }
        }
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

        PKSigningInformation pkSigningInformation = PKSigningUtil.getInstance().loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(
                keyStorePath, keyStorePassword, appleWWDRCA);
        byte[] signedAndZippedPkPassArchive = PKSigningUtil.getInstance().createSignedAndZippedPkPassArchive(pass, getPassFolderPath(), pkSigningInformation);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);
        IOUtils.copy(inputStream, new FileOutputStream("target/passJSON.zip"));
    }

    private String getPassFolderPath() throws URISyntaxException {
        return getPathFromClasspath(PASS_TEMPLATE_FOLDER);
    }

    private String getPathFromClasspath(String path) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(path).toURI()).toString();
    }
}
