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

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.brendamour.jpasskit.PKPushToken;

public abstract class PKDeviceResource extends ServerResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PKDeviceResource.class);
	private ObjectMapper jsonObjectMapper;

	public PKDeviceResource() {
		jsonObjectMapper = new ObjectMapper();

	}

	/*
	 * GET request to webServiceURL/version/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}?passesUpdatedSince=tag
	 */
	@Get("json")
	public final Representation getSerialNumbersForPasses(final Representation entity) {
		Request request = getRequest();
		Map<String, Object> requestAttributes = request.getAttributes();
		String deviceLibraryIdentifier = (String) requestAttributes.get("deviceLibraryIdentifier");
		String passTypeIdentifier = (String) requestAttributes.get("passTypeIdentifier");
		String passesUpdatedSince = (String) requestAttributes.get("passesUpdatedSince");
		LOGGER.debug("getSerialNumbersForPasses - deviceLibraryIdentifier: {}", deviceLibraryIdentifier);
		LOGGER.debug("getSerialNumbersForPasses - passTypeIdentifier: {}", passTypeIdentifier);
		LOGGER.debug("getSerialNumbersForPasses - passesUpdatedSince: {}", passesUpdatedSince);
		PKSerialNumbersOfPassesForDeviceResponse serialNumbersOfPassesForDevice = getSerialNumberOfPassesForDevice(deviceLibraryIdentifier,
				passTypeIdentifier, passesUpdatedSince);
		if (serialNumbersOfPassesForDevice == null || serialNumbersOfPassesForDevice.getSerialNumbers() == null) {
			setStatusIntoResponse(Status.SUCCESS_NO_CONTENT, getResponse());
			return null;
		}
		String responseJSONString;
		try {
			responseJSONString = jsonObjectMapper.writeValueAsString(serialNumbersOfPassesForDevice);
			return new StringRepresentation(responseJSONString, MediaType.APPLICATION_JSON);
		} catch (Exception e) {
			LOGGER.error("Error when parsing response to JSON:", e);
		}
		setStatusIntoResponse(Status.SERVER_ERROR_INTERNAL, getResponse());
		return null;
	}

	/*
	 * POST request to webServiceURL/version/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}
	 */
	@Post("json")
	public final void registerDeviceRequest(final Representation entity) {
		Request request = getRequest();
		Map<String, Object> requestAttributes = request.getAttributes();
		String deviceLibraryIdentifier = (String) requestAttributes.get("deviceLibraryIdentifier");
		String passTypeIdentifier = (String) requestAttributes.get("passTypeIdentifier");
		String serialNumber = (String) requestAttributes.get("serialNumber");
		String authString = request.getChallengeResponse().getRawValue();
		Status responseStatus;
		try {
			String jsonPushToken = entity.getText();
			PKPushToken pkPushToken = jsonObjectMapper.readValue(jsonPushToken, PKPushToken.class);

			LOGGER.debug("registerDeviceRequest - deviceLibraryIdentifier: {}", deviceLibraryIdentifier);
			LOGGER.debug("registerDeviceRequest - passTypeIdentifier: {}", passTypeIdentifier);
			LOGGER.debug("registerDeviceRequest - serialNumber: {}", serialNumber);
			LOGGER.debug("registerDeviceRequest - authString: {}", authString);
			LOGGER.debug("registerDeviceRequest - jsonPushToken: {}", jsonPushToken);
			LOGGER.debug("registerDeviceRequest - pkPushToken: {}", pkPushToken);
			responseStatus = handleRegisterDeviceRequest(deviceLibraryIdentifier, passTypeIdentifier, serialNumber, authString, pkPushToken);
		} catch (PKAuthTokenNotValidException e) {
			responseStatus = Status.CLIENT_ERROR_UNAUTHORIZED;
		} catch (IOException e) {
			responseStatus = Status.SERVER_ERROR_INTERNAL;
		}
		setStatusIntoResponse(responseStatus, getResponse());
	}

	private void setStatusIntoResponse(final Status status, final Response response) {
		response.setStatus(status);
	}

	/*
	 * DELETE request to webServiceURL/version/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}
	 */
	@Delete("json")
	public final void deleteDeviceRegistrationRequest(final Representation entity) {
		Request request = getRequest();
		Map<String, Object> requestAttributes = request.getAttributes();
		String deviceLibraryIdentifier = (String) requestAttributes.get("deviceLibraryIdentifier");
		String passTypeIdentifier = (String) requestAttributes.get("passTypeIdentifier");
		String serialNumber = (String) requestAttributes.get("serialNumber");
		ChallengeResponse authString = request.getChallengeResponse();

		LOGGER.debug("deleteDeviceRegistrationRequest - deviceLibraryIdentifier: {}", deviceLibraryIdentifier);
		LOGGER.debug("deleteDeviceRegistrationRequest - passTypeIdentifier: {}", passTypeIdentifier);
		LOGGER.debug("deleteDeviceRegistrationRequest - serialNumber: {}", serialNumber);
		LOGGER.debug("deleteDeviceRegistrationRequest - authString: {}", authString);

		Status responseStatus;
		try {
			responseStatus = handleUnregisterDeviceRequest(deviceLibraryIdentifier, passTypeIdentifier, serialNumber, authString);
		} catch (PKAuthTokenNotValidException e) {
			responseStatus = Status.CLIENT_ERROR_UNAUTHORIZED;
		}
		setStatusIntoResponse(responseStatus, getResponse());
	}

	protected abstract Status handleRegisterDeviceRequest(String deviceLibraryIdentifier, String passTypeIdentifier, String serialNumber,
			String authString, PKPushToken pkPushToken) throws PKAuthTokenNotValidException;

	protected abstract Status handleUnregisterDeviceRequest(String deviceLibraryIdentifier, String passTypeIdentifier, String serialNumber,
			ChallengeResponse authString) throws PKAuthTokenNotValidException;

	protected abstract PKSerialNumbersOfPassesForDeviceResponse getSerialNumberOfPassesForDevice(String deviceLibraryIdentifier,
			String passTypeIdentifier, String passesUpdatedSince);

}
