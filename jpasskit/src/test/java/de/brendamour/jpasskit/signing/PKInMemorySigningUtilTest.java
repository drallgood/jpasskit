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
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.brendamour.jpasskit.PKPass;

public class PKInMemorySigningUtilTest {

    private static final String PASS_TEMPLATE_FOLDER = PKPassTemplateFolderTest.class.getClassLoader().getResource("StoreCard.raw").getPath();
    private static final String appleWWDRCA = "passbook/ca-chain.cert.pem";
    private static final String keyStorePath = "passbook/jpasskittest.p12";
    private static final String keyStorePassword = "password";
    private PKInMemorySigningUtil pkInMemorySigningUtil;

    @BeforeMethod
    public void prepare() {
        pkInMemorySigningUtil = new PKInMemorySigningUtil();
    }

    @Test
    public void testWithFolderBasedTemplate() throws Exception {
        PKPassTemplateFolder pkPassTemplateFolder = new PKPassTemplateFolder(PASS_TEMPLATE_FOLDER);

        PKPass pass = new ObjectMapper().readValue(new File(getPathFromClasspath("pass2.json")), PKPass.class);

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

        PKPass pass = new ObjectMapper().readValue(new File(getPathFromClasspath("pass2.json")), PKPass.class);

        createZipAndAssert(pkPassTemplateInMemory, pass, "target/passInMemoryStream.zip");
    }

    private void createZipAndAssert(IPKPassTemplate pkPassTemplate, PKPass pkPass, String fileName) throws Exception {
        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil()
                .loadSigningInformationFromPKCS12AndIntermediateCertificate(keyStorePath, keyStorePassword, appleWWDRCA);
        IPKSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
        byte[] signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPkPassArchive(pkPass, pkPassTemplate, pkSigningInformation);
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
