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

import java.util.Properties;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PKRestServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(PKRestServer.class);
	private static final String SERVER_BIND_IP_KEY = "rest.bindIP";
	private static final String SERVER_BIND_PORT_KEY = "rest.bindPort";
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
					&& serverConfigurationProperties.containsKey(SERVER_BIND_PORT_KEY);
			if (allPropertiesSet) {
				LOGGER.debug("Checked properties. Everything we need is present");
				return;
			}
		}
		throw new RuntimeException("Server needs to be configured accordingly.");
	}

	private void createPKRestWebService() {
		restTrustedServerComponent = new Component();
		String bindIp = serverConfigurationProperties.getProperty(SERVER_BIND_IP_KEY);
		int bindPort = Integer.parseInt(serverConfigurationProperties.getProperty(SERVER_BIND_PORT_KEY));

		// TODO: introduce HTTPS
		restTrustedServer = new Server(Protocol.HTTP, bindIp, bindPort);
		restTrustedServerComponent.getServers().add(restTrustedServer);

		final Router router = new Router(restTrustedServerComponent.getContext().createChildContext());
		// TODO: register Resources if needed
		restTrustedServerComponent.getDefaultHost().attach("", router);

		PKDeviceResourceFactory pkDeviceResourceFactory = new PKDeviceResourceFactory(pkRestletServerResourceFactory);
		PKPassResourceFactory pkPassResourceFactory = new PKPassResourceFactory(pkRestletServerResourceFactory);
		PKLogResourceFactory pkLogResourceFactory = new PKLogResourceFactory(pkRestletServerResourceFactory);

		router.attach("/" + version + "/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}",
				pkDeviceResourceFactory);
		router.attach("/" + version + "/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}", pkDeviceResourceFactory);
		router.attach("/" + version + "/passes/{passTypeIdentifier}/{serialNumber}", pkPassResourceFactory);
		router.attach("/" + version + "/log", pkLogResourceFactory);
		LOGGER.debug("Created Restlet components");
	}

	public final void stop() throws Exception {
		LOGGER.info("####################### Stopping PassKitServer ###########################");
		restTrustedServerComponent.stop();
	}

}
