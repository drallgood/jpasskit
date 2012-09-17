package com.bitzeche.jpasskit.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.bitzeche.jpasskit.PKBarcode;
import com.bitzeche.jpasskit.PKBarcodeFormat;
import com.bitzeche.jpasskit.PKField;
import com.bitzeche.jpasskit.PKPass;
import com.bitzeche.jpasskit.PKSerialNumbersOfPassesForDeviceResponse;
import com.bitzeche.jpasskit.passes.PKStoreCard;
import com.bitzeche.jpasskit.server.IPKRestletServerResourceFactory;
import com.bitzeche.jpasskit.server.PKAuthTokenNotValidException;
import com.bitzeche.jpasskit.server.PKDeviceResource;
import com.bitzeche.jpasskit.server.PKLogResource;
import com.bitzeche.jpasskit.server.PKPassResource;

public class PKRestletServerResourceFactory implements
		IPKRestletServerResourceFactory {

	protected static final String AppleWWDRCACert_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/AppleWWDRCA.pem";
	protected static final String KEY_FILE_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/key_pktest.pem";
	protected static final String CERT_FILE_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/cert_pktest.pem";
	private X509CertificateObject signingCert;
	private PrivateKey signingPrivateKey;
	private X509CertificateObject appleWWDRCACert;
	private ObjectMapper jsonObjectMapper = new ObjectMapper();

	public PKDeviceResource getPKDeviceResource() {
		return new PKDeviceResource() {

			@Override
			protected Status handleRegisterDeviceRequest(
					final String deviceLibraryIdentifier,
					final String passTypeIdentifier, final String serialNumber,
					final String authString)
					throws PKAuthTokenNotValidException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected Status handleUnregisterDeviceRequest(
					final String deviceLibraryIdentifier,
					final String passTypeIdentifier, final String serialNumber,
					final ChallengeResponse authString)
					throws PKAuthTokenNotValidException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected PKSerialNumbersOfPassesForDeviceResponse getSerialNumberOfPassesForDevice(
					final String deviceLibraryIdentifier,
					final String passTypeIdentifier,
					final String passesUpdatedSince) {
				PKSerialNumbersOfPassesForDeviceResponse serialNumbersOfPassesForDeviceResponse = new PKSerialNumbersOfPassesForDeviceResponse();
				serialNumbersOfPassesForDeviceResponse.setLastUpdated(""
						+ System.currentTimeMillis());
				String[] serialNumbers = new String[] { "p69f2J" };
				serialNumbersOfPassesForDeviceResponse
						.setSerialNumbers(serialNumbers);
				return serialNumbersOfPassesForDeviceResponse;
			}

		};
	}

	public PKPassResource getPKPassResource() {
		try {
			loadKeysAndCerts(CERT_FILE_PATH, KEY_FILE_PATH,
					AppleWWDRCACert_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new PKPassResource() {

			@Override
			protected PKPass handleGetLatestVersionOfPass(
					final String passTypeIdentifier, final String serialNumber,
					final String authString)
					throws PKAuthTokenNotValidException {
				PKPass pass = new PKPass();
				try {
					pass = jsonObjectMapper
							.readValue(
									new File(
											"/Users/patrice/Downloads/passbook/Passes/bitzecheCoupons.raw/pass2.json"),
									PKPass.class);
					PKStoreCard storeCard = pass.getStoreCard();
					List<PKField> primaryFields = storeCard.getPrimaryFields();
					for (PKField field : primaryFields) {
						if ("balance".equals(field.getKey())) {
							field.setValue(23.40);
							field.setChangeMessage("Amount changed to 23.40");
							break;
						}

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return pass;
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

	private void loadKeysAndCerts(String certFilePath, String keyFilePath,
			String appleWWDRCACertPath) throws IOException {
		Security.addProvider(new BouncyCastleProvider());
		signingCert = (X509CertificateObject) readKeyPair(certFilePath);
		KeyPair privateKeyPair = (KeyPair) readKeyPair(keyFilePath);
		signingPrivateKey = privateKeyPair.getPrivate();

		appleWWDRCACert = (X509CertificateObject) readKeyPair(appleWWDRCACertPath);
	}

	private Object readKeyPair(final String file) throws IOException {
		FileReader fileReader = new FileReader(file);
		PEMReader r = new PEMReader(fileReader);
		try {
			return r.readObject();
		} catch (IOException ex) {
			throw new IOException("The key from '" + file
					+ "' could not be decrypted", ex);
		} finally {
			r.close();
			fileReader.close();
		}
	}

}
