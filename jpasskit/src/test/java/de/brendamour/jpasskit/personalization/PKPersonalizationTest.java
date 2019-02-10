/**
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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.brendamour.jpasskit.enums.PKPassPersonalizationField;

import java.util.Collections;

public class PKPersonalizationTest {

    private static final PKPassPersonalizationField PKPASSPERSONALIZATIONFIELD = PKPassPersonalizationField.PKPassPersonalizationFieldName;
    private static final PKPassPersonalizationField PKPASSPERSONALIZATIONFIELD2 = PKPassPersonalizationField.PKPassPersonalizationFieldEmailAddress;
    private static final String DESCRIPTION = "Fancy description";
    private static final String TERMS = "This is bullshit";

    private PKPersonalizationBuilder builder;

    @BeforeMethod
    public void prepareTest() {
        builder = PKPersonalization.builder();
    }

    private void fillPkPersonalizationFields() {
        builder.description(DESCRIPTION)
                .termsAndConditions(TERMS)
                .requiredPersonalizationFields(Collections.singletonList(PKPASSPERSONALIZATIONFIELD))
                .requiredPersonalizationField(PKPASSPERSONALIZATIONFIELD2);

    }

    @Test
    public void test_getSet() {
        fillPkPersonalizationFields();

        PKPersonalization personalization = builder.build();
        Assert.assertEquals(personalization.getDescription(), DESCRIPTION);
        Assert.assertEquals(personalization.getTermsAndConditions(), TERMS);
        Assert.assertNotNull(personalization.getRequiredPersonalizationFields());
        Assert.assertEquals(personalization.getRequiredPersonalizationFields().size(), 2);
        Assert.assertEquals(personalization.getRequiredPersonalizationFields().get(0), PKPASSPERSONALIZATIONFIELD);
    }

    @Test
    public void test_validation_valid() {
        fillPkPersonalizationFields();

        Assert.assertTrue(builder.isValid());
        Assert.assertTrue(builder.getValidationErrors().isEmpty());
    }

    @Test
    public void test_validation_valid_optionalNotSet() {
        fillPkPersonalizationFields();
        builder.termsAndConditions(null);

        Assert.assertTrue(builder.isValid());
        Assert.assertTrue(builder.getValidationErrors().isEmpty());
    }

    @Test
    public void test_validation_invalid() {
        Assert.assertFalse(builder.isValid());
        Assert.assertEquals(builder.getValidationErrors().size(), 2);
    }
}
