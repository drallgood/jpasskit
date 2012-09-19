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
