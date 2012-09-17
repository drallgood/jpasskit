package com.bitzeche.jpasskit.server;

import java.io.IOException;

import org.restlet.Request;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PKLogResource extends ServerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PKLogResource.class);

    /*
     * POST request to webServiceURL/version/log
     */
    @Post("json")
    public final Representation postLogMessage(final Representation entity) {
        Request request = getRequest();
        try {
            LOGGER.debug("postLogMessage: Log {}", entity.getText());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return handleLogRequest(entity);
    }

    @Delete("json")
    public final Representation deleteDeviceRegistrationRequest(final Representation entity) {
        return null;
    }

    public abstract Representation handleLogRequest(final Representation entity);
}
