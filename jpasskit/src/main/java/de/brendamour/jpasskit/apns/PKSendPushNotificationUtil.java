/**
 * Copyright (C) 2012 Patrice Brend'amour <p.brendamour@bitzeche.de>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

public class PKSendPushNotificationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PKSendPushNotificationUtil.class);
    private final String pathToP12;
    private final String passwordForP12;

    public PKSendPushNotificationUtil(final String pathToP12, final String passwordForP12) {
        this.pathToP12 = pathToP12;
        this.passwordForP12 = passwordForP12;
    }

    public void execute(final String pushtoken) {
        String emptyJSONString = "{}";

        LOGGER.debug("Sending Push notification for key: {}", pushtoken);
        ApnsService service = APNS.newService().withCert(pathToP12, passwordForP12).withProductionDestination().build();

        service.push(pushtoken, emptyJSONString);
        LOGGER.debug("Send Push notification for key: {}", pushtoken);
        
    }

}
