package de.brendamour.jpasskit;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.enums.PKNumberStyle;
import de.brendamour.jpasskit.enums.PKTextAlignment;

public class PKFieldTest {
    private static final String KEY = "key";
    private static final String VALUE_TEXT = "some Text";
    private static final String CHANGEMESSAGE = "Changed %@";
    private static final String LABEL = "Label";
    private static final BigDecimal VALUE_CURRENCY = new BigDecimal(25.20).setScale(2, RoundingMode.HALF_UP);
    private static final String CURRENCYCODE = "EUR";
    private PKField pkField;

    @BeforeMethod
    public void prepareTest() {
        pkField = new PKField();
    }

    @Test
    public void test_GetterSetter_Text() {
        fillFieldsText();

        Assert.assertEquals(pkField.getKey(), KEY);
        Assert.assertEquals(pkField.getValue(), VALUE_TEXT);
        Assert.assertEquals(pkField.getChangeMessage(), CHANGEMESSAGE);
        Assert.assertEquals(pkField.getLabel(), LABEL);
        Assert.assertTrue(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_NoKey() {
        fillFieldsText();
        pkField.setKey(null);

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_EmptyKey() {
        fillFieldsText();
        pkField.setKey("");

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_NoValue() {
        fillFieldsText();
        pkField.setValue(null);

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_InvalidValueType() {
        fillFieldsText();
        pkField.setValue(new PKField());

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_Currency() {
        fillFieldsCurrency();

        Assert.assertEquals(pkField.getValue(), VALUE_CURRENCY);
        Assert.assertTrue(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_CurrencyAndNumberFormatSet() {
        fillFieldsCurrency();
        pkField.setNumberStyle(PKNumberStyle.PKNumberStyleDecimal);

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_CurrencyAndDateStyleSet() {
        fillFieldsCurrency();
        pkField.setDateStyle(PKDateStyle.PKDateStyleFull);

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_CurrencyAndTimeStyleSet() {
        fillFieldsCurrency();
        pkField.setTimeStyle(PKDateStyle.PKDateStyleFull);

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_NumberAndDateStyleSet() {
        fillBasisFields();

        pkField.setNumberStyle(PKNumberStyle.PKNumberStyleDecimal);
        pkField.setDateStyle(PKDateStyle.PKDateStyleFull);

        Assert.assertFalse(pkField.isValid());

    }

    @Test
    public void test_GetterSetter_ChangeMessageWithNoPlaceholder() {
        fillBasisFields();

        pkField.setChangeMessage("Change");

        Assert.assertFalse(pkField.isValid());

    }

    private void fillFieldsText() {
        pkField.setValue(VALUE_TEXT);
        fillBasisFields();
    }

    private void fillBasisFields() {
        pkField.setKey(KEY);
        pkField.setChangeMessage(CHANGEMESSAGE);
        pkField.setLabel(LABEL);
        pkField.setTextAlignment(PKTextAlignment.PKTextAlignmentCenter);
    }

    private void fillFieldsCurrency() {
        pkField.setValue(VALUE_CURRENCY);
        pkField.setCurrencyCode(CURRENCYCODE);
        fillBasisFields();
    }
}
