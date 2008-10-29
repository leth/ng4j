// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/swp/setup/CreateTestKeystore.java,v 1.2 2008/10/29 18:36:46 hartig Exp $

package de.fuberlin.wiwiss.ng4j.swp.setup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
//import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import de.fuberlin.wiwiss.ng4j.swp.util.SWPSignatureUtilities;

import sun.misc.BASE64Encoder;

/** Creates the keystore used by the tests.
 *
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class CreateTestKeystore {

	private static final Logger logger = Logger.getLogger( CreateTestKeystore.class );
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidCipherTextException 
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String[] args) throws InvalidKeyException, 
	KeyStoreException, NoSuchAlgorithmException, CertificateException, 
	NoSuchProviderException, SignatureException, 
	InvalidCipherTextException, InvalidKeySpecException, IOException 
	{
		logger.setLevel(Level.ALL);
		createCertificate();
		
		// If the logger level is set to ALL as above, then expect 
		// output messages like the following:
		
//		INFO - CreateTestKeystore.createCertificate(146) | Generating certificate for distinguished subject name 'CN=NG4J test CA, O=NG4J test, ST=SH, C=DE', valid for 3000 days
//		DEBUG - CreateTestKeystore.createCertificate(152) | Generated keypair, extracting components and creating public structure for certificate
//		DEBUG - CreateTestKeystore.createCertificate(157) | New public key is '308187028181008558b0a3dd80e707497e2afae0ae57b63f149a963ebb306d0f04d0bda7450f98baa642ffa18b216f717e69e21f2c5b4b19729d0318a32dd41861168dae58415e97d9a195654cd3cd7ea28bbf8c2bb678aee1d8f1a8bfb4f741f493682e54e00bfa06e330264155b4cab32a95218aa8d66cf2a3eb83f4e4d103e7d7aa0afada6f020103, exponent=3, modulus=93639058320031193716807082276072113639476687230687150205039085570071439862828044153264713283924021649674726066472471649686572139422688154478222383941077758488517214513596945845319393080523313699530959308767272968457334729193729778799057416104218817379376224970089426940334823685385785303502698392290221283951
//		DEBUG - CreateTestKeystore.createCertificate(191) | Certificate structure generated, creating SHA1 digest
//		DEBUG - CreateTestKeystore.createCertificate(205) | Block to sign is '30820170a0030201020206011c296af6ed300d06092a864886f70d010105050030483118301606035504030c0f434e3d4e47344a207465737420434131123010060355040a0c094e47344a2074657374310b300906035504080c025348310b3009060355040613024445301e170d3038303930333138313330375a170d3136313132303139313330375a30483118301606035504030c0f434e3d4e47344a207465737420434131123010060355040a0c094e47344a2074657374310b300906035504080c025348310b300906035504061302444530819d300d06092a864886f70d010101050003818b00308187028181008558b0a3dd80e707497e2afae0ae57b63f149a963ebb306d0f04d0bda7450f98baa642ffa18b216f717e69e21f2c5b4b19729d0318a32dd41861168dae58415e97d9a195654cd3cd7ea28bbf8c2bb678aee1d8f1a8bfb4f741f493682e54e00bfa06e330264155b4cab32a95218aa8d66cf2a3eb83f4e4d103e7d7aa0afada6f020103'
//		DEBUG - CreateTestKeystore.createCertificate(216) | SHA1/RSA signature of digest is '71e3d44ac3cf3ed105fe50f2d488c27b4201cb69c7e6c59279f106f1b2c3a81dd60861f2145223ab5c75bee10780688eaa1223c6536710bab754d4a4085920c7a2cd8ca39e34afd4fcdba62a80230efbc6888a4d6c65741fc15707fc4157e30012fc77436d013b1a2341ee17042790f5c54846f82e8657a19e9464b96d04d099'
//		DEBUG - CreateTestKeystore.createCertificate(226) | Verifying certificate for correct signature with CA public key
//		DEBUG - CreateTestKeystore.createCertificate(232) | Exporting certificate in PKCS12 format
//		INFO - CreateTestKeystore.createCertificate(248) | Generating certificate for distinguished subject name 'CN=NG4J test, O=NG4J test, L=Kiel, ST=SH, C=DE', valid for 1000 days
//		DEBUG - CreateTestKeystore.createCertificate(254) | Generated keypair, extracting components and creating public structure for certificate
//		DEBUG - CreateTestKeystore.createCertificate(259) | New public key is '30818702818100873c079fe1c87f5d9b6c27ab80256803bf848f0862d4be9e059ccd08429f367e443cd0efd51d3a95b28543a36f1ad62b2e6f4cf17fcd8ce61eb6e8d05d3a87e508d320689ddc5bc2cf6ce5d21fd17c547aa36e0f5ebd634211207ccb9eb364232ca80e7c383059fba236939eae00f2e494794444b349ea0f13b7ae81b94f5dab020103, exponent=3, modulus=94964889328409624208863996852639754228839220656011084775799286173333465455230138462307809145644482970224488806529838690211275578355190503635307944811710659520638886284253466151257952391265619968539187994334739363608876245090216750871409761151227453035069813401866155304515529925054428665723002770474295844267
//		DEBUG - CreateTestKeystore.createCertificate(287) | Certificate structure generated, creating SHA1 digest
//		DEBUG - CreateTestKeystore.createCertificate(301) | Block to sign is '3082017ca0030201020206011c296afbe0300d06092a864886f70d010105050030483118301606035504030c0f434e3d4e47344a207465737420434131123010060355040a0c094e47344a2074657374310b300906035504080c025348310b3009060355040613024445301e170d3038303930333138313330395a170d3131303533313138313330395a30543115301306035504030c0c434e3d4e47344a207465737431123010060355040a0c094e47344a2074657374310d300b06035504070c044b69656c310b300906035504080c025348310b300906035504061302444530819d300d06092a864886f70d010101050003818b0030818702818100873c079fe1c87f5d9b6c27ab80256803bf848f0862d4be9e059ccd08429f367e443cd0efd51d3a95b28543a36f1ad62b2e6f4cf17fcd8ce61eb6e8d05d3a87e508d320689ddc5bc2cf6ce5d21fd17c547aa36e0f5ebd634211207ccb9eb364232ca80e7c383059fba236939eae00f2e494794444b349ea0f13b7ae81b94f5dab020103'
//		DEBUG - CreateTestKeystore.createCertificate(312) | SHA1/RSA signature of digest is '652596e2cf7700dc3e359ce01a14eacd65d50fe9b19192947c1de46c951a4b67e57c43557b6a6e9cc16fe712f1fa9cc2c866b17d50f2d4974a29c58a9e79c1afe2b0c581fb403f78673747248e72b0ccabdf781aecb29c8034cd793305888bc566726599665fc02b7e877f96e39aeb8e32c4856e3e187e0fb96e54b0692f2069'
//		DEBUG - CreateTestKeystore.createCertificate(322) | Verifying certificate for correct signature with CA public key
//		DEBUG - CreateTestKeystore.createCertificate(326) | Exporting certificate in PKCS12 format
//		INFO - CreateTestKeystore.createCertificate(371) | SHA1/RSA signature of digest is 'ZSWW4s93ANw+NZzgGhTqzWXVD+mxkZKUfB3kbJUaS2flfENVe2punMFv5xLx+pzCyGaxfVDy1JdK
//		KcWKnnnBr+KwxYH7QD94ZzdHJI5ysMyr33ga7LKcgDTNeTMFiIvFZnJlmWZfwCt+h3+W45rrjjLE
//		hW4+GH4PuW5UsGkvIGk=
//		'
//		INFO - CreateTestKeystore.createCertificate(381) | SHA224/RSA signature of digest is 'Kzzo/VIg+hpZkJS628S8ut1IQZSYXvV/1WSXRHD9nRjfyzlqZKbTmClPZDG/+TfBfjDzC+L8S9gU
//		l/Vmqq47QIafM/vIqALS5b0/1/xfIROqj594mxk/J25wy4GQmGIjnCv6NoNG/KJDO2WBklQFeU5h
//		lz9UON1lcp/CzGdcJJs=
//		'
		
	}

	// This code borrows heavily from the sample code given
	// http://www.mayrhofer.eu.org/Default.aspx?pageindex=4&pageid=39
	// by Rene Mayrhofer in "Creating X.509 certificates programmatically in Java"
	private static void createCertificate() 
	throws KeyStoreException, NoSuchAlgorithmException, CertificateException, 
	IOException, InvalidKeyException, NoSuchProviderException, 
	SignatureException,	InvalidCipherTextException, InvalidKeySpecException {
    	
    	// generate the keypair for the new certificate
    	String clientDN = "CN=NG4J test, O=NG4J test, L=Kiel, ST=SH, C=DE";
    	String caDN = "CN=NG4J test CA, O=NG4J test, ST=SH, C=DE";
    	int caValidityDays = 3000;
    	int clientValidityDays = 1000;
    	String exportPassword = "dpuser";
    	String exportFile = "tests/ng4jtest.p12";
    	
    	
    	
    	/* ***** First make the certificate authority certificate ****** */
    	
		logger.info("Generating certificate for distinguished subject name '" + 
				caDN + "', valid for " + caValidityDays + " days");
    	SecureRandom srForCA = new SecureRandom();
    	RSAKeyPairGenerator genForCA = new RSAKeyPairGenerator();
    	genForCA.init(new RSAKeyGenerationParameters(BigInteger.valueOf(3), srForCA, 1024, 80));
        AsymmetricCipherKeyPair caKeypair = genForCA.generateKeyPair();
        logger.debug("Generated keypair, extracting components and creating public structure for certificate");
		RSAKeyParameters caPublicKey = (RSAKeyParameters) caKeypair.getPublic();
        RSAPrivateCrtKeyParameters caPrivateKey = (RSAPrivateCrtKeyParameters) caKeypair.getPrivate();
        // used to get proper encoding for the certificate
        RSAPublicKeyStructure caPkStruct = new RSAPublicKeyStructure(caPublicKey.getModulus(), caPublicKey.getExponent());
        logger.debug("New public key is '" + makeHexString(caPkStruct.getEncoded()) + 
				", exponent=" + caPublicKey.getExponent() + ", modulus=" + caPublicKey.getModulus());
        // JCE format needed for the certificate - because getEncoded() is necessary...
        PublicKey caPubKey = KeyFactory.getInstance("RSA").generatePublic(
                new RSAPublicKeySpec(caPublicKey.getModulus(), caPublicKey.getExponent()));
        // and this one for the KeyStore
        PrivateKey caPrivKey = KeyFactory.getInstance("RSA").generatePrivate(
                new RSAPrivateCrtKeySpec(caPublicKey.getModulus(), caPublicKey.getExponent(),
                		caPrivateKey.getExponent(), caPrivateKey.getP(), caPrivateKey.getQ(), 
                		caPrivateKey.getDP(), caPrivateKey.getDQ(), caPrivateKey.getQInv()));
    	
        // put the new public key into a new X.509 certificate structure
        Calendar caExpiry = Calendar.getInstance();
        caExpiry.add(Calendar.DAY_OF_YEAR, caValidityDays);
		
		X509Name caX509Name = new X509Name("CN=" + caDN);
		
		V3TBSCertificateGenerator caCertGen = new V3TBSCertificateGenerator();
		caCertGen.setSerialNumber(new DERInteger(BigInteger.valueOf(System.currentTimeMillis())));
		//certGen.setIssuer(PrincipalUtil.getSubjectX509Principal(caCert));
	    caCertGen.setIssuer(caX509Name); // replaced the above line with this one to avoid needing a separate certificate authority
	    caCertGen.setSubject(caX509Name);
		
		// Instead of the following, get the algorithm directly so we don't need X509Util
		//DERObjectIdentifier sigOID = X509Util.getAlgorithmOID("SHA1WithRSAEncryption");
		DERObjectIdentifier sigOID = (DERObjectIdentifier) PKCSObjectIdentifiers.sha1WithRSAEncryption;
		
		AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(sigOID, new DERNull());
		caCertGen.setSignature(sigAlgId);
		caCertGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new ASN1InputStream(
                new ByteArrayInputStream(caPubKey.getEncoded())).readObject()));
		caCertGen.setStartDate(new Time(new Date(System.currentTimeMillis())));
		caCertGen.setEndDate(new Time(caExpiry.getTime()));
		
		logger.debug("Certificate structure generated, creating SHA1 digest");
		// attention: hard coded to be SHA1+RSA!
		SHA1Digest caDigester = new SHA1Digest();
		AsymmetricBlockCipher rsaForCA = new PKCS1Encoding(new RSAEngine());
		TBSCertificateStructure tbsCertForCA = caCertGen.generateTBSCertificate();
    	
		ByteArrayOutputStream   bOutForCA = new ByteArrayOutputStream();
		DEROutputStream         dOutForCA = new DEROutputStream(bOutForCA);
		dOutForCA.writeObject(tbsCertForCA);
		
		// and now sign
		byte[] signature;
		byte[] certBlockForCA = bOutForCA.toByteArray();
		// first create digest
		logger.debug("Block to sign is '" + makeHexString(certBlockForCA) + "'");		
		caDigester.update(certBlockForCA, 0, certBlockForCA.length);
		byte[] caHash = new byte[caDigester.getDigestSize()];
		caDigester.doFinal(caHash, 0);
		// and sign that (self-sign for now)
		rsaForCA.init(true, caPrivateKey);
		DigestInfo dInfo = new DigestInfo( new AlgorithmIdentifier(X509ObjectIdentifiers.id_SHA1, null), caHash);
		//DigestInfo dInfo = new DigestInfo( sigAlgId, caHash);
		byte[] digest = dInfo.getEncoded(ASN1Encodable.DER);
		signature = rsaForCA.processBlock(digest, 0, digest.length);
		
		logger.debug("SHA1/RSA signature of digest is '" + makeHexString(signature) + "'");

		// and finally construct the certificate structure
        ASN1EncodableVector  vForCA = new ASN1EncodableVector();

        vForCA.add(tbsCertForCA);
        vForCA.add(sigAlgId);
        vForCA.add(new DERBitString(signature));

        X509CertificateObject caCert = new X509CertificateObject(new X509CertificateStructure(new DERSequence(vForCA))); 
        logger.debug("Verifying certificate for correct signature with CA public key");
        //clientCert.verify(caCert.getPublicKey());
        // instead of the above, verify against self-certificate
        caCert.verify(caPubKey); // an error occurs here because of an inconsistency in bouncycastle 1.38 

        // and export as PKCS12 formatted file along with the private key and the CA certificate 
        logger.debug("Exporting certificate in PKCS12 format");

        PKCS12BagAttributeCarrier bagCert = caCert;
        bagCert.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
        		new DERBMPString("Certificate Authority for NG4J JUnit tests certificate"));
        bagCert.setBagAttribute(
                PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
                new SubjectKeyIdentifierStructure(caPubKey));
        
        
        
        
        
    	/* ***** Next make the client certificate ****** */
        
        
		logger.info("Generating certificate for distinguished subject name '" + 
				clientDN + "', valid for " + clientValidityDays + " days");
    	SecureRandom srForClient = new SecureRandom();
    	RSAKeyPairGenerator genForClient = new RSAKeyPairGenerator();
    	genForClient.init(new RSAKeyGenerationParameters(BigInteger.valueOf(3), srForClient, 1024, 80));
        AsymmetricCipherKeyPair clientKeypair = genForClient.generateKeyPair();
        logger.debug("Generated keypair, extracting components and creating public structure for certificate");
		RSAKeyParameters clientPublicKey = (RSAKeyParameters) clientKeypair.getPublic();
        RSAPrivateCrtKeyParameters clientPrivateKey = (RSAPrivateCrtKeyParameters) clientKeypair.getPrivate();
        // used to get proper encoding for the certificate
        RSAPublicKeyStructure clientPkStruct = new RSAPublicKeyStructure(clientPublicKey.getModulus(), clientPublicKey.getExponent());
        logger.debug("New public key is '" + makeHexString(clientPkStruct.getEncoded()) + 
				", exponent=" + clientPublicKey.getExponent() + ", modulus=" + clientPublicKey.getModulus());
        // JCE format needed for the certificate - because getEncoded() is necessary...
        PublicKey clientPubKey = KeyFactory.getInstance("RSA").generatePublic(
                new RSAPublicKeySpec(clientPublicKey.getModulus(), clientPublicKey.getExponent()));
        // and this one for the KeyStore
        PrivateKey clientPrivKey = KeyFactory.getInstance("RSA").generatePrivate(
                new RSAPrivateCrtKeySpec(clientPublicKey.getModulus(), clientPublicKey.getExponent(),
                		clientPrivateKey.getExponent(), clientPrivateKey.getP(), clientPrivateKey.getQ(), 
                		clientPrivateKey.getDP(), clientPrivateKey.getDQ(), clientPrivateKey.getQInv()));
    	
        // put the new public key into a new X.509 certificate structure
        Calendar clientExpiry = Calendar.getInstance();
        clientExpiry.add(Calendar.DAY_OF_YEAR, clientValidityDays);
		
		X509Name clientX509Name = new X509Name("CN=" + clientDN);
		
		V3TBSCertificateGenerator clientCertGen = new V3TBSCertificateGenerator();
		clientCertGen.setSerialNumber(new DERInteger(BigInteger.valueOf(System.currentTimeMillis())));
	    clientCertGen.setIssuer(PrincipalUtil.getSubjectX509Principal(caCert));
	    clientCertGen.setSubject(clientX509Name);
		
		clientCertGen.setSignature(sigAlgId);
		clientCertGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new ASN1InputStream(
                new ByteArrayInputStream(clientPubKey.getEncoded())).readObject()));
		clientCertGen.setStartDate(new Time(new Date(System.currentTimeMillis())));
		clientCertGen.setEndDate(new Time(clientExpiry.getTime()));
		
		logger.debug("Certificate structure generated, creating SHA1 digest");
		// attention: hard coded to be SHA1+RSA!
		SHA1Digest clientDigester = new SHA1Digest();
		AsymmetricBlockCipher rsaForClient = new PKCS1Encoding(new RSAEngine());
		TBSCertificateStructure tbsCertForClient = clientCertGen.generateTBSCertificate();
    	
		ByteArrayOutputStream   bOutForClient = new ByteArrayOutputStream();
		DEROutputStream         dOutForClient = new DEROutputStream(bOutForClient);
		dOutForClient.writeObject(tbsCertForClient);
		
		// and now sign
		byte[] clientSignature;
		byte[] certBlockForClient = bOutForClient.toByteArray();
		// first create digest
		logger.debug("Block to sign is '" + makeHexString(certBlockForClient) + "'");		
		clientDigester.update(certBlockForClient, 0, certBlockForClient.length);
		byte[] clientHash = new byte[clientDigester.getDigestSize()];
		clientDigester.doFinal(clientHash, 0);
		// and sign that
		rsaForClient.init(true, caPrivateKey);
		//PKCSObjectIdentifiers.sha1WithRSAEncryption
		dInfo = new DigestInfo( new AlgorithmIdentifier(X509ObjectIdentifiers.id_SHA1, null), clientHash);
		digest = dInfo.getEncoded(ASN1Encodable.DER);
		clientSignature = rsaForClient.processBlock(digest, 0, digest.length);
		
		logger.debug("SHA1/RSA signature of digest is '" + makeHexString(clientSignature) + "'");

		// and finally construct the certificate structure
        ASN1EncodableVector  vForClient = new ASN1EncodableVector();

        vForClient.add(tbsCertForClient);
        vForClient.add(sigAlgId);
        vForClient.add(new DERBitString(clientSignature));

        X509CertificateObject clientCert = new X509CertificateObject(new X509CertificateStructure(new DERSequence(vForClient))); 
        logger.debug("Verifying certificate for correct signature with CA public key");
        clientCert.verify(caCert.getPublicKey());

        // and export as PKCS12 formatted file along with the private key and the CA certificate 
        logger.debug("Exporting certificate in PKCS12 format");

        PKCS12BagAttributeCarrier bagCert1 = clientCert;
        bagCert1.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
        		new DERBMPString("Certificate for NG4J JUnit tests"));
        bagCert1.setBagAttribute(
                PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
                new SubjectKeyIdentifierStructure(clientPubKey));
        
        
        
        
        
        
        
        
    	/* ***** Finally, store both certificates in the keystore ****** */
        
        KeyStore store = KeyStore.getInstance("PKCS12");

        store.load(null, null);

        X509Certificate[] chain = new X509Certificate[2];
        // first the client, then the CA certificate
        chain[0] = clientCert;
        chain[1] = caCert;
        
        store.setKeyEntry("Private key for NG4J JUnit tests", clientPrivKey, exportPassword.toCharArray(), chain);

        FileOutputStream fOut = new FileOutputStream(exportFile);

        store.store(fOut, exportPassword.toCharArray());
        
        
        /* ***** Print a final helpful message for editing the JUnit tests ****** */

		//DERObjectIdentifier sigOID = (DERObjectIdentifier) PKCSObjectIdentifiers.sha224WithRSAEncryption;
        
