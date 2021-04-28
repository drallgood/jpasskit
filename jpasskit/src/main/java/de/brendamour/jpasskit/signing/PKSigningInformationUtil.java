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

import de.brendamour.jpasskit.util.CertUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class PKSigningInformationUtil {

    /**
     * Load all signing information necessary for pass generation from the filesystem or classpath.
     *
     * @param keyStoreFilePath
     *            path to keystore (classpath or filesystem)
     * @param keyStorePassword
     *            Password used to access the key store
     * @param appleWWDRCAFilePath
     *            path to apple's WWDRCA certificate file (classpath or filesystem)
     * @return
     *        a {@link PKSigningInformation} object filled with all certificates from the provided files
     * @throws PKSigningException
     */
    public PKSigningInformation loadSigningInformation(final String keyStoreFilePath,
                                                       final String keyStorePassword,
                                                       final String appleWWDRCAFilePath) throws PKSigningException {
        try {
            return loadSigningInformationFromPKCS12AndIntermediateCertificate(keyStoreFilePath, keyStorePassword, appleWWDRCAFilePath);
        } catch (IOException | CertificateException e) {
            throw new PKSigningException("Failed to load signing information", e);
        }
    }

    /**
     * Load all signing information necessary for pass generation from the filesystem or classpath.
     * 
     * @param keyPath
     *            path to keystore (classpath or filesystem)
     * @param keyPassword
     *            Password used to access the key store
     * @param appleWWDRCAFilePath
     *            path to apple's WWDRCA certificate file (classpath or filesystem)
     * @return
     *        a {@link PKSigningInformation} object filled with all certificates from the provided files
     * @throws IOException
     * @throws IllegalStateException
     * @throws CertificateException
     */
    public PKSigningInformation loadSigningInformationFromPKCS12AndIntermediateCertificate(final String keyPath,final String keyPassword, final String appleWWDRCAFilePath)
            throws IOException, CertificateException {

        try (InputStream walletCertStream = CertUtils.toInputStream(keyPath);
             InputStream appleWWDRCertStream = CertUtils.toInputStream(appleWWDRCAFilePath)) {

            KeyStore pkcs12KeyStore = loadPKCS12File(walletCertStream, keyPassword);
            X509Certificate appleWWDRCert = loadDERCertificate(appleWWDRCertStream);

            return loadSigningInformation(pkcs12KeyStore, keyPassword, appleWWDRCert);
        }
    }

    /**
     * Load all signing information necessary for pass generation using two input streams for the key store and the Apple WWDRCA certificate.
     * 
     * The caller is responsible for closing the stream after this method returns successfully or fails.
     * 
     * @param keyStoreInputStream
     *            <code>InputStream</code> of the key store
     * @param keyStorePassword
     *            Password used to access the key store
     * @param appleWWDRCAFileInputStream
     *            <code>InputStream</code> of the Apple WWDRCA certificate.
     * @return Signing information necessary to sign a pass.
     * @throws IOException
     * @throws IllegalStateException
     * @throws CertificateException
     */
    public PKSigningInformation loadSigningInformationFromPKCS12AndIntermediateCertificate(InputStream keyStoreInputStream,
            String keyStorePassword, InputStream appleWWDRCAFileInputStream) throws IOException, CertificateException {

        KeyStore pkcs12KeyStore = loadPKCS12File(keyStoreInputStream, keyStorePassword);
        X509Certificate appleWWDRCACert = loadDERCertificate(appleWWDRCAFileInputStream);

        return loadSigningInformation(pkcs12KeyStore, keyStorePassword, appleWWDRCACert);
    }

    private PKSigningInformation loadSigningInformation(KeyStore keyStore, String keyStorePassword, X509Certificate appleWWDRCACert) throws IOException, CertificateException {
        Pair<PrivateKey, X509Certificate> pair = CertUtils.extractCertificateWithKey(keyStore, keyStorePassword.toCharArray());
        return checkCertsAndReturnSigningInformationObject(pair.getLeft(), pair.getRight(), appleWWDRCACert);
    }

    /**
     * Load PKCS12 keystore file from file (will try to load it from the classpath)
     * 
     * @param pathToP12
     *            path to PKCS 12 file (on the filesystem or classpath)
     * @param password
     *            password to access the key store
     * @return Key store loaded from the provided files
     * @throws IOException
     * @throws CertificateException
     * @throws IllegalArgumentException
     * @deprecated
     */
    @Deprecated
    public KeyStore loadPKCS12File(String pathToP12, String password) throws CertificateException, IOException {
        try (InputStream keystoreInputStream = CertUtils.toInputStream(pathToP12)) {
            return loadPKCS12File(keystoreInputStream, password);
        }
    }

    /**
     * Load the keystore from an already opened input stream.
     * 
     * The caller is responsible for closing the stream after this method returns successfully or fails.
     * 
     * @param inputStreamOfP12
     *            <code>InputStream</code> containing the signing key store.
     * @param password
     *            Password to access the key store
     * @return Key store loaded from <code>inputStreamOfP12</code>
     * @throws IOException
     * @throws CertificateException
     * @throws IllegalArgumentException
     *             If the parameter <code>inputStreamOfP12</code> is <code>null</code>.
     * @deprecated
     */
    @Deprecated
    public KeyStore loadPKCS12File(InputStream inputStreamOfP12, String password) throws CertificateException, IOException {
        try {
            return CertUtils.toKeyStore(inputStreamOfP12, password.toCharArray());
        } catch (IllegalStateException ex) {
            throw new IOException("Key from the input stream could not be decrypted", ex);
        }
    }

    /**
     * Load certificate file in DER format from the filesystem or the classpath
     *
     * @param filePath
     *          Path to the file, containing the certificate.
     * @return Loaded certificate.
     * @throws IOException
     * @throws CertificateException
     * @deprecated
     */
    @Deprecated
    public X509Certificate loadDERCertificate(String filePath) throws IOException, CertificateException {
        try (InputStream certificateInputStream = CertUtils.toInputStream(filePath)) {
            return loadDERCertificate(certificateInputStream);
        }
    }

    /**
     * Load a DER Certificate from an <code>InputStream</code>.
     * 
     * The caller is responsible for closing the stream after this method returns successfully or fails.
     * 
     * @param certificateInputStream
     *            <code>InputStream</code> containing the certificate.
     * @return Loaded certificate.
     * @throws IOException
     * @throws CertificateException
     * @deprecated
     */
    @Deprecated
    public X509Certificate loadDERCertificate(InputStream certificateInputStream) throws IOException, CertificateException {
        try {
            return CertUtils.toX509Certificate(certificateInputStream);
        } catch (IllegalStateException ex) {
            throw new IOException("Certificate from the input stream could not be decrypted", ex);
        }
    }

    private PKSigningInformation checkCertsAndReturnSigningInformationObject(PrivateKey signingPrivateKey, X509Certificate signingCert,
            X509Certificate appleWWDRCACert) throws IOException, CertificateException {
        if (signingCert == null || signingPrivateKey == null || appleWWDRCACert == null) {
            throw new IOException("Couldn't load all the necessary certificates/keys.");
        }

        // check the Validity of the Certificate to make sure it isn't expired
        appleWWDRCACert.checkValidity();
        signingCert.checkValidity();
        return new PKSigningInformation(signingCert, signingPrivateKey, appleWWDRCACert);
    }
}
