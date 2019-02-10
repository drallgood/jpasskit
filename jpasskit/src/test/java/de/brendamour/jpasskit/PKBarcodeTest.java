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

import java.nio.charset.Charset;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

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
    public void test_getSet() {
        Assert.assertTrue(builder.isValid());

        PKBarcode barcode = builder.build();
        Assert.assertEquals(barcode.getMessage(), MESSAGE);
        Assert.assertEquals(barcode.getAltText(), ALT_TEXT);
        Assert.assertEquals(barcode.getMessageEncoding(), CHARSET_NAME);
        Assert.assertEquals(barcode.getFormat(), PKBARCODEFORMAT);
    }

    private void fillBarcode() {
        builder.altText(ALT_TEXT)
                .format(PKBARCODEFORMAT)
                .message(MESSAGE)
                .messageEncoding(CHARSET);
    }

    @Test
    public void test_noFormat() {
        builder.format(null);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_noMessage() {
        builder.message(null);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_emptyMessage() {
        builder.message("");

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_noMessageEncoding() {
        builder.messageEncoding((Charset) null);
        Assert.assertFalse(builder.isValid());

        builder.messageEncoding((String) null);
        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_noAltText() {
        builder.altText(null);

        Assert.assertTrue(builder.isValid());
    }
}
