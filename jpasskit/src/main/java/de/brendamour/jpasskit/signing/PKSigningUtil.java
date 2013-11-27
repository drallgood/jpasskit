/**
 * Copyright (C) 2012 Patrice Brend'amour <p.brendamour@bitzeche.de>
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;

public final class PKSigningUtil {

    private static final int ZIP_BUFFER_SIZE = 8192;
    private static final String FILE_SEPARATOR_UNIX = "/";
    private static final String MANIFEST_JSON_FILE_NAME = "manifest.json";
    private static final String PASS_JSON_FILE_NAME = "pass.json";

    private PKSigningUtil() {
    }

    public static byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final URL fileUrlOfTemplateDirectory,
            final PKSigningInformation signingInformation) throws Exception {
        String pathToTemplateDirectory = URLDecoder.decode(fileUrlOfTemplateDirectory.getFile(), "UTF-8");
        return createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory, signingInformation);
    }

    public static byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final String pathToTemplateDirectory,
            final PKSigningInformation signingInformation) throws Exception {

        File tempPassDir = Files.createTempDir();
        FileUtils.copyDirectory(new File(pathToTemplateDirectory), tempPassDir);

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonObjectMapper.setDateFormat(new ISO8601DateFormat());

        createPassJSONFile(pass, tempPassDir, jsonObjectMapper);

        File manifestJSONFile = createManifestJSONFile(tempPassDir, jsonObjectMapper);

        signManifestFile(tempPassDir, manifestJSONFile, signingInformation);

        byte[] zippedPass = createZippedPassAndReturnAsByteArray(tempPassDir);

        FileUtils.deleteDirectory(tempPassDir);
        return zippedPass;
    }

    public static void signManifestFile(final File temporaryPassDirectory, final File manifestJSONFile,
            final PKSigningInformation signingInformation) throws Exception {

        if (temporaryPassDirectory == null || manifestJSONFile == null || signingInformation == null || !signingInformation.isValid()) {
            throw new IllegalArgumentException("Null params are not supported");
        }
        addBCProvider();

        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(
                signingInformation.getSigningPrivateKey());

        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(
                BouncyCastleProvider.PROVIDER_NAME).build()).build(sha1Signer, signingInformation.getSigningCert()));

        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(signingInformation.getAppleWWDRCACert());
        certList.add(signingInformation.getSigningCert());

        Store certs = new JcaCertStore(certList);

        generator.addCertificates(certs);

        CMSSignedData sigData = generator.generate(new CMSProcessableFile(manifestJSONFile), false);
        byte[] signedDataBytes = sigData.getEncoded();

        File signatureFile = new File(temporaryPassDirectory.getAbsolutePath() + File.separator + "signature");
        FileOutputStream signatureOutputStream = new FileOutputStream(signatureFile);
        signatureOutputStream.write(signedDataBytes);
        signatureOutputStream.close();
    }

    public static PKSigningInformation loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(final String pkcs12KeyStoreFilePath,
            final String keyStorePassword, final String appleWWDRCAFilePath) throws IOException, NoSuchAlgorithmException, CertificateException,
            KeyStoreException, NoSuchProviderException, UnrecoverableKeyException {
        addBCProvider();

        KeyStore pkcs12KeyStore = loadPKCS12File(pkcs12KeyStoreFilePath, keyStorePassword);
        Enumeration<String> aliases = pkcs12KeyStore.aliases();

        PrivateKey signingPrivateKey = null;
        X509Certificate signingCert = null;

        while (aliases.hasMoreElements()) {
            String aliasName = aliases.nextElement();

            Key key = pkcs12KeyStore.getKey(aliasName, keyStorePassword.toCharArray());
            if (key instanceof PrivateKey) {
                signingPrivateKey = (PrivateKey) key;
                Object cert = pkcs12KeyStore.getCertificate(aliasName);
                if (cert instanceof X509Certificate) {
                    signingCert = (X509Certificate) cert;
                    break;
                }
            }
        }

        X509Certificate appleWWDRCACert = loadDERCertificate(appleWWDRCAFilePath);
        if (signingCert == null || signingPrivateKey == null || appleWWDRCACert == null) {
            throw new IOException("Couldn#t load all the neccessary certificates/keys");
        }

        return new PKSigningInformation(signingCert, signingPrivateKey, appleWWDRCACert);
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
     * @return Signing informatino necessary to sign a pass.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws UnrecoverableKeyException
     */
    public static PKSigningInformation loadSigningInformationFromPKCS12AndIntermediateCertificateStreams(
            final InputStream pkcs12KeyStoreInputStream, final String keyStorePassword, final InputStream appleWWDRCAFileInputStream)
            throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException,
            UnrecoverableKeyException {
        addBCProvider();

        KeyStore pkcs12KeyStore = loadPKCS12File(pkcs12KeyStoreInputStream, keyStorePassword);
        Enumeration<String> aliases = pkcs12KeyStore.aliases();

        PrivateKey signingPrivateKey = null;
        X509Certificate signingCert = null;

        while (aliases.hasMoreElements()) {
            String aliasName = aliases.nextElement();

            Key key = pkcs12KeyStore.getKey(aliasName, keyStorePassword.toCharArray());
            if (key instanceof PrivateKey) {
                signingPrivateKey = (PrivateKey) key;
                Object cert = pkcs12KeyStore.getCertificate(aliasName);
                if (cert instanceof X509Certificate) {
                    signingCert = (X509Certificate) cert;
                    break;
                }
            }
        }

        X509Certificate appleWWDRCACert = loadDERCertificate(appleWWDRCAFileInputStream);
        if (signingCert == null || signingPrivateKey == null || appleWWDRCACert == null) {
            throw new IOException("Couldn#t load all the neccessary certificates/keys");
        }

        return new PKSigningInformation(signingCert, signingPrivateKey, appleWWDRCACert);
    }

    public static KeyStore loadPKCS12File(final String pathToP12, final String password) throws IOException, NoSuchAlgorithmException,
            CertificateException, KeyStoreException, NoSuchProviderException {
        addBCProvider();
        KeyStore keystore = KeyStore.getInstance("PKCS12");

        File p12File = new File(pathToP12);
        if (!p12File.exists()) {
            // try loading it from the classpath
            URL localP12File = PKSigningUtil.class.getClassLoader().getResource(pathToP12);
            if (localP12File == null) {
                throw new FileNotFoundException("File at " + pathToP12 + " not found");
            }
            p12File = new File(localP12File.getFile());
        }
        InputStream streamOfFile = new FileInputStream(p12File);

        keystore.load(streamOfFile, password.toCharArray());
        IOUtils.closeQuietly(streamOfFile);
        return keystore;
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
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     *             If the parameter <code>inputStreamOfP12</code> is <code>null</code>.
     */
    public static KeyStore loadPKCS12File(final InputStream inputStreamOfP12, final String password) throws IOException,
            NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException {
        if (inputStreamOfP12 == null) {
            throw new IllegalArgumentException("InputStream of key store must not be null");
        }
        addBCProvider();
        KeyStore keystore = KeyStore.getInstance("PKCS12");

        keystore.load(inputStreamOfP12, password.toCharArray());
        return keystore;
    }

    public static X509Certificate loadDERCertificate(final String filePath) throws IOException, CertificateException {
        FileInputStream certificateFileInputStream = null;
        try {
            File certFile = new File(filePath);
            if (!certFile.exists()) {
                // try loading it from the classpath
                URL localCertFile = PKSigningUtil.class.getClassLoader().getResource(filePath);
                if (localCertFile == null) {
                    throw new FileNotFoundException("File at " + filePath + " not found");
                }
                certFile = new File(localCertFile.getFile());
            }
            certificateFileInputStream = new FileInputStream(certFile);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
            Certificate certificate = certificateFactory.generateCertificate(certificateFileInputStream);
            if (certificate instanceof X509Certificate) {
                return (X509Certificate) certificate;
            }
            throw new IOException("The key from '" + filePath + "' could not be decrypted");
        } catch (IOException ex) {
            throw new IOException("The key from '" + filePath + "' could not be decrypted", ex);
        } catch (NoSuchProviderException ex) {
            throw new IOException("The key from '" + filePath + "' could not be decrypted", ex);
        } finally {
            IOUtils.closeQuietly(certificateFileInputStream);
        }
    }

    /**
     * Load a DEAR Certificate from an <code>InputStream</code>.
     * 
     * The caller is responsible for closing the stream after this method returns successfully or fails.
     * 
     * @param certificateInputStream
     *            <code>InputStream</code> containing the certificate.
     * @return Loaded certificate.
     * @throws IOException
     * @throws CertificateException
     */
    public static X509Certificate loadDERCertificate(final InputStream certificateInputStream) throws IOException, CertificateException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
            Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);
            if (certificate instanceof X509Certificate) {
                return (X509Certificate) certificate;
            }
            throw new IOException("The key from the input stream could not be decrypted");
        } catch (IOException ex) {
            throw new IOException("The key from the input stream could not be decrypted", ex);
        } catch (NoSuchProviderException ex) {
            throw new IOException("The key from the input stream could not be decrypted", ex);
        }
    }

    private static void addBCProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

    }

    private static void createPassJSONFile(final PKPass pass, final File tempPassDir, final ObjectMapper jsonObjectMapper) throws IOException,
            JsonGenerationException, JsonMappingException {
        File passJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + PASS_JSON_FILE_NAME);

        SimpleFilterProvider filters = new SimpleFilterProvider();

        // haven't found out, how to stack filters. Copying the validation one for now.
        filters.addFilter("validateFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors"));
        filters.addFilter("pkPassFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors", "foregroundColorAsObject",
                "backgroundColorAsObject", "labelColorAsObject"));
        filters.addFilter("barcodeFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors", "messageEncodingAsString"));
        filters.addFilter("charsetFilter", SimpleBeanPropertyFilter.filterOutAllExcept("name"));
        jsonObjectMapper.setSerializationInclusion(Include.NON_NULL);
        jsonObjectMapper.addMixInAnnotations(Object.class, ValidateFilterMixIn.class);
        jsonObjectMapper.addMixInAnnotations(PKPass.class, PkPassFilterMixIn.class);
        jsonObjectMapper.addMixInAnnotations(PKBarcode.class, BarcodeFilterMixIn.class);
        jsonObjectMapper.addMixInAnnotations(Charset.class, CharsetFilterMixIn.class);

        ObjectWriter objectWriter = jsonObjectMapper.writer(filters);
        objectWriter.writeValue(passJSONFile, pass);
    }

    private static File createManifestJSONFile(final File tempPassDir, final ObjectMapper jsonObjectMapper) throws IOException,
            JsonGenerationException, JsonMappingException {
        Map<String, String> fileWithHashMap = new HashMap<String, String>();

        HashFunction hashFunction = Hashing.sha1();
        File[] filesInTempDir = tempPassDir.listFiles();
        hashFilesInDirectory(filesInTempDir, fileWithHashMap, hashFunction, null);
        File manifestJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + MANIFEST_JSON_FILE_NAME);
        jsonObjectMapper.writeValue(manifestJSONFile, fileWithHashMap);
        return manifestJSONFile;
    }

    /* Windows OS separators did not work */
    private static void hashFilesInDirectory(final File[] files, final Map<String, String> fileWithHashMap, final HashFunction hashFunction,
            final String parentName) throws IOException {
        StringBuilder name;
        HashCode hash;
        for (File passResourceFile : files) {
            name = new StringBuilder();
            if (passResourceFile.isFile()) {
                hash = Files.hash(passResourceFile, hashFunction);
                if (StringUtils.isEmpty(parentName)) {
                    // direct call
                    name.append(passResourceFile.getName());
                } else {
                    // recursive call (apeending parent directory)
                    name.append(parentName);
                    name.append(FILE_SEPARATOR_UNIX);
                    name.append(passResourceFile.getName());
                }
                fileWithHashMap.put(name.toString(), Hex.encodeHexString(hash.asBytes()));
            } else if (passResourceFile.isDirectory()) {
                if (StringUtils.isEmpty(parentName)) {
                    // direct call
                    name.append(passResourceFile.getName());
                } else {
                    // recursive call (apeending parent directory)
                    name.append(parentName);
                    name.append(FILE_SEPARATOR_UNIX);
                    name.append(passResourceFile.getName());
                }
                hashFilesInDirectory(passResourceFile.listFiles(), fileWithHashMap, hashFunction, name.toString());
            }
        }
    }

    private static byte[] createZippedPassAndReturnAsByteArray(final File tempPassDir) throws IOException {
        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass);
        zip(tempPassDir, tempPassDir, zipOutputStream);
        zipOutputStream.close();
        return byteArrayOutputStreamForZippedPass.toByteArray();
    }

    private static final void zip(final File directory, final File base, final ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[ZIP_BUFFER_SIZE];
        int read = 0;
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zipOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(getRelativePathOfZipEntry(files[i], base));
                zipOutputStream.putNextEntry(entry);
                while (-1 != (read = fileInputStream.read(buffer))) {
                    zipOutputStream.write(buffer, 0, read);
                }
                fileInputStream.close();
            }
        }
    }

    private static String getRelativePathOfZipEntry(final File fileToZip, final File base) {
        String relativePathOfFile = fileToZip.getPath().substring(base.getPath().length() + 1);
        if (File.separatorChar != '/') {
            relativePathOfFile = relativePathOfFile.replace(File.separatorChar, '/');
        }

        return relativePathOfFile;
    }

    @JsonFilter("pkPassFilter")
    private static class PkPassFilterMixIn {
        // just a dummy
    }

    @JsonFilter("validateFilter")
    private static class ValidateFilterMixIn {
        // just a dummy
    }

    @JsonFilter("barcodeFilter")
    private static class BarcodeFilterMixIn {
        // just a dummy
    }

    @JsonFilter("charsetFilter")
    private static class CharsetFilterMixIn {
        // just a dummy
    }
}
