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

import java.awt.Color;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.enums.PKPassType;
import de.brendamour.jpasskit.passes.PKBoardingPass;
import de.brendamour.jpasskit.passes.PKGenericPass;
import de.brendamour.jpasskit.passes.PKGenericPassBuilder;
import de.brendamour.jpasskit.util.BuilderUtils;

/**
 * Allows constructing and validating {@link PKPass} entities.
 *
 * @author Igor Stepanov
 */
@JsonPOJOBuilder(withPrefix = "")
public class PKPassBuilder implements IPKValidateable, IPKBuilder<PKPass> {

    private static final int EXPECTED_AUTHTOKEN_LENGTH = 16;

    private PKPass pkPass;
    private PKGenericPassBuilder pass;

    protected List<PKBeaconBuilder> beacons;
    protected List<PKLocationBuilder> locations;
    protected List<PKBarcodeBuilder> barcodes;
    protected List<PWAssociatedAppBuilder> associatedApps;
    protected List<Long> associatedStoreIdentifiers;

    protected PKPassBuilder() {
        this.pkPass = new PKPass();
        this.pass = PKGenericPass.builder();
        this.beacons = new CopyOnWriteArrayList<>();
        this.locations = new CopyOnWriteArrayList<>();
        this.barcodes = new CopyOnWriteArrayList<>();
        this.associatedApps = new CopyOnWriteArrayList<>();
        this.associatedStoreIdentifiers = new CopyOnWriteArrayList<>();
    }

    public List<PKBeaconBuilder> getBeaconBuilders() {
        return this.beacons;
    }

    public List<PKLocationBuilder> getLocationBuilders() {
        return this.locations;
    }

    public List<PKBarcodeBuilder> getBarcodeBuilders() {
        return this.barcodes;
    }

    public List<Long> getAssociatedStoreIdentifiers() {
        return this.associatedStoreIdentifiers;
    }

    public List<PWAssociatedAppBuilder> getAssociatedAppBuilders() {
        return this.associatedApps;
    }

    public PKGenericPassBuilder getPassBuilder() {
        return this.pass;
    }

    @Override
    public PKPassBuilder of(final PKPass pass) {
        if (pass != null) {
            this.pkPass = pass.clone();
            if (pass.getGeneric() != null) {
                this.pkPass.generic = null;
                pass(pass.getGeneric());
            }
            if (pass.getBoardingPass() != null) {
                this.pkPass.boardingPass = null;
                pass(pass.getBoardingPass());
            }
            if (pass.getCoupon() != null) {
                this.pkPass.coupon = null;
                pass(pass.getCoupon());
            }
            if (pass.getEventTicket() != null) {
                this.pkPass.eventTicket = null;
                pass(pass.getEventTicket());
            }
            if (pass.getStoreCard() != null) {
                this.pkPass.storeCard = null;
                pass(pass.getStoreCard());
            }
            this.beacons = BuilderUtils.toBeaconBuilderList(pass.getBeacons());
            this.locations = BuilderUtils.toLocationBuilderList(pass.getLocations());
            this.barcodes = BuilderUtils.toBarcodeBuilderList(pass.getBarcodes());
            this.associatedApps = BuilderUtils.toAssociatedAppBuilderList(pass.getAssociatedApps());
            if (pass.getAssociatedStoreIdentifiers() != null) {
                this.associatedStoreIdentifiers = new CopyOnWriteArrayList<>(pass.getAssociatedStoreIdentifiers());
            }
        }
        return this;
    }

    public PKPassBuilder serialNumber(String serialNumber) {
        this.pkPass.serialNumber = serialNumber;
        return this;
    }

    public PKPassBuilder passTypeIdentifier(String passTypeIdentifier) {
        this.pkPass.passTypeIdentifier = passTypeIdentifier;
        return this;
    }

    public PKPassBuilder webServiceURL(URL webServiceURL) {
        this.pkPass.webServiceURL = webServiceURL;
        return this;
    }

    public PKPassBuilder appLaunchURL(String appLaunchURL) {
        this.pkPass.appLaunchURL = appLaunchURL;
        return this;
    }

    public PKPassBuilder authenticationToken(String authenticationToken) {
        this.pkPass.authenticationToken = authenticationToken;
        return this;
    }

    public PKPassBuilder formatVersion(int formatVersion) {
        this.pkPass.formatVersion = formatVersion;
        return this;
    }

    public PKPassBuilder description(String description) {
        this.pkPass.description = description;
        return this;
    }

    public PKPassBuilder teamIdentifier(String teamIdentifier) {
        this.pkPass.teamIdentifier = teamIdentifier;
        return this;
    }

    public PKPassBuilder voided(boolean voided) {
        this.pkPass.voided = voided;
        return this;
    }

    public PKPassBuilder organizationName(String organizationName) {
        this.pkPass.organizationName = organizationName;
        return this;
    }

