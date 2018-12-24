/**
 * Copyright (C) 2018 Patrice Brend'amour <patrice@brendamour.net>
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import com.turo.pushy.apns.util.concurrent.PushNotificationResponseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PKSendPushNotificationUtil implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PKSendPushNotificationUtil.class);
    private static final String EMPTY_PUSH_JSON_STRING = "{}";
    private static final int POOL_SIZE_DEFAULT = 10;

    private ApnsClient client;

    public PKSendPushNotificationUtil(final String pathToP12, final String passwordForP12) throws IOException {
        this(pathToP12, passwordForP12, POOL_SIZE_DEFAULT);
    }

    public PKSendPushNotificationUtil(final String pathToP12, final String passwordForP12, final int poolSize) throws IOException {
        try (InputStream certificateStream = getStreamOfP12File(pathToP12)) {
            client = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST, ApnsClientBuilder.DEFAULT_APNS_PORT)
                    .setClientCredentials(certificateStream, passwordForP12)
                    .setConcurrentConnections(poolSize)
                    .build();
        }
    }

    public void setClient(ApnsClient client) {
        this.client = client;
    }

    protected InputStream getStreamOfP12File(final String pathToP12) throws FileNotFoundException {
        File p12File = new File(pathToP12);
        if (!p12File.exists()) {
            // try loading it from the classpath
            URL localP12File = this.getClass().getClassLoader().getResource(pathToP12);
            if (localP12File == null) {
                throw new FileNotFoundException("File at " + pathToP12 + " not found");
            }
            p12File = new File(localP12File.getFile());
        }
        return new FileInputStream(p12File);
    }

    /**
     * @deprecated
     * @since 0.1.0
     */
    @Deprecated
    public void sendPushNotification(final String pushtoken) {
        try {
            
            PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> notificationFuture = sendPushNotificationAsync(pushtoken);
            notificationFuture.addListener(new ApnsLoggingDelegate());
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = notificationFuture.get();
    
            if (pushNotificationResponse.isAccepted()) {
                LOGGER.debug("Push notification accepted by APNs gateway.");
            } else {
                LOGGER.debug("Notification rejected by the APNs gateway: {}",
                        pushNotificationResponse.getRejectionReason());
        
                if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                    LOGGER.debug("\t…and the token is invalid as of {}",
                        pushNotificationResponse.getTokenInvalidationTimestamp());
                }
            }
        } catch (final ExecutionException e) {
            LOGGER.error("Failed to send push notification.", e);
        }  catch (final InterruptedException e) {
            LOGGER.error("Failed to send push notification.", e);
            Thread.currentThread().interrupt();
        }
    }

    public PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendPushNotificationAsync(final String pushtoken) {

        LOGGER.debug("Sending Push notification for key: {}", pushtoken);
        
        final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setAlertBody(EMPTY_PUSH_JSON_STRING);

        final String payload = payloadBuilder.buildWithDefaultMaximumLength();
        final String token = TokenUtil.sanitizeTokenString(pushtoken);

        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, "com.example.myApp", payload);
        LOGGER.debug("Send Push notification for key: {}", pushtoken);
        return client.sendNotification(pushNotification);
    }

    /**
     * @deprecated
     * @since 0.1.0
     */
    @Deprecated
    public void sendMultiplePushNotifications(final List<String> pushtokens) {

        LOGGER.debug("Sending Push notification for keys: {}", pushtokens);
        for (String token : pushtokens) {
            sendPushNotification(token);  
        }
    }

    @Override
    public void close() throws InterruptedException {
        if (this.client != null) {
            this.client.close().await();
        }
    }

    class ApnsLoggingDelegate implements PushNotificationResponseListener<SimpleApnsPushNotification> {

            @Override
            public void operationComplete(final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> future) throws Exception {
            // When using a listener, callers should check for a failure to send a
            // notification by checking whether the future itself was successful
            // since an exception will not be thrown.
            if (future.isSuccess()) {
                LOGGER.debug("Successfully sent");
            } else {
                // Something went wrong when trying to send the notification to the
                // APNs gateway. We can find the exception that caused the failure
                // by getting future.cause().
                LOGGER.error("Error sending push notification",future.cause());
            }
        }
    }
}
