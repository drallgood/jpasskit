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

@JsonDeserialize(builder = PKSeatBuilder.class)
public class PKSeat implements Cloneable, Serializable {

    private static final long serialVersionUID = -8422267622415789780L;

    protected String seatSection;
    protected String seatRow;
    protected String seatNumber;
    protected String seatIdentifier;
    protected String seatType;
    protected String seatDescription;

    public String getSeatSection() {
        return seatSection;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatIdentifier() {
        return seatIdentifier;
    }

    public String getSeatType() {
        return seatType;
    }

    public String getSeatDescription() {
        return seatDescription;
    }

    @Override
    protected PKSeat clone() {
        try {
            return (PKSeat) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKCurrencyAmount instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKSeatBuilder builder() {
        return new PKSeatBuilder();
    }

    public static PKSeatBuilder builder(PKSeat seat) {
        return builder().of(seat);
    }
}