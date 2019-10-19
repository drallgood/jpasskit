/**
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import de.brendamour.jpasskit.semantics.PKCurrencyAmount;
import de.brendamour.jpasskit.semantics.PKSeat;
import de.brendamour.jpasskit.semantics.PKSemanticLocation;

/**
 * Allows constructing and validating {@link PKSemantics} entities.
 *
 * @author Patrice Brend'amour
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKSemanticsBuilder implements IPKValidateable, IPKBuilder<PKSemantics> {

    private PKSemantics semantics;

    protected PKSemanticsBuilder() {
        this.semantics = new PKSemantics();
    }

    @Override
    public PKSemanticsBuilder of(final PKSemantics source) {
        if (source != null) {
            this.semantics = source.clone();
        }
        return this;
    }

    public PKSemanticsBuilder totalPrice(PKCurrencyAmount totalPrice) {
        this.semantics.totalPrice = totalPrice;
        return this;
    }

    public PKSemanticsBuilder duration(Number duration) {
        this.semantics.duration = duration;
        return this;
    }

    public PKSemanticsBuilder seats(List<PKSeat> seats) {
        this.semantics.seats = seats;
        return this;
    }

    public PKSemanticsBuilder silenceRequested(Boolean silenceRequested) {
        this.semantics.silenceRequested = silenceRequested;
        return this;
    }

    public PKSemanticsBuilder departureLocation(PKSemanticLocation departureLocation) {
        this.semantics.departureLocation = departureLocation;
        return this;
    }

    public PKSemanticsBuilder departureLocationDescription(String departureLocationDescription) {
        this.semantics.departureLocationDescription = departureLocationDescription;
        return this;
    }

    public PKSemanticsBuilder destinationLocation(PKSemanticLocation destinationLocation) {
        this.semantics.destinationLocation = destinationLocation;
        return this;
    }

    public PKSemanticsBuilder destinationLocationDescription(String destinationLocationDescription) {
        this.semantics.destinationLocationDescription = destinationLocationDescription;
        return this;
    }

    public PKSemanticsBuilder transitProvider(String transitProvider) {
        this.semantics.transitProvider = transitProvider;
        return this;
    }

    public PKSemanticsBuilder vehicleName(String vehicleName) {
        this.semantics.vehicleName = vehicleName;
        return this;
    }

    public PKSemanticsBuilder vehicleNumber(String vehicleNumber) {
        this.semantics.vehicleNumber = vehicleNumber;
        return this;
    }

    public PKSemanticsBuilder vehicleType(String vehicleType) {
        this.semantics.vehicleType = vehicleType;
        return this;
    }

    public PKSemanticsBuilder originalDepartureDate(Date originalDepartureDate) {
        this.semantics.originalDepartureDate = originalDepartureDate;
        return this;
    }

    public PKSemanticsBuilder currentDepartureDate(Date currentDepartureDate) {
        this.semantics.currentDepartureDate = currentDepartureDate;
        return this;
    }

    public PKSemanticsBuilder originalArrivalDate(Date originalArrivalDate) {
        this.semantics.originalArrivalDate = originalArrivalDate;
        return this;
    }

    public PKSemanticsBuilder currentArrivalDate(Date currentArrivalDate) {
        this.semantics.currentArrivalDate = currentArrivalDate;
        return this;
    }

    public PKSemanticsBuilder originalBoardingDate(Date originalBoardingDate) {
        this.semantics.originalBoardingDate = originalBoardingDate;
        return this;
    }

    public PKSemanticsBuilder currentBoardingDate(Date currentBoardingDate) {
        this.semantics.currentBoardingDate = currentBoardingDate;
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
    public PKSemantics build() {
        return this.semantics;
    }
}
