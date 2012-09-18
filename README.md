jpasskit
========

jPasskit is an Java&trade; implementation of the Appe&trade; PassKit Web Service.

There are two separate projects:
1. jPasskit - Which contains the Pass objects and useful utilities. It is designed to be included in existing Webservices, thus not including any request handling.
2. jPasskit.server -  Which contains an additional REST Webservice, that can be included in existing Applications that do not use their own Webservice already. Note: This is still no standalone implementation. Things like storing passes and handling device (un)registrations are left open for the Application to implement.

# Installation
Using Maven:
	<dependency>
		<groupId>de.brendamour</groupId>
		<artifactId>jpasskit</artifactId>
	</dependency>
	<dependency>
		<groupId>de.brendamour</groupId>
		<artifactId>jpasskit.server</artifactId>
	</dependency>
	
# Using jPasskit