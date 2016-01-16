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
import java.net.URL;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.brendamour.jpasskit.PKPass;

public class PKInMemorySigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = PKPassTemplateFolderTest.class.getClassLoader().getResource("StoreCard.raw").getPath();
    private static final String appleWWDRCA = "passbook/ca-chain.cert.pem";
    private static final String keyStorePath = "passbook/jpasskittest.p12";
    private static final String keyStorePassword = "password";
    private PKInMemorySigningUtil pkInMemorySigningUtil;

    @BeforeClass
    public void beforeClass() {
        Security.addProvider(new BouncyCastleProvider());

    }

    @BeforeMethod
    public void prepare() {
        pkInMemorySigningUtil = new PKInMemorySigningUtil(new ObjectMapper());
    }

    @Test
    public void testWithFolderBasedTemplate() throws JsonParseException, JsonMappingException, IOException, URISyntaxException,
            UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException,
            PKSigningException {
        PKPassTemplateFolder pkPassTemplateFolder = new PKPassTemplateFolder(PASS_TEMPLATE_FOLDER);

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        PKPass pass = jsonObjectMapper.readValue(new File(getPathFromClasspath("pass2.json")), PKPass.class);

        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                keyStorePath, keyStorePassword, appleWWDRCA);

        byte[] signedAndZippedPkPassArchive = pkInMemorySigningUtil.createSignedAndZippedPkPassArchive(pass, pkPassTemplateFolder,
                pkSigningInformation);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);

        File passZipFile = new File("target/passZIPFolder.zip");
        if (passZipFile.exists()) {
            passZipFile.delete();
        }
        IOUtils.copy(inputStream, new FileOutputStream(passZipFile));
        Assert.assertTrue(passZipFile.exists());
        Assert.assertTrue(passZipFile.length() > 0);
        AssertZip.assertValid(passZipFile);
    }

    @Test
    public void testWithInMemoryTemplate() throws JsonParseException, JsonMappingException, IOException, URISyntaxException,
            UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException,
            PKSigningException {
        PKPassTemplateInMemory pkPassTemplateInMemory = new PKPassTemplateInMemory();

        // icon
        URL iconFileURL = PKInMemorySigningUtilTest.class.getClassLoader().getResource("StoreCard.raw/icon@2x.png");
        File iconFile = new File(iconFileURL.getFile());
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, iconFile);

        // icon for language
        pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, iconFile);

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        PKPass pass = jsonObjectMapper.readValue(new File(getPathFromClasspath("pass2.json")), PKPass.class);

        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                keyStorePath, keyStorePassword, appleWWDRCA);

        byte[] signedAndZippedPkPassArchive = pkInMemorySigningUtil.createSignedAndZippedPkPassArchive(pass, pkPassTemplateInMemory,
                pkSigningInformation);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(signedAndZippedPkPassArchive);

        File passZipFile = new File("target/passZIPInMemory.zip");
        if (passZipFile.exists()) {
            passZipFile.delete();
        }
        IOUtils.copy(inputStream, new FileOutputStream(passZipFile));
        Assert.assertTrue(passZipFile.exists());
        Assert.assertTrue(passZipFile.length() > 0);
        AssertZip.assertValid(passZipFile);
    }

    private String getPathFromClasspath(String path) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(path).toURI()).toString();
    }
}
