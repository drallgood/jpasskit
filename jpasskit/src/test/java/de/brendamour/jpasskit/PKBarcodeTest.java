package de.brendamour.jpasskit;

import java.nio.charset.Charset;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

public class PKBarcodeTest {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final PKBarcodeFormat PKBARCODEFORMAT = PKBarcodeFormat.PKBarcodeFormatQR;
    private static final String ALT_TEXT = "Text";
    private static final String MESSAGE = "Message";
    private PKBarcode pkBarcode;

    @BeforeMethod
    public void prepareTest() {
        pkBarcode = new PKBarcode();
        fillBarcode();
    }

    @Test
    public void test_getSet() {
        fillBarcode();

        Assert.assertEquals(pkBarcode.getMessage(), MESSAGE);
        Assert.assertEquals(pkBarcode.getAltText(), ALT_TEXT);
        Assert.assertEquals(pkBarcode.getMessageEncoding(), CHARSET);
        Assert.assertEquals(pkBarcode.getFormat(), PKBARCODEFORMAT);
        Assert.assertTrue(pkBarcode.isValid());
    }

    private void fillBarcode() {
        pkBarcode.setAltText(ALT_TEXT);
        pkBarcode.setFormat(PKBARCODEFORMAT);
        pkBarcode.setMessage(MESSAGE);
        pkBarcode.setMessageEncoding(CHARSET);
    }

    @Test
    public void test_noFormat() {
        pkBarcode.setFormat(null);

        Assert.assertFalse(pkBarcode.isValid());
    }

    @Test
    public void test_noMessage() {
        pkBarcode.setMessage(null);

        Assert.assertFalse(pkBarcode.isValid());
    }

    @Test
    public void test_emptyMessage() {
        pkBarcode.setMessage("");

        Assert.assertFalse(pkBarcode.isValid());
    }

    @Test
    public void test_noMessageEncoding() {
        pkBarcode.setMessageEncoding(null);

        Assert.assertFalse(pkBarcode.isValid());
    }

    @Test
    public void test_noAltText() {
        pkBarcode.setAltText(null);

        Assert.assertTrue(pkBarcode.isValid());
    }
}
