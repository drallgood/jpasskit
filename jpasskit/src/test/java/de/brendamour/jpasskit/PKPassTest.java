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

import static de.brendamour.jpasskit.passes.PKGenericPassTest.SOME;
import static de.brendamour.jpasskit.passes.PKGenericPassTest.field;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.brendamour.jpasskit.enums.PKTransitType;
import de.brendamour.jpasskit.passes.PKBoardingPass;
import de.brendamour.jpasskit.passes.PKCoupon;
import de.brendamour.jpasskit.passes.PKEventTicket;
import de.brendamour.jpasskit.passes.PKGenericPass;
import de.brendamour.jpasskit.passes.PKGenericPassBuilder;
import de.brendamour.jpasskit.passes.PKStoreCard;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import de.brendamour.jpasskit.enums.PKBarcodeFormat;

public class PKPassTest {

    private static final int FORMAT = 5;
    private static final int FORMAT_DEFAULT = 1;
    private static final String SERIAL_NUMBER = "4277713";
    private static final String PASS_TYPE_ID = "com.stepio.my_best_pass";
    private static final String AUTH_TOKEN = "FSHDFUHDGHKFDSLFKHSDLKFGDSHFLSDF";
    private static final String ORGANIZATION_NAME = "JPasskit";
    private static final String TEAM_ID = "drallgood";
    private static final String SERVICE_URL_STRING = "https://github.com/drallgood/jpasskit";
    private static final URL SERVICE_URL = asUrl(SERVICE_URL_STRING);
    private static final String LOGO_TEXT = "Best Pass Ever";
    private static final String DESCRIPTION = "Pass helps people";

    private static final String COLOR_STRING = "rgb(1,2,3)";
    private static final Color COLOR_OBJECT = new Color(1, 2, 3);
    private static final String APP_LAUNCH_URL = "myapplication://open";
    private static final String GROUPING_ID = "group-1234";
    private static final Long MAX_DISTANCE = 99999L;
    private static final Map<String, Object> USER_INFO = ImmutableMap.<String, Object> of("name", "John Doe");
    private static final Instant EXPIRATION_DATE = Instant.now();

    private PKPassBuilder builder;

    @BeforeMethod
    public void prepareTest() {
        this.builder = PKPass.builder();
    }

    private void fillBasicFields() {
        this.builder.formatVersion(FORMAT)
                .serialNumber(SERIAL_NUMBER)
                .passTypeIdentifier(PASS_TYPE_ID)
                .webServiceURL(SERVICE_URL)
                .authenticationToken(AUTH_TOKEN)
                .description(DESCRIPTION)
                .organizationName(ORGANIZATION_NAME)
                .teamIdentifier(TEAM_ID);
    }

