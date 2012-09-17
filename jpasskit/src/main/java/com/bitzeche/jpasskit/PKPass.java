package com.bitzeche.jpasskit;

import java.awt.Color;
import java.net.URL;
import java.util.List;

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
    private Color foregroundColor;
    private Color backgroundColor;

    private List<PKLocation> locations;

    private PKBarcode barcode;

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

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(final Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(final Color backgroundColor) {
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

}
