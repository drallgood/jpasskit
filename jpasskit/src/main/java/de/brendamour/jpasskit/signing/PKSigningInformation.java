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
