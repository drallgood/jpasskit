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

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.enums.PKDataDetectorType;
import de.brendamour.jpasskit.enums.PKDateStyle;
import de.brendamour.jpasskit.enums.PKNumberStyle;
import de.brendamour.jpasskit.enums.PKTextAlignment;

@JsonDeserialize(builder = PKFieldBuilder.class)
public class PKField implements Cloneable, Serializable {

    private static final long serialVersionUID = -6362596567978565530L;

    protected String key;
    protected String label;
    protected Serializable value;
    /**
     * @since iOS 7.0
     */
    protected Serializable attributedValue;
    protected String changeMessage;
    protected PKTextAlignment textAlignment;

    /**
     * @since iOS 7.0
     */
    protected List<PKDataDetectorType> dataDetectorTypes;

    /*
     * Number Fields
     */
    protected String currencyCode;
    protected PKNumberStyle numberStyle;

    /*
     * Date Fields
     */
    protected PKDateStyle dateStyle;
    protected PKDateStyle timeStyle;
    protected Boolean isRelative;
    /**
     * @since iOS 7.0 Has to be null by default, since if it's set, iOS will validate the field as a date even the API consumer didn't want that.
     */
    protected Boolean ignoresTimeZone; // The key is optional, default value is null

    // Feature added in iOS 12.0. It is not applicable to older iOS
    protected PKSemantics semantics;
    protected Integer row;

    protected PKField() {
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public Serializable getValue() {
        return value;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public PKTextAlignment getTextAlignment() {
        return textAlignment;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public PKNumberStyle getNumberStyle() {
        return numberStyle;
    }

    public PKDateStyle getDateStyle() {
        return dateStyle;
    }

    public PKDateStyle getTimeStyle() {
        return timeStyle;
    }

    public Boolean getIsRelative() {
        return isRelative;
    }

    public Serializable getAttributedValue() {
        return attributedValue;
    }

    public List<PKDataDetectorType> getDataDetectorTypes() {
        return dataDetectorTypes;
    }

    public Boolean getIgnoresTimeZone() {
        return ignoresTimeZone;
    }

    public PKSemantics getSemantics() {
        return semantics;
    }

    public Integer getRow() {
        return row;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    protected PKField clone() {
        try {
            return (PKField) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKField instance", ex);
        }
    }

    public static PKFieldBuilder builder() {
        return new PKFieldBuilder();
    }

    public static PKFieldBuilder builder(PKField field) {
        return builder().of(field);
    }
}
