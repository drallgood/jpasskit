package de.brendamour.jpasskit;

import java.awt.Color;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PKPassTest {
    private static final String COLOR_STRING = "rgb(1,2,3)";
    private static final Color COLOR_OBJECT = new Color(1, 2, 3);
    private PKPass pkPass;

    @BeforeMethod
    public void prepareTest() {
        pkPass = new PKPass();
    }

    @Test
    public void test_colorConversionFromString() {

        pkPass.setBackgroundColor(COLOR_STRING);

        Assert.assertEquals(pkPass.getBackgroundColor(), COLOR_STRING);
        Assert.assertEquals(pkPass.getBackgroundColorAsObject(), COLOR_OBJECT);

    }

    @Test
    public void test_colorConversionFromObject() {

        pkPass.setBackgroundColorAsObject(COLOR_OBJECT);

        Assert.assertEquals(pkPass.getBackgroundColor(), COLOR_STRING);
        Assert.assertEquals(pkPass.getBackgroundColorAsObject(), COLOR_OBJECT);

    }

}
