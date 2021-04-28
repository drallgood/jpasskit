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

public class PWAssociatedAppTest {

    private static final String TITLE = "PassWallet - Test";
    private static final String ID_GOOGLE_PLAY = "com.attidomobile.passwallet.test.google";
    private static final String ID_AMAZON = "com.attidomobile.passwallet.test.amazon";

    private PWAssociatedAppBuilder builder;

    private void fillAssociatedApp() {
        builder.title(TITLE)
                .idGooglePlay(ID_GOOGLE_PLAY)
                .idAmazon(ID_AMAZON);
    }

    @BeforeMethod
    public void prepareTest() {
        builder = PWAssociatedApp.builder();
        fillAssociatedApp();
    }

    @Test
    public void test_builder() {
        assertThat(builder.build())
                .hasFieldOrPropertyWithValue("title", TITLE)
                .hasFieldOrPropertyWithValue("idGooglePlay", ID_GOOGLE_PLAY)
                .hasFieldOrPropertyWithValue("idAmazon", ID_AMAZON);
    }

    @Test
    public void test_clone() {
        PWAssociatedApp associatedApp = builder.build();
        PWAssociatedApp copy = PWAssociatedApp.builder(associatedApp).build();

        assertThat(copy)
                .isNotSameAs(associatedApp)
                .isEqualToComparingFieldByFieldRecursively(associatedApp);

        assertThat(copy.getTitle()).isEqualTo(TITLE);
        assertThat(copy.getIdGooglePlay()).isEqualTo(ID_GOOGLE_PLAY);
        assertThat(copy.getIdAmazon()).isEqualTo(ID_AMAZON);
    }

    @Test
    public void test_getters() {
        PWAssociatedApp associatedApp = builder.build();
        assertThat(associatedApp.getTitle()).isEqualTo(TITLE);
        assertThat(associatedApp.getIdGooglePlay()).isEqualTo(ID_GOOGLE_PLAY);
        assertThat(associatedApp.getIdAmazon()).isEqualTo(ID_AMAZON);
    }

    @Test
    public void test_toString() {
        PWAssociatedApp associatedApp = builder.build();
        assertThat(associatedApp.toString())
                .contains(TITLE)
                .contains(ID_GOOGLE_PLAY)
                .contains(ID_AMAZON);
    }
}
