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
import java.util.Date;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.signing.IPKSigningUtil;
import de.brendamour.jpasskit.signing.PKFileBasedSigningUtil;
import de.brendamour.jpasskit.signing.PKPassTemplateFolder;
import de.brendamour.jpasskit.signing.PKSigningInformation;

public abstract class PKPassResource extends ServerResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PKPassResource.class);
	private ObjectMapper jsonObjectMapper;
	private String pathToPassTemplate;
	private IPKSigningUtil signingUtil;

	public PKPassResource(final String pathToPassTemplate) {
		this.pathToPassTemplate = pathToPassTemplate;
		jsonObjectMapper = new ObjectMapper();
		jsonObjectMapper.setSerializationInclusion(Include.NON_NULL);
		signingUtil = new PKFileBasedSigningUtil(jsonObjectMapper.writer());
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

			if (getPKPassResponse != null && PKPass.builder(getPKPassResponse.getPass()).isValid()) {
				latestPassVersion = getPKPassResponse.getPass();
				PKSigningInformation pkSigningInformation = getSingingInformation();

				byte[] signedAndZippedPkPassArchive = signingUtil.createSignedAndZippedPkPassArchive(latestPassVersion,
						new PKPassTemplateFolder(pathToPassTemplate), pkSigningInformation);
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

	protected abstract PKSigningInformation getSingingInformation();
}
