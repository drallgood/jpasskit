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

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;

import de.brendamour.jpasskit.PKPass;

public interface IPKSigningUtil {

    /**
     * Creates a signed and zipped pass using a template
     * @param pass
     *   The pass to sign
     * @param passTemplate
     *   A {@link IPKPassTemplate} object
     * @param signingInformation
     *   A {@link PKSigningInformation} object containing the signing info
     * @return
     *   a signed and zipped .pkpass file
     * @throws Exception
     */
    public byte[] createSignedAndZippedPkPassArchive(PKPass pass, IPKPassTemplate passTemplate,
            PKSigningInformation signingInformation) throws Exception;

    public byte[] signManifestFile(byte[] manifestJSON, PKSigningInformation signingInformation) throws CertificateEncodingException,
    OperatorCreationException, CMSException, IOException;

}
