/**
 * Copyright (C) 2019 Patrice Brend'amour <patrice@brendamour.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.brendamour.jpasskit;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.semantics.PKCurrencyAmount;
import de.brendamour.jpasskit.semantics.PKSeat;
import de.brendamour.jpasskit.semantics.PKSemanticLocation;

@JsonDeserialize(builder = PKSemanticsBuilder.class)
public class PKSemantics implements Cloneable, Serializable {

    private static final long serialVersionUID = 2460120192671479897L;

    protected PKCurrencyAmount totalPrice;

    /* ONLY FOR boarding passes and events */
    protected Number duration;

    protected List<PKSeat> seats;

    protected Boolean silenceRequested;

    /* ONLY for boarding passes */
    protected PKSemanticLocation departureLocation;

    protected String departureLocationDescription;

    protected PKSemanticLocation destinationLocation;

    protected String destinationLocationDescription;
    
    protected String transitProvider;

    protected String vehicleName;

    protected String vehicleNumber;

    protected String vehicleType;

    public PKCurrencyAmount getTotalPrice() {
        return totalPrice;
    }

    public Number getDuration() {
        return duration;
    }

     public List<PKSeat> getSeats() {
        return seats;
    }

    public Boolean getSilenceRequested() {
        return silenceRequested;
    }

    public PKSemanticLocation getDepartureLocation() {
        return departureLocation;
    }

    public String getDepartureLocationDescription() {
        return departureLocationDescription;
    }

    public PKSemanticLocation getDestinationLocation() {
        return destinationLocation;
    }

    public String getDestinationLocationDescription() {
        return destinationLocationDescription;
    }

    public String getTransitProvider() {
        return transitProvider;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }
    

@Override
    protected PKSemantics clone() {
        try {
            return (PKSemantics) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKSemantics instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKSemanticsBuilder builder() {
        return new PKSemanticsBuilder();
    }

    public static PKSemanticsBuilder builder(PKSemantics semantics) {
        return builder().of(semantics);
    }
}