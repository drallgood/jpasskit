/**
 * Copyright (C) 2024 Patrice Brend'amour <patrice@brendamour.net>
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

import de.brendamour.jpasskit.enums.PKEventType;
import de.brendamour.jpasskit.semantics.PKCurrencyAmount;
import de.brendamour.jpasskit.semantics.PKPersonNameComponents;
import de.brendamour.jpasskit.semantics.PKSeat;
import de.brendamour.jpasskit.semantics.PKSemanticLocation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PKSemanticsBuilderTest {

    private PKSemanticsBuilder builder;
    private Date testDate;
    private PKCurrencyAmount testCurrencyAmount;
    private PKSemanticLocation testLocation;
    private PKPersonNameComponents testPersonName;
    private PKSeat testSeat;

    @BeforeMethod
    public void setUp() {
        builder = PKSemantics.builder();
        testDate = new Date();
        testCurrencyAmount = PKCurrencyAmount.builder().currencyCode("USD").amount("10.00").build();
        testLocation = PKSemanticLocation.builder().latitude(40.7128).longitude(-74.0060).build();
        testPersonName = PKPersonNameComponents.builder().givenName("John").familyName("Doe").build();
        testSeat = PKSeat.builder().seatNumber("12A").seatRow("12").build();
    }

    @Test
    public void testOfWithNullSource() {
        PKSemantics result = builder.of(null).build();
        assertThat(result).isNotNull();
    }

    @Test
    public void testOfWithValidSource() {
        PKSemantics source = new PKSemantics();
        source.eventName = "Test Event";
        
        PKSemantics result = builder.of(source).build();
        assertThat(result.eventName).isEqualTo("Test Event");
    }

    @Test
    public void testTotalPrice() {
        PKSemantics result = builder.totalPrice(testCurrencyAmount).build();
        assertThat(result.totalPrice).isEqualTo(testCurrencyAmount);
    }

    @Test
    public void testDuration() {
        Number duration = 120;
        PKSemantics result = builder.duration(duration).build();
        assertThat(result.duration).isEqualTo(duration);
    }

    @Test
    public void testSeats() {
        List<PKSeat> seats = Arrays.asList(testSeat);
        PKSemantics result = builder.seats(seats).build();
        assertThat(result.seats).isEqualTo(seats);
    }

    @Test
    public void testSilenceRequested() {
        PKSemantics result = builder.silenceRequested(true).build();
        assertThat(result.silenceRequested).isTrue();
    }

    @Test
    public void testDepartureLocation() {
        PKSemantics result = builder.departureLocation(testLocation).build();
        assertThat(result.departureLocation).isEqualTo(testLocation);
    }

    @Test
    public void testDepartureLocationDescription() {
        String description = "Airport Terminal 1";
        PKSemantics result = builder.departureLocationDescription(description).build();
        assertThat(result.departureLocationDescription).isEqualTo(description);
    }

    @Test
    public void testDestinationLocation() {
        PKSemantics result = builder.destinationLocation(testLocation).build();
        assertThat(result.destinationLocation).isEqualTo(testLocation);
    }

    @Test
    public void testDestinationLocationDescription() {
        String description = "Downtown Station";
        PKSemantics result = builder.destinationLocationDescription(description).build();
        assertThat(result.destinationLocationDescription).isEqualTo(description);
    }

    @Test
    public void testTransitProvider() {
        String provider = "Metro Transit";
        PKSemantics result = builder.transitProvider(provider).build();
        assertThat(result.transitProvider).isEqualTo(provider);
    }

    @Test
    public void testVehicleName() {
        String name = "Express Train";
        PKSemantics result = builder.vehicleName(name).build();
        assertThat(result.vehicleName).isEqualTo(name);
    }

    @Test
    public void testVehicleNumber() {
        String number = "1234";
        PKSemantics result = builder.vehicleNumber(number).build();
        assertThat(result.vehicleNumber).isEqualTo(number);
    }

    @Test
    public void testVehicleType() {
        String type = "Bus";
        PKSemantics result = builder.vehicleType(type).build();
        assertThat(result.vehicleType).isEqualTo(type);
    }

    @Test
    public void testOriginalDepartureDate() {
        PKSemantics result = builder.originalDepartureDate(testDate).build();
        assertThat(result.originalDepartureDate).isEqualTo(testDate);
    }

    @Test
    public void testCurrentDepartureDate() {
        PKSemantics result = builder.currentDepartureDate(testDate).build();
        assertThat(result.currentDepartureDate).isEqualTo(testDate);
    }

    @Test
    public void testOriginalArrivalDate() {
        PKSemantics result = builder.originalArrivalDate(testDate).build();
        assertThat(result.originalArrivalDate).isEqualTo(testDate);
    }

    @Test
    public void testCurrentArrivalDate() {
        PKSemantics result = builder.currentArrivalDate(testDate).build();
        assertThat(result.currentArrivalDate).isEqualTo(testDate);
    }

    @Test
    public void testOriginalBoardingDate() {
        PKSemantics result = builder.originalBoardingDate(testDate).build();
        assertThat(result.originalBoardingDate).isEqualTo(testDate);
    }

    @Test
    public void testCurrentBoardingDate() {
        PKSemantics result = builder.currentBoardingDate(testDate).build();
        assertThat(result.currentBoardingDate).isEqualTo(testDate);
    }

    @Test
    public void testBoardingGroup() {
        String group = "Group A";
        PKSemantics result = builder.boardingGroup(group).build();
        assertThat(result.boardingGroup).isEqualTo(group);
    }

    @Test
    public void testBoardingSequenceNumber() {
        String sequence = "001";
        PKSemantics result = builder.boardingSequenceNumber(sequence).build();
        assertThat(result.boardingSequenceNumber).isEqualTo(sequence);
    }

    @Test
    public void testConfirmationNumber() {
        String confirmation = "ABC123";
        PKSemantics result = builder.confirmationNumber(confirmation).build();
        assertThat(result.confirmationNumber).isEqualTo(confirmation);
    }

    @Test
    public void testTransitStatus() {
        String status = "On Time";
        PKSemantics result = builder.transitStatus(status).build();
        assertThat(result.transitStatus).isEqualTo(status);
    }

    @Test
    public void testTransitStatusReason() {
        String reason = "Weather Delay";
        PKSemantics result = builder.transitStatusReason(reason).build();
        assertThat(result.transitStatusReason).isEqualTo(reason);
    }

    @Test
    public void testPassengerName() {
        PKSemantics result = builder.passengerName(testPersonName).build();
        assertThat(result.passengerName).isEqualTo(testPersonName);
    }

    @Test
    public void testMembershipProgramName() {
        String program = "Frequent Flyer";
        PKSemantics result = builder.membershipProgramName(program).build();
        assertThat(result.membershipProgramName).isEqualTo(program);
    }

    @Test
    public void testMembershipProgramNumber() {
        String number = "FF123456";
        PKSemantics result = builder.membershipProgramNumber(number).build();
        assertThat(result.membershipProgramNumber).isEqualTo(number);
    }

    @Test
    public void testPriorityStatus() {
        String status = "Gold";
        PKSemantics result = builder.priorityStatus(status).build();
        assertThat(result.priorityStatus).isEqualTo(status);
    }

    @Test
    public void testSecurityScreening() {
        String screening = "TSA PreCheck";
        PKSemantics result = builder.securityScreening(screening).build();
        assertThat(result.securityScreening).isEqualTo(screening);
    }

    @Test
    public void testFlightCode() {
        String code = "AA123";
        PKSemantics result = builder.flightCode(code).build();
        assertThat(result.flightCode).isEqualTo(code);
    }

    @Test
    public void testAirlineCode() {
        String code = "AA";
        PKSemantics result = builder.airlineCode(code).build();
        assertThat(result.airlineCode).isEqualTo(code);
    }

    @Test
    public void testFlightNumber() {
        Number number = 123;
        PKSemantics result = builder.flightNumber(number).build();
        assertThat(result.flightNumber).isEqualTo(number);
    }

    @Test
    public void testDepartureAirportCode() {
        String code = "JFK";
        PKSemantics result = builder.departureAirportCode(code).build();
        assertThat(result.departureAirportCode).isEqualTo(code);
    }

    @Test
    public void testDepartureAirportName() {
        String name = "John F. Kennedy International Airport";
        PKSemantics result = builder.departureAirportName(name).build();
        assertThat(result.departureAirportName).isEqualTo(name);
    }

    @Test
    public void testDepartureTerminal() {
        String terminal = "Terminal 4";
        PKSemantics result = builder.departureTerminal(terminal).build();
        assertThat(result.departureTerminal).isEqualTo(terminal);
    }

    @Test
    public void testDepartureGate() {
        String gate = "A12";
        PKSemantics result = builder.departureGate(gate).build();
        assertThat(result.departureGate).isEqualTo(gate);
    }

    @Test
    public void testDestinationAirportCode() {
        String code = "LAX";
        PKSemantics result = builder.destinationAirportCode(code).build();
        assertThat(result.destinationAirportCode).isEqualTo(code);
    }

    @Test
    public void testDestinationAirportName() {
        String name = "Los Angeles International Airport";
        PKSemantics result = builder.destinationAirportName(name).build();
        assertThat(result.destinationAirportName).isEqualTo(name);
    }

    @Test
    public void testDestinationTerminal() {
        String terminal = "Terminal 1";
        PKSemantics result = builder.destinationTerminal(terminal).build();
        assertThat(result.destinationTerminal).isEqualTo(terminal);
    }

    @Test
    public void testDestinationGate() {
        String gate = "B5";
        PKSemantics result = builder.destinationGate(gate).build();
        assertThat(result.destinationGate).isEqualTo(gate);
    }

    @Test
    public void testDeparturePlatform() {
        String platform = "Platform 9";
        PKSemantics result = builder.departurePlatform(platform).build();
        assertThat(result.departurePlatform).isEqualTo(platform);
    }

    @Test
    public void testDepartureStationName() {
        String station = "Grand Central";
        PKSemantics result = builder.departureStationName(station).build();
        assertThat(result.departureStationName).isEqualTo(station);
    }

    @Test
    public void testDestinationPlatform() {
        String platform = "Platform 3";
        PKSemantics result = builder.destinationPlatform(platform).build();
        assertThat(result.destinationPlatform).isEqualTo(platform);
    }

    @Test
    public void testDestinationStationName() {
        String station = "Penn Station";
        PKSemantics result = builder.destinationStationName(station).build();
        assertThat(result.destinationStationName).isEqualTo(station);
    }

    @Test
    public void testCarNumber() {
        String car = "Car 5";
        PKSemantics result = builder.carNumber(car).build();
        assertThat(result.carNumber).isEqualTo(car);
    }

    @Test
    public void testEventName() {
        String name = "Concert";
        PKSemantics result = builder.eventName(name).build();
        assertThat(result.eventName).isEqualTo(name);
    }

    @Test
    public void testVenueName() {
        String name = "Madison Square Garden";
        PKSemantics result = builder.venueName(name).build();
        assertThat(result.venueName).isEqualTo(name);
    }

    @Test
    public void testVenueLocation() {
        PKSemantics result = builder.venueLocation(testLocation).build();
        assertThat(result.venueLocation).isEqualTo(testLocation);
    }

    @Test
    public void testVenueEntrance() {
        String entrance = "Main Entrance";
        PKSemantics result = builder.venueEntrance(entrance).build();
        assertThat(result.venueEntrance).isEqualTo(entrance);
    }

    @Test
    public void testVenuePhoneNumber() {
        String phone = "+1-555-123-4567";
        PKSemantics result = builder.venuePhoneNumber(phone).build();
        assertThat(result.venuePhoneNumber).isEqualTo(phone);
    }

    @Test
    public void testVenueRoom() {
        String room = "Room 101";
        PKSemantics result = builder.venueRoom(room).build();
        assertThat(result.venueRoom).isEqualTo(room);
    }

    @Test
    public void testEventType() {
        PKEventType type = PKEventType.PKEventTypeGeneric;
        PKSemantics result = builder.eventType(type).build();
        assertThat(result.eventType).isEqualTo(type);
    }

    @Test
    public void testEventStartDate() {
        PKSemantics result = builder.eventStartDate(testDate).build();
        assertThat(result.eventStartDate).isEqualTo(testDate);
    }

    @Test
    public void testEventEndDate() {
        PKSemantics result = builder.eventEndDate(testDate).build();
        assertThat(result.eventEndDate).isEqualTo(testDate);
    }

    @Test
    public void testArtistIDs() {
        List<String> ids = Arrays.asList("artist1", "artist2");
        PKSemantics result = builder.artistIDs(ids).build();
        assertThat(result.artistIDs).isEqualTo(ids);
    }

    @Test
    public void testPerformerNames() {
        List<String> names = Arrays.asList("John Doe", "Jane Smith");
        PKSemantics result = builder.performerNames(names).build();
        assertThat(result.performerNames).isEqualTo(names);
    }

    @Test
    public void testGenre() {
        String genre = "Rock";
        PKSemantics result = builder.genre(genre).build();
        assertThat(result.genre).isEqualTo(genre);
    }

    @Test
    public void testLeagueName() {
        String league = "NFL";
        PKSemantics result = builder.leagueName(league).build();
        assertThat(result.leagueName).isEqualTo(league);
    }

    @Test
    public void testLeagueAbbreviation() {
        String abbrev = "NFL";
        PKSemantics result = builder.leagueAbbreviation(abbrev).build();
        assertThat(result.leagueAbbreviation).isEqualTo(abbrev);
    }

    @Test
    public void testHomeTeamLocation() {
        String location = "New York";
        PKSemantics result = builder.homeTeamLocation(location).build();
        assertThat(result.homeTeamLocation).isEqualTo(location);
    }

    @Test
    public void testHomeTeamName() {
        String name = "Giants";
        PKSemantics result = builder.homeTeamName(name).build();
        assertThat(result.homeTeamName).isEqualTo(name);
    }

    @Test
    public void testHomeTeamAbbreviation() {
        String abbrev = "NYG";
        PKSemantics result = builder.homeTeamAbbreviation(abbrev).build();
        assertThat(result.homeTeamAbbreviation).isEqualTo(abbrev);
    }

    @Test
    public void testAwayTeamLocation() {
        String location = "Dallas";
        PKSemantics result = builder.awayTeamLocation(location).build();
        assertThat(result.awayTeamLocation).isEqualTo(location);
    }

    @Test
    public void testAwayTeamName() {
        String name = "Cowboys";
        PKSemantics result = builder.awayTeamName(name).build();
        assertThat(result.awayTeamName).isEqualTo(name);
    }

    @Test
    public void testAwayTeamAbbreviation() {
        String abbrev = "DAL";
        PKSemantics result = builder.awayTeamAbbreviation(abbrev).build();
        assertThat(result.awayTeamAbbreviation).isEqualTo(abbrev);
    }

    @Test
    public void testSportName() {
        String sport = "Football";
        PKSemantics result = builder.sportName(sport).build();
        assertThat(result.sportName).isEqualTo(sport);
    }

    @Test
    public void testBalance() {
        PKSemantics result = builder.balance(testCurrencyAmount).build();
        assertThat(result.balance).isEqualTo(testCurrencyAmount);
    }

    @Test
    public void testIsValid() {
        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void testGetValidationErrors() {
        assertThat(builder.getValidationErrors()).isEmpty();
    }

    @Test
    public void testChainedMethods() {
        PKSemantics result = builder
            .eventName("Test Event")
            .venueName("Test Venue")
            .eventType(PKEventType.PKEventTypeGeneric)
            .eventStartDate(testDate)
            .build();
        
        assertThat(result.eventName).isEqualTo("Test Event");
        assertThat(result.venueName).isEqualTo("Test Venue");
        assertThat(result.eventType).isEqualTo(PKEventType.PKEventTypeGeneric);
        assertThat(result.eventStartDate).isEqualTo(testDate);
    }
}
