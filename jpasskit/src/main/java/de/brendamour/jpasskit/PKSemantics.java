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
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.enums.PKEventType;
import de.brendamour.jpasskit.semantics.PKCurrencyAmount;
import de.brendamour.jpasskit.semantics.PKPersonNameComponents;
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

    protected Date originalDepartureDate;

    protected Date currentDepartureDate;

    protected Date originalArrivalDate;

    protected Date currentArrivalDate;

    protected Date originalBoardingDate;

    protected Date currentBoardingDate;

    protected String boardingGroup;

    protected String boardingSequenceNumber;

    protected String confirmationNumber;

    protected String transitStatus;

    protected String transitStatusReason;

    protected PKPersonNameComponents passengerName;

    protected String membershipProgramName;

    protected String membershipProgramNumber;

    protected String priorityStatus;

    protected String securityScreening;

    /* Airline Boarding Passes */
    protected String flightCode;

    protected String airlineCode;

    protected Number flightNumber;

    protected String departureAirportCode;

    protected String departureAirportName;

    protected String departureTerminal;

    protected String departureGate;

    protected String destinationAirportCode;

    protected String destinationAirportName;

    protected String destinationTerminal;

    protected String destinationGate;

    /* Train and Other Rail Boarding Passes */
    protected String departurePlatform;

    protected String departureStationName;

    protected String destinationPlatform;

    protected String destinationStationName;

    protected String carNumber;

    /* Event Tickets */
    protected String eventName;

    protected String venueName;

    protected PKSemanticLocation venueLocation;

    protected String venueEntrance;

    protected String venuePhoneNumber;

    protected String venueRoom;

    protected PKEventType eventType;

    protected Date eventStartDate;

    protected Date eventEndDate;

    protected List<String> artistIDs;

    protected List<String> performerNames;

    protected String genre;

    /* Sports Event Tickets */
    protected String leagueName;

    protected String leagueAbbreviation;

    protected String homeTeamLocation;

    protected String homeTeamName;

    protected String homeTeamAbbreviation;

    protected String awayTeamLocation;

    protected String awayTeamName;

    protected String awayTeamAbbreviation;

    protected String sportName;

    /* Store Card Passes */
    protected PKCurrencyAmount balance;

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

    public Date getCurrentArrivalDate() {
        return currentArrivalDate;
    }

    public Date getCurrentBoardingDate() {
        return currentBoardingDate;
    }

    public Date getCurrentDepartureDate() {
        return currentDepartureDate;
    }

    public Date getOriginalArrivalDate() {
        return originalArrivalDate;
    }

    public Date getOriginalBoardingDate() {
        return originalBoardingDate;
    }

    public Date getOriginalDepartureDate() {
        return originalDepartureDate;
    }

    public String getBoardingGroup() {
        return boardingGroup;
    }

    public String getBoardingSequenceNumber() {
        return boardingSequenceNumber;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public String getMembershipProgramName() {
        return membershipProgramName;
    }

    public String getMembershipProgramNumber() {
        return membershipProgramNumber;
    }

    public PKPersonNameComponents getPassengerName() {
        return passengerName;
    }

    public String getPriorityStatus() {
        return priorityStatus;
    }

    public String getSecurityScreening() {
        return securityScreening;
    }

    public String getTransitStatus() {
        return transitStatus;
    }

    public String getTransitStatusReason() {
        return transitStatusReason;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public String getDepartureAirportName() {
        return departureAirportName;
    }

    public String getDepartureGate() {
        return departureGate;
    }

    public String getDepartureTerminal() {
        return departureTerminal;
    }

    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }

    public String getDestinationAirportName() {
        return destinationAirportName;
    }

    public String getDestinationGate() {
        return destinationGate;
    }

    public String getDestinationTerminal() {
        return destinationTerminal;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public Number getFlightNumber() {
        return flightNumber;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getDeparturePlatform() {
        return departurePlatform;
    }

    public String getDepartureStationName() {
        return departureStationName;
    }

    public String getDestinationPlatform() {
        return destinationPlatform;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public List<String> getArtistIDs() {
        return artistIDs;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public String getEventName() {
        return eventName;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public PKEventType getEventType() {
        return eventType;
    }

    public String getGenre() {
        return genre;
    }

    public List<String> getPerformerNames() {
        return performerNames;
    }

    public String getVenueEntrance() {
        return venueEntrance;
    }

    public PKSemanticLocation getVenueLocation() {
        return venueLocation;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getVenuePhoneNumber() {
        return venuePhoneNumber;
    }

    public String getVenueRoom() {
        return venueRoom;
    }

    public String getAwayTeamAbbreviation() {
        return awayTeamAbbreviation;
    }

    public String getAwayTeamLocation() {
        return awayTeamLocation;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public PKCurrencyAmount getBalance() {
        return balance;
    }

    public String getHomeTeamAbbreviation() {
        return homeTeamAbbreviation;
    }

    public String getHomeTeamLocation() {
        return homeTeamLocation;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getLeagueAbbreviation() {
        return leagueAbbreviation;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getSportName() {
        return sportName;
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