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

package de.brendamour.jpasskit;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

public class PKBarcode implements IPKValidateable {

    private PKBarcodeFormat format;
    private String altText;
    private String message;
    private Charset messageEncoding;

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
        return messageEncoding;
    }

    public void setMessageEncoding(final Charset messageEncoding) {
        this.messageEncoding = messageEncoding;
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

    public List<String> getValidationErrors() {
        List<String> validationErrors = new ArrayList<String>();

        if (format == null || StringUtils.isEmpty(message) || messageEncoding == null) {
            validationErrors.add("Not all required Fields are set. Format: " + format + " Message: " + message + " MessageEncoding: "
                    + messageEncoding);
        }
        return validationErrors;
    }
}
