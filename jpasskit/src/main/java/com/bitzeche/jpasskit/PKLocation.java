package com.bitzeche.jpasskit;


public class PKLocation {
    private float latitude;
    private float longitude;
    private float altitude;
    private String relevantText;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(final float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(final float longitude) {
        this.longitude = longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(final float altitude) {
        this.altitude = altitude;
    }

    public String getRelevantText() {
        return relevantText;
    }

    public void setRelevantText(final String relevantText) {
        this.relevantText = relevantText;
    }

    public boolean isValid() {
        boolean valid = true;

        // nothing to check here?

        return valid;
    }
}
