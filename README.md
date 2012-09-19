#jpasskit

jPasskit is an Java&trade; implementation of the Apple&trade; PassKit Web Service.

There are two separate projects:

1. jPasskit - Which contains the Pass objects and useful utilities. It is designed to be included in existing Webservices, thus not including any request handling.
2. jPasskit.server -  Which contains an additional REST Webservice, that can be included in existing Applications that do not use their own Webservice already. Note: This is still no standalone implementation. Things like storing passes and handling device (un)registrations are left open for the Application to implement.

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


Repositories:

	<repository>
		<id>bitzeche-release</id>
		<name>Bitzeche Release</name>
		<url>http://trac.bitzeche.de/archiva/repository/release/</url>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
	</repository>
	<repository>
		<id>bitzeche-snapshots</id>
		<name>Bitzeche Release</name>
		<url>http://trac.bitzeche.de/archiva/repository/snapshots/</url>
		<releases>
			<enabled>false</enabled>
			</releases>
		</repository>
	
## Using jPasskit

Using jPasskit is pretty straight forward:

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
<img src="https://github.com/bitzeche/jpasskit/blob/master/passFolder.png">

Using the PKSigningUtil method createSignedAndZippedPkPassArchive (which takes the PKPass, the template directory, the certificate/private key and the Apple intermediate certificate as parameters), you can create the finished Pass archive.

