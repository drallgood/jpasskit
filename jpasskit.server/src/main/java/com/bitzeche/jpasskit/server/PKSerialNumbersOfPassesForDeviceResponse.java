package com.bitzeche.jpasskit.server;

public class PKSerialNumbersOfPassesForDeviceResponse {

    private String lastUpdated;
    private String[] serialNumbers;

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final String lastUpdate) {
        this.lastUpdated = lastUpdate;
    }

    public String[] getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(final String[] serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

}
