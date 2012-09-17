package com.bitzeche.jpasskit.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.bitzeche.jpasskit.PKField;
import com.bitzeche.jpasskit.PKPass;
import com.bitzeche.jpasskit.PKPushToken;
import com.bitzeche.jpasskit.passes.PKStoreCard;

public class PKRestletServerResourceFactory implements IPKRestletServerResourceFactory {

	protected static final String APPLE_WWDRCA_CERT_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/AppleWWDRCA.pem";
	protected static final String KEY_FILE_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/key_pktest.pem";
	protected static final String CERT_FILE_PATH = "/Users/patrice/Documents/bitzeche/Projects/passkit/cert_pktest.pem";
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
			loadKeysAndCerts(CERT_FILE_PATH, KEY_FILE_PATH, APPLE_WWDRCA_CERT_PATH);
		} catch (IOException e) {
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
							field.setChangeMessage("Amount changed to %@");
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

	private void loadKeysAndCerts(final String certFilePath, final String keyFilePath, final String appleWWDRCACertPath) throws IOException {
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
			throw new IOException("The key from '" + file + "' could not be decrypted", ex);
		} finally {
			r.close();
			fileReader.close();
		}
	}

}