//        Signature sig = new JDKDigestSignature.SHA224WithRSAEncryption();
//        clientCert.getSignature()
        
        BASE64Encoder base64encoder = new BASE64Encoder();
        String sigForTests;
        
        sigForTests = base64encoder.encodeBuffer(clientSignature);
        logger.info("SHA1/RSA signature of digest is '" + sigForTests + "'");
        
        //Signature sig = Signature.getInstance(sigOID.getId());
        //Signature sig = new JDKDigestSignature.SHA1WithRSAEncryption();
        //Signature sig = new JDKDigestSignature.SHA224WithRSAEncryption();
        Signature sig = Signature.getInstance(SWPSignatureUtilities.ALG_ID_SIGNATURE_SHA224withRSA,
        		new BouncyCastleProvider() );
        sig.initSign(caPrivKey, srForClient);
        sig.update(bOutForClient.toByteArray());
        byte[] signatureForTests = sig.sign();
        
        sigForTests = base64encoder.encodeBuffer(signatureForTests);
        logger.info("SHA224/RSA signature of digest is '" + sigForTests + "'");
        
	}


	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray(); //$NON-NLS-1$

	/** Returns a hex string representing the given byte array,
	 * left-to-right (big-endian).  The result will contain
	 * 2*bytes.size characters, leading 0s included.
	 */
	public static String makeHexString( byte[] bytes ) {
		StringBuffer sb = new StringBuffer();
		
		int i=0;
		while ( i < bytes.length ) {
			sb.append( HEX_CHARS[ (bytes[i] >> 4) & 0x0F ] );
			sb.append( HEX_CHARS[ bytes[i] & 0x0F] );
			i++;
		}
		
		// TODO if/when we change compiler compliance to Java 5 or above, 
		// can change to the simpler form:
//		for ( byte b : bytes ) {
//			sb.append( HEX_CHARS[ (b >> 4) & 0x0F ] );
//			sb.append( HEX_CHARS[ b & 0x0F] );
//		}
		
		return sb.toString();
	}
}
