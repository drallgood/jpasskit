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

public class PKNFCBuilderTest {

    private PKNFCBuilder builder;

    @BeforeMethod
    public void setUp() {
        builder = PKNFC.builder();
    }

    @Test
    public void testDefaultConstructor() {
        PKNFC nfc = builder.build();
        Assert.assertNotNull(nfc);
        Assert.assertNull(nfc.message);
        Assert.assertNull(nfc.encryptionPublicKey);
        Assert.assertNull(nfc.requiresAuthentication);
    }

    @Test
    public void testMessage() {
        String message = "NFC Test Message";
        builder.message(message);
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.message, message);
    }

    @Test
    public void testMessageWithNull() {
        builder.message(null);
        PKNFC nfc = builder.build();
        Assert.assertNull(nfc.message);
    }

    @Test
    public void testMessageWithEmptyString() {
        builder.message("");
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.message, "");
    }

    @Test
    public void testEncryptionPublicKey() {
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...";
        builder.encryptionPublicKey(publicKey);
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.encryptionPublicKey, publicKey);
    }

    @Test
    public void testEncryptionPublicKeyWithNull() {
        builder.encryptionPublicKey(null);
        PKNFC nfc = builder.build();
        Assert.assertNull(nfc.encryptionPublicKey);
    }

    @Test
    public void testRequiresAuthenticationTrue() {
        builder.requiresAuthentication(true);
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.requiresAuthentication, Boolean.TRUE);
    }

    @Test
    public void testRequiresAuthenticationFalse() {
        builder.requiresAuthentication(false);
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.requiresAuthentication, Boolean.FALSE);
    }

    @Test
    public void testRequiresAuthenticationWithNull() {
        builder.requiresAuthentication(null);
        PKNFC nfc = builder.build();
        Assert.assertNull(nfc.requiresAuthentication);
    }

    @Test
    public void testChainedSetters() {
        String message = "Tap to pay";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...";
        Boolean requiresAuth = true;

        PKNFC nfc = builder
                .message(message)
                .encryptionPublicKey(publicKey)
                .requiresAuthentication(requiresAuth)
                .build();

        Assert.assertEquals(nfc.message, message);
        Assert.assertEquals(nfc.encryptionPublicKey, publicKey);
        Assert.assertEquals(nfc.requiresAuthentication, requiresAuth);
    }

    @Test
    public void testOfMethod() {
        PKNFC source = new PKNFC();
        source.message = "Source Message";
        source.encryptionPublicKey = "SourcePublicKey";
        source.requiresAuthentication = false;

        PKNFC nfc = builder.of(source).build();

        Assert.assertEquals(nfc.message, source.message);
        Assert.assertEquals(nfc.encryptionPublicKey, source.encryptionPublicKey);
        Assert.assertEquals(nfc.requiresAuthentication, source.requiresAuthentication);
    }

    @Test
    public void testOfMethodWithNull() {
        builder.of(null);
        PKNFC nfc = builder.build();
        Assert.assertNotNull(nfc);
        Assert.assertNull(nfc.message);
        Assert.assertNull(nfc.encryptionPublicKey);
        Assert.assertNull(nfc.requiresAuthentication);
    }

    @Test
    public void testOfMethodWithCloning() {
        PKNFC source = new PKNFC();
        source.message = "Original Message";
        source.requiresAuthentication = true;

        PKNFC nfc1 = builder.of(source).build();
        PKNFC nfc2 = PKNFC.builder().of(source).message("Modified Message").build();

        // Verify original is not modified
        Assert.assertEquals(source.message, "Original Message");
        Assert.assertEquals(nfc1.message, "Original Message");
        Assert.assertEquals(nfc2.message, "Modified Message");
    }

    @Test
    public void testBuilderReuse() {
        // First build
        PKNFC nfc1 = builder.message("Message 1").build();
        Assert.assertEquals(nfc1.message, "Message 1");

        // Modify and build again
        PKNFC nfc2 = builder.message("Message 2").build();
        Assert.assertEquals(nfc2.message, "Message 2");

        // Verify both NFCs share the same instance (builder pattern behavior)
        Assert.assertSame(nfc1, nfc2);
    }

    @Test
    public void testAllFieldsSet() {
        String message = "Complete NFC Message";
        String publicKey = "CompletePublicKey123";
        Boolean requiresAuth = true;

        PKNFC nfc = builder
                .message(message)
                .encryptionPublicKey(publicKey)
                .requiresAuthentication(requiresAuth)
                .build();

        Assert.assertEquals(nfc.message, message);
        Assert.assertEquals(nfc.encryptionPublicKey, publicKey);
        Assert.assertEquals(nfc.requiresAuthentication, requiresAuth);
    }

    @Test
    public void testOverwriteValues() {
        PKNFC nfc = builder
                .message("First Message")
                .message("Second Message")
                .encryptionPublicKey("FirstKey")
                .encryptionPublicKey("SecondKey")
                .requiresAuthentication(true)
                .requiresAuthentication(false)
                .build();

        Assert.assertEquals(nfc.message, "Second Message");
        Assert.assertEquals(nfc.encryptionPublicKey, "SecondKey");
        Assert.assertEquals(nfc.requiresAuthentication, Boolean.FALSE);
    }

    @Test
    public void testLongMessage() {
        String longMessage = "This is a very long NFC message that might be used in some specific scenarios where detailed information needs to be transmitted via NFC technology.";
        builder.message(longMessage);
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.message, longMessage);
    }

    @Test
    public void testSpecialCharactersInMessage() {
        String specialMessage = "NFC Message with special chars: àáâãäåæçèéêë ñòóôõö ùúûüý";
        builder.message(specialMessage);
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.message, specialMessage);
    }

    @Test
    public void testComplexPublicKey() {
        String complexKey = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...\n-----END PUBLIC KEY-----";
        builder.encryptionPublicKey(complexKey);
        PKNFC nfc = builder.build();
        Assert.assertEquals(nfc.encryptionPublicKey, complexKey);
    }
}
