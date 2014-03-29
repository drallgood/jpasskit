/**
 * Copyright (C) 2014 Patrice Brend'amour <p.brendamour@bitzeche.de>
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
 *
 * @author stepio
 * @since PassWallet v1.31
 *
 * Date: 29.03.14
 */
package de.brendamour.jpasskit;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PWAssociatedAppTest {
    private static final String TITLE = "PassWallet - Test";
    private static final String ID_GOOGLE_PLAY = "com.attidomobile.passwallet.test.google";
    private static final String ID_AMAZON = "com.attidomobile.passwallet.test.amazon";
    private PWAssociatedApp pwAssociatedApp;

    private void fillAssociatedApp() {
        pwAssociatedApp.setTitle(TITLE);
        pwAssociatedApp.setIdGooglePlay(ID_GOOGLE_PLAY);
        pwAssociatedApp.setIdAmazon(ID_AMAZON);
    }

    @BeforeMethod
    public void prepareTest() {
        pwAssociatedApp = new PWAssociatedApp();
        fillAssociatedApp();
    }

    @Test
    public void test_getSet() {

        Assert.assertEquals(pwAssociatedApp.getTitle(), TITLE);
        Assert.assertEquals(pwAssociatedApp.getIdGooglePlay(), ID_GOOGLE_PLAY);
        Assert.assertEquals(pwAssociatedApp.getIdAmazon(), ID_AMAZON);
        Assert.assertTrue(pwAssociatedApp.isValid());
    }
}
