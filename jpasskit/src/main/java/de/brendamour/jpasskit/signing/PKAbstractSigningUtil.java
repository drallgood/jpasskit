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

import java.io.File;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKPass;

public abstract class PKAbstractSigningUtil implements IPKSigningUtil {

    /*
     * (non-Javadoc)
     * 
     * @see de.brendamour.jpasskit.signing.IPKSigningUtil#signManifestFile(byte[], de.brendamour.jpasskit.signing.PKSigningInformation)
     */
    @Override
    public byte[] signManifestFile(byte[] manifestJSON, PKSigningInformation signingInformation) throws PKSigningException {
        if (manifestJSON == null) {
            throw new IllegalArgumentException("manifestJSON has tobe present");
        }

        CMSProcessableByteArray content = new CMSProcessableByteArray(manifestJSON);
        return signManifestUsingContent(signingInformation, content);
    }

    protected byte[] signManifestUsingContent(PKSigningInformation signingInformation, CMSTypedData content) throws PKSigningException {
        if (signingInformation == null || !signingInformation.isValid()) {
            throw new IllegalArgumentException("Signing information not valid");
        }

        try {
            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(
                    signingInformation.getSigningPrivateKey());

            final ASN1EncodableVector signedAttributes = new ASN1EncodableVector();
            final Attribute signingAttribute = new Attribute(CMSAttributes.signingTime, new DERSet(new DERUTCTime(new Date())));
            signedAttributes.add(signingAttribute);

            // Create the signing table
            final AttributeTable signedAttributesTable = new AttributeTable(signedAttributes);
            // Create the table table generator that will added to the Signer builder
            final DefaultSignedAttributeTableGenerator signedAttributeGenerator = new DefaultSignedAttributeTableGenerator(signedAttributesTable);

            generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(
                    BouncyCastleProvider.PROVIDER_NAME).build()).setSignedAttributeGenerator(signedAttributeGenerator).build(sha1Signer,
                    signingInformation.getSigningCert()));

            List<X509Certificate> certList = new ArrayList<X509Certificate>();
            certList.add(signingInformation.getAppleWWDRCACert());
            certList.add(signingInformation.getSigningCert());

            JcaCertStore certs = new JcaCertStore(certList);

            generator.addCertificates(certs);

            CMSSignedData sigData = generator.generate(content, false);
            return sigData.getEncoded();
        } catch (Exception e) {
            throw new PKSigningException("Error when signing manifest", e);
        }
    }

    protected ObjectWriter configureObjectMapper(final ObjectMapper jsonObjectMapper) {
        jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonObjectMapper.setDateFormat(new ISO8601DateFormat());

        SimpleFilterProvider filters = new SimpleFilterProvider();

        // haven't found out, how to stack filters. Copying the validation one for now.
        filters.addFilter("validateFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors"));
        filters.addFilter("pkPassFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors", "foregroundColorAsObject",
                "backgroundColorAsObject", "labelColorAsObject", "passThatWasSet"));
        filters.addFilter("barcodeFilter", SimpleBeanPropertyFilter.serializeAllExcept("valid", "validationErrors", "messageEncodingAsString"));
        filters.addFilter("charsetFilter", SimpleBeanPropertyFilter.filterOutAllExcept("name"));
        jsonObjectMapper.setSerializationInclusion(Include.NON_NULL);
        jsonObjectMapper.addMixIn(Object.class, ValidateFilterMixIn.class);
        jsonObjectMapper.addMixIn(PKPass.class, PkPassFilterMixIn.class);
        jsonObjectMapper.addMixIn(PKBarcode.class, BarcodeFilterMixIn.class);
        jsonObjectMapper.addMixIn(Charset.class, CharsetFilterMixIn.class);
        return jsonObjectMapper.writer(filters);
    }

    protected String getRelativePathOfZipEntry(final String fileToZip, final String base) {
        String relativePathOfFile = fileToZip.substring(base.length()).replaceAll("^/+", "");
        if (File.separatorChar != '/') {
            relativePathOfFile = relativePathOfFile.replace(File.separatorChar, '/');
        }

        return relativePathOfFile;
    }

    protected @JsonFilter("pkPassFilter") class PkPassFilterMixIn {
        // just a dummy
    }

    protected @JsonFilter("validateFilter") class ValidateFilterMixIn {
        // just a dummy
    }

    protected @JsonFilter("barcodeFilter") class BarcodeFilterMixIn {
        // just a dummy
    }

    protected @JsonFilter("charsetFilter") class CharsetFilterMixIn {
        // just a dummy
    }

}
