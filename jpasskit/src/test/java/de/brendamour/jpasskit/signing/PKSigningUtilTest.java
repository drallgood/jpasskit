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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.signing.PKSigningUtil;

public class PKSigningUtilTest {

    private String appleWWDRCA = "/Users/patrice/Documents/bitzeche/Projects/passkit/AppleWWDRCA.pem";
    private String certFilePath = "/Users/patrice/Documents/bitzeche/Projects/passkit/cert_pktest.pem";
    private String keyFilePath = "/Users/patrice/Documents/bitzeche/Projects/passkit/key_pktest.pem";

    
    
    
//    @Test
    public void execute() throws IOException, Exception {

        Security.addProvider(new BouncyCastleProvider());
        File temporaryPassDir = new File("/Users/patrice/Documents/bitzeche/Projects/passkit/");
        File manifestJSONFile = new File("/Users/patrice/Downloads/passbook/Passes/BoardingPass.zip Folder/manifest.json");
        X509CertificateObject appleWWDRCAcert = (X509CertificateObject) readKeyPair(appleWWDRCA);

        PKSigningUtil.signManifestFile(temporaryPassDir, manifestJSONFile, getPublicKey(), getPrivateKey(), appleWWDRCAcert);
    }

    private X509Certificate getPublicKey() throws IOException {
        X509CertificateObject cert = (X509CertificateObject) readKeyPair(certFilePath);
        // return cert.getPublicKey();
        return cert;

    }

    private PrivateKey getPrivateKey() throws IOException {
        KeyPair key = (KeyPair) readKeyPair(keyFilePath);
        return key.getPrivate();
    }

    private Object readKeyPair(final String file) throws IOException {
        FileReader fileReader = new FileReader(file);
        PEMReader r = new PEMReader(fileReader);
        try {
            return r.readObject();
        } catch (IOException ex) {
            throw new IOException("The key from '" + file + "' could not be decrypted", ex);
        } finally {
            r.close();
            fileReader.close();
        }
    }
}
