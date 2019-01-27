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
package de.brendamour.jpasskit.apns;

import java.io.InputStream;

import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.server.AcceptAllPushNotificationHandlerFactory;
import com.turo.pushy.apns.server.MockApnsServer;
import com.turo.pushy.apns.server.MockApnsServerBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static de.brendamour.jpasskit.util.CertUtils.toInputStream;

public class PKSendPushNotificationUtilTest {

    private static final String keyStorePath = "passbook/jpasskittest.p12";
    private static final String keyStorePassword = "password";
    private static final String CA_CERTIFICATE_FILENAME = "/ca.pem";
    private static final String SERVER_CERTIFICATES_FILENAME = "/server-certs.pem";
    private static final String SERVER_KEY_FILENAME = "/server-key.pem";
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private PKSendPushNotificationUtil util;
    private MockApnsServer apnsServer;

    @BeforeClass
    public void prepareTest() throws Exception {
        util = new PKSendPushNotificationUtil(keyStorePath, keyStorePassword);
        try (InputStream certificateStream = toInputStream(keyStorePath)) {
            ApnsClient client = new ApnsClientBuilder().setApnsServer(HOST, PORT)
                    .setClientCredentials(certificateStream, keyStorePassword)
                    .setTrustedServerCertificateChain(getClass().getResourceAsStream(CA_CERTIFICATE_FILENAME))
                    .build();
            util.setClient(client);
        }
        apnsServer = new MockApnsServerBuilder()
                .setHandlerFactory(new AcceptAllPushNotificationHandlerFactory())
                .setServerCredentials(getClass().getResourceAsStream(SERVER_CERTIFICATES_FILENAME), getClass().getResourceAsStream(SERVER_KEY_FILENAME), null)
                .build();
        apnsServer.start(PORT).await();
    }

    @Test
    public void sendPushNotification()  throws Exception {
        final PushNotificationResponse<SimpleApnsPushNotification> response = util.sendPushNotificationAsync("ABC1234").get();
        Assert.assertTrue(response.isAccepted());
    }

    @AfterClass
    public void shutDownTest() throws Exception {
        util.close();
        apnsServer.shutdown();
    }
}
