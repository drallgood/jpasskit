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
import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.testng.annotations.Test;

public class PKSigningUtilTest {

    private String appleWWDRCA = "/Users/patrice/Documents/bitzeche/Projects/passkit/AppleWWDRCA.pem";
    private String keyStorePath = "/Users/patrice/Documents/bitzeche/Projects/passkit/Certificates.p12";
    private String keyStorePassword = "cert";

    // @Test
    public void execute() throws IOException, Exception {

        Security.addProvider(new BouncyCastleProvider());
        File temporaryPassDir = new File("/Users/patrice/Documents/bitzeche/Projects/passkit/");
        File manifestJSONFile = new File("/Users/patrice/Downloads/passbook/Passes/BoardingPass.zip Folder/manifest.json");

        PKSigningInformation pkSigningInformation = PKSigningUtil.loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(
                keyStorePath, keyStorePassword, appleWWDRCA);
        PKSigningUtil.signManifestFile(temporaryPassDir, manifestJSONFile, pkSigningInformation);
    }

}
