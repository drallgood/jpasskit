/**
 * Copyright (C) 2015 Patrice Brend'amour <patrice@brendamour.net>
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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.util.Enumeration;

public class PKSigningInformationUtil {

    public PKSigningInformationUtil() {
        addBCProvider();
    }

    /**
     * Load all signing information necessary for pass generation from the filesystem or classpath.
     *
     * @param pkcs12KeyStoreFilePath
     *            path to keystore (classpath or filesystem)
     * @param keyStorePassword
     *            Password used to access the key store
     * @param appleWWDRCAFilePath
     *            path to apple's WWDRCA certificate file (classpath or filesystem)
     * @return
     *        a {@link PKSigningInformation} object filled with all certificates from the provided files
     * @throws PKSigningException
     */
    public PKSigningInformation loadSigningInformation(final String pkcs12KeyStoreFilePath,
                                                       final String keyStorePassword,
                                                       final String appleWWDRCAFilePath) throws PKSigningException {
        try {
            return loadSigningInformationFromPKCS12AndIntermediateCertificate(pkcs12KeyStoreFilePath, keyStorePassword, appleWWDRCAFilePath);
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | UnrecoverableKeyException e) {
            throw new PKSigningException("Failed to load signing information", e);
        }
    }

    /**
     * Load all signing information necessary for pass generation from the filesystem or classpath.
     * 
     * @param pkcs12KeyStoreFilePath
     *            path to keystore (classpath or filesystem)
     * @param keyStorePassword
     *            Password used to access the key store
     * @param appleWWDRCAFilePath
     *            path to apple's WWDRCA certificate file (classpath or filesystem)
     * @return
     *        a {@link PKSigningInformation} object filled with all certificates from the provided files
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     */
    public PKSigningInformation loadSigningInformationFromPKCS12AndIntermediateCertificate(final String pkcs12KeyStoreFilePath,
            final String keyStorePassword, final String appleWWDRCAFilePath) throws IOException, NoSuchAlgorithmException, CertificateException,
            KeyStoreException, UnrecoverableKeyException {

        KeyStore pkcs12KeyStore = loadPKCS12File(pkcs12KeyStoreFilePath, keyStorePassword);
        X509Certificate appleWWDRCACert = loadDERCertificate(appleWWDRCAFilePath);

        return loadSigningInformationFromPKCS12AndIntermediateCertificate(pkcs12KeyStore, keyStorePassword.toCharArray(), appleWWDRCACert);
    }

    /**
     * Load all signing information necessary for pass generation using two input streams for the key store and the Apple WWDRCA certificate.
     * 
     * The caller is responsible for closing the stream after this method returns successfully or fails.
     * 
     * @param pkcs12KeyStoreInputStream
     *            <code>InputStream</code> of the key store
     * @param keyStorePassword
     *            Password used to access the key store
     * @param appleWWDRCAFileInputStream
     *            <code>InputStream</code> of the Apple WWDRCA certificate.
     * @return Signing information necessary to sign a pass.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     */
    public PKSigningInformation loadSigningInformationFromPKCS12AndIntermediateCertificate(final InputStream pkcs12KeyStoreInputStream,
            final String keyStorePassword, final InputStream appleWWDRCAFileInputStream) throws IOException, NoSuchAlgorithmException,
            CertificateException, KeyStoreException, UnrecoverableKeyException {

        KeyStore pkcs12KeyStore = loadPKCS12File(pkcs12KeyStoreInputStream, keyStorePassword);
        X509Certificate appleWWDRCACert = loadDERCertificate(appleWWDRCAFileInputStream);

        return loadSigningInformationFromPKCS12AndIntermediateCertificate(pkcs12KeyStore, keyStorePassword.toCharArray(), appleWWDRCACert);
    }

    private PKSigningInformation loadSigningInformationFromPKCS12AndIntermediateCertificate(final KeyStore pkcs12KeyStore,
                                                                                            final char[] keyStorePassword,
                                                                                            final X509Certificate appleWWDRCACert) throws IOException, NoSuchAlgorithmException,
            CertificateException, KeyStoreException, UnrecoverableKeyException {

        Enumeration<String> aliases = pkcs12KeyStore.aliases();

        PrivateKey signingPrivateKey = null;
        X509Certificate signingCert = null;

        while (aliases.hasMoreElements()) {
            String aliasName = aliases.nextElement();

            Key key = pkcs12KeyStore.getKey(aliasName, keyStorePassword);
            if (key instanceof PrivateKey) {
                signingPrivateKey = (PrivateKey) key;
                Object cert = pkcs12KeyStore.getCertificate(aliasName);
                if (cert instanceof X509Certificate) {
                    signingCert = (X509Certificate) cert;
                    break;
                }
            }
        }

        return checkCertsAndReturnSigningInformationObject(signingPrivateKey, signingCert, appleWWDRCACert);
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
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws IllegalArgumentException
     */
    public KeyStore loadPKCS12File(final String pathToP12, final String password) throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        File p12File = new File(pathToP12);
        if (!p12File.exists()) {
            // try loading it from the classpath
            URL localP12File = PKFileBasedSigningUtil.class.getClassLoader().getResource(pathToP12);
            if (localP12File == null) {
                throw new FileNotFoundException("File at " + pathToP12 + " not found");
            }
            p12File = new File(localP12File.getFile());
        }
        try (InputStream streamOfFile = new FileInputStream(p12File)) {
            return loadPKCS12File(streamOfFile, password);
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
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws IllegalArgumentException
     *             If the parameter <code>inputStreamOfP12</code> is <code>null</code>.
     */
    public KeyStore loadPKCS12File(final InputStream inputStreamOfP12, final String password) throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        if (inputStreamOfP12 == null) {
            throw new IllegalArgumentException("InputStream of key store must not be null");
        }
        KeyStore keystore = KeyStore.getInstance("PKCS12");

        keystore.load(inputStreamOfP12, password.toCharArray());
        return keystore;
    }

    /**
     * Load certificate file in DER format from the filesystem or the classpath
     * 
     * @param filePath
     *          Path to the file, containing the certificate.
     * @return Loaded certificate.
     * @throws IOException
     * @throws CertificateException
     */
    public X509Certificate loadDERCertificate(final String filePath) throws IOException, CertificateException {
        File certFile = new File(filePath);
        if (!certFile.exists()) {
            // try loading it from the classpath
            URL localCertFile = PKFileBasedSigningUtil.class.getClassLoader().getResource(filePath);
            if (localCertFile == null) {
                throw new FileNotFoundException("File at " + filePath + " not found");
            }
            certFile = new File(localCertFile.getFile());
        }
        try (FileInputStream certificateFileInputStream = new FileInputStream(certFile)) {
            return loadDERCertificate(certificateFileInputStream);
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
     */
    public X509Certificate loadDERCertificate(final InputStream certificateInputStream) throws IOException, CertificateException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
            Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);
            if (certificate instanceof X509Certificate) {
                ((X509Certificate) certificate).checkValidity();
                return (X509Certificate) certificate;
            }
            throw new IOException("The key from the input stream could not be decrypted");
        } catch (IOException ex) {
            throw new IOException("The key from the input stream could not be decrypted", ex);
        } catch (NoSuchProviderException ex) {
            throw new IOException("The key from the input stream could not be decrypted", ex);
        }
    }

    private PKSigningInformation checkCertsAndReturnSigningInformationObject(PrivateKey signingPrivateKey, X509Certificate signingCert,
            X509Certificate appleWWDRCACert) throws IOException, CertificateExpiredException, CertificateNotYetValidException {
        if (signingCert == null || signingPrivateKey == null || appleWWDRCACert == null) {
            throw new IOException("Couldn't load all the neccessary certificates/keys.");
        }

        // check the Validity of the Certificate to make sure it isn't expired
        appleWWDRCACert.checkValidity();
        signingCert.checkValidity();
        return new PKSigningInformation(signingCert, signingPrivateKey, appleWWDRCACert);
    }

    private void addBCProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}
