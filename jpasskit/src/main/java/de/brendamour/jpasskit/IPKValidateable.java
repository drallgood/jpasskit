package de.brendamour.jpasskit;

import java.util.List;

public interface IPKValidateable {
    public boolean isValid();

    public List<String> getValidationErrors();
}
