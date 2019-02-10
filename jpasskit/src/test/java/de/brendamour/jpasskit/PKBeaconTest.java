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
    public void test_getSet() {
        Assert.assertTrue(builder.isValid());

        PKBeacon beacon = builder.build();
        Assert.assertEquals(beacon.getMajor(), MAJOR);
        Assert.assertEquals(beacon.getMinor(), MINOR);
        Assert.assertEquals(beacon.getProximityUUID(), UUID);
        Assert.assertEquals(beacon.getRelevantText(), TEXT);
    }

    @Test
    public void test_noProximityUUID() {
        builder.proximityUUID(null);

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_emptyProximityUUID() {
        builder.proximityUUID("");

        Assert.assertFalse(builder.isValid());
    }

    @Test
    public void test_noRelevantText() {
        builder.relevantText(null);

        Assert.assertTrue(builder.isValid());
    }

    @Test
    public void test_noMajor() {
        builder.major(null);

        Assert.assertTrue(builder.isValid());
    }

    @Test
    public void test_noMinor() {
        builder.minor(null);

        Assert.assertTrue(builder.isValid());
    }
}
