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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.enums.PKDataDetectorType;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.enums.PKNumberStyle;
import de.brendamour.jpasskit.enums.PKTextAlignment;

import static org.assertj.core.api.Assertions.assertThat;

public class PKFieldTest {

    private static final String KEY = "key";
    private static final String VALUE_TEXT = "some Text";
    private static final Number VALUE_NUMBER = 43;
    private static final Date VALUE_DATE = new Date();
    private static final String CHANGEMESSAGE = "Changed %@";
    private static final String LABEL = "Label";
    private static final Integer ROW = 1;
    private static final BigDecimal VALUE_CURRENCY = new BigDecimal("25.20").setScale(2, RoundingMode.HALF_UP);
    private static final String CURRENCYCODE = "EUR";
    private static final String ATTRIBUTED_VALUE = "<a href='http://example.com/customers/123'>Edit my profile</a>";

    private PKFieldBuilder builder;

    @BeforeMethod
    public void prepareTest() {
        builder = PKField.builder();
    }

    @Test
    public void test_clone() {
        fillFieldsText();
        PKField field = builder.build();
        PKField copy = PKField.builder(field).build();

        assertThat(copy)
                .isNotSameAs(field)
                .isEqualToComparingFieldByFieldRecursively(field);

        assertThat(copy.getKey()).isEqualTo(KEY);
        assertThat(copy.getValue()).isEqualTo(VALUE_TEXT);
        assertThat(copy.getChangeMessage()).isEqualTo(CHANGEMESSAGE);
        assertThat(copy.getLabel()).isEqualTo(LABEL);
        assertThat(copy.getRow()).isEqualTo(ROW);
        assertThat(copy.getAttributedValue()).isEqualTo(ATTRIBUTED_VALUE);
        assertThat(copy.getDataDetectorTypes())
                .hasSize(1)
                .containsExactly(PKDataDetectorType.PKDataDetectorTypeAddress);
    }

    @Test
    public void test_getters_Text() {
        fillFieldsText();
        assertThat(builder.isValid()).isTrue();

        PKField field = builder.build();
        assertThat(field.getKey()).isEqualTo(KEY);
        assertThat(field.getValue()).isEqualTo(VALUE_TEXT);
        assertThat(field.getChangeMessage()).isEqualTo(CHANGEMESSAGE);
        assertThat(field.getLabel()).isEqualTo(LABEL);
        assertThat(field.getRow()).isEqualTo(ROW);
        assertThat(field.getAttributedValue()).isEqualTo(ATTRIBUTED_VALUE);
        assertThat(field.getDataDetectorTypes())
                .hasSize(1)
                .containsExactly(PKDataDetectorType.PKDataDetectorTypeAddress);
    }

    @Test
    public void test_getters_Currency() {
        fillFieldsCurrency();

        assertThat(builder.isValid()).isTrue();
        assertThat(builder.build().getValue()).isEqualTo(VALUE_CURRENCY);
    }

    @Test
    public void test_getters_Number() {
        fillBasisFields();
        PKField field = builder.numberStyle(PKNumberStyle.PKNumberStyleDecimal)
                .textAlignment(PKTextAlignment.PKTextAlignmentCenter)
                .currencyCode("GBP")
                .isRelative(true)
                .build();

        assertThat(field.getDateStyle()).isNull();
        assertThat(field.getTimeStyle()).isNull();
        assertThat(field.getIgnoresTimeZone()).isNull();

        assertThat(field.getNumberStyle()).isEqualTo(PKNumberStyle.PKNumberStyleDecimal);
        assertThat(field.getTextAlignment()).isEqualTo(PKTextAlignment.PKTextAlignmentCenter);
        assertThat(field.getCurrencyCode()).isEqualTo("GBP");
        assertThat(field.getIsRelative()).isTrue();
    }

    @Test
    public void test_getters_DateTime() {
        fillBasisFields();
        PKField field = builder.dateStyle(PKDateStyle.PKDateStyleMedium)
                .timeStyle(PKDateStyle.PKDateStyleFull)
                .ignoresTimeZone(true)
                .build();

        assertThat(field.getDateStyle()).isEqualTo(PKDateStyle.PKDateStyleMedium);
        assertThat(field.getTimeStyle()).isEqualTo(PKDateStyle.PKDateStyleFull);
        assertThat(field.getIgnoresTimeZone()).isTrue();

        assertThat(field.getNumberStyle()).isNull();
        assertThat(field.getTextAlignment()).isNull();
        assertThat(field.getCurrencyCode()).isNull();
        assertThat(field.getIsRelative()).isNull();
    }

    @Test
    public void test_validation_NoKey() {
        fillFieldsText();
        builder.key(null);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_EmptyKey() {
        fillFieldsText();
        builder.key("");

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_NoValue() {
        fillFieldsText();

        assertThat(builder.value((String) null).isValid()).isFalse();
        assertThat(builder.value((Integer) null).isValid()).isFalse();
        assertThat(builder.value((Float) null).isValid()).isFalse();
        assertThat(builder.value((Long) null).isValid()).isFalse();
        assertThat(builder.value((Double) null).isValid()).isFalse();
        assertThat(builder.value((BigDecimal) null).isValid()).isFalse();
        assertThat(builder.value((Date) null).isValid()).isFalse();
    }

    @Test
    public void test_validation_CurrencyAndNumberFormatSet() {
        fillFieldsCurrency();
        builder.numberStyle(PKNumberStyle.PKNumberStyleDecimal);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_CurrencyAndDateStyleSet() {
        fillFieldsCurrency();
        builder.dateStyle(PKDateStyle.PKDateStyleFull);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_CurrencyAndTimeStyleSet() {
        fillFieldsCurrency();
        builder.timeStyle(PKDateStyle.PKDateStyleFull);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_CurrencyAndValueNotANumber() {
        fillFieldsCurrency();
        builder.value("2.20");

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_NumberAndDateStyleSet() {
        fillBasisFields();
        builder.numberStyle(PKNumberStyle.PKNumberStyleDecimal)
                .dateStyle(PKDateStyle.PKDateStyleFull);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_toString() {
        fillFieldsText();
        PKField field = builder.build();
        assertThat(field.toString())
                .contains(KEY)
                .contains(CHANGEMESSAGE)
                .contains(LABEL)
                .contains(ROW.toString())
                .contains(VALUE_TEXT)
                .contains(ATTRIBUTED_VALUE);
    }

    private void fillFieldsText() {
        fillBasisFields();
        builder.value(VALUE_TEXT)
                .dataDetectorType(PKDataDetectorType.PKDataDetectorTypeAddress);
    }

    private void fillBasisFields() {
        builder.key(KEY)
                .changeMessage(CHANGEMESSAGE)
                .label(LABEL)
                .row(ROW)
                .attributedValue(ATTRIBUTED_VALUE);
    }

    private void fillFieldsCurrency() {
        fillBasisFields();
        builder.value(VALUE_CURRENCY)
                .currencyCode(CURRENCYCODE);
    }
}
