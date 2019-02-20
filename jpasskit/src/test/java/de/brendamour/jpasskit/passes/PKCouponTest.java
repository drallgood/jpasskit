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
package de.brendamour.jpasskit.passes;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static de.brendamour.jpasskit.passes.PKGenericPassTest.SOME;
import static de.brendamour.jpasskit.passes.PKGenericPassTest.field;
import static org.assertj.core.api.Assertions.assertThat;

public class PKCouponTest {

    private PKGenericPassBuilder builder;

    @BeforeMethod
    public void setUp() {
        this.builder = PKCoupon.builder();
    }

    @Test
    public void test_builder() {
        assertThat(this.builder.isValid()).isTrue();
        this.builder.headerField(field(SOME + 1));
        this.builder.primaryField(field(SOME + 2));
        this.builder.secondaryField(field(SOME + 3));
        this.builder.auxiliaryField(field(SOME + 4));
        this.builder.backField(field(SOME + 5));
        assertThat(this.builder.isValid()).isTrue();

        PKCoupon pass = this.builder.buildCoupon();
        assertThat(pass).isNotNull();

        pass = (PKCoupon) this.builder.build();
        assertThat(pass).isNotNull();
    }

    @Test
    public void test_clone() {
        assertThat(this.builder.isValid()).isTrue();
        this.builder.headerField(field(SOME + 1));
        this.builder.primaryField(field(SOME + 2));
        this.builder.secondaryField(field(SOME + 3));
        this.builder.auxiliaryField(field(SOME + 4));
        this.builder.backField(field(SOME + 5));

        PKCoupon pass = (PKCoupon) this.builder.build();
        PKCoupon clone = (PKCoupon) PKCoupon.builder(pass).build();
        assertThat(clone).isNotNull()
                .isNotSameAs(pass)
                .isEqualToComparingFieldByFieldRecursively(pass);
    }
}
