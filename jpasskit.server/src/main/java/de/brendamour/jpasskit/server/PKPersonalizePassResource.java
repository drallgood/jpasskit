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
package de.brendamour.jpasskit.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.brendamour.jpasskit.signing.PKInMemorySigningUtil;
import de.brendamour.jpasskit.signing.PKSigningException;
import de.brendamour.jpasskit.signing.PKSigningInformation;

public abstract class PKPersonalizePassResource extends ServerResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PKPersonalizePassResource.class);
	private ObjectMapper jsonObjectMapper;
	private PKInMemorySigningUtil signingUtil;

	public PKPersonalizePassResource() {
		jsonObjectMapper = new ObjectMapper();
		signingUtil = new PKInMemorySigningUtil();
	}

	/*
	 * GET request to webServiceURL/version/passes/{passTypeIdentifier}/(serialNumber)/personalize
	 */
	@Post("json")
	public final Representation signUpUser(final Representation entity) {
		Request request = getRequest();
		Map<String, Object> requestAttributes = request.getAttributes();
		String passTypeIdentifier = (String) requestAttributes.get("passTypeIdentifier");
		String serialNumber = (String) requestAttributes.get("serialNumber");
		String authString = request.getChallengeResponse().getRawValue();

		LOGGER.debug("signUpUser: passTypeIdentifier: {}", passTypeIdentifier);
		LOGGER.debug("signUpUser: serialNumber: {}", serialNumber);
		LOGGER.debug("signUpUser - authString: {}", authString);

		Status responseStatus;
		Response response = getResponse();
		try {
			String payload = entity.getText();
			PKPersonalizePassPayload personalizePayload = jsonObjectMapper.readValue(payload, PKPersonalizePassPayload.class);

			LOGGER.debug("signUpUser - personalizePayload: {}", personalizePayload);
			responseStatus = handleSignUpUserRequest(passTypeIdentifier, serialNumber, authString, personalizePayload);
			if (responseStatus == Status.SUCCESS_OK || responseStatus == Status.SUCCESS_CREATED) {
				byte[] signedToken = signingUtil.signManifestFile(personalizePayload.getPersonalizationToken().getBytes(),
						getSingingInformation());
				InputRepresentation inputRepresentation = new InputRepresentation(new ByteArrayInputStream(signedToken));
				inputRepresentation.setMediaType(MediaType.APPLICATION_OCTET_STREAM);
				return inputRepresentation;
			}
		} catch (PKAuthTokenNotValidException e) {
			LOGGER.error("Error when processing signup request",e);
			responseStatus = Status.CLIENT_ERROR_UNAUTHORIZED;
		} catch (IOException | PKSigningException e) {
			LOGGER.error("Error when processing signup request",e);
			responseStatus = Status.SERVER_ERROR_INTERNAL;
		}
		response.setStatus(responseStatus);
		return null;

	}

	protected abstract Status handleSignUpUserRequest(String passTypeIdentifier, String serialNumber, String authString,
			PKPersonalizePassPayload personalizePayload) throws PKAuthTokenNotValidException;

	protected abstract PKSigningInformation getSingingInformation();
}
