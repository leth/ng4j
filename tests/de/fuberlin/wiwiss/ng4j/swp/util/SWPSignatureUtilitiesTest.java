/*
 * Created on 16-Feb-2005
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.util;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPAlgorithmNotSupportedException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPInvalidKeyException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPNoSuchAlgorithmException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPNoSuchDigestMethodException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPValidationException;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.util.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.util.SWPSignatureUtilities;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP_V;
import junit.framework.TestCase;

/**
 * Tests the SWP signature utilities.
 * To create a new certificate chain (in case the old certificates have been
 * expired), i.e. a new ./tests/test.p12 file, you have to execute the
 * following four commands that use the CA.pl script provided with OpenSSL.
 * <ul>
 *   <li><tt>perl /etc/ssl/misc/CA.pl -newca</tt></li>
 *   <li><tt>perl /etc/ssl/misc/CA.pl -newreq</tt></li>
 *   <li><tt>perl /etc/ssl/misc/CA.pl -sign</tt></li>
 *   <li><tt>perl /etc/ssl/misc/CA.pl -pkcs12</tt></li>
 * </ul>
 * The fourth command creates newcert.p12 with which you can replace test.p12.
 * The following lines present a sample session executing the four commands.
 * <pre>
 *   <code>
 * $ perl /etc/ssl/misc/CA.pl -newca
 * CA certificate filename (or enter to create)
 *
 * Making CA certificate ...
 * Generating a 1024 bit RSA private key
 * .........++++++
 * .......++++++
 * writing new private key to './demoCA/private/cakey.pem'
 * Enter PEM pass phrase:
 * Verifying - Enter PEM pass phrase:
 * -----
 * You are about to be asked to enter information that will be incorporated
 * into your certificate request.
 * What you are about to enter is what is called a Distinguished Name or a DN.
 * There are quite a few fields but you can leave some blank
 * For some fields there will be a default value,
 * If you enter '.', the field will be left blank.
 * -----
 * Country Name (2 letter code) [AU]:DE
 * State or Province Name (full name) [Some-State]:SH
 * Locality Name (eg, city) []:
 * Organization Name (eg, company) [Internet Widgits Pty Ltd]:NG4J test
 * Organizational Unit Name (eg, section) []:
 * Common Name (eg, YOUR name) []:NG4J test CA
 * Email Address []:
 *
 * Please enter the following 'extra' attributes
 * to be sent with your certificate request
 * A challenge password []:
 * An optional company name []:
 * Using configuration from /etc/ssl/openssl.cnf
 * Enter pass phrase for ./demoCA/private/cakey.pem:
 * Check that the request matches the signature
 * Signature ok
 * Certificate Details:
 *         Serial Number:
 *             b0:c7:44:8e:06:b9:1b:75
 *         Validity
 *             Not Before: Aug 29 16:13:52 2008 GMT
 *             Not After : Aug 29 16:13:52 2011 GMT
 *         Subject:
 *             countryName               = DE
 *             stateOrProvinceName       = SH
 *             organizationName          = NG4J test
 *             commonName                = NG4J test CA
 *         X509v3 extensions:
 *             X509v3 Subject Key Identifier:
 *                 B6:22:EA:DD:72:DE:4B:01:65:0A:BB:8C:A1:0F:4D:B0:2E:A2:8B:1E
 *             X509v3 Authority Key Identifier:
 *                 keyid:B6:22:EA:DD:72:DE:4B:01:65:0A:BB:8C:A1:0F:4D:B0:2E:A2:8B:1E
 *                 DirName:/C=DE/ST=SH/O=NG4J test/CN=NG4J test CA
 *                 serial:B0:C7:44:8E:06:B9:1B:75
 *
 *             X509v3 Basic Constraints:
 *                 CA:TRUE
 * Certificate is to be certified until Aug 29 16:13:52 2011 GMT (1095 days)
 *
 * Write out database with 1 new entries
 * Data Base Updated
 *
 *
 * $ perl /etc/ssl/misc/CA.pl -newreq
 * Generating a 1024 bit RSA private key
 * ......++++++
 * .......................++++++
 * writing new private key to 'newkey.pem'
 * Enter PEM pass phrase:
 * Verifying - Enter PEM pass phrase:
 * -----
 * You are about to be asked to enter information that will be incorporated
 * into your certificate request.
 * What you are about to enter is what is called a Distinguished Name or a DN.
 * There are quite a few fields but you can leave some blank
 * For some fields there will be a default value,
 * If you enter '.', the field will be left blank.
 * -----
 * Country Name (2 letter code) [AU]:DE
 * State or Province Name (full name) [Some-State]:SH
 * Locality Name (eg, city) []:Kiel
 * Organization Name (eg, company) [Internet Widgits Pty Ltd]:NG4J test
 * Organizational Unit Name (eg, section) []:
 * Common Name (eg, YOUR name) []:NG4J test
 * Email Address []:
 *
 * Please enter the following 'extra' attributes
 * to be sent with your certificate request
 * A challenge password []:
 * An optional company name []:
 * Request is in newreq.pem, private key is in newkey.pem
 *
 *
 * $ perl /etc/ssl/misc/CA.pl -sign
 * Using configuration from /etc/ssl/openssl.cnf
 * Enter pass phrase for ./demoCA/private/cakey.pem:
 * Check that the request matches the signature
 * Signature ok
 * Certificate Details:
 *         Serial Number:
 *             b0:c7:44:8e:06:b9:1b:76
 *         Validity
 *             Not Before: Aug 29 16:15:58 2008 GMT
 *             Not After : Aug 29 16:15:58 2009 GMT
 *         Subject:
 *             countryName               = DE
 *             stateOrProvinceName       = SH
 *             localityName              = Kiel
 *             organizationName          = NG4J test
 *             commonName                = NG4J test
 *         X509v3 extensions:
 *             X509v3 Basic Constraints: CA:FALSE
 *             Netscape Comment: OpenSSL Generated Certificate
 *             X509v3 Subject Key Identifier: 96:3E:20:03:50:2A:63:F3:C6:56:68:48:21:70:D5:58:EF:61:84:DB
 *             X509v3 Authority Key Identifier: keyid:B6:22:EA:DD:72:DE:4B:01:65:0A:BB:8C:A1:0F:4D:B0:2E:A2:8B:1E
 *
 * Certificate is to be certified until Aug 29 16:15:58 2009 GMT (365 days)
 * Sign the certificate? [y/n]:y
 *
 * 1 out of 1 certificate requests certified, commit? [y/n]y
 * Write out database with 1 new entries
 * Data Base Updated
 * Signed certificate is in newcert.pem
 *
 *
 * $ perl /etc/ssl/misc/CA.pl -pkcs12
 * Enter pass phrase for newkey.pem:
 * Enter Export Password:
 * Verifying - Enter Export Password:
 * PKCS #12 file is in newcert.p12
 *   </code>
 * </pre>
 *
 * @author Rowland Watkins (rowland@grid.cx)
 *
 * 
 */
