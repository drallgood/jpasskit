/**
 * Copyright (C) 2024 Patrice Brend'amour <patrice@brendamour.net>
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
package de.brendamour.jpasskit.semantics;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PKCurrencyAmountBuilderTest {

    private PKCurrencyAmountBuilder builder;

    @BeforeMethod
    public void setUp() {
        builder = PKCurrencyAmount.builder();
    }

    @Test
    public void testOfWithNullSource() {
        PKCurrencyAmount result = builder.of(null).build();
        assertThat(result).isNotNull();
    }

    @Test
    public void testOfWithValidSource() {
        PKCurrencyAmount source = PKCurrencyAmount.builder()
            .currencyCode("USD")
            .amount("19.99")
            .build();
        
        PKCurrencyAmount result = builder.of(source).build();
        assertThat(result.currencyCode).isEqualTo("USD");
        assertThat(result.amount).isEqualTo("19.99");
    }

    @Test
    public void testCurrencyCode() {
        String currencyCode = "EUR";
        PKCurrencyAmount result = builder.currencyCode(currencyCode).build();
        assertThat(result.currencyCode).isEqualTo(currencyCode);
    }

    @Test
    public void testAmount() {
        String amount = "25.50";
        PKCurrencyAmount result = builder.amount(amount).build();
        assertThat(result.amount).isEqualTo(amount);
    }

    @Test
    public void testIsValidWithValidData() {
        builder.currencyCode("USD").amount("10.00");
        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void testIsValidWithNullCurrencyCode() {
        builder.currencyCode(null).amount("10.00");
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testIsValidWithEmptyCurrencyCode() {
        builder.currencyCode("").amount("10.00");
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testIsValidWithNullAmount() {
        builder.currencyCode("USD").amount(null);
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testIsValidWithEmptyAmount() {
        builder.currencyCode("USD").amount("");
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testIsValidWithBothEmpty() {
        builder.currencyCode("").amount("");
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testGetValidationErrorsWithValidData() {
        builder.currencyCode("USD").amount("10.00");
        assertThat(builder.getValidationErrors()).isEmpty();
    }

    @Test
    public void testGetValidationErrorsWithInvalidData() {
        builder.currencyCode(null).amount("");
        assertThat(builder.getValidationErrors()).hasSize(1);
        assertThat(builder.getValidationErrors().get(0)).contains("Not all required Fields are set");
        assertThat(builder.getValidationErrors().get(0)).contains("CurrencyCode: null");
        assertThat(builder.getValidationErrors().get(0)).contains("Amount:");
    }

    @Test
    public void testGetValidationErrorsWithMissingCurrencyCode() {
        builder.currencyCode("").amount("10.00");
        assertThat(builder.getValidationErrors()).hasSize(1);
        assertThat(builder.getValidationErrors().get(0)).contains("Not all required Fields are set");
    }

    @Test
    public void testGetValidationErrorsWithMissingAmount() {
        builder.currencyCode("USD").amount("");
        assertThat(builder.getValidationErrors()).hasSize(1);
        assertThat(builder.getValidationErrors().get(0)).contains("Not all required Fields are set");
    }

    @Test
    public void testChainedMethods() {
        PKCurrencyAmount result = builder
            .currencyCode("GBP")
            .amount("15.75")
            .build();
        
        assertThat(result.currencyCode).isEqualTo("GBP");
        assertThat(result.amount).isEqualTo("15.75");
    }

    @Test
    public void testVariousCurrencyCodes() {
        PKCurrencyAmount usd = builder.currencyCode("USD").amount("100.00").build();
        assertThat(usd.currencyCode).isEqualTo("USD");

        PKCurrencyAmount eur = PKCurrencyAmount.builder().currencyCode("EUR").amount("85.50").build();
        assertThat(eur.currencyCode).isEqualTo("EUR");

        PKCurrencyAmount jpy = PKCurrencyAmount.builder().currencyCode("JPY").amount("10000").build();
        assertThat(jpy.currencyCode).isEqualTo("JPY");
    }

    @Test
    public void testVariousAmountFormats() {
        PKCurrencyAmount decimal = builder.currencyCode("USD").amount("19.99").build();
        assertThat(decimal.amount).isEqualTo("19.99");

        PKCurrencyAmount whole = PKCurrencyAmount.builder().currencyCode("USD").amount("20").build();
        assertThat(whole.amount).isEqualTo("20");

        PKCurrencyAmount precise = PKCurrencyAmount.builder().currencyCode("USD").amount("123.456").build();
        assertThat(precise.amount).isEqualTo("123.456");
    }
}
