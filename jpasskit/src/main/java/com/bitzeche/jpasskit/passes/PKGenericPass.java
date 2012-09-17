package com.bitzeche.jpasskit.passes;

import java.util.List;

import com.bitzeche.jpasskit.PKField;
import com.google.common.collect.Lists;

public class PKGenericPass {

    protected List<PKField> headerFields;
    protected List<PKField> primaryFields;
    protected List<PKField> secondaryFields;
    protected List<PKField> auxiliaryFields;
    protected List<PKField> backFields;

    public List<PKField> getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(final List<PKField> primaryFields) {
        this.primaryFields = primaryFields;
    }

    public List<PKField> getSecondaryFields() {
        return secondaryFields;
    }

    public void setSecondaryFields(final List<PKField> secondaryFields) {
        this.secondaryFields = secondaryFields;
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

    public List<PKField> getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(final List<PKField> headerFields) {
        this.headerFields = headerFields;
    }

    public boolean isValid() {
        boolean valid = true;

        List<List<PKField>> lists = Lists.newArrayList();
        lists.add(primaryFields);
        lists.add(secondaryFields);
        lists.add(headerFields);
        lists.add(backFields);

        for (List<PKField> list : lists) {
            if (list != null) {
                for (PKField pkField : list) {
                    if (!pkField.isValid()) {
                        return false;
                    }
                }
            }
        }

        return valid;
    }
}