public class SWPSignatureUtilitiesTest extends TestCase 
{
	protected final static String uri1 = "http://example.org/graph1";
	protected final static String uri2 = "http://example.org/graph2";
	protected final static Node foo = Node.createURI("http://example.org/#foo");
	protected final static Node bar = Node.createURI("http://example.org/#bar");
	protected final static Node baz = Node.createURI("http://example.org/#baz");
	protected final static String keystore = "tests/test.p12";
	protected final static String password = "dpuser";
	
	protected SWPNamedGraphSet set;
	protected NamedGraph g1;
	protected NamedGraph g2;
	
	protected String g1signature;
	protected String g2signature;
	protected String setSignature;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		this.set = createSWPNamedGraphSet();
		g1 = this.set.createGraph( uri1 );
		g2 = this.set.createGraph( uri2 );
		g1.add( new Triple( foo, bar, baz ) );
		g2.add( new Triple( bar, baz, foo ) );
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception 
	{
		this.set.close();
	}

	public void testGetCanonicalGraph() 
	{
		String canon1 = SWPSignatureUtilities.getCanonicalGraph( g1 );
		String canon2 = SWPSignatureUtilities.getCanonicalGraph( g1 );
		String canon3 = SWPSignatureUtilities.getCanonicalGraph( g2 );
		String canon4 = SWPSignatureUtilities.getCanonicalGraph( g2 );
		assertEquals(canon1, canon2);
		assertNotSame(canon1, canon3);
		assertNotSame(canon1, canon4);
		assertNotSame(canon2, canon3);
		assertNotSame(canon2, canon4);
	}

