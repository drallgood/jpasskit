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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.brendamour.jpasskit.passes.PKGenericPassBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

public class PKPassTest {

    private static final String COLOR_STRING = "rgb(1,2,3)";
    private static final Color COLOR_OBJECT = new Color(1, 2, 3);
    private static final String APP_LAUNCH_URL = "myapplication://open";
    private static final String GROUPING_ID = "group-1234";
    private static final Long MAX_DISTANCE = 99999L;
    private static final Map<String, Object> USER_INFO = ImmutableMap.<String, Object> of("name", "John Doe");
    private static final Date EXPIRATION_DATE = new Date();

    private PKPassBuilder builder;

    @BeforeMethod
    public void prepareTest() {
        builder = PKPass.builder();
    }

    private void fillPkPassFields() {
        builder.appLaunchURL(APP_LAUNCH_URL)
                .groupingIdentifier(GROUPING_ID)
                .maxDistance(MAX_DISTANCE)
                .voided(true)
                .userInfo(USER_INFO)
                .expirationDate(EXPIRATION_DATE)
                .sharingProhibited(true)
                .barcodes(Arrays.asList(
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatQR)
                                .build(),
                        PKBarcode.builder()
                                .format(PKBarcodeFormat.PKBarcodeFormatCode128)
                                .build()
                ));
    }

    @Test
    public void test_getSet() {
        fillPkPassFields();

        PKPass pass = builder.build();
        Assert.assertEquals(pass.getAppLaunchURL(), APP_LAUNCH_URL);
        Assert.assertEquals(pass.getGroupingIdentifier(), GROUPING_ID);
        Assert.assertEquals(pass.getMaxDistance(), MAX_DISTANCE);
        Assert.assertTrue(pass.isVoided());
        Assert.assertEquals(pass.getUserInfo(), USER_INFO);
        Assert.assertEquals(pass.getExpirationDate(), EXPIRATION_DATE);
        Assert.assertTrue(pass.isSharingProhibited());
        List<PKBarcode> barcodes = pass.getBarcodes();
        Assert.assertNotNull(barcodes);
        Assert.assertEquals(barcodes.size(), 2);
    }

    @Test
    public void test_colorConversionFromString() {
        builder.backgroundColor(COLOR_STRING);

        Assert.assertEquals(builder.build().getBackgroundColor(), COLOR_STRING);
    }

    @Test
    public void test_colorConversionFromObject() {
        builder.backgroundColor(COLOR_OBJECT);

        Assert.assertEquals(builder.build().getBackgroundColor(), COLOR_STRING);
    }

    @Test
    public void test_includesPassErrors() {
        PKGenericPassBuilder subPass = mock(PKGenericPassBuilder.class);
        List<String> subArrayListWithErrors = new ArrayList<>();
        String someValidationMessage = "Some error";
        subArrayListWithErrors.add(someValidationMessage);

        builder.pass(subPass);

        when(subPass.isValid()).thenReturn(false);
        when(subPass.getValidationErrors()).thenReturn(subArrayListWithErrors);

        List<String> validationErrors = builder.getValidationErrors();

        Assert.assertTrue(validationErrors.size() > 0);
        Assert.assertTrue(validationErrors.contains(someValidationMessage));
    }
}
