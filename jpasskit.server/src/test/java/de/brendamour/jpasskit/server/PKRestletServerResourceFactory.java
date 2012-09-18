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

package de.brendamour.jpasskit.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.PKPushToken;
import de.brendamour.jpasskit.passes.PKStoreCard;
import de.brendamour.jpasskit.server.GetPKPassResponse;
import de.brendamour.jpasskit.server.IPKRestletServerResourceFactory;
import de.brendamour.jpasskit.server.PKAuthTokenNotValidException;
import de.brendamour.jpasskit.server.PKDeviceResource;
import de.brendamour.jpasskit.server.PKLogResource;
import de.brendamour.jpasskit.server.PKPassNotModifiedException;
import de.brendamour.jpasskit.server.PKPassResource;
import de.brendamour.jpasskit.server.PKSerialNumbersOfPassesForDeviceResponse;

public class PKRestletServerResourceFactory implements IPKRestletServerResourceFactory {

	protected static final String APPLE_WWDRCA_CERT_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/AppleWWDRCA.pem";
	protected static final String PKCS12_FILE_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/Certificates.p12";
	protected static final String PKCS12_FILE_PASSWORD = "cert";
	private X509CertificateObject signingCert;
	private PrivateKey signingPrivateKey;
	private X509CertificateObject appleWWDRCACert;
	private ObjectMapper jsonObjectMapper = new ObjectMapper();

	public PKDeviceResource getPKDeviceResource() {
		return new PKDeviceResource() {

			@Override
			protected Status handleRegisterDeviceRequest(final String deviceLibraryIdentifier, final String passTypeIdentifier,
					final String serialNumber, final String authString, final PKPushToken pushToken) throws PKAuthTokenNotValidException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected Status handleUnregisterDeviceRequest(final String deviceLibraryIdentifier, final String passTypeIdentifier,
					final String serialNumber, final ChallengeResponse authString) throws PKAuthTokenNotValidException {
				// TODO Auto-generated method stub
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
		try {
			loadKeysAndCerts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new PKPassResource() {

			@Override
			protected GetPKPassResponse handleGetLatestVersionOfPass(final String passTypeIdentifier, final String serialNumber,
					final String authString, final Date modifiedSince) throws PKAuthTokenNotValidException, PKPassNotModifiedException {
				PKPass pass = new PKPass();
				try {
					pass = jsonObjectMapper.readValue(new File("/Users/patrice/Downloads/passbook/Passes/bitzecheCoupons.raw/pass2.json"),
							PKPass.class);

					float newAmount = getNewRandomAmount();

					PKStoreCard storeCard = pass.getStoreCard();
					List<PKField> primaryFields = storeCard.getPrimaryFields();
					for (PKField field : primaryFields) {
						if ("balance".equals(field.getKey())) {
							field.setValue(newAmount);
							field.setChangeMessage("Amount changed to %@");
							break;
						}

					}
					List<PKField> headerFields = storeCard.getHeaderFields();
					for (PKField field : headerFields) {
						if ("balanceHeader".equals(field.getKey())) {
							field.setValue(newAmount);
							break;
						}

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GetPKPassResponse getPKPassResponse = new GetPKPassResponse(pass, new Date());

				return getPKPassResponse;
			}

			private float getNewRandomAmount() {
				Random random = new Random();
				float amount = random.nextInt(100) + random.nextFloat();
				BigDecimal bigDecimalForRounding = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN);
				return bigDecimalForRounding.floatValue();
			}

			@Override
			protected X509Certificate getSigningCert() {
				return signingCert;
			}

			@Override
			protected X509Certificate getAppleWWDRCACert() {
				return appleWWDRCACert;
			}

			@Override
			protected PrivateKey getSigningPrivateKey() {
				return signingPrivateKey;
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

	private void loadKeysAndCerts() throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException,
			NoSuchProviderException, UnrecoverableKeyException {
		Security.addProvider(new BouncyCastleProvider());

		KeyStore pkcs12KeyStore = readPKCS12File(PKCS12_FILE_PATH, PKCS12_FILE_PASSWORD);
		Enumeration<String> aliases = pkcs12KeyStore.aliases();
		while (aliases.hasMoreElements()) {
			String aliasName = aliases.nextElement();

			Key key = pkcs12KeyStore.getKey(aliasName, PKCS12_FILE_PASSWORD.toCharArray());
			if (key instanceof PrivateKey) {
				signingPrivateKey = (PrivateKey) key;
				Object cert = pkcs12KeyStore.getCertificate(aliasName);
				if (cert instanceof X509Certificate) {
					signingCert = (X509CertificateObject) cert;
					break;
				}
			}
		}

		appleWWDRCACert = (X509CertificateObject) readKeyPair(APPLE_WWDRCA_CERT_PATH);
	}

	private KeyStore readPKCS12File(final String file, final String password) throws IOException, NoSuchAlgorithmException,
			CertificateException, KeyStoreException, NoSuchProviderException {
		KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");

		keystore.load(new FileInputStream(file), password.toCharArray());
		return keystore;
	}

	private Object readKeyPair(final String file) throws IOException {
		FileReader fileReader = new FileReader(file);
		PEMReader r = new PEMReader(fileReader);
		try {
			return r.readObject();
		} catch (IOException ex) {
			throw new IOException("The key from '" + file + "' could not be decrypted", ex);
		} finally {
			r.close();
			fileReader.close();
		}
	}

}
