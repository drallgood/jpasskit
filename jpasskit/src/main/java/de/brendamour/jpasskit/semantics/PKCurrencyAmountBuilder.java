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
package de.brendamour.jpasskit.semantics;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import org.apache.commons.lang3.StringUtils;

import de.brendamour.jpasskit.IPKBuilder;
import de.brendamour.jpasskit.IPKValidateable;

/**
 * Allows constructing and validating {@link PKCurrencyAmount} entities.
 *
 * @author Patrice Brend'amour
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKCurrencyAmountBuilder implements IPKValidateable, IPKBuilder<PKCurrencyAmount> {

    private PKCurrencyAmount currencyAmount;

    protected PKCurrencyAmountBuilder() {
        this.currencyAmount = new PKCurrencyAmount();
    }

    @Override
    public PKCurrencyAmountBuilder of(final PKCurrencyAmount source) {
        if (source != null) {
            this.currencyAmount = source.clone();
        }
        return this;
    }

    public PKCurrencyAmountBuilder currencyCode(String currencyCode) {
        this.currencyAmount.currencyCode = currencyCode;
        return this;
    }

    public PKCurrencyAmountBuilder amount(String amount) {
        this.currencyAmount.amount = amount;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {

        if (this.currencyAmount.currencyCode == null || StringUtils.isEmpty(this.currencyAmount.currencyCode)
                || StringUtils.isEmpty(this.currencyAmount.amount)) {
            return Collections.singletonList("Not all required Fields are set. CurrencyCode: " + this.currencyAmount.currencyCode
                    + " Amount: " + this.currencyAmount.amount);
        }
        return Collections.emptyList();
    }

    @Override
    public PKCurrencyAmount build() {
        return this.currencyAmount;
    }
}
