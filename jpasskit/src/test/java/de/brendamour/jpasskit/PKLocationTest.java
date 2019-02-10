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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    public void test_getterSetter() {
        Assert.assertTrue(builder.isValid());

        PKLocation location = builder.build();
        Assert.assertEquals(location.getAltitude(), ALTITUDE);
        Assert.assertEquals(location.getLatitude(), LATITUDE);
        Assert.assertEquals(location.getLongitude(), LONGITUDE);
        Assert.assertEquals(location.getRelevantText(), RELEVANT_TEXT);

    }

    @Test
    public void test_getterSetter_NoLongitude() {
        builder.longitude(0);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_getterSetter_NoLatitude() {
        builder.latitude(0);

        Assert.assertFalse(builder.isValid());
    }

    public void fillLocation() {
        builder.longitude(LONGITUDE)
                .latitude(LATITUDE)
                .altitude(ALTITUDE)
                .relevantText(RELEVANT_TEXT);
    }
}
