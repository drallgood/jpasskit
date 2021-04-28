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

/**
 * @author stipe
 * Date: 06.02.14
 */
public class PKBeaconTest {

    private static final Integer MAJOR = 3;
    private static final Integer MINOR = 29;
    private static final String UUID = "123456789-abcdefghijklmnopqrstuwxyz";
    private static final String TEXT = "County of Zadar";

    private PKBeaconBuilder builder;

    private void fillBeacon() {
        builder.major(MAJOR)
                .minor(MINOR)
                .proximityUUID(UUID)
                .relevantText(TEXT);
    }

    @BeforeMethod
    public void prepareTest() {
        builder = PKBeacon.builder();
        fillBeacon();
    }

    @Test
    public void test_getters() {
        assertThat(builder.isValid()).isTrue();

        PKBeacon beacon = builder.build();

        assertThat(beacon.getMajor()).isEqualTo(MAJOR);
        assertThat(beacon.getMinor()).isEqualTo(MINOR);
        assertThat(beacon.getProximityUUID()).isEqualTo(UUID);
        assertThat(beacon.getRelevantText()).isEqualTo(TEXT);
    }

    @Test
    public void test_clone() {
        PKBeacon beacon = builder.build();
        PKBeacon copy = PKBeacon.builder(beacon).build();

        assertThat(copy)
                .isNotSameAs(beacon)
                .isEqualToComparingFieldByFieldRecursively(beacon);

        assertThat(copy.getMajor()).isEqualTo(MAJOR);
        assertThat(copy.getMinor()).isEqualTo(MINOR);
        assertThat(copy.getProximityUUID()).isEqualTo(UUID);
        assertThat(copy.getRelevantText()).isEqualTo(TEXT);
    }

    @Test
    public void test_validation_noProximityUUID() {
        builder.proximityUUID(null);

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_emptyProximityUUID() {
        builder.proximityUUID("");

        assertThat(builder.isValid()).isFalse();
    }

    @Test
    public void test_validation_noRelevantText() {
        builder.relevantText(null);

        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void test_validation_noMajor() {
        builder.major(null);

        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void test_validation_noMinor() {
        builder.minor(null);

        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void test_toString() {
        PKBeacon beacon = builder.build();

        assertThat(beacon.toString())
                .contains(String.valueOf(MAJOR))
                .contains(String.valueOf(MINOR))
                .contains(UUID)
                .contains(TEXT);
    }
}
