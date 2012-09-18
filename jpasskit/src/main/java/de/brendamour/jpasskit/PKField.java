/**
 * Copyright (C) 2012 Patrice Brend'amour <p.brendamour@bitzeche.de>
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

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PKField {
    private String key;
    private String label;
    private Object value;
    private String changeMessage;
    private String textAlignment;

    private String currencyCode;
    private String numberStyle;

    private PKDateStyle dateStyle;
    private PKDateStyle timeStyle;
    private boolean isRelative;

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

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(final String changeMessage) {
        this.changeMessage = changeMessage;
    }

    public String getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(final String textAlignment) {
        this.textAlignment = textAlignment;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getNumberStyle() {
        return numberStyle;
    }

    public void setNumberStyle(final String numberStyle) {
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

    public boolean isRelative() {
        return isRelative;
    }

    public void setRelative(final boolean isRelative) {
        this.isRelative = isRelative;
    }

    public boolean isValid() {
        boolean valid = true;

        if (value == null || key == null) {
            valid = false;
        } else if (!(value instanceof String || value instanceof Integer || value instanceof Float || value instanceof Long
                || value instanceof Double || value instanceof Date)) {
            valid = false;
        } else if (currencyCode != null && numberStyle != null) {
            valid = false;
        } else if (currencyCode != null && numberStyle != null) {
            valid = false;
        } else if (currencyCode != null && numberStyle != null) {
            valid = false;
        }

        return valid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