    public PKPassBuilder userInfo(Map<String, Object> userInfo) {
        this.pkPass.userInfo = userInfo;
        return this;
    }

    public PKPassBuilder logoText(String logoText) {
        this.pkPass.logoText = logoText;
        return this;
    }

    public PKPassBuilder foregroundColor(String foregroundColor) {
        this.pkPass.foregroundColor = foregroundColor;
        return this;
    }

    public PKPassBuilder foregroundColor(Color foregroundColor) {
        return foregroundColor(convertColorToString(foregroundColor));
    }

    public PKPassBuilder backgroundColor(String backgroundColor) {
        this.pkPass.backgroundColor = backgroundColor;
        return this;
    }

    public PKPassBuilder backgroundColor(Color backgroundColor) {
        return backgroundColor(convertColorToString(backgroundColor));
    }

    public PKPassBuilder beaconsBuilder(PKBeaconBuilder beacon) {
        if (beacon != null) {
            this.beacons.add(beacon);
        }
        return this;
    }

    public PKPassBuilder beacons(List<PKBeacon> beacons) {
        if (beacons == null || beacons.isEmpty()) {
            this.beacons.clear();
            return this;
        }
        beacons.stream().map(PKBeacon::builder).forEach(this::beaconsBuilder);
        return this;
    }

    public PKPassBuilder maxDistance(Long maxDistance) {
        this.pkPass.maxDistance = maxDistance;
        return this;
    }

    public PKPassBuilder locationBuilder(PKLocationBuilder location) {
        if (location != null) {
            this.locations.add(location);
        }
        return this;
    }

