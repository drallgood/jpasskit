package com.bitzeche.jpasskit;

public class PKSerialNumbersOfPassesForDeviceResponse {

    private String lastUpdate;
    private String[] serialNumbers;

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(final String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String[] getSerialNumbers() {
        return serialNumbers;
    }

    public void setSerialNumbers(final String[] serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

}
