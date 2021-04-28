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
package de.brendamour.jpasskit;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PKNFCTest {

    private static final String MESSAGE = "Dummy NFC message";
    private static final String ENCRYPTION_PUBLIC_KEY = "AFEFEFEAKHFBKAFVGSFJFDJFKDABFFLSB";
    private static final Boolean REQUIRES_AUTHENTICATION = true;

    private PKNFCBuilder builder;

    private void fillProperties() {
        builder.message(MESSAGE)
                .encryptionPublicKey(ENCRYPTION_PUBLIC_KEY)
                .requiresAuthentication(REQUIRES_AUTHENTICATION);
    }

    @BeforeMethod
    public void prepareTest() {
        builder = PKNFC.builder();
        fillProperties();
    }

    @Test
    public void test_builder() {
        assertThat(builder.build())
                .hasFieldOrPropertyWithValue("message", MESSAGE)
                .hasFieldOrPropertyWithValue("encryptionPublicKey", ENCRYPTION_PUBLIC_KEY)
                .hasFieldOrPropertyWithValue("requiresAuthentication", REQUIRES_AUTHENTICATION);
    }

    @Test
    public void test_getters() {
        PKNFC nfc = builder.build();

        assertThat(nfc.getMessage()).isEqualTo(MESSAGE);
        assertThat(nfc.getEncryptionPublicKey()).isEqualTo(ENCRYPTION_PUBLIC_KEY);
        assertThat(nfc.getRequiresAuthentication()).isEqualTo(REQUIRES_AUTHENTICATION);
    }

    @Test
    public void test_clone() {
        PKNFC nfc = builder.build();
        PKNFC copy = PKNFC.builder(nfc).build();

        assertThat(copy)
                .isNotSameAs(nfc)
                .isEqualToComparingFieldByFieldRecursively(nfc);

        assertThat(copy.getMessage()).isEqualTo(MESSAGE);
        assertThat(copy.getEncryptionPublicKey()).isEqualTo(ENCRYPTION_PUBLIC_KEY);
        assertThat(copy.getRequiresAuthentication()).isEqualTo(REQUIRES_AUTHENTICATION);
    }

    @Test
    public void test_toString() {
        PKNFC nfc = builder.build();
        assertThat(nfc.toString())
                .contains(MESSAGE)
                .contains(ENCRYPTION_PUBLIC_KEY)
                .contains(REQUIRES_AUTHENTICATION.toString());
    }
}
