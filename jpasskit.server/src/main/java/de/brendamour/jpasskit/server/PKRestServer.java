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

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PKRestServer {
	public static final String SERVER_BIND_IP_KEY = "rest.bindIP";
	public static final String SERVER_BIND_PORT_KEY = "rest.bindPort";
	public static final String SERVER_BIND_SSL_ENABLED_KEY = "rest.ssl.enabled";
	public static final String SERVER_BIND_SSL_KEYSTORE_PATH_KEY = "rest.ssl.keystore.path";
	public static final String SERVER_BIND_SSL_KEYSTORE_TYPE_KEY = "rest.ssl.keystore.type";
	public static final String SERVER_BIND_SSL_KEYSTORE_PASSWORD_KEY = "rest.ssl.keystore.password";
	public static final String SERVER_BIND_SSL_KEY_PASSWORD_KEY = "rest.ssl.key.password";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PKRestServer.class);
	private Properties serverConfigurationProperties;
	private Component restTrustedServerComponent;
	private Server restTrustedServer;
	private final IPKRestletServerResourceFactory pkRestletServerResourceFactory;
	private String version = "v1";

	public PKRestServer(final Properties serverConfigurationProperties, final IPKRestletServerResourceFactory pkRestletServerResourceFactory) {
		this.serverConfigurationProperties = serverConfigurationProperties;
		this.pkRestletServerResourceFactory = pkRestletServerResourceFactory;
	}

	public final void start() throws Exception {
		LOGGER.info("####################### Starting PassKitServer ###########################");

		checkConfigurationProperties();
		createPKRestWebService();

		restTrustedServerComponent.start();
	}

	private void checkConfigurationProperties() {
		if (serverConfigurationProperties != null) {
			boolean allPropertiesSet = serverConfigurationProperties.containsKey(SERVER_BIND_IP_KEY)
					&& serverConfigurationProperties.containsKey(SERVER_BIND_PORT_KEY)
					&& serverConfigurationProperties.containsKey(SERVER_BIND_SSL_ENABLED_KEY);
			if (allPropertiesSet) {
				LOGGER.debug("Checked properties. Everything we need is present");
				return;
			}
		}
		throw new PKServerConfigurationException("Server needs to be configured accordingly.");
	}

	private void createPKRestWebService() {
		restTrustedServerComponent = new Component();

		String bindIp = serverConfigurationProperties.getProperty(SERVER_BIND_IP_KEY);

		int bindPort = Integer.parseInt(serverConfigurationProperties.getProperty(SERVER_BIND_PORT_KEY));
		boolean useSSL = Boolean.parseBoolean(serverConfigurationProperties.getProperty(SERVER_BIND_SSL_ENABLED_KEY));

		Protocol httpProtocol = Protocol.HTTP;
		if (useSSL) {
			httpProtocol = Protocol.HTTPS;
		}

		restTrustedServer = new Server(httpProtocol, bindIp, bindPort);
		restTrustedServerComponent.getServers().add(restTrustedServer);

		if (useSSL) {
			setupSSL();
		}

		final Router router = new Router(restTrustedServerComponent.getContext().createChildContext());
		restTrustedServerComponent.getDefaultHost().attach("", router);

		PKDeviceResourceFactory pkDeviceResourceFactory = new PKDeviceResourceFactory(pkRestletServerResourceFactory);
		PKPassResourceFactory pkPassResourceFactory = new PKPassResourceFactory(pkRestletServerResourceFactory);
		PKPersonalizePassResourceFactory pkPersonalizePassResourceFactory = new PKPersonalizePassResourceFactory(pkRestletServerResourceFactory);
		PKLogResourceFactory pkLogResourceFactory = new PKLogResourceFactory(pkRestletServerResourceFactory);

		router.attach("/" + version + "/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}",
				pkDeviceResourceFactory);
		router.attach("/" + version + "/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}", pkDeviceResourceFactory);
		router.attach("/" + version + "/passes/{passTypeIdentifier}/{serialNumber}", pkPassResourceFactory);
		router.attach("/" + version + "/passes/{passTypeIdentifier}/{serialNumber}/personalize", pkPersonalizePassResourceFactory);
		router.attach("/" + version + "/log", pkLogResourceFactory);
		LOGGER.debug("Created Restlet components");
	}

	private void setupSSL() {
		LOGGER.info("Enabling SSL");

		String keystorePath = serverConfigurationProperties.getProperty(SERVER_BIND_SSL_KEYSTORE_PATH_KEY);
		String keystoreType = serverConfigurationProperties.getProperty(SERVER_BIND_SSL_KEYSTORE_TYPE_KEY);
		String keystorePassword = serverConfigurationProperties.getProperty(SERVER_BIND_SSL_KEYSTORE_PASSWORD_KEY);
		String keyPassword = serverConfigurationProperties.getProperty(SERVER_BIND_SSL_KEY_PASSWORD_KEY);

		if (StringUtils.isEmpty(keystorePath) || StringUtils.isEmpty(keystoreType)) {
			throw new PKServerConfigurationException("SSL is enabled but not set up correct. We need at least a keystore path and -type");
		}

		Series<Parameter> parameters = restTrustedServer.getContext().getParameters();
		parameters.add("sslContextFactory", "org.restlet.engine.ssl.DefaultSslContextFactory");
		parameters.add("keystorePath", keystorePath);
		parameters.add("keystorePassword", keystorePassword);
		parameters.add("keyPassword", keyPassword);
		parameters.add("keystoreType", keystoreType);
	}

	public final void stop() throws Exception {
		LOGGER.info("####################### Stopping PassKitServer ###########################");
		restTrustedServerComponent.stop();
	}

}
