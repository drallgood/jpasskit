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
package de.brendamour.jpasskit.passes;

import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKFieldBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class PKGenericPassTest {

    public static final String HEADER = "header";
    public static final String PRIMARY = "primary";
    public static final String SECONDARY = "secondary";
    public static final String AUXILIARY = "auxiliary";
    public static final String BACK = "back";

    public static final String SOME = "some";

    public static final String KEY = "Key";
    public static final String LABEL = "Label";
    public static final String VALUE = "Value";

    private PKGenericPassBuilder builder;

    @BeforeMethod
    public void setUp() {
        this.builder = PKGenericPass.builder();
    }

    @Test
    public void test_builder() {
        assertThat(this.builder.isValid()).isTrue();
        this.builder.headerField(field(HEADER));
        this.builder.primaryField(field(PRIMARY));
        this.builder.secondaryField(field(SECONDARY));
        this.builder.auxiliaryField(field(AUXILIARY));
        this.builder.backField(field(BACK));
        assertThat(this.builder.isValid()).isTrue();

        assertThat(this.builder.getHeaderFieldBuilders()).hasSize(1)
                .extracting(PKFieldBuilder::build)
                .extracting(PKField::getKey)
                .contains(HEADER + KEY);
        assertThat(this.builder.getPrimaryFieldBuilders()).hasSize(1)
                .extracting(PKFieldBuilder::build)
                .extracting(PKField::getKey)
                .contains(PRIMARY + KEY);
        assertThat(this.builder.getSecondaryFieldBuilders()).hasSize(1)
                .extracting(PKFieldBuilder::build)
                .extracting(PKField::getKey)
                .contains(SECONDARY + KEY);
        assertThat(this.builder.getAuxiliaryFieldBuilders()).hasSize(1)
                .extracting(PKFieldBuilder::build)
                .extracting(PKField::getKey)
                .contains(AUXILIARY + KEY);
        assertThat(this.builder.getBackFieldBuilders()).hasSize(1)
                .extracting(PKFieldBuilder::build)
                .extracting(PKField::getKey)
                .contains(BACK + KEY);

        PKGenericPass pass = this.builder.build();
        assertThat(pass).isNotNull();

        assertThat(pass.getHeaderFields()).hasSize(1)
                .extracting(PKField::getKey)
                .contains(HEADER + KEY);
        assertThat(pass.getPrimaryFields()).hasSize(1)
                .extracting(PKField::getKey)
                .contains(PRIMARY + KEY);
        assertThat(pass.getSecondaryFields()).hasSize(1)
                .extracting(PKField::getKey)
                .contains(SECONDARY + KEY);
        assertThat(pass.getAuxiliaryFields()).hasSize(1)
                .extracting(PKField::getKey)
                .contains(AUXILIARY + KEY);
        assertThat(pass.getBackFields()).hasSize(1)
                .extracting(PKField::getKey)
                .contains(BACK + KEY);
    }

    @Test
    public void test_clone() {
        assertThat(this.builder.isValid()).isTrue();
        this.builder.headerFields(asList(field(SOME + 1), field(SOME + 6)));
        this.builder.primaryFields(asList(field(SOME + 2), field(SOME + 7)));
        this.builder.secondaryFields(asList(field(SOME + 3), field(SOME + 8)));
        this.builder.auxiliaryFields(asList(field(SOME + 4), field(SOME + 9)));
        this.builder.backFields(asList(field(SOME + 5), field(SOME + 10)));

        PKGenericPass pass = this.builder.build();
        PKGenericPass clone = PKGenericPass.builder(pass).build();
        assertThat(clone).isNotNull()
                .isNotSameAs(pass)
                .isEqualToComparingFieldByFieldRecursively(pass);

        assertThat(clone.getHeaderFields()).hasSize(2);
        assertThat(clone.getPrimaryFields()).hasSize(2);
        assertThat(clone.getSecondaryFields()).hasSize(2);
        assertThat(clone.getAuxiliaryFields()).hasSize(2);
        assertThat(clone.getBackFields()).hasSize(2);
    }

    public static PKField field(String prefix) {
        return PKField.builder()
                .key(prefix + KEY)
                .label(prefix + LABEL)
                .value(prefix + VALUE)
                .build();
    }

    public static PKGenericPassBuilder fillDummy(PKGenericPassBuilder builder) {
        int i = 0;
        builder.headerField(field(SOME + ++i));
        builder.primaryField(field(SOME + ++i));
        builder.secondaryField(field(SOME + ++i));
        builder.auxiliaryField(field(SOME + ++i));
        builder.backField(field(SOME + ++i));
        return builder;
    }
}
