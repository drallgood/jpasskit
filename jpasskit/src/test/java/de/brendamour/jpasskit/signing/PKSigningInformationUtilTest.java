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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.security.cert.CertificateExpiredException;

public class PKSigningInformationUtilTest {

    private static final String APPLE_WWDRCA = "passbook/ca-chain.cert.pem";
    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String KEYSTORE_PATH_EXPIRED = "passbook/expired_cert.p12";
    private static final String KEYSTORE_PASSWORD_EXPIRED = "cert";

    @Test
    public void testLoadFiles() throws Exception {
        PKSigningInformation pkSigningInformation = new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                KEYSTORE_PATH, KEYSTORE_PASSWORD, APPLE_WWDRCA);
        checkSigningInfoContent(pkSigningInformation);
    }

    @Test(expectedExceptions = CertificateExpiredException.class)
    public void testLoadFiles_invalidCert() throws Exception {
        new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(KEYSTORE_PATH_EXPIRED,
                KEYSTORE_PASSWORD_EXPIRED, APPLE_WWDRCA);
    }

    @Test
    public void testLoadStreams() throws Exception {

        try (InputStream keyStoreFIS = this.getClass().getResourceAsStream("/" + KEYSTORE_PATH);
             InputStream appleWWDRCAFIS = this.getClass().getResourceAsStream("/" + APPLE_WWDRCA)) {
            Assert.assertNotNull(keyStoreFIS, "Could not find key store file");
            Assert.assertNotNull(appleWWDRCAFIS, "Could not find certificate file");

            PKSigningInformation pkSigningInformation = new PKSigningInformationUtil()
                    .loadSigningInformationFromPKCS12AndIntermediateCertificate(keyStoreFIS, KEYSTORE_PASSWORD, appleWWDRCAFIS);
            checkSigningInfoContent(pkSigningInformation);
        }
    }

    private void checkSigningInfoContent(PKSigningInformation pkSigningInformation) {
        Assert.assertNotNull(pkSigningInformation);
        Assert.assertNotNull(pkSigningInformation.getAppleWWDRCACert());
        Assert.assertNotNull(pkSigningInformation.getSigningCert());
        Assert.assertNotNull(pkSigningInformation.getSigningPrivateKey());
    }
}
