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

import java.time.Instant;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import de.brendamour.jpasskit.enums.PKDataDetectorType;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.enums.PKNumberStyle;
import de.brendamour.jpasskit.enums.PKTextAlignment;

/**
 * Allows constructing and validating {@link PKField} entities.
 *
 * @author Igor Stepanov
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKFieldBuilder implements IPKValidateable, IPKBuilder<PKField> {

    private PKField field;
    private List<PKDataDetectorType> dataDetectorTypes;

    protected PKFieldBuilder() {
        this.field = new PKField();
        this.dataDetectorTypes = new CopyOnWriteArrayList<>();
    }

    @Override
    public PKFieldBuilder of(final PKField source) {
        if (source != null) {
            this.field = source.clone();
            if (this.field.dataDetectorTypes == null) {
                this.dataDetectorTypes = new CopyOnWriteArrayList<>();
            } else {
                this.dataDetectorTypes = new CopyOnWriteArrayList<>(this.field.dataDetectorTypes);
            }
        }
        return this;
    }

    public PKFieldBuilder key(String key) {
        this.field.key = key;
        return this;
    }

    public PKFieldBuilder label(String label) {
        this.field.label = label;
        return this;
    }

    public PKFieldBuilder value(String value) {
        this.field.value = value;
        return this;
    }

    public PKFieldBuilder value(Integer value) {
        this.field.value = value;
        return this;
    }

    public PKFieldBuilder value(Float value) {
        this.field.value = value;
        return this;
    }

    public PKFieldBuilder value(Long value) {
        this.field.value = value;
        return this;
    }

    public PKFieldBuilder value(Double value) {
        this.field.value = value;
        return this;
    }

    public PKFieldBuilder value(BigDecimal value) {
        this.field.value = value;
        return this;
    }

    @Deprecated
    public PKFieldBuilder value(Date value) {
        this.field.value = value;
        return this;
    }

    public PKFieldBuilder value(Instant value) {
        this.field.value = value;
        return this;
    }

    @JsonProperty("value")
    protected PKFieldBuilder value(Serializable value) {
        this.field.value = value;
        return this;
    }

    public PKFieldBuilder changeMessage(String changeMessage) {
        this.field.changeMessage = changeMessage;
        return this;
    }

    public PKFieldBuilder textAlignment(PKTextAlignment textAlignment) {
        this.field.textAlignment = textAlignment;
        return this;
    }

    public PKFieldBuilder currencyCode(String currencyCode) {
        this.field.currencyCode = currencyCode;
        return this;
    }

    public PKFieldBuilder numberStyle(PKNumberStyle numberStyle) {
        this.field.numberStyle = numberStyle;
        return this;
    }

    public PKFieldBuilder dateStyle(PKDateStyle dateStyle) {
        this.field.dateStyle = dateStyle;
        return this;
    }

    public PKFieldBuilder timeStyle(PKDateStyle timeStyle) {
        this.field.timeStyle = timeStyle;
        return this;
    }

    public PKFieldBuilder isRelative(Boolean isRelative) {
        this.field.isRelative = isRelative;
        return this;
    }

    public PKFieldBuilder attributedValue(Serializable attributedValue) {
        this.field.attributedValue = attributedValue;
        return this;
    }

    public PKFieldBuilder dataDetectorType(PKDataDetectorType dataDetectorType) {
        this.dataDetectorTypes.add(dataDetectorType);
        return this;
    }

    public PKFieldBuilder dataDetectorTypes(final List<PKDataDetectorType> dataDetectorTypes) {
        if (dataDetectorTypes == null || dataDetectorTypes.isEmpty()) {
            this.dataDetectorTypes.clear();
            return this;
        }
        this.dataDetectorTypes.addAll(dataDetectorTypes);
        return this;
    }

    public PKFieldBuilder ignoresTimeZone(final Boolean ignoresTimeZone) {
        this.field.ignoresTimeZone = ignoresTimeZone;
        return this;
    }

    public PKFieldBuilder row(final Integer row) {
        this.field.row = row;
        return this;
    }

    public PKFieldBuilder semantics(final PKSemantics semantics) {
        this.field.semantics = semantics;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {

        List<String> validationErrors = new ArrayList<>();
        checkRequiredFields(validationErrors);
        checkValueType(validationErrors);
        checkCurrencyCodeAndNumberStyleAreNotBothSet(validationErrors);
        checkNumberOrCurrencyAndDateNotSetAtTheSameTime(validationErrors);
        checkCurrencyValueIsNumeric(validationErrors);
        return validationErrors;
    }

    private void checkCurrencyValueIsNumeric(List<String> validationErrors) {
        if (this.field.currencyCode != null && !isNumeric(this.field.value)) {
            validationErrors.add("Field 'currencyCode' must be set only for numeric types. When using currencies, the values have to be numbers");
        }
    }

    private void checkNumberOrCurrencyAndDateNotSetAtTheSameTime(List<String> validationErrors) {
        if ((this.field.currencyCode != null || this.field.numberStyle != null) && (this.field.dateStyle != null || this.field.timeStyle != null)) {
            validationErrors.add(
                    "Either 'currencyCode' or 'numberStyle' are set along with 'dateStyle' and/or 'timeStyle'." +
                            " PKField cannot be number/currency and date at the same time");
        }
    }

    private void checkCurrencyCodeAndNumberStyleAreNotBothSet(List<String> validationErrors) {
        if (this.field.currencyCode != null && this.field.numberStyle != null) {
            validationErrors.add(
                    "Fields currencyCode and numberStyle are both set." +
                            " PKField cannot be number and currency at the same time");
        }
    }

    private void checkValueType(List<String> validationErrors) {
        if (!(this.field.value instanceof String || isNumeric(this.field.value) || this.field.value instanceof Date || this.field.value instanceof Instant)) {
            validationErrors.add(
                    "Invalid value type. Allowed: String, Integer, Float, Long, Double, java.util.Date, Instant, BigDecimal");
        }
    }

    private void checkRequiredFields(List<String> validationErrors) {
        if (this.field.value == null || isEmpty(this.field.key)) {
            validationErrors.add("Not all required fields are set. Key: " + this.field.key + " Value: " + this.field.value);
        }
    }

    @Override
    public PKField build() {
        if (!this.dataDetectorTypes.isEmpty()) {
            this.field.dataDetectorTypes = Collections.unmodifiableList(this.dataDetectorTypes);
        }
        return this.field;
    }

    private static boolean isNumeric(Serializable value) {
        return value instanceof Integer
                || value instanceof Float
                || value instanceof Long
                || value instanceof Double
                || value instanceof BigDecimal;
    }
}
