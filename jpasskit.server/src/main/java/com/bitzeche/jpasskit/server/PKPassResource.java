package com.bitzeche.jpasskit.server;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitzeche.jpasskit.PKPass;
import com.bitzeche.jpasskit.signing.PKSigningUtil;

public abstract class PKPassResource extends ServerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PKPassResource.class);
    private ObjectMapper jsonObjectMapper;

    public PKPassResource() {
        jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    /*
     * GET request to webServiceURL/version/passes/{passTypeIdentifier}/(serialNumber)
     */
    @Get("json")
    public final Representation getLatestVersionOfPass(final Representation entity) {
        Request request = getRequest();
        Map<String, Object> requestAttributes = request.getAttributes();
        String passTypeIdentifier = (String) requestAttributes.get("passTypeIdentifier");
        String serialNumber = (String) requestAttributes.get("serialNumber");
        String authString = request.getChallengeResponse().getRawValue();

        LOGGER.debug("getLatestVersionOfPass: passTypeIdentifier: {}", passTypeIdentifier);
        LOGGER.debug("getLatestVersionOfPass: serialNumber: {}", serialNumber);
        LOGGER.debug("getLatestVersionOfPass: authString: {}", authString);

        PKPass latestPassVersion = null;
        try {
            latestPassVersion = handleGetLatestVersionOfPass(passTypeIdentifier, serialNumber, authString);
            if (latestPassVersion != null) {

                PKSigningUtil.createSignedAndZippedPkPassArchive(latestPassVersion,
                        "/Users/patrice/Downloads/passbook/Passes/bitzecheCoupons.raw");
                String responseJSONString = jsonObjectMapper.writeValueAsString(latestPassVersion);
                LOGGER.debug(responseJSONString);
                return new StringRepresentation(responseJSONString, MediaType.APPLICATION_JSON);
            }
        } catch (PKAuthTokenNotValidException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error when parsing response to JSON:", e);
        }

        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        return null;

    }

    protected abstract PKPass handleGetLatestVersionOfPass(String passTypeIdentifier, String serialNumber, String authString)
            throws PKAuthTokenNotValidException;
}
