package com.bitzeche.jpasskit.passes;

import com.bitzeche.jpasskit.PKTransitType;

public class PKBoardingPass extends PKGenericPass {
    private PKTransitType transitType;

    public PKTransitType getTransitType() {
        return transitType;
    }

    public void setTransitType(final PKTransitType transitType) {
        this.transitType = transitType;
    }

    public boolean isValid() {
        boolean valid = super.isValid();
        if (transitType == null) {
            valid = false;
        }
        return valid;
    }
}
