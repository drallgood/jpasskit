#jpasskit

jPasskit is an Java&trade; implementation of the Appe&trade; PassKit Web Service.

There are two separate projects:

1. jPasskit - Which contains the Pass objects and useful utilities. It is designed to be included in existing Webservices, thus not including any request handling.
2. jPasskit.server -  Which contains an additional REST Webservice, that can be included in existing Applications that do not use their own Webservice already. Note: This is still no standalone implementation. Things like storing passes and handling device (un)registrations are left open for the Application to implement.

## Installation

Using Maven:

	<dependency>
		<groupId>de.brendamour</groupId>
		<artifactId>jpasskit</artifactId>
	</dependency>
	
	<dependency>
		<groupId>de.brendamour</groupId>
		<artifactId>jpasskit.server</artifactId>
	</dependency>
	
## Using jPasskit

Using jPasskit is pretty straight forward:

The class PKPass is the toplevel class. It represents the pass.json file. Everything else can just be added like one would add it on the JSON side.

Example:
	PKPass pass = new PKPass();
	PKStoreCard storeCard = new PKStoreCard();
	
	pass.setPassTypeIdentifier("pass.some.passTypeIdentifier");
	pass.setStoreCard(storeCard);
	...


In Addition to the pass, there needs to be a template directory that contains the other resources (like images and translations). The content of this directory is defined in the PassKit Developer Documentation.
<img src="https://github.com/bitzeche/jpasskit/blob/master/passFolder.png">

Using the PKSigningUtil method createSignedAndZippedPkPassArchive (which takes the PKPass, the template directory, the certificate/private key and the Apple intermediate certificate as parameters), you can create the finished Pass archive.

