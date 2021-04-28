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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.brendamour.jpasskit.PKFieldBuilder;
import de.brendamour.jpasskit.PKPassBuilder;
import de.brendamour.jpasskit.passes.PKGenericPassBuilder;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.PKPushToken;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import de.brendamour.jpasskit.signing.PKSigningInformationUtil;

public class PKRestletServerResourceFactory implements IPKRestletServerResourceFactory {

	protected static final String APPLE_WWDRCA_CERT_PATH = "passkit/AppleWWDRCA.pem";
	protected static final String PKCS12_FILE_PATH = "passkit/Certificates.p12";
	protected static final String PKCS12_FILE_PASSWORD = "cert";
	private ObjectMapper jsonObjectMapper = new ObjectMapper();

	public PKDeviceResource getPKDeviceResource() {
		return new PKDeviceResource() {

			@Override
			protected Status handleRegisterDeviceRequest(final String deviceLibraryIdentifier, final String passTypeIdentifier,
					final String serialNumber, final String authString, final PKPushToken pushToken) {
				return null;
			}

			@Override
			protected Status handleUnregisterDeviceRequest(final String deviceLibraryIdentifier, final String passTypeIdentifier,
					final String serialNumber, final ChallengeResponse authString) {
				return null;
			}

			@Override
			protected PKSerialNumbersOfPassesForDeviceResponse getSerialNumberOfPassesForDevice(final String deviceLibraryIdentifier,
					final String passTypeIdentifier, final String passesUpdatedSince) {
				PKSerialNumbersOfPassesForDeviceResponse serialNumbersOfPassesForDeviceResponse = new PKSerialNumbersOfPassesForDeviceResponse();
				serialNumbersOfPassesForDeviceResponse.setLastUpdated("" + System.currentTimeMillis());
				String[] serialNumbers = new String[] { "p69f2J" };
				serialNumbersOfPassesForDeviceResponse.setSerialNumbers(serialNumbers);
				return serialNumbersOfPassesForDeviceResponse;
			}

		};
	}

	public PKPassResource getPKPassResource() {
		return new PKPassResource("passes/coupons.raw") {

			@Override
			protected GetPKPassResponse handleGetLatestVersionOfPass(final String passTypeIdentifier, final String serialNumber, final String authString, final Date modifiedSince) {
				PKPassBuilder builder = PKPass.builder();
				try {
					PKPass pass = jsonObjectMapper.readValue(new File("passes/coupons.raw/pass2.json"), PKPass.class);

					float newAmount = getNewRandomAmount();
					builder.of(pass);
					PKGenericPassBuilder storeCard = builder.getPassBuilder();
					List<PKFieldBuilder> primaryFields = storeCard.getPrimaryFieldBuilders();
					for (PKFieldBuilder field : primaryFields) {
						if ("balance".equals(field.build().getKey())) {
							field.value(newAmount);
							field.changeMessage("Amount changed to %@");
							break;
						}

					}
					List<PKFieldBuilder> headerFields = storeCard.getHeaderFieldBuilders();
					for (PKFieldBuilder field : headerFields) {
						if ("balanceHeader".equals(field.build().getKey())) {
							field.value(newAmount);
							break;
						}

					}

				} catch (IOException e) {
					throw new IllegalStateException("Failed to read pass from JSON file", e);
				}
				GetPKPassResponse getPKPassResponse = new GetPKPassResponse(builder.build(), new Date());

				return getPKPassResponse;
			}

			private float getNewRandomAmount() {
				Random random = new Random();
				float amount = random.nextInt(100) + random.nextFloat();
				BigDecimal bigDecimalForRounding = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN);
				return bigDecimalForRounding.floatValue();
			}

			@Override
			protected PKSigningInformation getSingingInformation() {
				try {
					return new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(PKCS12_FILE_PATH,
							PKCS12_FILE_PASSWORD, APPLE_WWDRCA_CERT_PATH);
				} catch (Exception e) {
					throw new PKServerConfigurationException(e);
				}
			}

		};
	}

	public PKLogResource getPKLogResource() {
		return new PKLogResource() {

			@Override
			public Representation handleLogRequest(final Representation entity) {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

	@Override
	public PKPersonalizePassResource getPKPersonalizePassResource() {
		return new PKPersonalizePassResource() {

			@Override
			protected Status handleSignUpUserRequest(String passTypeIdentifier, String serialNumber, String authString, PKPersonalizePassPayload personalizePayload) {
				return Status.SUCCESS_CREATED;
			}

			@Override
			protected PKSigningInformation getSingingInformation() {
				try {
					return new PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(PKCS12_FILE_PATH,
							PKCS12_FILE_PASSWORD, APPLE_WWDRCA_CERT_PATH);
				} catch (Exception e) {
					throw new PKServerConfigurationException(e);
				}
			}
		};
	}
}
