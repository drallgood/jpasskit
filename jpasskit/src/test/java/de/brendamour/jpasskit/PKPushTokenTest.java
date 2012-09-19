package de.brendamour.jpasskit;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PKPushTokenTest {
    private static final String PUSHTOKEN = "abcdef49";
    private PKPushToken pkPushToken;

    @BeforeMethod
    public void prepareTest() {
        pkPushToken = new PKPushToken();
    }

    @Test
    public void test_getterSetter() {
        pkPushToken.setPushToken(PUSHTOKEN);

        Assert.assertEquals(pkPushToken.getPushToken(), PUSHTOKEN);

    }

}
