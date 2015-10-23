#jpasskit

jPasskit is an Java&trade; implementation of the Apple&trade; PassKit Web Service.

There are two separate projects:

1. jPasskit - Which contains the Pass objects and useful utilities. It is designed to be included in existing Webservices, thus not including any request handling.
2. jPasskit Server -  Which contains an additional REST Webservice, that can be included in existing Applications that do not use their own Webservice already. Note: This is still no standalone implementation. Things like storing passes and handling device (un)registrations are left open for the Application to implement.

**Current stable release:** 0.0.7

**Development Version:** 0.0.8-SNAPSHOT

**Support jPasskit by contributing to my [GitTip fund](https://www.gittip.com/drallgood/).**

## Installation

### Using Maven

	<dependency>
		<groupId>de.brendamour</groupId>
		<artifactId>jpasskit</artifactId>
	</dependency>

or:
	
	<dependency>
		<groupId>de.brendamour</groupId>
		<artifactId>jpasskit.server</artifactId>
	</dependency>


**The artifact is now available at Maven Central (Snapshots only)**
	
	
## Using jPasskit

Using jPasskit is pretty straight forward:

### Creating a Pass

The class PKPass is the toplevel class. It represents the pass.json file. Everything else can just be added like one would add it on the JSON side.

Example:

	PKPass pass = new PKPass();
	PKBarcode barcode = new PKBarcode();
	PKStoreCard storeCard = new PKStoreCard();
	List<PKField> primaryFields = new ArrayList<PKField>();
	
	PKField balanceField = new PKField();
	balanceField.setKey( "balance" );
	balanceField.setLabel( "balance" );
	balanceField.setValue( 20.0 );
	balanceField.setCurrencyCode( "EUR" );

	primaryFields.add( balanceField );

	barcode.setFormat( PKBarcodeFormat.PKBarcodeFormatQR );
	barcode.setMessage( "ABCDEFG" );
	barcode.setMessageEncoding( Charset.forName( "utf-8" ) );

	storeCard.setPrimaryFields( primaryFields );
	
	pass.setFormatVersion( 1 );
	pass.setPassTypeIdentifier( "pass.some.passTypeIdentifier" );
	pass.setSerialNumber( "000000001" );
	pass.setTeamIdentifier( "myTeamId" );
	pass.setBarcode( barcode );
	pass.setOrganizationName( "OrgName" );
	pass.setLogoText( "MyPass" );
	pass.setStoreCard( storeCard );
	pass.setDescription( "My PassBook" );
	pass.setBackgroundColorAsObject( Color.BLACK );
	pass.setForegroundColor( "rgb(255,255,255 )" );
	...


In Addition to the pass, there needs to be a template directory that contains the other resources (like images and translations). The content of this directory is defined in the PassKit Developer Documentation.

<img src="https://github.com/bitzeche/jpasskit/raw/master/passFolder.png">

### Signing and Zipping a Pass

The PKSigningUtil contains all necessary methods to:

1. Load the Pass Certificate
2. Load the Apple Worldwide Developer Relations CA (AppleWWDRCA)
3. Create the pass.json file
4. Hash all files in the Pass directory and create the Manifest file (manifest.json)
5. Sign the Manifest file
6. ZIP the finished Pass directory


Example to do it all in one step: 

	PKSigningInformation pkSigningInformation = PKSigningUtil.loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(
               keyStorePath, keyStorePassword, appleWWDRCA);
	byte[] passZipAsByteArray = PKSigningUtil.createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory, pkSigningInformation);
	...
	
## Using the jPasskit Server

The jPasskit Server doesn't provide a full fledged PassKit Web Service but merely the basics you need implement your own standalone server. Things like storing passes and registrations still need to be implemented according to your own needs (or added to an existing Application).

### Setup

The set up and start the Server you need two things:

1. Create a Java Property object containing at least the three keys 'rest.bindIP', 'rest.bindPort' and 'rest.ssl.enabled'.
    
	**Note:**
	For the Production mode, you'll have to enable SSL and provide the following 4 keys:
	- rest.ssl.keystore.path : The path to the keystore where the SSL certificate for this server is stored
	- rest.ssl.keystore.type : The type of this keystore (e.g. PKCS12 or JKS)
	- rest.ssl.keystore.password : The password to access the keystore
	- rest.ssl.key.password : The password to access the private key
	
	Apple requires all production passes to use SSL.
2. An implementation of IPKRestletServerResourceFactory.

The IPKRestletServerResourceFactory is used to create instances of three classes: PKDeviceResource. PKPassResource, PKLogResource. You'll need to subclass each of these and provide your own implementations. 

*PKDeviceResource* is used to register/unregister devices and to get the serialNumbers of changed passes.

*PKPassResource* is used to fetch the latest version of a pass.

*PKLogResource* is used for the log messages, that the devices send in case of an error.

Then you create the server instance:

	Properties serverConfigurationProperties = new Properties();
	serverConfigurationProperties.put("rest.bindIP", "::");
	serverConfigurationProperties.put("rest.bindPort", "8082");

	IPKRestletServerResourceFactory pkRestletServerResourceFactory = new MyOwnPKRestletServerResourceFactory();
	PKRestServer pkRestServer = new PKRestServer(serverConfigurationProperties, pkRestletServerResourceFactory);
	try {
		pkRestServer.start();
	} catch (Exception e) {
		e.printStackTrace();
	}
	
That's it. You webservice is running. Just point your passes to the URL where the server is running.
