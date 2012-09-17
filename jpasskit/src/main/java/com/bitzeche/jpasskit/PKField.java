package com.bitzeche.jpasskit;

public class PKField {
    private String key;
    private String label;
    private Object value;
    private String changeMessage;
    private String textAlignment;

    private String currencyCode;
    private String numberStyle;

    private PKDateStyle dateStyle;
    private PKDateStyle timeStyle;
    private boolean isRelative;

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(final String changeMessage) {
        this.changeMessage = changeMessage;
    }

    public String getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(final String textAlignment) {
        this.textAlignment = textAlignment;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getNumberStyle() {
        return numberStyle;
    }

    public void setNumberStyle(final String numberStyle) {
        this.numberStyle = numberStyle;
    }

    public PKDateStyle getDateStyle() {
        return dateStyle;
    }

    public void setDateStyle(final PKDateStyle dateStyle) {
        this.dateStyle = dateStyle;
    }

    public PKDateStyle getTimeStyle() {
        return timeStyle;
    }

    public void setTimeStyle(final PKDateStyle timeStyle) {
        this.timeStyle = timeStyle;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public void setRelative(final boolean isRelative) {
        this.isRelative = isRelative;
    }

}
