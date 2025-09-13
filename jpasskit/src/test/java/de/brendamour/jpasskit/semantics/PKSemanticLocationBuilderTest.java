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

public class PKSemanticLocationBuilderTest {

    private PKSemanticLocationBuilder builder;

    @BeforeMethod
    public void setUp() {
        builder = PKSemanticLocation.builder();
    }

    @Test
    public void testOfWithNullSource() {
        PKSemanticLocation result = builder.of(null).build();
        assertThat(result).isNotNull();
    }

    @Test
    public void testOfWithValidSource() {
        PKSemanticLocation source = PKSemanticLocation.builder()
            .latitude(40.7128)
            .longitude(-74.0060)
            .build();
        
        PKSemanticLocation result = builder.of(source).build();
        assertThat(result.latitude).isEqualTo(40.7128);
        assertThat(result.longitude).isEqualTo(-74.0060);
    }

    @Test
    public void testLatitude() {
        double latitude = 37.7749;
        PKSemanticLocation result = builder.latitude(latitude).build();
        assertThat(result.latitude).isEqualTo(latitude);
    }

    @Test
    public void testLongitude() {
        double longitude = -122.4194;
        PKSemanticLocation result = builder.longitude(longitude).build();
        assertThat(result.longitude).isEqualTo(longitude);
    }

    @Test
    public void testIsValidWithValidCoordinates() {
        builder.latitude(40.7128).longitude(-74.0060);
        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void testIsValidWithZeroLatitude() {
        builder.latitude(0.0).longitude(-74.0060);
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testIsValidWithZeroLongitude() {
        builder.latitude(40.7128).longitude(0.0);
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testIsValidWithBothZero() {
        builder.latitude(0.0).longitude(0.0);
        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void testGetValidationErrorsWithValidCoordinates() {
        builder.latitude(40.7128).longitude(-74.0060);
        assertThat(builder.getValidationErrors()).isEmpty();
    }

    @Test
    public void testGetValidationErrorsWithInvalidCoordinates() {
        builder.latitude(0.0).longitude(-74.0060);
        assertThat(builder.getValidationErrors()).hasSize(1);
        assertThat(builder.getValidationErrors().get(0)).contains("Not all required Fields are set");
    }

    @Test
    public void testChainedMethods() {
        PKSemanticLocation result = builder
            .latitude(51.5074)
            .longitude(-0.1278)
            .build();
        
        assertThat(result.latitude).isEqualTo(51.5074);
        assertThat(result.longitude).isEqualTo(-0.1278);
    }

    @Test
    public void testToString() {
        String toString = builder.latitude(40.7128).longitude(-74.0060).toString();
        assertThat(toString).isNotNull();
        assertThat(toString).contains("PKSemanticLocationBuilder");
    }
}
