package com.bitzeche.jpasskit;

import java.net.URL;
import java.util.List;

import com.bitzeche.jpasskit.passes.PKBoardingPass;
import com.bitzeche.jpasskit.passes.PKCoupon;
import com.bitzeche.jpasskit.passes.PKEventTicket;
import com.bitzeche.jpasskit.passes.PKGenericPass;
import com.bitzeche.jpasskit.passes.PKStoreCard;

public class PKPass {
    private String serialNumber;
    private String passTypeIdentifier;
    private URL webServiceURL;
    private String authenticationToken;
    private int formatVersion = 1;

    private String description;

    private String teamIdentifier;

    private String organizationName;
    private String logoText;
    private String foregroundColor;
    private String backgroundColor;

    private List<PKLocation> locations;

    private PKBarcode barcode;

    private PKEventTicket eventTicket;
    private PKCoupon coupon;
    private PKStoreCard storeCard;
    private PKBoardingPass boardingPass;
    private PKGenericPass generic;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPassTypeIdentifier() {
        return passTypeIdentifier;
    }

    public void setPassTypeIdentifier(final String passTypeIdentifier) {
        this.passTypeIdentifier = passTypeIdentifier;
    }

    public URL getWebServiceURL() {
        return webServiceURL;
    }

    public void setWebServiceURL(final URL webServiceURL) {
        this.webServiceURL = webServiceURL;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(final String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(final int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getTeamIdentifier() {
        return teamIdentifier;
    }

    public void setTeamIdentifier(final String teamIdentifier) {
        this.teamIdentifier = teamIdentifier;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLogoText() {
        return logoText;
    }

    public void setLogoText(final String logoText) {
        this.logoText = logoText;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(final String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(final String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public List<PKLocation> getLocations() {
        return locations;
    }

    public void setLocations(final List<PKLocation> locations) {
        this.locations = locations;
    }

    public PKBarcode getBarcode() {
        return barcode;
    }

    public void setBarcode(final PKBarcode barcode) {
        this.barcode = barcode;
    }

    public PKEventTicket getEventTicket() {
        return eventTicket;
    }

    public void setEventTicket(final PKEventTicket eventTicket) {
        this.eventTicket = eventTicket;
    }

    public PKCoupon getCoupon() {
        return coupon;
    }

    public void setCoupon(final PKCoupon coupon) {
        this.coupon = coupon;
    }

    public PKStoreCard getStoreCard() {
        return storeCard;
    }

    public void setStoreCard(final PKStoreCard storeCard) {
        this.storeCard = storeCard;
    }

    public PKBoardingPass getBoardingPass() {
        return boardingPass;
    }

    public void setBoardingPass(final PKBoardingPass boardingPass) {
        this.boardingPass = boardingPass;
    }

    public PKGenericPass getGeneric() {
        return generic;
    }

    public void setGeneric(final PKGenericPass generic) {
        this.generic = generic;
    }

}
