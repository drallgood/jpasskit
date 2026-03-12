package de.brendamour.jpasskit.signing;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PKSigningInformationUtilTest {

    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String APPLE_WWDRCA_CERT_PATH = "passbook/ca-chain.cert.pem";

    @Test
    public void testLoadSigningInformationFromPKCS12AndIntermediateCertificate_classpath() throws Exception {
        PKSigningInformation info = new PKSigningInformationUtil()
                .loadSigningInformationFromPKCS12AndIntermediateCertificate(KEYSTORE_PATH, KEYSTORE_PASSWORD, APPLE_WWDRCA_CERT_PATH);

        Assert.assertNotNull(info);
        Assert.assertTrue(info.isValid());
        Assert.assertNotNull(info.getSigningPrivateKey());
        Assert.assertNotNull(info.getSigningCert());
        Assert.assertNotNull(info.getAppleWWDRCACert());
    }

    @Test
    public void testLoadSigningInformation_wrapsExceptions() {
        assertThatThrownBy(() -> new PKSigningInformationUtil()
                .loadSigningInformation("does-not-exist.p12", "password", "passbook/ca-chain.cert.pem"))
                .isInstanceOf(PKSigningException.class)
                .hasMessage("Failed to load signing information");
    }

    @Test
    public void testDeprecatedLoadDERCertificate_invalidInputStreamThrowsIOException() {
        InputStream invalid = new ByteArrayInputStream("not-a-cert".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> new PKSigningInformationUtil().loadDERCertificate(invalid))
                .isInstanceOfAny(CertificateException.class, java.io.IOException.class);
    }

    @Test
    public void testDeprecatedLoadPKCS12File_invalidInputStreamThrowsIOException() {
        InputStream invalid = new ByteArrayInputStream("not-a-keystore".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> new PKSigningInformationUtil().loadPKCS12File(invalid, "password"))
                .isInstanceOfAny(CertificateException.class, java.io.IOException.class);
    }
}
