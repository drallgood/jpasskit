package de.brendamour.jpasskit;

import java.io.Serializable;

public class PKNFC implements Serializable {

    private static final long serialVersionUID = -2017873167088954297L;
    private String message;
    private String encryptionPublicKey;
    
    public PKNFC() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEncryptionPublicKey() {
        return encryptionPublicKey;
    }

    public void setEncryptionPublicKey(String encryptionPublicKey) {
        this.encryptionPublicKey = encryptionPublicKey;
    }

}
