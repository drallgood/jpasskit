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

import java.nio.charset.Charset;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

import static org.assertj.core.api.Assertions.assertThat;

public class PKBarcodeTest {

    private static final String CHARSET_NAME = "UTF-8";
    private static final Charset CHARSET = Charset.forName(CHARSET_NAME);
    private static final PKBarcodeFormat PKBARCODEFORMAT = PKBarcodeFormat.PKBarcodeFormatQR;
    private static final String ALT_TEXT = "Text";
    private static final String MESSAGE = "Message";

    private PKBarcodeBuilder builder;

    @BeforeMethod
    public void prepareTest() {
        builder = PKBarcode.builder();
        fillBarcode();
    }

    @Test
    public void test_getters() {
        assertThat(builder.isValid()).isTrue();

        PKBarcode barcode = builder.build();
        assertThat(barcode.getMessage()).isEqualTo(MESSAGE);
        assertThat(barcode.getAltText()).isEqualTo(ALT_TEXT);
        assertThat(barcode.getMessageEncoding()).isEqualTo(CHARSET_NAME);
        assertThat(barcode.getFormat()).isEqualTo(PKBARCODEFORMAT);
    }

    @Test
    public void test_clone() {
        PKBarcode barcode = builder.build();
        PKBarcode copy = PKBarcode.builder(barcode).build();

        assertThat(copy)
                .isNotSameAs(barcode)
                .isEqualToComparingFieldByFieldRecursively(barcode);

        assertThat(copy.getMessage()).isEqualTo(MESSAGE);
        assertThat(copy.getAltText()).isEqualTo(ALT_TEXT);
        assertThat(copy.getMessageEncoding()).isEqualTo(CHARSET_NAME);
        assertThat(copy.getFormat()).isEqualTo(PKBARCODEFORMAT);
    }

    @Test
    public void test_validation_noFormat() {
        builder.format(null);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_noMessage() {
        builder.message(null);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_emptyMessage() {
        builder.message("");

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_noMessageEncoding() {
        builder.messageEncoding((Charset) null);
        assertThat(builder.isValid()).isFalse();

        builder.messageEncoding((String) null);
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_noAltText() {
        builder.altText(null);

        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void test_toString() {
        PKBarcode barcode = builder.build();

        assertThat(barcode.toString())
                .contains(MESSAGE)
                .contains(ALT_TEXT)
                .contains(CHARSET_NAME);
    }

    private void fillBarcode() {
        builder.altText(ALT_TEXT)
                .format(PKBARCODEFORMAT)
                .message(MESSAGE)
                .messageEncoding(CHARSET);
    }
}
