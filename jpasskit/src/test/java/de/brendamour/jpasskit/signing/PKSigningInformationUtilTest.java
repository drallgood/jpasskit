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

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateExpiredException;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PKSigningInformationUtilTest {

    private static final String appleWWDRCA = "passbook/ca-chain.cert.pem";
    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String INVALID_KEYSTORE_PATH = "passbook/expired_cert.p12";
    private static final String INVALID_KEYSTORE_PASSWORD = "cert";

    @Test
    public void testLoadFiles() throws IOException, Exception {
        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                KEYSTORE_PATH, KEYSTORE_PASSWORD, appleWWDRCA);
        checkSigningInfoContent(pkSigningInformation);
    }

    @Test(expectedExceptions = CertificateExpiredException.class)
    public void testLoadFiles_invalidCert() throws IOException, Exception {
        new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(INVALID_KEYSTORE_PATH,
                INVALID_KEYSTORE_PASSWORD, appleWWDRCA);
    }

    @Test
    public void testLoadStreams() throws IOException, Exception {
        InputStream keyStoreFIS = null;
        InputStream appleWWDRCAFIS = null;

        try {
            keyStoreFIS = this.getClass().getResourceAsStream("/" + KEYSTORE_PATH);
            Assert.assertNotNull(keyStoreFIS, "Could not find key store file");

            appleWWDRCAFIS = this.getClass().getResourceAsStream("/" + appleWWDRCA);
            Assert.assertNotNull(appleWWDRCAFIS, "Could not find certificate file");

            PKSigningInformation pkSigningInformation = new PKSigningInformationUtil()
                    .loadSigningInformationFromPKCS12AndIntermediateCertificate(keyStoreFIS, KEYSTORE_PASSWORD, appleWWDRCAFIS);
            checkSigningInfoContent(pkSigningInformation);
        } finally {
            IOUtils.closeQuietly(appleWWDRCAFIS);
            IOUtils.closeQuietly(keyStoreFIS);
        }
    }

    private void checkSigningInfoContent(PKSigningInformation pkSigningInformation) {
        Assert.assertNotNull(pkSigningInformation);
        Assert.assertNotNull(pkSigningInformation.getAppleWWDRCACert());
        Assert.assertNotNull(pkSigningInformation.getSigningCert());
        Assert.assertNotNull(pkSigningInformation.getSigningPrivateKey());
    }
}
