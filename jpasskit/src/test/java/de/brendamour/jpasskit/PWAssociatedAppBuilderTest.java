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
package de.brendamour.jpasskit;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PWAssociatedAppBuilderTest {

    private PWAssociatedAppBuilder builder;

    @BeforeMethod
    public void setUp() {
        builder = PWAssociatedApp.builder();
    }

    @Test
    public void testDefaultConstructor() {
        PWAssociatedApp app = builder.build();
        Assert.assertNotNull(app);
        Assert.assertNull(app.title);
        Assert.assertNull(app.idGooglePlay);
        Assert.assertNull(app.idAmazon);
    }

    @Test
    public void testTitle() {
        String title = "My App";
        builder.title(title);
        PWAssociatedApp app = builder.build();
        Assert.assertEquals(app.title, title);
    }

    @Test
    public void testTitleWithNull() {
        builder.title(null);
        PWAssociatedApp app = builder.build();
        Assert.assertNull(app.title);
    }

    @Test
    public void testTitleWithEmptyString() {
        builder.title("");
        PWAssociatedApp app = builder.build();
        Assert.assertEquals(app.title, "");
    }

    @Test
    public void testIdGooglePlay() {
        String googlePlayId = "com.example.app";
        builder.idGooglePlay(googlePlayId);
        PWAssociatedApp app = builder.build();
        Assert.assertEquals(app.idGooglePlay, googlePlayId);
    }

    @Test
    public void testIdGooglePlayWithNull() {
        builder.idGooglePlay(null);
        PWAssociatedApp app = builder.build();
        Assert.assertNull(app.idGooglePlay);
    }

    @Test
    public void testIdAmazon() {
        String amazonId = "B01234567890";
        builder.idAmazon(amazonId);
        PWAssociatedApp app = builder.build();
        Assert.assertEquals(app.idAmazon, amazonId);
    }

    @Test
    public void testIdAmazonWithNull() {
        builder.idAmazon(null);
        PWAssociatedApp app = builder.build();
        Assert.assertNull(app.idAmazon);
    }

    @Test
    public void testChainedSetters() {
        String title = "Test App";
        String googlePlayId = "com.test.app";
        String amazonId = "B09876543210";

        PWAssociatedApp app = builder
                .title(title)
                .idGooglePlay(googlePlayId)
                .idAmazon(amazonId)
                .build();

        Assert.assertEquals(app.title, title);
        Assert.assertEquals(app.idGooglePlay, googlePlayId);
        Assert.assertEquals(app.idAmazon, amazonId);
    }

    @Test
    public void testOfMethod() {
        PWAssociatedApp source = new PWAssociatedApp();
        source.title = "Source App";
        source.idGooglePlay = "com.source.app";
        source.idAmazon = "B11111111111";

        PWAssociatedApp app = builder.of(source).build();

        Assert.assertEquals(app.title, source.title);
        Assert.assertEquals(app.idGooglePlay, source.idGooglePlay);
        Assert.assertEquals(app.idAmazon, source.idAmazon);
    }

    @Test
    public void testOfMethodWithNull() {
        builder.of(null);
        PWAssociatedApp app = builder.build();
        Assert.assertNotNull(app);
        Assert.assertNull(app.title);
        Assert.assertNull(app.idGooglePlay);
        Assert.assertNull(app.idAmazon);
    }

    @Test
    public void testOfMethodWithCloning() {
        PWAssociatedApp source = new PWAssociatedApp();
        source.title = "Original";
        source.idGooglePlay = "com.original.app";

        PWAssociatedApp app1 = builder.of(source).build();
        PWAssociatedApp app2 = PWAssociatedApp.builder().of(source).title("Modified").build();

        // Verify original is not modified
        Assert.assertEquals(source.title, "Original");
        Assert.assertEquals(app1.title, "Original");
        Assert.assertEquals(app2.title, "Modified");
    }

    @Test
    public void testBuilderReuse() {
        // First build
        PWAssociatedApp app1 = builder.title("App 1").build();
        Assert.assertEquals(app1.title, "App 1");

        // Modify and build again
        PWAssociatedApp app2 = builder.title("App 2").build();
        Assert.assertEquals(app2.title, "App 2");

        // Verify both apps share the same instance (builder pattern behavior)
        Assert.assertSame(app1, app2);
    }

    @Test
    public void testAllFieldsSet() {
        String title = "Complete App";
        String googlePlayId = "com.complete.app";
        String amazonId = "B99999999999";

        PWAssociatedApp app = builder
                .title(title)
                .idGooglePlay(googlePlayId)
                .idAmazon(amazonId)
                .build();

        Assert.assertEquals(app.title, title);
        Assert.assertEquals(app.idGooglePlay, googlePlayId);
        Assert.assertEquals(app.idAmazon, amazonId);
    }

    @Test
    public void testOverwriteValues() {
        PWAssociatedApp app = builder
                .title("First Title")
                .title("Second Title")
                .idGooglePlay("com.first.app")
                .idGooglePlay("com.second.app")
                .idAmazon("B11111111111")
                .idAmazon("B22222222222")
                .build();

        Assert.assertEquals(app.title, "Second Title");
        Assert.assertEquals(app.idGooglePlay, "com.second.app");
        Assert.assertEquals(app.idAmazon, "B22222222222");
    }
}
