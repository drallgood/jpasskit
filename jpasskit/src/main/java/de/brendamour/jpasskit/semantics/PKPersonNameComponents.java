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

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonDeserialize(builder = PKPersonNameComponentsBuilder.class)
public class PKPersonNameComponents implements Cloneable, Serializable {

    private static final long serialVersionUID = -8422267622415789780L;

    protected String givenName;
    protected String middleName;
    protected String familyName;
    protected String namePrefix;
    protected String nameSuffix;
    protected String nickname;
    protected PKPersonNameComponents phoneticRepresentation;

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public String getNamePrefix() {
        return namePrefix;
    }
    
    public String getNameSuffix() {
        return nameSuffix;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public PKPersonNameComponents getPhoneticRepresentation() {
        return phoneticRepresentation;
    }

    @Override
    protected PKPersonNameComponents clone() {
        try {
            return (PKPersonNameComponents) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKCurrencyAmount instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKPersonNameComponentsBuilder builder() {
        return new PKPersonNameComponentsBuilder();
    }

    public static PKPersonNameComponentsBuilder builder(PKPersonNameComponents seat) {
        return builder().of(seat);
    }
}