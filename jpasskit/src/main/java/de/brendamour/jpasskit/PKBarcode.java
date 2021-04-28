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
package de.brendamour.jpasskit;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

@JsonDeserialize(builder = PKBarcodeBuilder.class)
public class PKBarcode implements Cloneable, Serializable {

    private static final long serialVersionUID = -7661537217765974179L;

    protected PKBarcodeFormat format;
    protected String altText;
    protected String message;
    protected String messageEncoding;

    protected PKBarcode() {
    }

    public String getMessage() {
        return message;
    }

    public PKBarcodeFormat getFormat() {
        return format;
    }

    public String getMessageEncoding() {
        return messageEncoding;
    }

    public String getAltText() {
        return altText;
    }

    @Override
    protected PKBarcode clone() {
        try {
            return (PKBarcode) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKBarcode instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKBarcodeBuilder builder() {
        return new PKBarcodeBuilder();
    }

    public static PKBarcodeBuilder builder(PKBarcode barcode) {
        return builder().of(barcode);
    }
}
