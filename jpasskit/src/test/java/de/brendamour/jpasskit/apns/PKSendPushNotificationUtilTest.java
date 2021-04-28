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
package de.brendamour.jpasskit.apns;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.server.AcceptAllPushNotificationHandlerFactory;
import com.eatthepath.pushy.apns.server.MockApnsServer;
import com.eatthepath.pushy.apns.server.MockApnsServerBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import org.assertj.core.api.ThrowableAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;

import static de.brendamour.jpasskit.util.CertUtils.toInputStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PKSendPushNotificationUtilTest {

    private static final String keyStorePathNoTopics = "passbook/jpasskittest.p12";
    private static final char[] keyStorePasswordNoTopics = "password".toCharArray();
    private static final String keyStorePath = "passbook/expired_cert.p12";
    private static final char[] keyStorePassword = "cert".toCharArray();
    private static final String CA_CERTIFICATE_FILENAME = "/ca.pem";
    private static final String SERVER_CERTIFICATES_FILENAME = "/server-certs.pem";
    private static final String SERVER_KEY_FILENAME = "/server-key.pem";
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private PKSendPushNotificationUtil badCertPushUtil;
    private PKSendPushNotificationUtil mockPushUtil;
    private MockApnsServer apnsServer;

    @BeforeClass
    public void prepareTest() throws Exception {
        badCertPushUtil = new PKSendPushNotificationUtil(keyStorePathNoTopics, keyStorePasswordNoTopics);
        mockPushUtil = new PKSendPushNotificationUtil(keyStorePath, keyStorePassword);
        try (InputStream certificateStream = toInputStream(keyStorePath)) {
            ApnsClient client = new ApnsClientBuilder().setApnsServer(HOST, PORT)
                    .setClientCredentials(certificateStream, String.valueOf(keyStorePassword))
                    .setTrustedServerCertificateChain(getClass().getResourceAsStream(CA_CERTIFICATE_FILENAME))
                    .build();
            mockPushUtil.setClient(client);
        }
        apnsServer = new MockApnsServerBuilder()
                .setHandlerFactory(new AcceptAllPushNotificationHandlerFactory())
                .setServerCredentials(getClass().getResourceAsStream(SERVER_CERTIFICATES_FILENAME), getClass().getResourceAsStream(SERVER_KEY_FILENAME), null)
                .build();
        apnsServer.start(PORT).get();
    }

    @Test
    public void sendPushNotificationWithMockAPNS() throws Exception {
        final PushNotificationResponse<SimpleApnsPushNotification> response = mockPushUtil.sendPushNotificationAsync("ABC1234").get();
        assertThat(response.isAccepted()).isTrue();
    }

    @Test
    public void sendPushNotificationWithBadCertificate() {
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Exception {
                badCertPushUtil.sendPushNotificationAsync("ABC1234").get();
            }
        }).isInstanceOf(IllegalStateException.class).hasMessage("APNS topic is required for sending a push notification");
    }

    @AfterClass
    public void shutDownTest() throws Exception {
        badCertPushUtil.close();
        mockPushUtil.close();
        apnsServer.shutdown();
    }
}
