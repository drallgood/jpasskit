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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PKLocationTest {

    private static final double LONGITUDE = 1.0;
    private static final double LATITUDE = 2.0;
    private static final double ALTITUDE = 3.0;
    private static final String RELEVANT_TEXT = "Text";

    private PKLocationBuilder builder;

    @BeforeMethod
    public void prepareTest() {
        builder = PKLocation.builder();
        fillLocation();
    }

    @Test
    public void test_getters() {
        assertThat(builder.isValid()).isTrue();

        PKLocation location = builder.build();

        assertThat(location.getAltitude()).isEqualTo(ALTITUDE);
        assertThat(location.getLatitude()).isEqualTo(LATITUDE);
        assertThat(location.getLongitude()).isEqualTo(LONGITUDE);
        assertThat(location.getRelevantText()).isEqualTo(RELEVANT_TEXT);
    }

    @Test
    public void test_clone() {
        PKLocation location = builder.build();
        PKLocation copy = PKLocation.builder(location).build();

        assertThat(copy)
                .isNotSameAs(location)
                .isEqualToComparingFieldByFieldRecursively(location);

        assertThat(copy.getAltitude()).isEqualTo(ALTITUDE);
        assertThat(copy.getLatitude()).isEqualTo(LATITUDE);
        assertThat(copy.getLongitude()).isEqualTo(LONGITUDE);
        assertThat(copy.getRelevantText()).isEqualTo(RELEVANT_TEXT);
    }

    @Test
    public void test_validation_NoLongitude() {
        builder.longitude(0);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_NoLatitude() {
        builder.latitude(0);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_toString() {
        PKLocation location = builder.build();
        assertThat(location.toString())
                .contains(String.valueOf(LONGITUDE))
                .contains(String.valueOf(ALTITUDE))
                .contains(String.valueOf(LONGITUDE))
                .contains(RELEVANT_TEXT);
    }

    public void fillLocation() {
        builder.longitude(LONGITUDE)
                .latitude(LATITUDE)
                .altitude(ALTITUDE)
                .relevantText(RELEVANT_TEXT);
    }
}
