package de.brendamour.jpasskit.signing;

public class PKSigningException extends Exception {
    private static final long serialVersionUID = -2076454300033957976L;

    public PKSigningException(String cause, Exception exception) {
        super(cause, exception);
    }

    public PKSigningException(Exception exception) {
        super(exception);
    }

}
