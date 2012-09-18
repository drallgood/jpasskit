package com.bitzeche.jpasskit;

import java.nio.charset.Charset;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PKBarcode {

    private PKBarcodeFormat format;
    private String altText;
    private String message;
    private Charset messageEncoding;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public PKBarcodeFormat getFormat() {
        return format;
    }

    public void setFormat(final PKBarcodeFormat format) {
        this.format = format;
    }

    public Charset getMessageEncoding() {
        return messageEncoding;
    }

    public void setMessageEncoding(final Charset messageEncoding) {
        this.messageEncoding = messageEncoding;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(final String altText) {
        this.altText = altText;
    }

    public boolean isValid() {
        boolean valid = true;

        if (format == null || message == null || messageEncoding == null) {
            valid = false;
        }
        return valid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
