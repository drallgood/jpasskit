/**
 * Copyright (C) 2024 Patrice Brend'amour <patrice@brendamour.net>
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
package de.brendamour.jpasskit.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CertUtilsTest {

    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final char[] KEYSTORE_PASSWORD = "password".toCharArray();
    private static final String CERTIFICATE_PATH = "passbook/ca-chain.cert.pem";
    private static final String CERTIFICATE_WITH_UID_PATH = "passbook/expired_cert.p12";

    @Test
    public void testToInputStream_missingFileThrows() {
        assertThatThrownBy(() -> CertUtils.toInputStream("does-not-exist.p12"))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    public void testToKeyStore_extractCertificateWithKey() throws Exception {
        try (InputStream keyStoreStream = CertUtils.toInputStream(KEYSTORE_PATH)) {
            KeyStore keyStore = CertUtils.toKeyStore(keyStoreStream, KEYSTORE_PASSWORD);
            ImmutablePair<PrivateKey, X509Certificate> pair = CertUtils.extractCertificateWithKey(keyStore, KEYSTORE_PASSWORD);

            assertThat(pair.getLeft()).isNotNull();
            assertThat(pair.getRight()).isNotNull();
        }
    }

    @Test
    public void testToX509Certificate_andExtractApnsTopics() throws Exception {
        try (InputStream certificateStream = CertUtils.toInputStream(CERTIFICATE_PATH)) {
            X509Certificate certificate = CertUtils.toX509Certificate(certificateStream);
            assertThat(certificate).isNotNull();

            // This certificate typically doesn't contain the Pass-specific topic extension; still exercises null-extension branch.
            assertThat(CertUtils.extractApnsTopics(certificate)).isNotNull();
        }

        // Use an APNS-like Pass certificate with UID in subject to exercise UID extraction.
        try (InputStream expiredCertP12 = CertUtils.toInputStream(CERTIFICATE_WITH_UID_PATH)) {
            KeyStore keyStore = CertUtils.toKeyStore(expiredCertP12, "cert".toCharArray());
            ImmutablePair<PrivateKey, X509Certificate> pair = CertUtils.extractCertificateWithKey(keyStore, "cert".toCharArray());

            // May be expired; extractApnsTopics doesn't validate.
            var topics = CertUtils.extractApnsTopics(pair.getRight());
            Assert.assertNotNull(topics);
        }
    }
}
