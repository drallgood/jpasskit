package com.bitzeche.jpasskit.server;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bitzeche.jpasskit.PKPass;
import com.bitzeche.jpasskit.signing.PKSigningUtil;
import com.google.common.net.HttpHeaders;

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
		Date modifiedSince = request.getConditions().getModifiedSince();

		LOGGER.debug("getLatestVersionOfPass: passTypeIdentifier: {}", passTypeIdentifier);
		LOGGER.debug("getLatestVersionOfPass: serialNumber: {}", serialNumber);
		LOGGER.debug("getLatestVersionOfPass: authString: {}", authString);
		LOGGER.debug("getLatestVersionOfPass: modifiedSince: {}", modifiedSince);

		PKPass latestPassVersion = null;
		try {
			GetPKPassResponse getPKPassResponse = handleGetLatestVersionOfPass(passTypeIdentifier, serialNumber, authString, modifiedSince);

			if (getPKPassResponse != null && getPKPassResponse.getPass().isValid()) {
				latestPassVersion = getPKPassResponse.getPass();

				byte[] signedAndZippedPkPassArchive = PKSigningUtil.createSignedAndZippedPkPassArchive(latestPassVersion,
						"/Users/patrice/Downloads/passbook/Passes/bitzecheCoupons.raw", getSigningCert(), getSigningPrivateKey(),
						getAppleWWDRCACert());
				String responseJSONString = jsonObjectMapper.writeValueAsString(latestPassVersion);
				LOGGER.debug(responseJSONString);

				InputRepresentation inputRepresentation = new InputRepresentation(new ByteArrayInputStream(signedAndZippedPkPassArchive));
				inputRepresentation.setModificationDate(getPKPassResponse.getLastUpdated());
				return inputRepresentation;
			}
			LOGGER.error("Pass {} is not valid", latestPassVersion);
		} catch (PKAuthTokenNotValidException e) {
			getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		} catch (PKPassNotModifiedException e) {
			getResponse().setStatus(Status.REDIRECTION_NOT_MODIFIED);
			return null;
		} catch (Exception e) {
			LOGGER.error("Error when parsing response to JSON:", e);
		}

		getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return null;

	}

	protected abstract GetPKPassResponse handleGetLatestVersionOfPass(String passTypeIdentifier, String serialNumber, String authString,
			Date modifiedSince) throws PKAuthTokenNotValidException, PKPassNotModifiedException;

	protected abstract X509Certificate getSigningCert();

	protected abstract X509Certificate getAppleWWDRCACert();

	protected abstract PrivateKey getSigningPrivateKey();
}
