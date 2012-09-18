package com.bitzeche.jpasskit.apns;

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
