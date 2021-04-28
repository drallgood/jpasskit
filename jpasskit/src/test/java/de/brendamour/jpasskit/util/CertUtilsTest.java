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
package de.brendamour.jpasskit.util;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.ThrowableAssert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import static de.brendamour.jpasskit.util.CertUtils.extractApnsTopics;
import static de.brendamour.jpasskit.util.CertUtils.extractCertificateWithKey;
import static de.brendamour.jpasskit.util.CertUtils.toInputStream;
import static de.brendamour.jpasskit.util.CertUtils.toKeyStore;
import static de.brendamour.jpasskit.util.CertUtils.toX509Certificate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CertUtilsTest {

    private static final String APPLE_WWDRCA = "passbook/ca-chain.cert.pem";
    private static final String KEYSTORE_PATH = "passbook/jpasskittest.p12";
    private static final char [] KEYSTORE_PASSWORD = "password".toCharArray();
    private static final String KEYSTORE_PATH_EXPIRED = "passbook/expired_cert.p12";
    private static final char [] KEYSTORE_PASSWORD_EXPIRED = "cert".toCharArray();

    @Test
    public void toInputStreamWithBadPath() {
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws IOException {
                try (InputStream stream = toInputStream("dummy.p12")) {
                    assertThat(stream).isNotNull();
                }
            }
        }).isInstanceOf(FileNotFoundException.class).hasMessage("File at dummy.p12 not found");
    }

    @Test
    public void toInputStreamWithValidKeyStore() throws IOException {
        try (InputStream stream = toInputStream(KEYSTORE_PATH)) {
            assertThat(stream).isNotNull();
        }
    }

    @Test
    public void toKeyStoreWithBadFileType() throws IOException {
        try (InputStream stream = toInputStream(APPLE_WWDRCA)) {
            assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() throws CertificateException {
                    toKeyStore(stream, KEYSTORE_PASSWORD);
                }
            }).isInstanceOf(IllegalStateException.class).hasMessage("Failed to load signing information");
        }
    }

    @Test
    public void toKeyStoreWithValidKeyStore() throws IOException, CertificateException {
        try (InputStream stream = toInputStream(KEYSTORE_PATH)) {
            assertThat(toKeyStore(stream, KEYSTORE_PASSWORD)).isNotNull();
        }
    }

    @Test
    public void toX509CertificateWithValidCertificate() throws IOException, CertificateException {
        try (InputStream stream = toInputStream(APPLE_WWDRCA)) {
            assertThat(toX509Certificate(stream)).isNotNull();
        }
    }

    @Test
    public void extractCertificateWithKeyWithBadPassword() throws IOException, CertificateException {
        try (InputStream stream = toInputStream(KEYSTORE_PATH)) {
            final KeyStore keyStore = toKeyStore(stream, KEYSTORE_PASSWORD);
            assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    extractCertificateWithKey(keyStore, KEYSTORE_PASSWORD_EXPIRED);
                }
            }).isInstanceOf(IllegalStateException.class).hasMessage("Failed to extract a valid key-certificate pair from key store");
        }
    }

    @Test
    public void extractCertificateWithKeyWithValidCertificate() throws IOException, CertificateException {
        try (InputStream stream = toInputStream(KEYSTORE_PATH)) {
            KeyStore keyStore = toKeyStore(stream, KEYSTORE_PASSWORD);
            Pair<PrivateKey, X509Certificate> pair = extractCertificateWithKey(keyStore, KEYSTORE_PASSWORD);
            assertThat(pair).isNotNull();
            assertThat(pair.getLeft()).isNotNull();
            assertThat(pair.getRight()).isNotNull();
        }
    }

    @Test
    public void extractApnsTopicsWithValidCertificate() throws IOException, CertificateException {
        try (InputStream stream = toInputStream(KEYSTORE_PATH_EXPIRED)) {
            KeyStore keyStore = toKeyStore(stream, KEYSTORE_PASSWORD_EXPIRED);
            Pair<PrivateKey, X509Certificate> pair = extractCertificateWithKey(keyStore, KEYSTORE_PASSWORD_EXPIRED);
            Set<String> topics = extractApnsTopics(pair.getRight());
            assertThat(topics).isNotNull().contains("pass.bitzeche.com.couponTest");
        }
    }

}
