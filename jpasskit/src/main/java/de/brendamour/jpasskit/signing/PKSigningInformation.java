package de.brendamour.jpasskit.signing;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class PKSigningInformation {

    private X509Certificate signingCert;
    private PrivateKey signingPrivateKey;
    private X509Certificate appleWWDRCACert;

    public PKSigningInformation() {
    }

    public PKSigningInformation(final X509Certificate signingCert, final PrivateKey signingPrivateKey, final X509Certificate appleWWDRCACert) {
        this.signingCert = signingCert;
        this.signingPrivateKey = signingPrivateKey;
        this.appleWWDRCACert = appleWWDRCACert;
    }

    public X509Certificate getSigningCert() {
        return signingCert;
    }

    public void setSigningCert(final X509Certificate signingCert) {
        this.signingCert = signingCert;
    }

    public PrivateKey getSigningPrivateKey() {
        return signingPrivateKey;
    }

    public void setSigningPrivateKey(final PrivateKey signingPrivateKey) {
        this.signingPrivateKey = signingPrivateKey;
    }

    public X509Certificate getAppleWWDRCACert() {
        return appleWWDRCACert;
    }

    public void setAppleWWDRCACert(final X509Certificate appleWWDRCACert) {
        this.appleWWDRCACert = appleWWDRCACert;
    }

    public boolean isValid() {
        return signingCert != null && signingPrivateKey != null && appleWWDRCACert != null;
    }

}
