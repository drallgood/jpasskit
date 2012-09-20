package de.brendamour.jpasskit.signing;

import static org.mockito.Mockito.*;

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
