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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.enums.PKDataDetectorType;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.enums.PKNumberStyle;
import de.brendamour.jpasskit.enums.PKTextAlignment;

public class PKFieldTest {

    private static final String KEY = "key";
    private static final String VALUE_TEXT = "some Text";
    private static final String CHANGEMESSAGE = "Changed %@";
    private static final String LABEL = "Label";
    private static final BigDecimal VALUE_CURRENCY = new BigDecimal("25.20").setScale(2, RoundingMode.HALF_UP);
    private static final String CURRENCYCODE = "EUR";
    private static final String ATTRIBUTED_VALUE = "<a href='http://example.com/customers/123'>Edit my profile</a>";

    private PKFieldBuilder builder;

    @BeforeMethod
    public void prepareTest() {
        builder = PKField.builder();
    }

    @Test
    public void test_GetterSetter_Text() {
        fillFieldsText();
        Assert.assertTrue(builder.isValid());

        PKField field = builder.build();
        Assert.assertEquals(field.getKey(), KEY);
        Assert.assertEquals(field.getValue(), VALUE_TEXT);
        Assert.assertEquals(field.getChangeMessage(), CHANGEMESSAGE);
        Assert.assertEquals(field.getLabel(), LABEL);
        Assert.assertEquals(field.getAttributedValue(), ATTRIBUTED_VALUE);
        Assert.assertEquals(field.getDataDetectorTypes().size(), 1);
    }

    @Test
    public void test_GetterSetter_NoKey() {
        fillFieldsText();
        builder.key(null);
        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_EmptyKey() {
        fillFieldsText();
        builder.key("");

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_NoValue() {
        fillFieldsText();
        builder.value(null);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_InvalidValueType() {
        fillFieldsText();
        builder.value(new PKField());

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_Currency() {
        fillFieldsCurrency();
        Assert.assertTrue(builder.isValid());

        PKField field = builder.build();
        Assert.assertEquals(field.getValue(), VALUE_CURRENCY);
    }

    @Test
    public void test_GetterSetter_CurrencyAndNumberFormatSet() {
        fillFieldsCurrency();
        builder.numberStyle(PKNumberStyle.PKNumberStyleDecimal);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_CurrencyAndDateStyleSet() {
        fillFieldsCurrency();
        builder.dateStyle(PKDateStyle.PKDateStyleFull);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_CurrencyAndTimeStyleSet() {
        fillFieldsCurrency();
        builder.timeStyle(PKDateStyle.PKDateStyleFull);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_CurrencyAndValueNotANumber() {
        fillFieldsCurrency();
        builder.value("2.20");

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_NumberAndDateStyleSet() {
        fillBasisFields();
        builder.numberStyle(PKNumberStyle.PKNumberStyleDecimal)
                .dateStyle(PKDateStyle.PKDateStyleFull);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_GetterSetter_ChangeMessageWithNoPlaceholder() {
        fillBasisFields();
        builder.changeMessage("Change");

        Assert.assertFalse(builder.isValid());
    }

    private void fillFieldsText() {
        fillBasisFields();
        builder.value(VALUE_TEXT);
    }

    private void fillBasisFields() {
        builder.key(KEY)
                .changeMessage(CHANGEMESSAGE)
                .label(LABEL)
                .textAlignment(PKTextAlignment.PKTextAlignmentCenter)
                .attributedValue(ATTRIBUTED_VALUE)
                .dataDetectorType(PKDataDetectorType.PKDataDetectorTypeAddress);
    }

    private void fillFieldsCurrency() {
        fillBasisFields();
        builder.value(VALUE_CURRENCY)
                .currencyCode(CURRENCYCODE);
    }
}
