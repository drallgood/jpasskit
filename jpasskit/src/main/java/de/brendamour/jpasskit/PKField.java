/**
 * Copyright (C) 2018 Patrice Brend'amour <patrice@brendamour.net>
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.enums.PKDataDetectorType;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.enums.PKNumberStyle;
import de.brendamour.jpasskit.enums.PKTextAlignment;

public class PKField implements IPKValidateable {

    private static final long serialVersionUID = -6362596567978565530L;

    private String key;
    private String label;
    private Serializable value;
    /**
     * @since iOS 7.0
     */
    private Serializable attributedValue;
    private String changeMessage;
    private PKTextAlignment textAlignment;

    /**
     * @since iOS 7.0
     */
    private List<PKDataDetectorType> dataDetectorTypes;

    /*
     * Number Fields
     */
    private String currencyCode;
    private PKNumberStyle numberStyle;

    /*
     * Date Fields
     */
    private PKDateStyle dateStyle;
    private PKDateStyle timeStyle;
    private Boolean isRelative;
    /**
     * @since iOS 7.0 Has to be null by default, since if it's set, iOS will validate the field as a date even the API consumer didn't want that.
     */
    private Boolean ignoresTimeZone; // The key is optional, default value is null

    public PKField() {
    }

    public PKField(String key, String label, Serializable value) {
        this.key = key;
        this.label = label;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(final Serializable value) {
        this.value = value;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(final String changeMessage) {
        this.changeMessage = changeMessage;
    }

    public PKTextAlignment getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(final PKTextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public PKNumberStyle getNumberStyle() {
        return numberStyle;
    }

    public void setNumberStyle(final PKNumberStyle numberStyle) {
        this.numberStyle = numberStyle;
    }

    public PKDateStyle getDateStyle() {
        return dateStyle;
    }

    public void setDateStyle(final PKDateStyle dateStyle) {
        this.dateStyle = dateStyle;
    }

    public PKDateStyle getTimeStyle() {
        return timeStyle;
    }

    public void setTimeStyle(final PKDateStyle timeStyle) {
        this.timeStyle = timeStyle;
    }

    public Boolean getIsRelative() {
        return isRelative;
    }

    public void setIsRelative(final Boolean isRelative) {
        this.isRelative = isRelative;
    }

    public Serializable getAttributedValue() {
        return attributedValue;
    }

    public void setAttributedValue(final Serializable attributedValue) {
        this.attributedValue = attributedValue;
    }

    public List<PKDataDetectorType> getDataDetectorTypes() {
        return dataDetectorTypes;
    }

    public void setDataDetectorTypes(final List<PKDataDetectorType> dataDetectorTypes) {
        this.dataDetectorTypes = dataDetectorTypes;
    }

    public Boolean getIgnoresTimeZone() {
        return ignoresTimeZone;
    }

    public void setIgnoresTimeZone(final Boolean ignoresTimeZone) {
        this.ignoresTimeZone = ignoresTimeZone;
    }

    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    public List<String> getValidationErrors() {

        List<String> validationErrors = new ArrayList<String>();
        checkRequiredFields(validationErrors);
        checkValueType(validationErrors);
        checkCurrencyCodeAndNumberStyleAreNotBothSet(validationErrors);
        checkNumberOrCurrencyAndDateNotSetAtTheSameTime(validationErrors);
        checkChangeMessageContainsPlaceholder(validationErrors);
        checkCurrencyValueIsNumeric(validationErrors);
        return validationErrors;
    }

	private void checkCurrencyValueIsNumeric(List<String> validationErrors) {
		if (currencyCode != null && !(value instanceof Integer || value instanceof Float || value instanceof Long
                || value instanceof Double || value instanceof BigDecimal)) {
            validationErrors.add("When using currencies, the values have to be numbers");
        }
    }

    private void checkChangeMessageContainsPlaceholder(List<String> validationErrors) {
        if (changeMessage != null && !changeMessage.contains("%@")) {
            validationErrors.add("ChangeMessage needs to contain %@ placeholder");
        }
    }

    private void checkNumberOrCurrencyAndDateNotSetAtTheSameTime(List<String> validationErrors) {
        if ((currencyCode != null || numberStyle != null) && (dateStyle != null || timeStyle != null)) {
            validationErrors.add("Can't be number/currency and date at the same time");
        }
    }

    private void checkCurrencyCodeAndNumberStyleAreNotBothSet(List<String> validationErrors) {
        if (currencyCode != null && numberStyle != null) {
            validationErrors.add("CurrencyCode and numberStyle are both set");
        }
    }

    private void checkValueType(List<String> validationErrors) {
		if (!(value instanceof String || value instanceof Integer || value instanceof Float || value instanceof Long
                || value instanceof Double || value instanceof Date || value instanceof BigDecimal)) {
            validationErrors.add(
                    "Invalid value type. Allowed: String, Integer, Float, Long, Double, java.util.Date, BigDecimal");
        }
    }

    private void checkRequiredFields(List<String> validationErrors) {
        if (value == null || StringUtils.isEmpty(key)) {
            validationErrors.add("Not all required Fields are set. Key: " + key + " Value:" + value);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
