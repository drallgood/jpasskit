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
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import de.brendamour.jpasskit.util.Assert;
import de.brendamour.jpasskit.util.CertUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class PKSendPushNotificationUtil implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String EMPTY_PUSH_JSON_STRING = "{}";
    private static final int POOL_SIZE_DEFAULT = 10;

    private ApnsClient client;
    private final Set<String> topics;

    public PKSendPushNotificationUtil(String keyStorePath, char[] keyStorePassword) throws IOException {
        this(keyStorePath, keyStorePassword, POOL_SIZE_DEFAULT);
    }

    public PKSendPushNotificationUtil(String keyStorePath, char[] keyStorePassword, int poolSize) throws IOException {
        try (InputStream keyStoreInputStream = CertUtils.toInputStream(keyStorePath)) {
            KeyStore keyStore = CertUtils.toKeyStore(keyStoreInputStream, keyStorePassword);
            Pair<PrivateKey, X509Certificate> certificate = CertUtils.extractCertificateWithKey(keyStore, keyStorePassword);
            this.client = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST, ApnsClientBuilder.DEFAULT_APNS_PORT)
                    .setClientCredentials(certificate.getRight(), certificate.getLeft(), String.valueOf(keyStorePassword))
                    .setConcurrentConnections(poolSize)
                    .build();
            this.topics = CertUtils.extractApnsTopics(certificate.getRight());
        } catch (CertificateException ex) {
            throw new IOException("Failed to load keystore from " + keyStorePath);
        }
    }

    public void setClient(ApnsClient client) {
        this.client = client;
    }

    public PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendPushNotificationAsync(final String pushtoken) {

        LOGGER.debug("Sending Push notification for key: {}", pushtoken);

        final ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
        payloadBuilder.setAlertBody(EMPTY_PUSH_JSON_STRING);

        final String payload = payloadBuilder.build();
        final String token = TokenUtil.sanitizeTokenString(pushtoken);
        Assert.state(!this.topics.isEmpty(), "APNS topic is required for sending a push notification");
        String topic = null;
        if (!this.topics.isEmpty()) {
            topic = this.topics.iterator().next();
            if (this.topics.size() > 1) {
                LOGGER.warn("Multiple APNS topics detected, using {} (first value out of {} available) for sending a push notification", topic, this.topics.size());
            }
        }
        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, topic, payload);
        LOGGER.debug("Send Push notification for key: {}", pushtoken);
        return client.sendNotification(pushNotification);
    }

    @Override
    public void close() throws InterruptedException, ExecutionException {
        if (this.client != null) {
            this.client.close().get();
        }
    }
}
