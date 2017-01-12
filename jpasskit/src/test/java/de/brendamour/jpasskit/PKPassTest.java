/**
 * Copyright (C) 2017 Patrice Brend'amour <patrice@brendamour.net>
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import de.brendamour.jpasskit.passes.PKGenericPass;

public class PKPassTest {
    private static final String COLOR_STRING = "rgb(1,2,3)";
    private static final Color COLOR_OBJECT = new Color(1, 2, 3);
    private static final String APP_LAUNCH_URL = "myapplication://open";
    private static final String GROUPING_ID = "group-1234";
    private static final Long MAX_DISTANCE = 99999l;
    private static final Map<String, Object> USER_INFO = ImmutableMap.<String, Object>of("name", "John Doe");
    private static final Date EXPIRATION_DATE = new Date();
    private PKPass pkPass;

    @BeforeMethod
    public void prepareTest() {
        pkPass = new PKPass();
    }

    private void fillPkPassFields() {
       pkPass.setAppLaunchURL(APP_LAUNCH_URL);
       pkPass.setGroupingIdentifier(GROUPING_ID);
       pkPass.setMaxDistance(MAX_DISTANCE);
       pkPass.setVoided(true);
       pkPass.setUserInfo(USER_INFO);
       pkPass.setExpirationDate(EXPIRATION_DATE);
    }

    @Test
    public void test_getSet() {
        fillPkPassFields();

        Assert.assertEquals(pkPass.getAppLaunchURL(), APP_LAUNCH_URL);
        Assert.assertEquals(pkPass.getGroupingIdentifier(), GROUPING_ID);
        Assert.assertEquals(pkPass.getMaxDistance(), MAX_DISTANCE);
        Assert.assertTrue(pkPass.isVoided());
        Assert.assertEquals(pkPass.getUserInfo(), USER_INFO);
        Assert.assertEquals(pkPass.getExpirationDate(), EXPIRATION_DATE);
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

    @Test
    public void test_includesPassErrors() {
        PKGenericPass subPass = mock(PKGenericPass.class);
        List<String> subArrayListWithErrors = new ArrayList<String>();
        String someValidationMessage = "Some error";
        subArrayListWithErrors.add(someValidationMessage);

        pkPass.setGeneric(subPass);

        when(subPass.isValid()).thenReturn(false);
        when(subPass.getValidationErrors()).thenReturn(subArrayListWithErrors);

        List<String> validationErrors = pkPass.getValidationErrors();

        Assert.assertTrue(validationErrors.size() > 0);
        Assert.assertTrue(validationErrors.contains(someValidationMessage));

    }
}
