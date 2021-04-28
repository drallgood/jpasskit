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
 * Allows constructing and validating {@link PKSeat} entities.
 *
 * @author Patrice Brend'amour
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKSeatBuilder implements IPKValidateable, IPKBuilder<PKSeat> {

    private PKSeat seat;

    protected PKSeatBuilder() {
        this.seat = new PKSeat();
    }

    @Override
    public PKSeatBuilder of(final PKSeat source) {
        if (source != null) {
            this.seat = source.clone();
        }
        return this;
    }

    public PKSeatBuilder seatDescription(String seatDescription) {
        this.seat.seatDescription = seatDescription;
        return this;
    }

    public PKSeatBuilder seatIdentifier(String seatIdentifier) {
        this.seat.seatIdentifier = seatIdentifier;
        return this;
    }

    public PKSeatBuilder seatNumber(String seatNumber) {
        this.seat.seatNumber = seatNumber;
        return this;
    }

    public PKSeatBuilder seatRow(String seatRow) {
        this.seat.seatRow = seatRow;
        return this;
    }

    public PKSeatBuilder seatSection(String seatSection) {
        this.seat.seatSection = seatSection;
        return this;
    }

    public PKSeatBuilder seatType(String seatType) {
        this.seat.seatType = seatType;
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
    public PKSeat build() {
        return this.seat;
    }
}
