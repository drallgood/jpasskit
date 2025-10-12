/**
 * Copyright (C) 2024 Patrice Brend'amour <patrice@brendamour.net>
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

import java.io.Serializable;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.passes.PKBoardingPass;
import de.brendamour.jpasskit.passes.PKCoupon;
import de.brendamour.jpasskit.passes.PKEventTicket;
import de.brendamour.jpasskit.passes.PKGenericPass;
import de.brendamour.jpasskit.passes.PKStoreCard;

public class PKPass implements Cloneable, Serializable {

    private static final long serialVersionUID = -1727648896679270606L;

    protected int formatVersion;
    protected String serialNumber;
    protected String passTypeIdentifier;

    protected URL webServiceURL;
    protected String authenticationToken;

    protected String description;

    protected String teamIdentifier;

    protected String organizationName;
    protected String logoText;

    protected String foregroundColor;
    protected String backgroundColor;
    protected String labelColor;

    protected String groupingIdentifier;

    protected List<PKBeacon> beacons;
    protected List<PKLocation> locations;
    protected List<PKRelevantDate> relevantDates;

    protected List<PKBarcode> barcodes;

    protected PKEventTicket eventTicket;
    protected PKCoupon coupon;
    protected PKStoreCard storeCard;
    protected PKBoardingPass boardingPass;
    protected PKGenericPass generic;

    // Associated App Keys
    protected String appLaunchURL; // X-Callback-URL
    protected List<Long> associatedStoreIdentifiers;

    // Attido PassWallet support
    protected List<PWAssociatedApp> associatedApps;

    // Companion App Keys
    protected Map<String, Object> userInfo; // any JSON data

    // Relevance Keys
    protected Long maxDistance;
    protected Instant relevantDate;

    // Expiration Keys
    protected Instant expirationDate;
    protected boolean voided; // The key is optional, default value is false

    // Feature added in iOS 9.0. It is not applicable to older iOS
    protected PKNFC nfc;

    // Added 2018-06-07
    protected boolean sharingProhibited;

    // Feature added in iOS 12.0. It is not applicable to older iOS
    protected PKSemantics semantics;

    // Features added in iOS 26. It is not applicable to older iOS.
    protected URL purchaseAdditionalBaggageURL;
    protected URL purchaseLoungeAccessURL;
    protected URL changeSeatURL;
    protected URL purchaseWifiURL;
    protected URL orderFoodURL;
    protected URL entertainmentURL;
    protected URL reportLostBagURL;
    protected URL managementURL;
    protected String transitProviderPhoneNumber;
    protected String transitProviderEmail;
    protected URL transitProviderWebsiteUrl;
    protected URL upgradeURL;
    protected URL bagPolicyURL;
    protected URL accessibilityURL;
    protected URL requestWheelchairURL;
    protected URL registerServiceAnimalURL;
    protected List<String> preferredStyleSchemes;

    protected PKPass() {
        this.formatVersion = 1;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getPassTypeIdentifier() {
        return passTypeIdentifier;
    }

    public URL getWebServiceURL() {
        return webServiceURL;
    }

    public String getAppLaunchURL() {
        return appLaunchURL;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public String getDescription() {
        return description;
    }

    public String getTeamIdentifier() {
        return teamIdentifier;
    }

    public boolean isVoided() {
        return voided;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    public String getLogoText() {
        return logoText;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public List<PKBeacon> getBeacons() {
        return beacons;
    }

    public Long getMaxDistance() {
        return maxDistance;
    }

    public List<PKLocation> getLocations() {
        return locations;
    }

    public List<PKRelevantDate> getRelevantDates() {
        return relevantDates;
    }

    public List<PKBarcode> getBarcodes() {
        return barcodes;
    }

    public PKEventTicket getEventTicket() {
        return eventTicket;
    }

    public PKCoupon getCoupon() {
        return coupon;
    }

    public PKStoreCard getStoreCard() {
        return storeCard;
    }

    public PKBoardingPass getBoardingPass() {
        return boardingPass;
    }

    public PKGenericPass getGeneric() {
        return generic;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public String getGroupingIdentifier() {
        return groupingIdentifier;
    }

    public List<Long> getAssociatedStoreIdentifiers() {
        return associatedStoreIdentifiers;
    }

    public List<PWAssociatedApp> getAssociatedApps() {
        return associatedApps;
    }

    public Instant getRelevantDate() {
        return relevantDate;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public PKNFC getNFC() {
        return this.nfc;
    }

    public boolean isSharingProhibited() {
        return sharingProhibited;
    }

    public PKSemantics getSemantics() {
        return semantics;
    }

    public URL getPurchaseAdditionalBaggageURL() {
        return purchaseAdditionalBaggageURL;
    }

    public URL getPurchaseLoungeAccessURL() {
        return purchaseLoungeAccessURL;
    }

    public URL getChangeSeatURL() {
        return changeSeatURL;
    }

    public URL getPurchaseWifiURL() {
        return purchaseWifiURL;
    }

    public URL getOrderFoodURL() {
        return orderFoodURL;
    }

    public URL getEntertainmentURL() {
        return entertainmentURL;
    }

    public URL getReportLostBagURL() {
        return reportLostBagURL;
    }

    public URL getManagementURL() {
        return managementURL;
    }
    public String getTransitProviderPhoneNumber() {
        return transitProviderPhoneNumber;
    }
    public String getTransitProviderEmail() {
        return transitProviderEmail;
    }
    public URL getTransitProviderWebsiteUrl() {
        return transitProviderWebsiteUrl;
    }
    public URL getUpgradeURL() {
        return upgradeURL;
    }
    public URL getBagPolicyURL() {
        return bagPolicyURL;
    }
    public URL getAccessibilityURL() {
        return accessibilityURL;
    }
    public URL getRequestWheelchairURL() {
        return requestWheelchairURL;
    }
    public URL getRegisterServiceAnimalURL() {
        return registerServiceAnimalURL;
    }

    public List<String> getPreferredStyleSchemes() {
        return preferredStyleSchemes;
    }

    @Override
    protected PKPass clone() {
        try {
            return (PKPass) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Failed to clone PKPass instance", ex);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static PKPassBuilder builder() {
        return new PKPassBuilder();
    }

    public static PKPassBuilder builder(PKPass pass) {
        return builder().of(pass);
    }
}
