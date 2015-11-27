# jpasskit

jPasskit is an Java&trade; implementation of the Apple&trade; PassKit Web Service.

There are two separate projects:

1. jPasskit - Which contains the Pass objects and useful utilities. It is designed to be included in existing Webservices, thus not including any request handling.
2. jPasskit Server -  Which contains an additional REST Webservice, that can be included in existing Applications that do not use their own Webservice already. Note: This is still no standalone implementation. Things like storing passes and handling device (un)registrations are left open for the Application to implement.

**Current stable release:** 0.0.8

**Development Version:** 0.0.9-SNAPSHOT

**Support jPasskit by contributing to my [Gratipay fund][1].**

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


**The released artifacts are now available at Maven Central**

Snapshot versions can be found here: https://oss.sonatype.org/content/repositories/snapshots/
 
 
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

### Providing pass templates

Usually, passes contain additional information that need to be included in the final, signed pass, e.g.

- Images (icons, logos, background images)
- Translations

jPasskit provides a flexible mechanism to provide such templates:

- as a folder (using ```PKPassTemplateFolder```)
- as a set of streams (using ```PKPassTemplateInMemory```)
- using your own implementation (implementing ```IPKPassTemplate```)

#### Folder-based templates (standard approach)

In order to use an existing folder on the file system as you pass's template, you create an instance of ```PKPassTemplateFolder``` using the path to your folder as argument:

```
IPKPassTemplate pkPassTemplateFolder = new PKPassTemplateFolder(PASS_TEMPLATE_FOLDER);
```

The content of this directory is defined in the PassKit Developer Documentation.

<img src="https://github.com/bitzeche/jpasskit/raw/master/passFolder.png">

That's it. When signing the pass, the contents of this folder will be copied into the pass.

#### Dynamic templates (in memory) 

This approach requires more code, but is also more flexible. The template is stored as a list of input streams, whose content gets copied into the pass when it is signed and packaged.

It does not matter, where the stream comes from, but the data needs to be available when the template is used.
For convenience, we provide methods to add several additional data types to the template:

```
pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_BACKGROUND, stream);
pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_BACKGROUND_RETINA, stringBuffer);
pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON, file);
pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, url);
``` 

As you can see, we're also providing static variables for the most common file names.

You can also add an optional locale parameter to the call, in which case the file will automatically be added only for the given language:
```
pkPassTemplateInMemory.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, Locale.ENGLISH, url); 
//content from URL will be placed in "en.lproj/icon@2x.png"
```

**Note:** There are no checks, that the content of a provided file is valid. So if you'd provide a PDF file but store it as icon.png, it will not work. 

### Signing and Zipping a Pass

The PKSigningUtil contains all necessary methods to:

1. Load the Pass Certificate
2. Load the Apple Worldwide Developer Relations CA (AppleWWDRCA)
3. Create the pass.json file
4. Hash all files in the Pass directory and create the Manifest file (manifest.json)
5. Sign the Manifest file
6. ZIP the finished Pass directory


Example to do it all in one step: 
```
PKSigningInformation pkSigningInformation = new  PKSigningInformationUtil().loadSigningInformationFromPKCS12AndIntermediateCertificate(keyStorePath,  keyStorePassword, appleWWDRCA);
PKPassTemplateFolder passTemplate = new PKPassTemplateFolder(template_path);
PKFileBasedSigningUtil pkSigningUtil = new PKFileBasedSigningUtil(new ObjectMapper());
byte[] signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPkPassArchive(pass, passTemplate, pkSigningInformation);
```
	
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
 
That's it. Your web service is running. Just point your passes to the URL where the server is running.

[1]:	https://www.gittip.com/drallgood/