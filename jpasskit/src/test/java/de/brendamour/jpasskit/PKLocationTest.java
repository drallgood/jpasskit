/**
 * Copyright (C) 2015 Patrice Brend'amour <patrice@brendamour.net>
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
    private PKLocation pkLocation;

    @BeforeMethod
    public void prepareTest() {
        pkLocation = new PKLocation();
        fillLocation();
    }

    @Test
    public void test_getterSetter() {
        Assert.assertEquals(pkLocation.getAltitude(), ALTITUDE);
        Assert.assertEquals(pkLocation.getLatitude(), LATITUDE);
        Assert.assertEquals(pkLocation.getLongitude(), LONGITUDE);
        Assert.assertEquals(pkLocation.getRelevantText(), RELEVANT_TEXT);
        Assert.assertTrue(pkLocation.isValid());

    }

    @Test
    public void test_getterSetter_NoLongitude() {
        pkLocation.setLongitude(0);

        Assert.assertFalse(pkLocation.isValid());

    }

    @Test
    public void test_getterSetter_NoLatitude() {
        pkLocation.setLatitude(0);

        Assert.assertFalse(pkLocation.isValid());
    }

    public void fillLocation() {
        pkLocation.setLongitude(LONGITUDE);
        pkLocation.setLatitude(LATITUDE);
        pkLocation.setAltitude(ALTITUDE);
        pkLocation.setRelevantText(RELEVANT_TEXT);
    }

}
