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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class CertUtils {

    private static final String PREFIX_UID = "uid=";
    private static final String TOPIC_OID = "1.2.840.113635.100.6.3.6";

    static {
        if (Security.getProvider(getProviderName()) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Load a keystore from an already opened {@link InputStream}.
     * The caller is responsible for closing the resulting stream when it's no longer needed.
     *
     * @param path path of a key store or certificate.
     * @return {@link InputStream} loaded from {@code path}
     * @throws FileNotFoundException
     *             If no file exists at the given {@code path}.
     */
    public static InputStream toInputStream(String path) throws FileNotFoundException {
        File certFile = new File(path);
        if (!certFile.exists()) {
            // try loading it from the classpath
            URL certResource = CertUtils.class.getClassLoader().getResource(path);
            if (certResource == null) {
                throw new FileNotFoundException("File at " + path + " not found");
            }
            certFile = new File(certResource.getFile());
        }
        return new FileInputStream(certFile);
    }

    /**
     * Load a keystore from an already opened {@link InputStream}.
     * The caller is responsible for closing the stream after this method completes successfully or fails.
     *
     * @param keyStoreInputStream {@link InputStream} containing the signing key store.
     * @param keyStorePassword password to access the key store.
     * @return {@link KeyStore} loaded from {@code keyPath}
     * @throws IllegalArgumentException
     *             If either {@code keyPath} is or {@code keyPath}  or {@code keyPassword} is empty.
     * @throws IllegalStateException
     *             If {@link KeyStore} loading failed.
     * @throws CertificateException
     *             If any of the certificates in the keystore could not be loaded.
     */
    public static KeyStore toKeyStore(InputStream keyStoreInputStream, char [] keyStorePassword) throws CertificateException {
        Assert.notNull(keyStoreInputStream, "InputStream of key store is mandatory");
        Assert.notNull(keyStorePassword, "Password for key store is mandatory");
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(keyStoreInputStream, keyStorePassword);
            return keystore;
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException ex) {
            throw new IllegalStateException("Failed to load signing information", ex);
        }
    }

    /**
     * Load a DER Certificate from an already opened {@link InputStream}.
     * The caller is responsible for closing the stream after this method completes successfully or fails.
     *
     * @param certificateInputStream {@link InputStream} containing the certificate.
     * @return {@link X509Certificate} loaded from {@code certificateInputStream}
     * @throws IllegalStateException
     *             If {@link X509Certificate} loading failed.
     * @throws CertificateException
     *             If {@link X509Certificate} is invalid or cannot be loaded.
     */
    public static X509Certificate toX509Certificate(InputStream certificateInputStream) throws CertificateException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", getProviderName());
            Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);
            if (certificate instanceof X509Certificate) {
                ((X509Certificate) certificate).checkValidity();
                return (X509Certificate) certificate;
            }
            throw new IllegalStateException("The key from the input stream could not be decrypted");
        } catch (NoSuchProviderException ex) {
            throw new IllegalStateException("The key from the input stream could not be decrypted", ex);
        }
    }

    /**
     * Extract a pair of key and certificate from an already loaded {@link KeyStore}.
     *
     * @param keyStore {@link KeyStore} instance.
     * @param keyStorePassword password to access the key store.
     * @return pair of valid {@link PrivateKey} and {@link X509Certificate} loaded from {@code keyStore}
     * @throws IllegalStateException
     *             If {@link X509Certificate} loading failed.
     */
    public static ImmutablePair<PrivateKey, X509Certificate> extractCertificateWithKey(KeyStore keyStore, char [] keyStorePassword) {
        Assert.notNull(keyStore, "KeyStore is mandatory");
        Assert.notNull(keyStorePassword, "Password for key store is mandatory");
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String aliasName = aliases.nextElement();

                Key key = keyStore.getKey(aliasName, keyStorePassword);
                if (key instanceof PrivateKey) {
                    PrivateKey privateKey = (PrivateKey) key;
                    Object cert = keyStore.getCertificate(aliasName);
                    if (cert instanceof X509Certificate) {
                        X509Certificate certificate = (X509Certificate) cert;
                        return ImmutablePair.of(privateKey, certificate);
                    }
                }
            }
            throw new IllegalStateException("No valid key-certificate pair in the key store");
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Failed to extract a valid key-certificate pair from key store", ex);
        }
    }

    /**
     * Extract topics for sending push notifications from {@link X509Certificate} certificate.
     *
     * @param certificate {@link X509Certificate} instance.
     * @return unique {@link Set} of topics from a certificate
     * @throws IOException
     *             If {@link X509Certificate} parsing failed.
     */
    public static Set<String> extractApnsTopics(X509Certificate certificate) throws IOException {
        Set<String> topics = new HashSet<>();

        Arrays.stream(certificate.getSubjectX500Principal().getName().split(","))
                .filter(p -> p.toLowerCase().startsWith(PREFIX_UID))
                .findAny()
                .map(p -> p.substring(PREFIX_UID.length()))
                .ifPresent(topics::add);

        byte[] topicExtensionData = certificate.getExtensionValue(TOPIC_OID);

        if (topicExtensionData != null) {
            ASN1Primitive extensionValue = JcaX509ExtensionUtils.parseExtensionValue(topicExtensionData);

            if (extensionValue instanceof ASN1Sequence) {
                for (Object object : (ASN1Sequence) extensionValue) {
                    if (object instanceof ASN1String) {
                        topics.add(String.valueOf(object));
                    }
                }
            }
        }

        return topics;
    }

    public static String getProviderName() {
        return BouncyCastleProvider.PROVIDER_NAME;
    }
}
