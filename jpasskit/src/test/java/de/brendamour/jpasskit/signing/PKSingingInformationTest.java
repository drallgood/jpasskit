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

import static org.mockito.Mockito.mock;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PKSingingInformationTest {
    private PKSigningInformation pkSigningInformation;
    private X509Certificate appleWWDRCACert;
    private X509Certificate signingCert;
    private PrivateKey signingPrivateKey;

    @BeforeMethod
    public void prepareTest() {
        pkSigningInformation = new PKSigningInformation();
        appleWWDRCACert = mock(X509Certificate.class);
        signingCert = mock(X509Certificate.class);
        signingPrivateKey = mock(PrivateKey.class);

        pkSigningInformation.setAppleWWDRCACert(appleWWDRCACert);
        pkSigningInformation.setSigningCert(signingCert);
        pkSigningInformation.setSigningPrivateKey(signingPrivateKey);
    }

    @Test
    public void testGetterSetter() {

        Assert.assertEquals(pkSigningInformation.getAppleWWDRCACert(), appleWWDRCACert);
        Assert.assertEquals(pkSigningInformation.getSigningCert(), signingCert);
        Assert.assertEquals(pkSigningInformation.getSigningPrivateKey(), signingPrivateKey);

    }

    @Test
    public void testIsValid() {
        Assert.assertTrue(pkSigningInformation.isValid());
    }

    @Test
    public void testIsValid_NoSigningCert() {
        pkSigningInformation.setSigningCert(null);

        Assert.assertFalse(pkSigningInformation.isValid());
    }

    @Test
    public void testIsValid_NoSigningPrivateKey() {
        pkSigningInformation.setSigningPrivateKey(null);

        Assert.assertFalse(pkSigningInformation.isValid());
    }

    @Test
    public void testIsValid_NoAppleWWDRCACert() {
        pkSigningInformation.setAppleWWDRCACert(null);

        Assert.assertFalse(pkSigningInformation.isValid());
    }

}