	public void testGetCanonicalGraphSet() 
	{
		String canon1 = SWPSignatureUtilities.getCanonicalGraphSet( this.set );
		String canon2 = SWPSignatureUtilities.getCanonicalGraphSet( this.set );
		assertEquals(canon1, canon2);
	}

	/*
	 * Class under test for String calculateDigest(NamedGraph, Node)
	 */
	public void testCalculateDigestNamedGraphNode() 
	throws SWPNoSuchDigestMethodException 
	{
		String sha1digest1 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha1 );
		String sha1digest2 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha1 );
		String sha1digest3 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha1 );
		String sha1digest4 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha1 );
		
		assertEquals( sha1digest1, sha1digest2 );
		assertEquals( sha1digest3, sha1digest4 );
		assertNotSame( sha1digest1, sha1digest3 );
		assertNotSame( sha1digest2, sha1digest3 );
		assertNotSame( sha1digest2, sha1digest4 );
		
		String sha224digest1 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha224 );
		String sha224digest2 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha224 );
		String sha224digest3 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha224 );
		String sha224digest4 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha224 );
		
		assertEquals( sha224digest1, sha224digest2 );
		assertEquals( sha224digest3, sha224digest4 );
		assertNotSame( sha224digest1, sha224digest3 );
		assertNotSame( sha224digest2, sha224digest3 );
		assertNotSame( sha224digest2, sha224digest4 );
		
		String sha256digest1 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha256 );
		String sha256digest2 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha256 );
		String sha256digest3 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha256 );
		String sha256digest4 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha256 );
		
		assertEquals( sha256digest1, sha256digest2 );
		assertEquals( sha256digest3, sha256digest4 );
		assertNotSame( sha256digest1, sha256digest3 );
		assertNotSame( sha256digest2, sha256digest3 );
		assertNotSame( sha256digest2, sha256digest4 );
		
		String sha384digest1 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha384 );
		String sha384digest2 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha384 );
		String sha384digest3 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha384 );
		String sha384digest4 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha384 );
		
		assertEquals( sha384digest1, sha384digest2 );
		assertEquals( sha384digest3, sha384digest4 );
		assertNotSame( sha384digest1, sha384digest3 );
		assertNotSame( sha384digest2, sha384digest3 );
		assertNotSame( sha384digest2, sha384digest4 );
		
		String sha512digest1 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha512 );
		String sha512digest2 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha512 );
		String sha512digest3 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha512 );
		String sha512digest4 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha512 );
		
		assertEquals( sha512digest1, sha512digest2 );
		assertEquals( sha512digest3, sha512digest4 );
		assertNotSame( sha512digest1, sha512digest3 );
		assertNotSame( sha512digest2, sha512digest3 );
		assertNotSame( sha512digest2, sha512digest4 );
	}

	/*
	 * Class under test for String calculateDigest(NamedGraphSet, Node)
	 */
	public void testCalculateDigestNamedGraphSetNode() 
	throws SWPNoSuchDigestMethodException 
	{
		String digest1 = SWPSignatureUtilities.calculateDigest( this.set, SWP.JjcRdfC14N_sha1 );
		String digest2 = SWPSignatureUtilities.calculateDigest( this.set, SWP.JjcRdfC14N_sha1 );
		assertEquals( digest1, digest2 );
	}

	/*
	 * Class under test for String calculateSignature(NamedGraph, Node, PrivateKey)
	 */
	public void testCalculateSignatureNamedGraphNodePrivateKey() 
	throws SWPInvalidKeyException, 
	SWPSignatureException, 
	SWPNoSuchAlgorithmException, 
	SWPValidationException, 
	SWPAlgorithmNotSupportedException, 
	SWPCertificateException 
	{
		assertNotNull( SWPSignatureUtilities.calculateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															PKCS12Utils.decryptPrivateKey( keystore, password ) ) );
	}

	/*
	 * Class under test for String calculateSignature(NamedGraphSet, Node, PrivateKey)
	 */
	public void testCalculateSignatureNamedGraphSetNodePrivateKey() 
	throws SWPInvalidKeyException, 
	SWPSignatureException, 
	SWPNoSuchAlgorithmException, 
	SWPValidationException 
	{
		assertNotNull( SWPSignatureUtilities.calculateSignature( this.set, 
															SWP.JjcRdfC14N_rsa_sha1, 
															PKCS12Utils.decryptPrivateKey( keystore, password ) ) );
	}

	/*
	 * Class under test for boolean validateSignature(NamedGraph, Node, String, String)
	 */
	public void testValidateSignatureNamedGraphNodeStringString() 
	throws SWPInvalidKeyException, 
	SWPSignatureException, 
	SWPNoSuchAlgorithmException, 
	SWPValidationException, 
	CertificateException 
	{
		String cert = "-----BEGIN CERTIFICATE-----\n"+
					"MIID8zCCA1ygAwIBAgIBAzANBgkqhkiG9w0BAQQFADCBoTELMAkGA1UEBhMCVUsxEjAQBgNVBAgT\n"+
					"CUhhbXBzaGlyZTEUMBIGA1UEBxMLU291dGhhbXB0b24xIjAgBgNVBAoTGVVuaXZlcnNpdHkgb2Yg\n"+
					"U291dGhhbXB0b24xDTALBgNVBAsTBERTU0UxDjAMBgNVBAMTBURQIENBMSUwIwYJKoZIhvcNAQkB\n"+
					"FhZlcncwMXJAZWNzLnNvdG9uLmFjLnVrMB4XDTA0MDYwNzIxMDIzM1oXDTA1MDYwNzIxMDIzM1ow\n"+
					"gasxCzAJBgNVBAYTAlVLMRIwEAYDVQQIEwlIYW1wc2hpcmUxFDASBgNVBAcTC1NvdXRoYW1wdG9u\n"+
					"MSIwIAYDVQQKExlVbml2ZXJzaXR5IG9mIFNvdXRoYW1wdG9uMQ0wCwYDVQQLEwREU1NFMRgwFgYD\n"+
					"VQQDEw9Sb3dsYW5kIFdhdGtpbnMxJTAjBgkqhkiG9w0BCQEWFmVydzAxckBlY3Muc290b24uYWMu\n"+
					"dWswgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAMlBcaqp10bBhIjCdnHg30/LYWQe4hdoHIm0\n"+
					"4b+REqJWELOJTp23io/+YEmrP+Oym5/HOuWfDPx7j+mS6R/GK7SyafV8qP1fiA8nsOsakj1eR0t8\n"+
					"ypnufUhlVY5G40FIYmTmtgH/gnNXWf0VWCasUjTSmaUWa4+VinhVr2d8P4FjAgMBAAGjggEtMIIB\n"+
					"KTAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0\n"+
					"ZTAdBgNVHQ4EFgQUjrkjaHUEtMqzJBV7MvCkywLpql4wgc4GA1UdIwSBxjCBw4AU2BlKcSrg9M5M\n"+
					"KQEX1T6vXkoMcPWhgaekgaQwgaExCzAJBgNVBAYTAlVLMRIwEAYDVQQIEwlIYW1wc2hpcmUxFDAS\n"+
					"BgNVBAcTC1NvdXRoYW1wdG9uMSIwIAYDVQQKExlVbml2ZXJzaXR5IG9mIFNvdXRoYW1wdG9uMQ0w\n"+
					"CwYDVQQLEwREU1NFMQ4wDAYDVQQDEwVEUCBDQTElMCMGCSqGSIb3DQEJARYWZXJ3MDFyQGVjcy5z\n"+
					"b3Rvbi5hYy51a4IBADANBgkqhkiG9w0BAQQFAAOBgQB1cOuBoxtpLfBnh7FMZNDgnTDSofvvRoCR\n"+
					"2aUMnvCDo84rkwbz/jXboP4VDRmmgqAgtjtgqR0PO/ua6pgF46Nax2hx+B+JHtvYuNDB2gUuRqpI\n"+
					"EJFdIZtF5zZawEkphFL+5N2fZhz6h/JWsqtkWmU7cflRI2luqUC3lmfpsI1zYw==\n"+
					"-----END CERTIFICATE-----";
		
		CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
		X509Certificate certificate = ( X509Certificate ) cf.generateCertificate( new ByteArrayInputStream( cert.getBytes() ) );
		String signature = "Q5giVuVAnlhxj9XEDws5erZA4yBmPHyzrh+BaI/7aIOAH9inXcaav1+yluhA5IG898ycUZsSqQLw" +
						"JdVtQhaZOvEUVggv7WWO0/RpjJnrrm1BpVFKGF8Wb/9mls+FDFAPFR03nPxCvWzpU+n4RRMbWqtf" +
						"6laHEeKwHV64f4L6tcw=";
		
		String badsignature = "Q5giVuVAnlhxj9XEDws5erZA4yBmPHyzrh+B/7aIOAH9inXcaav1+yluhA5IG898ycUZsSqQLw" +
						"JdVthaZOvEUVggv7WWO0/RpjJnrrm1pVFKGF8Wb/9mls+FDFAPFR03nPxCvWzpU+n4RRMbWqtf" +
						"6laHEeKwHV64f4L6tcw=";
		
		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															signature, 
															certificate ) );
		
		assertFalse( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															badsignature, 
															certificate ) );
	}
	

	/*
	 * Class under test for boolean validateSignature(NamedGraph, Node, String, X509Certificate, ArrayList)
	 */
	
	public void testValidateSignatureNamedGraphNodeStringX509CertificateArrayList() 
	throws SWPInvalidKeyException, 
	SWPSignatureException, 
	SWPCertificateException, 
	SWPNoSuchAlgorithmException, 
	SWPValidationException, SWPAlgorithmNotSupportedException 
	{
		Certificate[] certs = PKCS12Utils.getCertChain( keystore, password );
		
		final String signature = "yqskH08WOauqwIiQXJVCBUCKqlqK1WdVAFoYA9e++uZK+sRJjTyaAz+HL5VBDUytNDQPsiknmlB6"
                + "c1gVwF6/iWRzUd/25Lnz3IiS//WTFmCaDpkjeInj15zEw/uvQxdC1NXmGQiJlddotpsVMoOi+6Oy"
                + "ae4N0WrP9yLP/nVvqEA=";

        final String badsignature = "yqskH08W--wrong--JVCBUCKqlqK1WdVAFoYA9e++uZK+sRJjTyaAz+HL5VBDUytNDQPsiknmlB6"
            + "c1gVwF6/iWRzUd/25Lnz3IiS//WTFmCaDpkjeInj15zEw/uvQxdC1NXmGQiJlddotpsVMoOi+6Oy"
            + "ae4N0WrP9yLP/nVvqEA=";
		
		ArrayList list = new ArrayList();
		list.add( certs[1]);

		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															signature, 
															(X509Certificate )certs[0], 
															list ) );
		
		assertFalse( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															badsignature, 
															(X509Certificate )certs[0], 
															list ) );
	}
	/*
	 * Class under test for boolean validateSignature(NamedGraph, Node, String, X509Certificate, ArrayList, ArrayList)
	 */
	
	public void testValidateSignatureNamedGraphNodeStringX509CertificateArrayListArrayList() 
	throws SWPInvalidKeyException, 
	SWPSignatureException, 
	SWPCertificateException, 
	SWPNoSuchAlgorithmException, 
	SWPValidationException 
	{
		Certificate[] certs = PKCS12Utils.getCertChain( keystore, password );
		
        final String signature = "yqskH08WOauqwIiQXJVCBUCKqlqK1WdVAFoYA9e++uZK+sRJjTyaAz+HL5VBDUytNDQPsiknmlB6"
            + "c1gVwF6/iWRzUd/25Lnz3IiS//WTFmCaDpkjeInj15zEw/uvQxdC1NXmGQiJlddotpsVMoOi+6Oy"
            + "ae4N0WrP9yLP/nVvqEA=";

        final String badsignature = "yqskH08W--wrong--JVCBUCKqlqK1WdVAFoYA9e++uZK+sRJjTyaAz+HL5VBDUytNDQPsiknmlB6"
                + "c1gVwF6/iWRzUd/25Lnz3IiS//WTFmCaDpkjeInj15zEw/uvQxdC1NXmGQiJlddotpsVMoOi+6Oy"
                + "ae4N0WrP9yLP/nVvqEA=";
		
		ArrayList list = new ArrayList();
		list.add( certs[1]);
		
		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															signature, 
															(X509Certificate )certs[0], 
															list, 
															list ) );
		
		assertFalse( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															badsignature, 
															(X509Certificate )certs[0], 
															list, 
															list ) );
	}
	
	
	public void testVerifyCertificateX509CertificateArrayList() 
	throws CertificateExpiredException, 
	CertificateNotYetValidException, 
	GeneralSecurityException 
	{
		Certificate[] certs = PKCS12Utils.getCertChain( keystore, password );
		ArrayList list = new ArrayList();
		list.add( certs[1]);
		SWPSignatureUtilities.verifyCertificate( ( X509Certificate )certs[0], list );
	}
	
	/*
	public void testVerifyCertificationChainCertPathArrayList() 
	{
		//TODO Implement verifyCertificationChain().
	}*/
	
	/**
	 * 
	 */
	public SWPAuthority getAuthority( String keystore, String password )
	{
		SWPAuthority auth = new SWPAuthorityImpl();
		auth.setEmail( "mailto:rowland@grid.cx" );
		auth.setID( Node.createURI( "http://grid.cx/rowland" ) );
		Certificate[] chain = PKCS12Utils.getCertChain( keystore, password );
		auth.setCertificate( ( X509Certificate )chain[0] );
		
		return auth;
	}
	
	/**
	 * Creates the NamedGraphSet instance under test. Might be overridden by
	 * subclasses to test other NamedGraphSet implementations.
	 */
	protected SWPNamedGraphSet createSWPNamedGraphSet() throws Exception 
	{
		return new SWPNamedGraphSetImpl();
	}
    
    public void testIsEverySignatureValid_AllValid() {
        final NamedGraph verifiedSignatures = this.set
                .createGraph(SWP_V.default_graph);
        verifiedSignatures.add(new Triple(Node.createURI(uri1),
                SWP_V.successful, Node.createLiteral("true")));
        verifiedSignatures.add(new Triple(Node.createURI(uri2),
                SWP_V.successful, Node.createLiteral("true")));

        assertTrue("signatures valid", SWPSignatureUtilities
                .isEverySignatureValid(verifiedSignatures));
    }
    
    public void testIsEverySignatureValid_InvalidGraph() {
        final NamedGraph verifiedSignatures = this.set.createGraph(uri1);
        try {
            SWPSignatureUtilities.isEverySignatureValid(verifiedSignatures);
            fail("graph is not a 'verifiedSignatures' graph");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testIsEverySignatureValid_OneNotSuccessful() {
        final NamedGraph verifiedSignatures = this.set
                .createGraph(SWP_V.default_graph);
        verifiedSignatures.add(new Triple(Node.createURI(uri1),
                SWP_V.successful, Node.createLiteral("true")));
        verifiedSignatures.add(new Triple(Node.createURI(uri2),
                SWP_V.notSuccessful, Node.createLiteral("true")));

        assertFalse("signatures invalid", SWPSignatureUtilities
                .isEverySignatureValid(verifiedSignatures));
    }

}

/*
 *  (c)   Copyright 2004, 2005 Rowland Watkins (rowland@grid.cx) 
 *   	  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */