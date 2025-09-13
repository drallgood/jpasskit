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
package de.brendamour.jpasskit.semantics;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PKSeatBuilderTest {

    private PKSeatBuilder builder;

    @BeforeMethod
    public void setUp() {
        builder = PKSeat.builder();
    }

    @Test
    public void testOfWithNullSource() {
        PKSeat result = builder.of(null).build();
        assertThat(result).isNotNull();
    }

    @Test
    public void testOfWithValidSource() {
        PKSeat source = PKSeat.builder()
            .seatNumber("12A")
            .seatRow("12")
            .seatSection("Orchestra")
            .build();
        
        PKSeat result = builder.of(source).build();
        assertThat(result.seatNumber).isEqualTo("12A");
        assertThat(result.seatRow).isEqualTo("12");
        assertThat(result.seatSection).isEqualTo("Orchestra");
    }

    @Test
    public void testSeatDescription() {
        String description = "Premium seat with extra legroom";
        PKSeat result = builder.seatDescription(description).build();
        assertThat(result.seatDescription).isEqualTo(description);
    }

    @Test
    public void testSeatIdentifier() {
        String identifier = "SEAT-12A-ORCH";
        PKSeat result = builder.seatIdentifier(identifier).build();
        assertThat(result.seatIdentifier).isEqualTo(identifier);
    }

    @Test
    public void testSeatNumber() {
        String seatNumber = "15B";
        PKSeat result = builder.seatNumber(seatNumber).build();
        assertThat(result.seatNumber).isEqualTo(seatNumber);
    }

    @Test
    public void testSeatRow() {
        String seatRow = "Row 8";
        PKSeat result = builder.seatRow(seatRow).build();
        assertThat(result.seatRow).isEqualTo(seatRow);
    }

    @Test
    public void testSeatSection() {
        String seatSection = "Balcony";
        PKSeat result = builder.seatSection(seatSection).build();
        assertThat(result.seatSection).isEqualTo(seatSection);
    }

    @Test
    public void testSeatType() {
        String seatType = "VIP";
        PKSeat result = builder.seatType(seatType).build();
        assertThat(result.seatType).isEqualTo(seatType);
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
        PKSeat result = builder
            .seatSection("Orchestra")
            .seatRow("12")
            .seatNumber("A")
            .seatType("Premium")
            .seatDescription("Aisle seat with great view")
            .seatIdentifier("ORCH-12A")
            .build();
        
        assertThat(result.seatSection).isEqualTo("Orchestra");
        assertThat(result.seatRow).isEqualTo("12");
        assertThat(result.seatNumber).isEqualTo("A");
        assertThat(result.seatType).isEqualTo("Premium");
        assertThat(result.seatDescription).isEqualTo("Aisle seat with great view");
        assertThat(result.seatIdentifier).isEqualTo("ORCH-12A");
    }

    @Test
    public void testTheaterSeat() {
        PKSeat result = builder
            .seatSection("Mezzanine")
            .seatRow("F")
            .seatNumber("101")
            .seatType("Standard")
            .build();
        
        assertThat(result.seatSection).isEqualTo("Mezzanine");
        assertThat(result.seatRow).isEqualTo("F");
        assertThat(result.seatNumber).isEqualTo("101");
        assertThat(result.seatType).isEqualTo("Standard");
    }

    @Test
    public void testStadiumSeat() {
        PKSeat result = builder
            .seatSection("Section 200")
            .seatRow("15")
            .seatNumber("8")
            .seatType("Club Level")
            .seatDescription("Club level seat with access to premium amenities")
            .build();
        
        assertThat(result.seatSection).isEqualTo("Section 200");
        assertThat(result.seatRow).isEqualTo("15");
        assertThat(result.seatNumber).isEqualTo("8");
        assertThat(result.seatType).isEqualTo("Club Level");
        assertThat(result.seatDescription).isEqualTo("Club level seat with access to premium amenities");
    }

    @Test
    public void testAirplaneSeat() {
        PKSeat result = builder
            .seatSection("First Class")
            .seatRow("3")
            .seatNumber("A")
            .seatType("Window")
            .seatDescription("First class window seat")
            .seatIdentifier("3A-FC")
            .build();
        
        assertThat(result.seatSection).isEqualTo("First Class");
        assertThat(result.seatRow).isEqualTo("3");
        assertThat(result.seatNumber).isEqualTo("A");
        assertThat(result.seatType).isEqualTo("Window");
        assertThat(result.seatDescription).isEqualTo("First class window seat");
        assertThat(result.seatIdentifier).isEqualTo("3A-FC");
    }
}
