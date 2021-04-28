/*
 * Copyright (C) 2019 Patrice Brend'amour <patrice@brendamour.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.brendamour.jpasskit.personalization;

import de.brendamour.jpasskit.IPKBuilder;
import de.brendamour.jpasskit.IPKValidateable;
import de.brendamour.jpasskit.enums.PKPassPersonalizationField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Allows constructing and validating {@link PKPersonalization} entities.
 *
 * @see <a href="https://developer.apple.com/library/prerelease/content/documentation/UserExperience/Conceptual/PassKit_PG/PassPersonalization.html">Rewards Enrollment</a>
 *
 * @author patrice
 * @author Igor Stepanov
 */
public class PKPersonalizationBuilder implements IPKValidateable, IPKBuilder<PKPersonalization> {

    private PKPersonalization personalization;
    private List<PKPassPersonalizationField> requiredPersonalizationFields;

    public List<PKPassPersonalizationField> getRequiredPersonalizationFields() {
        return requiredPersonalizationFields;
    }

    protected PKPersonalizationBuilder() {
        this.personalization = new PKPersonalization();
        this.requiredPersonalizationFields = new CopyOnWriteArrayList<>();
    }

    @Override
    public PKPersonalizationBuilder of(final PKPersonalization source) {
        if (source != null) {
            this.personalization = source.clone();
            if (this.personalization.getRequiredPersonalizationFields() != null) {
                this.requiredPersonalizationFields = new CopyOnWriteArrayList<>(this.personalization.getRequiredPersonalizationFields());
            }
        }
        return this;
    }

    public PKPersonalizationBuilder requiredPersonalizationField(PKPassPersonalizationField personalizationField) {
        if (personalizationField != null) {
            this.requiredPersonalizationFields.add(personalizationField);
        }
        return this;
    }

    public PKPersonalizationBuilder requiredPersonalizationFields(List<PKPassPersonalizationField> personalizationFields) {
        if (personalizationFields == null || personalizationFields.isEmpty()) {
            this.requiredPersonalizationFields.clear();
            return this;
        }
        this.requiredPersonalizationFields.addAll(personalizationFields);
        return this;
    }

    public PKPersonalizationBuilder description(String description) {
        this.personalization.description = description;
        return this;
    }

    public PKPersonalizationBuilder termsAndConditions(String termsAndConditions) {
        this.personalization.termsAndConditions = termsAndConditions;
        return this;
    }

    @Override
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {
        List<String> validationErrors = new ArrayList<>();

        if(this.requiredPersonalizationFields == null || this.requiredPersonalizationFields.isEmpty()) {
            validationErrors.add("You need to provide at least one requiredPersonalizationField");
        }
        if (StringUtils.isEmpty(this.personalization.getDescription())) {
            validationErrors.add("You need to provide a description");
        }
        return validationErrors;
    }

    @Override
    public PKPersonalization build() {
        this.personalization.requiredPersonalizationFields = Collections.unmodifiableList(this.requiredPersonalizationFields);
        return this.personalization;
    }
}
