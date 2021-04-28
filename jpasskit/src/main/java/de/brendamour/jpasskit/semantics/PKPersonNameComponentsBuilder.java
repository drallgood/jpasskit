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

import de.brendamour.jpasskit.IPKBuilder;
import de.brendamour.jpasskit.IPKValidateable;

/**
 * Allows constructing and validating {@link PKPersonNameComponents} entities.
 *
 * @author Patrice Brend'amour
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKPersonNameComponentsBuilder implements IPKValidateable, IPKBuilder<PKPersonNameComponents> {

    private PKPersonNameComponents seat;

    protected PKPersonNameComponentsBuilder() {
        this.seat = new PKPersonNameComponents();
    }

    @Override
    public PKPersonNameComponentsBuilder of(final PKPersonNameComponents source) {
        if (source != null) {
            this.seat = source.clone();
        }
        return this;
    }

    public PKPersonNameComponentsBuilder familyName(String familyName) {
        this.seat.familyName = familyName;
        return this;
    }

    public PKPersonNameComponentsBuilder givenName(String givenName) {
        this.seat.givenName = givenName;
        return this;
    }

    public PKPersonNameComponentsBuilder middleName(String middleName) {
        this.seat.middleName = middleName;
        return this;
    }

    public PKPersonNameComponentsBuilder namePrefix(String namePrefix) {
        this.seat.namePrefix = namePrefix;
        return this;
    }

    public PKPersonNameComponentsBuilder nameSuffix(String nameSuffix) {
        this.seat.nameSuffix = nameSuffix;
        return this;
    }

    public PKPersonNameComponentsBuilder nickname(String nickname) {
        this.seat.nickname = nickname;
        return this;
    }

    public PKPersonNameComponentsBuilder phoneticRepresentation(PKPersonNameComponents phoneticRepresentation) {
        this.seat.phoneticRepresentation = phoneticRepresentation;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {
        return Collections.emptyList();
    }

    @Override
    public PKPersonNameComponents build() {
        return this.seat;
    }
}
