package com.bitzeche.jpasskit.server;

import java.util.Properties;

import com.bitzeche.jpasskit.server.PKRestletServerResourceFactory;
import com.bitzeche.jpasskit.server.IPKRestletServerResourceFactory;
import com.bitzeche.jpasskit.server.PKRestServer;

public class ServerActivatorTest {

	public static void main(final String[] args) {

		Properties serverConfigurationProperties = new Properties();
		serverConfigurationProperties.put("rest.bindIP", "::");
		serverConfigurationProperties.put("rest.bindPort", "8082");

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
