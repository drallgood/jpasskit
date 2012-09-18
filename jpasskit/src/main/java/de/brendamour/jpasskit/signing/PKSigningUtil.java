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
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import de.brendamour.jpasskit.PKPass;

public final class PKSigningUtil {

    private PKSigningUtil() {
    }

    public static byte[] createSignedAndZippedPkPassArchive(final PKPass pass, final String pathToTemplateDirectory,
            final X509Certificate signCertificate, final PrivateKey privateKey, final X509Certificate intermediateCertificate)
            throws Exception {
        File tempPassDir = Files.createTempDir();
        FileUtils.copyDirectory(new File(pathToTemplateDirectory), tempPassDir);
        File passJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + "pass.json");
        System.out.println(tempPassDir.getAbsolutePath());
        ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        jsonObjectMapper.writeValue(passJSONFile, pass);

        Map<String, String> fileWithHashMap = new HashMap<String, String>();

        HashFunction hashFunction = Hashing.sha1();
        File[] filesInTempDir = tempPassDir.listFiles();
        hashFilesInDirectory(filesInTempDir, fileWithHashMap, tempPassDir.getAbsolutePath() + File.separator, hashFunction);
        File manifestJSONFile = new File(tempPassDir.getAbsolutePath() + File.separator + "manifest.json");
        jsonObjectMapper.writeValue(manifestJSONFile, fileWithHashMap);

        signManifestFile(tempPassDir, manifestJSONFile, signCertificate, privateKey, intermediateCertificate);

        ByteArrayOutputStream byteArrayOutputStreamForZippedPass = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStreamForZippedPass);
        zip(tempPassDir, tempPassDir, zipOutputStream);
        zipOutputStream.close();

        FileUtils.deleteDirectory(tempPassDir);
        return byteArrayOutputStreamForZippedPass.toByteArray();
    }

    public static void signManifestFile(final File temporaryPassDirectory, final File manifestJSONFile,
            final X509Certificate signCertificate, final PrivateKey privateKey, final X509Certificate intermediateCertificate)
            throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);

        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC")
                .build()).build(sha1Signer, signCertificate));

        List<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(intermediateCertificate);
        certList.add(signCertificate);

        Store certs = new JcaCertStore(certList);

        generator.addCertificates(certs);

        CMSSignedData sigData = generator.generate(new CMSProcessableFile(manifestJSONFile), false);
        byte[] signedDataBytes = sigData.getEncoded();

        File signatureFile = new File(temporaryPassDirectory.getAbsolutePath() + File.separator + "signature");
        FileOutputStream signatureOutputStream = new FileOutputStream(signatureFile);
        signatureOutputStream.write(signedDataBytes);
        signatureOutputStream.close();

    }

    private static void hashFilesInDirectory(final File[] files, final Map<String, String> fileWithHashMap, final String workingDirectory,
            final HashFunction hashFunction) throws IOException {
        for (File passResourceFile : files) {
            if (passResourceFile.isFile()) {
                HashCode hash = Files.hash(passResourceFile, hashFunction);
                String name = passResourceFile.getAbsolutePath().replace(workingDirectory, "");
                fileWithHashMap.put(name, Hex.encodeHexString(hash.asBytes()));
            } else if (passResourceFile.isDirectory()) {
                hashFilesInDirectory(passResourceFile.listFiles(), fileWithHashMap, workingDirectory, hashFunction);
            }
        }
    }

    private static final void zip(final File directory, final File base, final ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[8192];
        int read = 0;
        for (int i = 0, n = files.length; i < n; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, zipOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
                zipOutputStream.putNextEntry(entry);
                while (-1 != (read = fileInputStream.read(buffer))) {
                    zipOutputStream.write(buffer, 0, read);
                }
                fileInputStream.close();
            }
        }
    }

}
