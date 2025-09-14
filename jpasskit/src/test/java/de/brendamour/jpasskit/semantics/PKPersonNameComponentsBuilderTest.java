/**
 * Copyright (C) 2024 Patrice Brend'amour <patrice@brendamour.net>
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
package de.brendamour.jpasskit.semantics;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PKPersonNameComponentsBuilderTest {

    private PKPersonNameComponentsBuilder builder;

    @BeforeMethod
    public void setUp() {
        builder = PKPersonNameComponents.builder();
    }

    @Test
    public void testOfWithNullSource() {
        PKPersonNameComponents result = builder.of(null).build();
        assertThat(result).isNotNull();
    }

    @Test
    public void testOfWithValidSource() {
        PKPersonNameComponents source = PKPersonNameComponents.builder()
            .familyName("Smith")
            .givenName("John")
            .build();
        
        PKPersonNameComponents result = builder.of(source).build();
        assertThat(result.familyName).isEqualTo("Smith");
        assertThat(result.givenName).isEqualTo("John");
    }

    @Test
    public void testFamilyName() {
        String familyName = "Johnson";
        PKPersonNameComponents result = builder.familyName(familyName).build();
        assertThat(result.familyName).isEqualTo(familyName);
    }

    @Test
    public void testGivenName() {
        String givenName = "Jane";
        PKPersonNameComponents result = builder.givenName(givenName).build();
        assertThat(result.givenName).isEqualTo(givenName);
    }

    @Test
    public void testMiddleName() {
        String middleName = "Marie";
        PKPersonNameComponents result = builder.middleName(middleName).build();
        assertThat(result.middleName).isEqualTo(middleName);
    }

    @Test
    public void testNamePrefix() {
        String namePrefix = "Dr.";
        PKPersonNameComponents result = builder.namePrefix(namePrefix).build();
        assertThat(result.namePrefix).isEqualTo(namePrefix);
    }

    @Test
    public void testNameSuffix() {
        String nameSuffix = "Jr.";
        PKPersonNameComponents result = builder.nameSuffix(nameSuffix).build();
        assertThat(result.nameSuffix).isEqualTo(nameSuffix);
    }

    @Test
    public void testNickname() {
        String nickname = "Johnny";
        PKPersonNameComponents result = builder.nickname(nickname).build();
        assertThat(result.nickname).isEqualTo(nickname);
    }

    @Test
    public void testPhoneticRepresentation() {
        PKPersonNameComponents phonetic = PKPersonNameComponents.builder()
            .givenName("Jon")
            .build();
        
        PKPersonNameComponents result = builder.phoneticRepresentation(phonetic).build();
        assertThat(result.phoneticRepresentation).isEqualTo(phonetic);
    }

    @Test
    public void testIsValid() {
        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void testGetValidationErrors() {
        assertThat(builder.getValidationErrors()).isEmpty();
    }

    @Test
    public void testChainedMethods() {
        PKPersonNameComponents result = builder
            .namePrefix("Dr.")
            .givenName("John")
            .middleName("Michael")
            .familyName("Smith")
            .nameSuffix("Jr.")
            .nickname("Johnny")
            .build();
        
        assertThat(result.namePrefix).isEqualTo("Dr.");
        assertThat(result.givenName).isEqualTo("John");
        assertThat(result.middleName).isEqualTo("Michael");
        assertThat(result.familyName).isEqualTo("Smith");
        assertThat(result.nameSuffix).isEqualTo("Jr.");
        assertThat(result.nickname).isEqualTo("Johnny");
    }

    @Test
    public void testCompleteNameWithPhonetic() {
        PKPersonNameComponents phonetic = PKPersonNameComponents.builder()
            .givenName("Jon")
            .familyName("Smyth")
            .build();
        
        PKPersonNameComponents result = builder
            .givenName("John")
            .familyName("Smith")
            .phoneticRepresentation(phonetic)
            .build();
        
        assertThat(result.givenName).isEqualTo("John");
        assertThat(result.familyName).isEqualTo("Smith");
        assertThat(result.phoneticRepresentation.givenName).isEqualTo("Jon");
        assertThat(result.phoneticRepresentation.familyName).isEqualTo("Smyth");
    }
}
