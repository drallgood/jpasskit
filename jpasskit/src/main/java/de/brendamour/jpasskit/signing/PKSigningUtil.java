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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import de.brendamour.jpasskit.PKPass;

@Deprecated
public class PKSigningUtil {

    @Deprecated
    public static byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final URL fileUrlOfTemplateDirectory,
            final PKSigningInformation signingInformation) throws Exception {
        return new PKFileBasedSigningUtil().createSignedAndZippedPkPassArchive(pass, fileUrlOfTemplateDirectory,
                signingInformation);
    }

    @Deprecated
    public static byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final String pathToTemplateDirectory,
            final PKSigningInformation signingInformation) throws Exception {
        return new PKFileBasedSigningUtil().createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory,
                signingInformation);
    }

    @Deprecated
    public static void signManifestFile(final File temporaryPassDirectory, final File manifestJSONFile,
            final PKSigningInformation signingInformation) throws Exception {
        new PKFileBasedSigningUtil().signManifestFileAndWriteToDirectory(temporaryPassDirectory, manifestJSONFile,
                signingInformation);
    }

    @Deprecated
    public static PKSigningInformation loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(final String pkcs12KeyStoreFilePath,
            final String keyStorePassword, final String appleWWDRCAFilePath) throws IOException, NoSuchAlgorithmException, CertificateException,
            KeyStoreException, UnrecoverableKeyException {

        return new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(pkcs12KeyStoreFilePath,
                keyStorePassword, appleWWDRCAFilePath);
    }

    @Deprecated
    public static PKSigningInformation loadSigningInformationFromPKCS12AndIntermediateCertificateStreams(
            final InputStream pkcs12KeyStoreInputStream, final String keyStorePassword, final InputStream appleWWDRCAFileInputStream)
            throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        return new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(pkcs12KeyStoreInputStream,
                keyStorePassword, appleWWDRCAFileInputStream);
    }

    @Deprecated
    public static KeyStore loadPKCS12File(final String pathToP12, final String password) throws IOException, NoSuchAlgorithmException,
            CertificateException, KeyStoreException {
        return new PKSigningInformationUtil().loadPKCS12File(pathToP12, password);
    }

    @Deprecated
    public static KeyStore loadPKCS12File(final InputStream inputStreamOfP12, final String password) throws IOException,
            NoSuchAlgorithmException, CertificateException, KeyStoreException {
        return new PKSigningInformationUtil().loadPKCS12File(inputStreamOfP12, password);
    }

    @Deprecated
    public X509Certificate loadDERCertificate(final String filePath) throws IOException, CertificateException {
        return new PKSigningInformationUtil().loadDERCertificate(filePath);
    }

    @Deprecated
    public static X509Certificate loadDERCertificate(final InputStream certificateInputStream) throws IOException, CertificateException {
        return new PKSigningInformationUtil().loadDERCertificate(certificateInputStream);
    }

}
