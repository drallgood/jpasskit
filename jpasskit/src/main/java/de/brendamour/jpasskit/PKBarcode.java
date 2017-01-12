/**
 * Copyright (C) 2017 Patrice Brend'amour <patrice@brendamour.net>
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
package de.brendamour.jpasskit;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

public class PKBarcode implements IPKValidateable {

    private static final long serialVersionUID = -7661537217765974179L;
    private static final List<PKBarcodeFormat> BARCODE_TYPES_BEFORE_IOS_9;
    static {
        List<PKBarcodeFormat> barcodeTypes = new ArrayList<>(3);
        barcodeTypes.add(PKBarcodeFormat.PKBarcodeFormatQR);
        barcodeTypes.add(PKBarcodeFormat.PKBarcodeFormatPDF417);
        barcodeTypes.add(PKBarcodeFormat.PKBarcodeFormatAztec);
        BARCODE_TYPES_BEFORE_IOS_9 = Collections.unmodifiableList(barcodeTypes);
    }

    private PKBarcodeFormat format;
    private String altText;
    private String message;
    // updated as Charset is not serializable
    private String messageEncoding;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public PKBarcodeFormat getFormat() {
        return format;
    }

    public void setFormat(final PKBarcodeFormat format) {
        this.format = format;
    }

    public Charset getMessageEncoding() {
        if (StringUtils.isNotEmpty(messageEncoding)) {
            return Charset.forName(messageEncoding);
        } else {
            return null;
        }
    }

    public String getMessageEncodingAsString() {
        return messageEncoding;
    }

    public void setMessageEncoding(final Charset messageEncoding) {
        if (messageEncoding != null) {
            this.messageEncoding = messageEncoding.name();
        } else {
            this.messageEncoding = null;
        }
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(final String altText) {
        this.altText = altText;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    protected boolean isValidInIosVersionsBefore9() {
        return BARCODE_TYPES_BEFORE_IOS_9.contains(getFormat());
    }

    public List<String> getValidationErrors() {
        List<String> validationErrors = new ArrayList<String>(1);

        if (format == null || StringUtils.isEmpty(message) || StringUtils.isEmpty(messageEncoding)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Not all required Fields are set. Format: ");
            builder.append(format);
            builder.append(" Message: ");
            builder.append(message);
            builder.append(" MessageEncoding: ");
            builder.append(messageEncoding);
            validationErrors.add(builder.toString());
        }
        return validationErrors;
    }
}
