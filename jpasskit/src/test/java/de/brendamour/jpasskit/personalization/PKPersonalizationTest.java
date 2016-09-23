/**
 * Copyright (C) 2016 Patrice Brend'amour <patrice@brendamour.net>
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

import com.google.common.collect.Lists;

import de.brendamour.jpasskit.enums.PKPassPersonalizationField;

public class PKPersonalizationTest {
    private static final PKPassPersonalizationField PKPASSPERSONALIZATIONFIELD = PKPassPersonalizationField.PKPassPersonalizationFieldName;
    private static final PKPassPersonalizationField PKPASSPERSONALIZATIONFIELD2 = PKPassPersonalizationField.PKPassPersonalizationFieldEmailAddress;
    private static final String DESCRIPTION = "Fancy description";
    private static final String TERMS = "This is bullshit";
    private PKPersonalization pkPersonalization;

    @BeforeMethod
    public void prepareTest() {
        pkPersonalization = new PKPersonalization();
    }

    private void fillPkPersonalizationFields() {
        pkPersonalization.setDescription(DESCRIPTION);
        pkPersonalization.setTermsAndConditions(TERMS);
        pkPersonalization.setRequiredPersonalizationFields(Lists.newArrayList(PKPASSPERSONALIZATIONFIELD));
        pkPersonalization.addRequiredPersonalizationField(PKPASSPERSONALIZATIONFIELD2);

    }

    @Test
    public void test_getSet() {
        fillPkPersonalizationFields();

        Assert.assertEquals(pkPersonalization.getDescription(), DESCRIPTION);
        Assert.assertEquals(pkPersonalization.getTermsAndConditions(), TERMS);
        Assert.assertNotNull(pkPersonalization.getRequiredPersonalizationFields());
        Assert.assertEquals(pkPersonalization.getRequiredPersonalizationFields().size(), 1);
        Assert.assertEquals(pkPersonalization.getRequiredPersonalizationFields().get(0), PKPASSPERSONALIZATIONFIELD);
    }

    @Test
    public void test_validation_valid() {
        fillPkPersonalizationFields();

        Assert.assertTrue(pkPersonalization.isValid());
        Assert.assertTrue(pkPersonalization.getValidationErrors().isEmpty());
    }

    @Test
    public void test_validation_valid_optionalNotSet() {
        fillPkPersonalizationFields();
        pkPersonalization.setTermsAndConditions(null);

        Assert.assertTrue(pkPersonalization.isValid());
        Assert.assertTrue(pkPersonalization.getValidationErrors().isEmpty());
    }

    @Test
    public void test_validation_invalid() {
        Assert.assertFalse(pkPersonalization.isValid());
        Assert.assertTrue(pkPersonalization.getValidationErrors().size() == 2);
    }
}
