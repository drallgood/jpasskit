/**
 * Copyright (C) 2015 Patrice Brend'amour <p.brendamour@bitzeche.de>
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
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.DeliveryError;

public class PKSendPushNotificationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PKSendPushNotificationUtil.class);
    private static final String EMPTY_PUSH_JSON_STRING = "{}";
    private ApnsService service;

    public PKSendPushNotificationUtil(final String pathToP12, final String passwordForP12) throws FileNotFoundException {
        this(pathToP12, passwordForP12, 10);
    }

    public PKSendPushNotificationUtil(final String pathToP12, final String passwordForP12, final int poolSize) throws FileNotFoundException {

        InputStream certificateStream = getStreamOfP12File(pathToP12);
        service = APNS.newService().withCert(certificateStream, passwordForP12).withProductionDestination()
                .withDelegate(new ApnsLoggingDelegate()).asPool(poolSize).build();
    }

    private InputStream getStreamOfP12File(final String pathToP12) throws FileNotFoundException {
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

    public void sendPushNotification(final String pushtoken) {

        LOGGER.debug("Sending Push notification for key: {}", pushtoken);

        service.push(pushtoken, EMPTY_PUSH_JSON_STRING);
        LOGGER.debug("Send Push notification for key: {}", pushtoken);

    }

    public void sendMultiplePushNotifications(final List<String> pushtokens) {

        LOGGER.debug("Sending Push notification for keys: {}", pushtokens);
        service.push(pushtokens, EMPTY_PUSH_JSON_STRING);
        LOGGER.debug("Send Push notification for keys: {}", pushtokens);

    }

    public Map<String, Date> getInactiveDevices() {
        LOGGER.debug("Querying inactive devices");
        Map<String, Date> inactiveDevices = service.getInactiveDevices();
        LOGGER.debug("Inactive devices: {}", inactiveDevices);
        return inactiveDevices;
    }

    class ApnsLoggingDelegate implements ApnsDelegate {

        @Override
        public void messageSendFailed(final ApnsNotification message, final Throwable e) {
            LOGGER.debug("Message failed: {}", message, e);
        }

        @Override
        public void connectionClosed(final DeliveryError e, final int messageIdentifier) {
            LOGGER.debug("Connection closed: {}", messageIdentifier, e);

        }

        @Override
        public void cacheLengthExceeded(final int newCacheLength) {
            LOGGER.debug("CacheLengthExceeded: {}", newCacheLength);
        }

        @Override
        public void messageSent(final ApnsNotification message, final boolean resent) {
            LOGGER.debug("Message sent: {} (resent={})", message, resent);
        }

        @Override
        public void notificationsResent(final int resendCount) {
            LOGGER.debug("Messages resent: {}", resendCount);
        }

    }
}
