/**
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

import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows constructing and validating {@link PKBarcode} entities.
 *
 * @author Igor Stepanov
 */
public class PKBarcodeBuilder implements IPKValidateable, IPKBuilder<PKBarcode> {

    private static final List<PKBarcodeFormat> BARCODE_TYPES_BEFORE_IOS_9;
    static {
        List<PKBarcodeFormat> barcodeTypes = new ArrayList<>();
        barcodeTypes.add(PKBarcodeFormat.PKBarcodeFormatQR);
        barcodeTypes.add(PKBarcodeFormat.PKBarcodeFormatPDF417);
        barcodeTypes.add(PKBarcodeFormat.PKBarcodeFormatAztec);
        BARCODE_TYPES_BEFORE_IOS_9 = Collections.unmodifiableList(barcodeTypes);
    }

    private PKBarcode barcode;

    protected PKBarcodeBuilder() {
        this.barcode = new PKBarcode();
    }

    @Override
    public PKBarcodeBuilder of(final PKBarcode source) {
        if (source != null) {
            this.barcode = source.clone();
        }
        return this;
    }

    public PKBarcodeBuilder message(String message) {
        this.barcode.message = message;
        return this;
    }

    public PKBarcodeBuilder format(PKBarcodeFormat format) {
        this.barcode.format = format;
        return this;
    }

    public PKBarcodeBuilder messageEncoding(String messageEncoding) {
        this.barcode.messageEncoding = messageEncoding;
        return this;
    }

    public PKBarcodeBuilder messageEncoding(Charset messageEncoding) {
        return messageEncoding(messageEncoding == null ? null : messageEncoding.name());
    }

    public PKBarcodeBuilder altText(String altText) {
        this.barcode.altText = altText;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {

        if (this.barcode.format == null || StringUtils.isEmpty(this.barcode.message)
                || StringUtils.isEmpty(this.barcode.messageEncoding)) {
            return Collections.singletonList("Not all required Fields are set. Format: " + this.barcode.format
                    + " Message: " + this.barcode.message + " MessageEncoding: " + this.barcode.messageEncoding);
        }
        return Collections.emptyList();
    }

    protected boolean isValidInIosVersionsBefore9() {
        return BARCODE_TYPES_BEFORE_IOS_9.contains(this.barcode.format);
    }

    @Override
    public PKBarcode build() {
        return this.barcode;
    }
}
