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


import de.brendamour.jpasskit.server.IPKRestletServerResourceFactory;
import de.brendamour.jpasskit.server.PKRestServer;
import de.brendamour.jpasskit.server.PKRestletServerResourceFactory;

public class ServerActivatorTest {

	public static void main(final String[] args) {

		Properties serverConfigurationProperties = new Properties();
		serverConfigurationProperties.put(PKRestServer.SERVER_BIND_IP_KEY, "::");
		serverConfigurationProperties.put(PKRestServer.SERVER_BIND_PORT_KEY, "8083");
		serverConfigurationProperties.put(PKRestServer.SERVER_BIND_SSL_ENABLED_KEY, "true");
		serverConfigurationProperties.put(PKRestServer.SERVER_BIND_SSL_KEYSTORE_PATH_KEY, "src/test/resources/serverX.jks");
		serverConfigurationProperties.put(PKRestServer.SERVER_BIND_SSL_KEYSTORE_PASSWORD_KEY, "password");
		serverConfigurationProperties.put(PKRestServer.SERVER_BIND_SSL_KEY_PASSWORD_KEY, "password");
		serverConfigurationProperties.put(PKRestServer.SERVER_BIND_SSL_KEYSTORE_TYPE_KEY, "JKS");
		

		IPKRestletServerResourceFactory pkRestletServerResourceFactory = new PKRestletServerResourceFactory();
		PKRestServer pkRestServer = new PKRestServer(serverConfigurationProperties, pkRestletServerResourceFactory);
		try {
			pkRestServer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
