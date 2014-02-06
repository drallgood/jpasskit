package de.brendamour.jpasskit;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by stipe on 06.02.14..
 */
public class PKBeaconTest {
    private static final Integer MAJOR = 3;
    private static final Integer MINOR = 29;
    private static final String UUID = "123456789-abcdefghijklmnopqrstuwxyz";
    private static final String TEXT = "County of Zadar";
    private PKBeacon pkBeacon;

    private void fillBeacon() {
        pkBeacon.setMajor(MAJOR);
        pkBeacon.setMinor(MINOR);
        pkBeacon.setProximityUUID(UUID);
        pkBeacon.setRelevantText(TEXT);
    }

    @BeforeMethod
    public void prepareTest() {
        pkBeacon = new PKBeacon();
        fillBeacon();
    }

    @Test
    public void test_getSet() {
        fillBeacon();

        Assert.assertEquals(pkBeacon.getMajor(), MAJOR);
        Assert.assertEquals(pkBeacon.getMinor(), MINOR);
        Assert.assertEquals(pkBeacon.getProximityUUID(), UUID);
        Assert.assertEquals(pkBeacon.getRelevantText(), TEXT);
        Assert.assertTrue(pkBeacon.isValid());
    }

    @Test
         public void test_noProximityUUID() {
        pkBeacon.setProximityUUID(null);

        Assert.assertFalse(pkBeacon.isValid());
    }

    @Test
    public void test_noRelevantText() {
        pkBeacon.setRelevantText(null);

        Assert.assertTrue(pkBeacon.isValid());
    }

    @Test
    public void test_noMajor() {
        pkBeacon.setMajor(null);

        Assert.assertTrue(pkBeacon.isValid());
    }

    @Test
    public void test_noMinor() {
        pkBeacon.setMinor(null);

        Assert.assertTrue(pkBeacon.isValid());
    }
}
