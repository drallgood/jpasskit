package com.bitzeche.jpasskit.passes;

import java.util.List;

import com.bitzeche.jpasskit.PKField;

public class PKGenericPass {

    protected List<PKField> primaryFields;
    protected List<PKField> auxiliaryFields;
    protected List<PKField> backFields;
    
    public List<PKField> getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(final List<PKField> primaryFields) {
        this.primaryFields = primaryFields;
    }

    public List<PKField> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public void setAuxiliaryFields(final List<PKField> auxiliaryFields) {
        this.auxiliaryFields = auxiliaryFields;
    }

    public List<PKField> getBackFields() {
        return backFields;
    }

    public void setBackFields(final List<PKField> backFields) {
        this.backFields = backFields;
    }
}