    public PKPassBuilder locations(List<PKLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            this.locations.clear();
            return this;
        }
        locations.stream().map(PKLocation::builder).forEach(this::locationBuilder);
        return this;
    }

    public PKPassBuilder barcodeBuilder(PKBarcodeBuilder barcode) {
        if (barcode != null) {
            this.barcodes.add(barcode);
        }
        return this;
    }

    public PKPassBuilder barcodes(List<PKBarcode> barcodes) {
        if (barcodes == null || barcodes.isEmpty()) {
            this.barcodes.clear();
            return this;
        }
        barcodes.stream().map(PKBarcode::builder).forEach(this::barcodeBuilder);
        return this;
    }

    public PKPassBuilder pass(PKGenericPassBuilder pass) {
        this.pass = pass;
        return this;
    }

    public PKPassBuilder pass(PKGenericPass generic) {
        return pass(PKGenericPass.builder(generic));
    }

    public PKPassBuilder pass(PKBoardingPass boardingPass) {
        return pass(PKBoardingPass.builder(boardingPass));
    }

    public PKPassBuilder labelColor(String labelColor) {
        this.pkPass.labelColor = labelColor;
        return this;
    }

    public PKPassBuilder labelColor(Color labelColor) {
        return labelColor(convertColorToString(labelColor));
    }

    public PKPassBuilder groupingIdentifier(String groupingIdentifier) {
        this.pkPass.groupingIdentifier = groupingIdentifier;
        return this;
    }

    public PKPassBuilder associatedStoreIdentifier(Long associatedStoreIdentifier) {
        if (associatedStoreIdentifier != null) {
            this.associatedStoreIdentifiers.add(associatedStoreIdentifier);
        }
        return this;
    }

    public PKPassBuilder associatedStoreIdentifiers(List<Long> associatedStoreIdentifiers) {
        if (associatedStoreIdentifiers == null || associatedStoreIdentifiers.isEmpty()) {
            this.associatedStoreIdentifiers.clear();
            return this;
        }
        this.associatedStoreIdentifiers.addAll(associatedStoreIdentifiers);
        return this;
    }

    public PKPassBuilder associatedAppBuilder(PWAssociatedAppBuilder associatedApp) {
        if (associatedApp != null) {
            this.associatedApps.add(associatedApp);
        }
        return this;
    }

    public PKPassBuilder associatedApps(List<PWAssociatedApp> associatedApps) {
        if (associatedApps == null || associatedApps.isEmpty()) {
            this.associatedApps.clear();
            return this;
        }
        associatedApps.stream().map(PWAssociatedApp::builder).forEach(this::associatedAppBuilder);
        return this;
    }

    @Deprecated
    public PKPassBuilder relevantDate(Date relevantDate) {
        this.pkPass.relevantDate = relevantDate.toInstant();
        return this;
    }

    @Deprecated
    public PKPassBuilder expirationDate(Date expirationDate) {
        this.pkPass.expirationDate = expirationDate.toInstant();
        return this;
    }

    public PKPassBuilder relevantDate(Instant relevantInstant) {
        this.pkPass.relevantDate = relevantInstant;
        return this;
    }

    public PKPassBuilder expirationDate(Instant expirationInstant) {
        this.pkPass.expirationDate = expirationInstant;
        return this;
    }

    public PKPassBuilder nfc(PKNFC nfc) {
        this.pkPass.nfc = nfc;
        return this;
    }

    public PKPassBuilder sharingProhibited(boolean sharingProhibited) {
        this.pkPass.sharingProhibited = sharingProhibited;
        return this;
    }

    public PKPassBuilder semantics(PKSemantics semantics) {
        this.pkPass.semantics = semantics;
        return this;
    }

    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    public List<String> getValidationErrors() {
        List<String> validationErrors = new ArrayList<>();

        checkRequiredFields(validationErrors);
        checkAuthToken(validationErrors);
        checkPass(validationErrors);
        checkAssociatedAppIfSet(validationErrors);
        checkGroupingIdentifierIsOnlySetWhenAllowed(validationErrors);
        checkSemanticsIfSet(validationErrors);
        return validationErrors;
    }

    private void checkGroupingIdentifierIsOnlySetWhenAllowed(List<String> validationErrors) {
        // groupingIdentifier key is optional for event tickets and boarding passes;
        // otherwise not allowed
        if (StringUtils.isNotEmpty(this.pkPass.groupingIdentifier)
                && this.pass.getPassType() == PKPassType.PKEventTicket
                && this.pass.getPassType() == PKPassType.PKBoardingPass) {
            validationErrors.add(
                    "The groupingIdentifier is optional for event tickets and boarding passes, otherwise not allowed");
        }
    }

    private void checkAssociatedAppIfSet(List<String> validationErrors) {
        // If appLaunchURL key is present, the associatedStoreIdentifiers key must also
        // be present
        if (this.pkPass.appLaunchURL != null && BuilderUtils.isEmpty(this.pkPass.associatedStoreIdentifiers)) {
            validationErrors.add("The appLaunchURL requires associatedStoreIdentifiers to be specified");
        }
    }

    private void checkPass(List<String> validationErrors) {
        if (this.pass == null) {
            validationErrors.add("No pass was defined");
        } else if (!this.pass.isValid()) {
            validationErrors.addAll(this.pass.getValidationErrors());
        }
    }

    private void checkAuthToken(List<String> validationErrors) {
        if (this.pkPass.authenticationToken != null
                && this.pkPass.authenticationToken.length() < EXPECTED_AUTHTOKEN_LENGTH) {
            validationErrors.add("The authenticationToken needs to be at least " + EXPECTED_AUTHTOKEN_LENGTH + " long");
        }
    }

    private void checkRequiredFields(List<String> validationErrors) {
        if (StringUtils.isEmpty(this.pkPass.serialNumber) || StringUtils.isEmpty(this.pkPass.passTypeIdentifier)
                || StringUtils.isEmpty(this.pkPass.teamIdentifier) || StringUtils.isEmpty(this.pkPass.description)
                || this.pkPass.formatVersion == 0 || StringUtils.isEmpty(this.pkPass.organizationName)) {
            validationErrors.add("Not all required Fields are set. SerialNumber" + this.pkPass.serialNumber
                    + " PassTypeIdentifier: " + this.pkPass.passTypeIdentifier + " teamIdentifier"
                    + this.pkPass.teamIdentifier + " Description: " + this.pkPass.description + " FormatVersion: "
                    + this.pkPass.formatVersion + " OrganizationName: " + this.pkPass.organizationName);
        }
    }

    private void checkSemanticsIfSet(List<String> validationErrors) {
        // TODO: figure out how to do efficient validation
        if (this.pkPass.semantics != null) {
            PKSemanticsBuilder semanticsBuilder = new PKSemanticsBuilder().of(this.pkPass.semantics);
            validationErrors.addAll(semanticsBuilder.getValidationErrors()); 
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private static String convertColorToString(final Color color) {
        if (color != null) {
            return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
        }
        return null;
    }

    @Override
    public PKPass build() {
        if (this.pass.getPassType() == null) {
            this.pkPass.generic = this.pass.build();
        } else {
            switch (pass.getPassType()) {
            case PKBoardingPass:
                this.pkPass.boardingPass = this.pass.buildBoardingPass();
                break;
            case PKCoupon:
                this.pkPass.coupon = this.pass.buildCoupon();
                break;
            case PKEventTicket:
                this.pkPass.eventTicket = this.pass.buildEventTicket();
                break;
            case PKStoreCard:
                this.pkPass.storeCard = this.pass.buildStoreCard();
                break;
            default:
                this.pkPass.generic = this.pass.build();
                break;
            }
        }
        this.pkPass.beacons = BuilderUtils.buildAll(this.beacons);
        this.pkPass.locations = BuilderUtils.buildAll(this.locations);
        this.pkPass.barcodes = BuilderUtils.buildAll(this.barcodes);
        this.pkPass.associatedApps = BuilderUtils.buildAll(this.associatedApps);
        this.pkPass.associatedStoreIdentifiers = Collections.unmodifiableList(this.associatedStoreIdentifiers);
        return this.pkPass;
    }
}
