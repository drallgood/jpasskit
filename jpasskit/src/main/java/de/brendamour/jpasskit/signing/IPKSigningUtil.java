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

import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.personalization.PKPersonalization;

public interface IPKSigningUtil {

    /**
     * Creates a signed and zipped pass using a template
     * 
     * @param pass
     *            The pass to sign
     * @param passTemplate
     *            A {@link IPKPassTemplate} object
     * @param signingInformation
     *            A {@link PKSigningInformation} object containing the signing info
     * @return a signed and zipped .pkpass file
     * @throws PKSigningException
     *             will throw any underlying exception in case something goes wrong (i.e. template not found)
     */
    byte[] createSignedAndZippedPkPassArchive(PKPass pass, IPKPassTemplate passTemplate, PKSigningInformation signingInformation)
            throws PKSigningException;

    /**
     * Creates a signed and zipped personalized pass using a template
     * 
     * @param pass
     *            The pass to sign
     * @param personalization
     *            Personalization info
     * @param passTemplate
     *            A {@link IPKPassTemplate} object
     * @param signingInformation
     *            A {@link PKSigningInformation} object containing the signing info
     * @return a signed and zipped .pkpass file
     * @throws PKSigningException
     *             will throw any underlying exception in case something goes wrong (i.e. template not found)
     */
    byte[] createSignedAndZippedPersonalizedPkPassArchive(PKPass pass, PKPersonalization personalization, IPKPassTemplate passTemplate,
            PKSigningInformation signingInformation) throws PKSigningException;

    /**
     * Sign the manifest file
     * 
     * @param manifestJSON
     *            JSON file as byte array
     * @param signingInformation
     *            A {@link PKSigningInformation} object containing the signing info
     * @return The signature for the manifest file
     * @throws PKSigningException
     *             will throw any underlying exception in case something goes wrong (i.e. template not found)
     */
    byte[] signManifestFile(byte[] manifestJSON, PKSigningInformation signingInformation) throws PKSigningException;

}
