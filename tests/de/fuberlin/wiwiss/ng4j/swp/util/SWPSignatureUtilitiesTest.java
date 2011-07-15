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
 * Tests the SWP signature utilities. <br>
 * 
 * If a new keystore needs to be created, then run the main()
 * in de.fuberlin.wiwiss.ng4j.swp.setup.CreateTestKeystore and
 * update the "signature" and "badsignature" constants as
 * described below.
 * 
 * @author Rowland Watkins (rowland@grid.cx)
 * @since 16-Feb-2005
 */
public class SWPSignatureUtilitiesTest extends TestCase 
{
	
	// The following two constants - signature and badsignature - 
	// should be changed whenever a new keystore is created.
	// The pattern for creating the badsignature is to insert
	// "--wrong--" after the first 8 characters of the signature.
	// The signature to use is the one for the client, not the CA - 
	// that is the last signature created and printed by the logger
	// when the CreateTestKeystore main() is run.
// These are the old values (from 2007 that worked with that keystore)
//    protected final String signature = "yqskH08WOauqwIiQXJVCBUCKqlqK1WdVAFoYA9e++uZK+sRJjTyaAz+HL5VBDUytNDQPsiknmlB6"
//        + "c1gVwF6/iWRzUd/25Lnz3IiS//WTFmCaDpkjeInj15zEw/uvQxdC1NXmGQiJlddotpsVMoOi+6Oy"
//        + "ae4N0WrP9yLP/nVvqEA=";
//    protected final String badsignature = "yqskH08W--wrong--JVCBUCKqlqK1WdVAFoYA9e++uZK+sRJjTyaAz+HL5VBDUytNDQPsiknmlB6"
//            + "c1gVwF6/iWRzUd/25Lnz3IiS//WTFmCaDpkjeInj15zEw/uvQxdC1NXmGQiJlddotpsVMoOi+6Oy"
//            + "ae4N0WrP9yLP/nVvqEA=";
	// These are the new values; unfortunately they do not work
	// TODO Figure out how to change CreateTestKeystore and/or these values so that the tests work as they should
	final static String signature = "Kzzo/VIg+hpZkJS628S8ut1IQZSYXvV/1WSXRHD9nRjfyzlqZKbTmClPZDG/+TfBfjDzC+L8S9gU"
    	+ "l/Vmqq47QIafM/vIqALS5b0/1/xfIROqj594mxk/J25wy4GQmGIjnCv6NoNG/KJDO2WBklQFeU5h"
    	+ "lz9UON1lcp/CzGdcJJs=";
    final static String badsignature = "Kzzo/VIg--wrong--+hpZkJS628S8ut1IQZSYXvV/1WSXRHD9nRjfyzlqZKbTmClPZDG/+TfBfjDzC+L8S9gU"
        + "l/Vmqq47QIafM/vIqALS5b0/1/xfIROqj594mxk/J25wy4GQmGIjnCv6NoNG/KJDO2WBklQFeU5h"
        + "lz9UON1lcp/CzGdcJJs=";
	
    
	final static String uri1 = "http://example.org/graph1";
	protected final static String uri2 = "http://example.org/graph2";
	final static Node foo = Node.createURI("http://example.org/#foo");
	final static Node bar = Node.createURI("http://example.org/#bar");
	final static Node baz = Node.createURI("http://example.org/#baz");
	final static String keystore = "tests/ng4jtest.p12";
	final static String password = "dpuser";
	
	protected SWPNamedGraphSet set;
	protected NamedGraph g1;
	protected NamedGraph g2;
	
	protected String g1signature;
	protected String g2signature;
	protected String setSignature;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception 
	{
		this.set = createSWPNamedGraphSet();
		g1 = this.set.createGraph( uri1 );
		g2 = this.set.createGraph( uri2 );
		g1.add( new Triple( foo, bar, baz ) );
		g2.add( new Triple( bar, baz, foo ) );
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
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
		// REVISIT Need to replace this value when the keystore is re-created
		assertEquals( "D212ca8NnqCnBoV5S0o72LgBf630LDyKi3FRiryu1pgI88/GQ1npTBT/hb4p9fLeFRG+6PzNg+7Z" +  
                     "lfKOGIOWDZaoCVhhBdi0yunAh55OM9goFwzh4e6RwT7TQuqbu2M9bNu2a8gwPfZvo5aX9E07DctK" +   
                     "TlgTsTIyPldI/1Zfetk=" ,  stripLineEnds(SWPSignatureUtilities.calculateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															PKCS12Utils.decryptPrivateKey( keystore, password ) ) ) );
	}

	private String stripLineEnds(String in) { 
		return in.replaceAll( "\n", "" ).replaceAll( "\r", "");
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
		// REVISIT Need to replace this value when the keystore is re-created
		assertEquals( "MLIqfJHNjut70siwNssDzdy81Y3S696hiW2P+qQLSDq04kWWwJvxeNEPqB1QYB0olXf3rmdxSxGN" +  
					  "LzHQSgRkDrh291A1D0E+z5uHN+gmjwebTsOAICEzWFp5vbzgJtY0iItuH5+0xyjn/oOnekUbiOcg" + 
					  "VLpAlzRIJUdGtBzKpD0=" ,  stripLineEnds( SWPSignatureUtilities.calculateSignature( this.set, 
															SWP.JjcRdfC14N_rsa_sha1, 
															PKCS12Utils.decryptPrivateKey( keystore, password ) ) ) );
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
		String localSignature = "Q5giVuVAnlhxj9XEDws5erZA4yBmPHyzrh+BaI/7aIOAH9inXcaav1+yluhA5IG898ycUZsSqQLw" +
						"JdVtQhaZOvEUVggv7WWO0/RpjJnrrm1BpVFKGF8Wb/9mls+FDFAPFR03nPxCvWzpU+n4RRMbWqtf" +
						"6laHEeKwHV64f4L6tcw=";
		
		String localBadSignature = "Q5giVuVAnlhxj9XEDws5erZA4yBmPHyzrh+B/7aIOAH9inXcaav1+yluhA5IG898ycUZsSqQLw" +
						"JdVthaZOvEUVggv7WWO0/RpjJnrrm1pVFKGF8Wb/9mls+FDFAPFR03nPxCvWzpU+n4RRMbWqtf" +
						"6laHEeKwHV64f4L6tcw=";
		
		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															localSignature, 
															certificate ) );
		
		assertFalse( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, 
															localBadSignature, 
															certificate ) );
	}
	
