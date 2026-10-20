/**
 * Copyright (C) 2026 Patrice Brend'amour <patrice@brendamour.net>
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

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/** Real, untrusted test certificates generated in memory for each test. */
final class SigningTestCertificates {
    private SigningTestCertificates() {
    }

    static PKSigningInformation loadSigningInformation() throws Exception {
        return loadSigningInformation(false);
    }

    static PKSigningInformation loadSigningInformation(boolean expiredSigner) throws Exception {
        Instant now = Instant.now();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair caKey = generator.generateKeyPair();
        KeyPair signingKey = generator.generateKeyPair();
        X500Name caName = new X500Name("CN=Synthetic JPasskit Test CA");
        X509Certificate ca = certificate(caName, caName, caKey, caKey.getPrivate(),
                now.minus(2, ChronoUnit.DAYS), now.plus(60, ChronoUnit.DAYS), true);
        X509Certificate signer = certificate(caName, new X500Name("CN=Synthetic JPasskit Test Signer"),
                signingKey, caKey.getPrivate(), now.minus(2, ChronoUnit.DAYS),
                expiredSigner ? now.minus(1, ChronoUnit.DAYS) : now.plus(30, ChronoUnit.DAYS), false);

        char[] password = "synthetic-test-only".toCharArray();
        KeyStore store = KeyStore.getInstance("PKCS12");
        store.load(null, password);
        store.setKeyEntry("synthetic", signingKey.getPrivate(), password, new Certificate[]{signer, ca});
        ByteArrayOutputStream pkcs12 = new ByteArrayOutputStream();
        store.store(pkcs12, password);
        return new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(
                new ByteArrayInputStream(pkcs12.toByteArray()), new String(password),
                new ByteArrayInputStream(ca.getEncoded()));
    }

    private static X509Certificate certificate(X500Name issuer, X500Name subject, KeyPair key,
                                               PrivateKey issuerKey, Instant notBefore, Instant notAfter,
                                               boolean ca) throws Exception {
        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer,
                new BigInteger(160, new SecureRandom()), Date.from(notBefore), Date.from(notAfter),
                subject, key.getPublic());
        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(ca));
        return new JcaX509CertificateConverter().getCertificate(builder.build(
                new JcaContentSignerBuilder("SHA256withRSA").build(issuerKey)));
    }
}
