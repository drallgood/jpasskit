package de.brendamour.jpasskit.personalization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.brendamour.jpasskit.IPKValidateable;
import de.brendamour.jpasskit.enums.PKPassPersonalizationField;

/**
 * See https://developer.apple.com/library/prerelease/content/documentation/UserExperience/Conceptual/PassKit_PG/PassPersonalization.html
 * @author patrice
 *
 */
public class PKPersonalization implements IPKValidateable {
    private static final long serialVersionUID = -7580722464940378982L;

    private List<PKPassPersonalizationField> requiredPersonalizationFields;

    private String description;
    
    private String termsAndConditions;
    
    public List<PKPassPersonalizationField> getRequiredPersonalizationFields() {
        return requiredPersonalizationFields;
    }

    public void setRequiredPersonalizationFields(List<PKPassPersonalizationField> requiredPersonalizationFields) {
        this.requiredPersonalizationFields = requiredPersonalizationFields;
    }
    
    public void addRequiredPersonalizationField(PKPassPersonalizationField requiredPersonalizationField) {
        if(this.requiredPersonalizationFields == null) {
            this.requiredPersonalizationFields = new ArrayList<PKPassPersonalizationField>();
        }
        this.requiredPersonalizationFields.add(requiredPersonalizationField);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {
        List<String> validationErrors = new ArrayList<String>();

        if(requiredPersonalizationFields == null || requiredPersonalizationFields.size() == 0) {
            validationErrors.add("You need to provide atleast one requiredPersonalizationField"); 
        }
        if (StringUtils.isEmpty(description)) {
            validationErrors.add("You need to provide a description");
        }
        return validationErrors;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