// Moved to SWPSignatureUtilitiesFailingTest
//	/*
//	 * Class under test for boolean validateSignature(NamedGraph, Node, String, X509Certificate, ArrayList)
//	 */
//	
//	public void testValidateSignatureNamedGraphNodeStringX509CertificateArrayList() 
//	throws SWPInvalidKeyException, 
//	SWPSignatureException, 
//	SWPCertificateException, 
//	SWPNoSuchAlgorithmException, 
//	SWPValidationException, SWPAlgorithmNotSupportedException 
//	{
//		Certificate[] certs = PKCS12Utils.getCertChain( keystore, password );
//		
//		ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
//		list.add( (X509Certificate )certs[1]);
//
//		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
//															SWP.JjcRdfC14N_rsa_sha224, //SWP.JjcRdfC14N_rsa_sha1, 
//															signature, 
//															(X509Certificate )certs[0], 
//															list ) );
//		
//		assertFalse( SWPSignatureUtilities.validateSignature( g1, 
//															SWP.JjcRdfC14N_rsa_sha224, //SWP.JjcRdfC14N_rsa_sha1, 
//															badsignature, 
//															(X509Certificate )certs[0], 
//															list ) );
//	}

// Commented the following test because the tested method has been commented.
// See the corresponding source file
//    src/de/fuberlin/wiwiss/ng4j/swp/util/SWPSignatureUtilities.java
// for the reason.
//                       01/10/09 Olaf
// 	/*
// 	 * Class under test for boolean validateSignature(NamedGraph, Node, String, X509Certificate, ArrayList, ArrayList)
// 	 */
// 	public void testValidateSignatureNamedGraphNodeStringX509CertificateArrayListArrayList() 
// 	throws SWPInvalidKeyException, 
// 	SWPSignatureException, 
// 	SWPCertificateException, 
// 	SWPNoSuchAlgorithmException, 
// 	SWPValidationException 
// 	{
// 		Certificate[] certs = PKCS12Utils.getCertChain( keystore, password );
// 		
// 		ArrayList list = new ArrayList();
// 		list.add( certs[1]);
// 		
// 		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
// 															SWP.JjcRdfC14N_rsa_sha224, //SWP.JjcRdfC14N_rsa_sha1, 
// 															signature, 
// 															(X509Certificate )certs[0], 
// 															list, 
// 															list ) );
// 		
// 		assertFalse( SWPSignatureUtilities.validateSignature( g1, 
// 															SWP.JjcRdfC14N_rsa_sha224, //SWP.JjcRdfC14N_rsa_sha1, 
// 															badsignature, 
// 															(X509Certificate )certs[0], 
// 															list, 
// 															list ) );
// 	}
	
	
	public void testVerifyCertificateX509CertificateArrayList() 
	throws CertificateExpiredException, 
	CertificateNotYetValidException, 
	GeneralSecurityException 
	{
		Certificate[] certs = PKCS12Utils.getCertChain( keystore, password );
		ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
		list.add(( X509Certificate )certs[1]);
		// REVISIT If this test fails, it may be a CertificateExpiredException.
		// If so, create a new test keystore (ng4jtest.p12) and update the test code
		// to reflect the new expected values.  See directions in ng4jtest.txt.
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
	public SWPAuthority getAuthority( String keystoreP, String passwordP )
	{
		SWPAuthority auth = new SWPAuthorityImpl();
		auth.setEmail( "mailto:rowland@grid.cx" );
		auth.setID( Node.createURI( "http://grid.cx/rowland" ) );
		Certificate[] chain = PKCS12Utils.getCertChain( keystoreP, passwordP );
		auth.setCertificate( ( X509Certificate )chain[0] );
		
		return auth;
	}
	
	/**
	 * Creates the NamedGraphSet instance under test. Might be overridden by
	 * subclasses to test other NamedGraphSet implementations.
	 */
	protected static SWPNamedGraphSet createSWPNamedGraphSet() throws Exception 
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
 *  (c)   Copyright 2004 - 2010 Rowland Watkins (rowland@grid.cx) 
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