    private void fillPkPassFields() {
        this.builder.appLaunchURL(APP_LAUNCH_URL)
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
    public void test_gettersBasic() {
        assertThat(this.builder.isValid()).isFalse();

        fillBasicFields();

        assertThat(this.builder.isValid()).isTrue();

        PKPass pass = this.builder.build();

        assertThat(pass.isVoided()).isFalse();
        assertThat(pass.isSharingProhibited()).isFalse();

        assertThat(pass.getFormatVersion()).isEqualTo(FORMAT);

        assertThat(pass.getSerialNumber()).isEqualTo(SERIAL_NUMBER);
        assertThat(pass.getPassTypeIdentifier()).isEqualTo(PASS_TYPE_ID);
        assertThat(pass.getWebServiceURL()).isEqualTo(SERVICE_URL);
        assertThat(pass.getAuthenticationToken()).isEqualTo(AUTH_TOKEN);
        assertThat(pass.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(pass.getOrganizationName()).isEqualTo(ORGANIZATION_NAME);
        assertThat(pass.getTeamIdentifier()).isEqualTo(TEAM_ID);

        pass = this.builder.logoText(LOGO_TEXT).build();
        assertThat(pass.getLogoText()).isEqualTo(LOGO_TEXT);

        assertThat(pass.getAppLaunchURL()).isNull();
        assertThat(pass.getGroupingIdentifier()).isNull();
        assertThat(pass.getMaxDistance()).isNull();
        assertThat(pass.getUserInfo()).isNull();
        assertThat(pass.getExpirationDate()).isNull();
        assertThat(pass.getBarcodes()).isNotNull().isEmpty();
    }

    @Test
    public void test_getters() {
        assertThat(this.builder.isValid()).isFalse();

        fillPkPassFields();

        PKPass pass = this.builder.build();

        assertThat(pass.isVoided()).isTrue();
        assertThat(pass.isSharingProhibited()).isTrue();

        assertThat(pass.getFormatVersion()).isEqualTo(FORMAT_DEFAULT);

        assertThat(pass.getSerialNumber()).isNull();
        assertThat(pass.getPassTypeIdentifier()).isNull();
        assertThat(pass.getWebServiceURL()).isNull();
        assertThat(pass.getAuthenticationToken()).isNull();
        assertThat(pass.getDescription()).isNull();
        assertThat(pass.getOrganizationName()).isNull();
        assertThat(pass.getTeamIdentifier()).isNull();
        assertThat(pass.getLogoText()).isNull();

        assertThat(pass.getAppLaunchURL()).isEqualTo(APP_LAUNCH_URL);
        assertThat(pass.getGroupingIdentifier()).isEqualTo(GROUPING_ID);
        assertThat(pass.getMaxDistance()).isEqualTo(MAX_DISTANCE);
        assertThat(pass.getUserInfo()).isEqualTo(USER_INFO);
        assertThat(pass.getExpirationDate()).isEqualTo(EXPIRATION_DATE);

        assertThat(pass.getBarcodes()).isNotNull().hasSize(2);

        assertThat(this.builder.isValid()).isFalse();
    }

    @Test
    public void test_getBackgroundColor() {
        assertThat(this.builder
                .backgroundColor(COLOR_STRING)
                .build()
                .getBackgroundColor()).isEqualTo(COLOR_STRING);
        assertThat(this.builder
                .backgroundColor((String) null)
                .build()
                .getBackgroundColor()).isNull();

        assertThat(this.builder
                .backgroundColor(COLOR_OBJECT)
                .build()
                .getBackgroundColor()).isEqualTo(COLOR_STRING);
        assertThat(this.builder
                .backgroundColor((Color) null)
                .build()
                .getBackgroundColor()).isNull();
    }

    @Test
    public void test_getForegroundColor() {
        assertThat(this.builder
                .foregroundColor(COLOR_STRING)
                .build()
                .getForegroundColor()).isEqualTo(COLOR_STRING);
        assertThat(this.builder
                .foregroundColor((String) null)
                .build()
                .getForegroundColor()).isNull();

        assertThat(this.builder
                .foregroundColor(COLOR_OBJECT)
                .build()
                .getForegroundColor()).isEqualTo(COLOR_STRING);
        assertThat(this.builder
                .foregroundColor((Color) null)
                .build()
                .getForegroundColor()).isNull();
    }

    @Test
    public void test_getLabelColor() {
        assertThat(this.builder
                .labelColor(COLOR_STRING)
                .build()
                .getLabelColor()).isEqualTo(COLOR_STRING);
        assertThat(this.builder
                .labelColor((String) null)
                .build()
                .getLabelColor()).isNull();

        assertThat(this.builder
                .labelColor(COLOR_OBJECT)
                .build()
                .getLabelColor()).isEqualTo(COLOR_STRING);
        assertThat(this.builder
                .labelColor((Color) null)
                .build()
                .getLabelColor()).isNull();
    }

    @Test
    public void test_includesPassErrors() {
        PKGenericPassBuilder subPass = mock(PKGenericPassBuilder.class);
        List<String> subArrayListWithErrors = new ArrayList<>();
        String someValidationMessage = "Some error";
        subArrayListWithErrors.add(someValidationMessage);

        this.builder.pass(subPass);

        when(subPass.isValid()).thenReturn(false);
        when(subPass.getValidationErrors()).thenReturn(subArrayListWithErrors);

        List<String> validationErrors = this.builder.getValidationErrors();

        Assert.assertTrue(validationErrors.size() > 0);
        Assert.assertTrue(validationErrors.contains(someValidationMessage));
    }

    @Test
    public void test_getGeneric() {
        this.builder.pass(PKGenericPass.builder())
                .getPassBuilder().primaryField(field(SOME));
        PKPass pass = this.builder.build();

        assertThat(pass.getGeneric()).isNotNull();
        assertThat(pass.getEventTicket()).isNull();
        assertThat(pass.getCoupon()).isNull();
        assertThat(pass.getStoreCard()).isNull();
        assertThat(pass.getBoardingPass()).isNull();

        PKPass clone = PKPass.builder(pass).build();

        assertThat(clone)
                .isEqualToComparingFieldByFieldRecursively(pass);
    }

    @Test
    public void test_getEventTicket() {
        this.builder.pass(PKEventTicket.builder())
                .getPassBuilder().primaryField(field(SOME));
        PKPass pass = this.builder.build();

        assertThat(pass.getGeneric()).isNull();
        assertThat(pass.getEventTicket()).isNotNull();
        assertThat(pass.getCoupon()).isNull();
        assertThat(pass.getStoreCard()).isNull();
        assertThat(pass.getBoardingPass()).isNull();

        PKPass clone = PKPass.builder(pass).build();

        assertThat(clone)
                .isEqualToComparingFieldByFieldRecursively(pass);
    }

    @Test
    public void test_getCoupon() {
        this.builder.pass(PKCoupon.builder())
                .getPassBuilder().primaryField(field(SOME));
        PKPass pass = this.builder.build();

        assertThat(pass.getGeneric()).isNull();
        assertThat(pass.getEventTicket()).isNull();
        assertThat(pass.getCoupon()).isNotNull();
        assertThat(pass.getStoreCard()).isNull();
        assertThat(pass.getBoardingPass()).isNull();

        PKPass clone = PKPass.builder(pass).build();

        assertThat(clone)
                .isEqualToComparingFieldByFieldRecursively(pass);
    }

    @Test
    public void test_getStoreCard() {
        this.builder.pass(PKStoreCard.builder())
                .getPassBuilder().primaryField(field(SOME));
        PKPass pass = this.builder.build();

        assertThat(pass.getGeneric()).isNull();
        assertThat(pass.getEventTicket()).isNull();
        assertThat(pass.getCoupon()).isNull();
        assertThat(pass.getStoreCard()).isNotNull();
        assertThat(pass.getBoardingPass()).isNull();

        PKPass clone = PKPass.builder(pass).build();

        assertThat(clone)
                .isEqualToComparingFieldByFieldRecursively(pass);
    }

    @Test
    public void test_getBoardingPass() {
        this.builder.pass(PKBoardingPass.builder())
                .getPassBuilder().transitType(PKTransitType.PKTransitTypeBoat);
        PKPass pass = this.builder.build();

        assertThat(pass.getGeneric()).isNull();
        assertThat(pass.getEventTicket()).isNull();
        assertThat(pass.getCoupon()).isNull();
        assertThat(pass.getStoreCard()).isNull();
        assertThat(pass.getBoardingPass()).isNotNull();

        PKPass clone = PKPass.builder(pass).build();

        assertThat(clone)
                .isEqualToComparingFieldByFieldRecursively(pass);
    }

    private static URL asUrl(String value) {
        try {
            return new URL(value);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Bad URL: " + value, ex);
        }
    }
}
