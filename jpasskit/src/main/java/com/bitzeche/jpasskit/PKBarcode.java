package com.bitzeche.jpasskit;

public class PKBarcode {

    private String message;
    private PKBarcodeFormat format;
    private String messageEncoding;

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

    public String getMessageEncoding() {
        return messageEncoding;
    }

    public void setMessageEncoding(final String messageEncoding) {
        this.messageEncoding = messageEncoding;
    }

}
