/*
 * Created on 16-Feb-2005
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.utils;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPInvalidKeyException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPNoSuchAlgorithmException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPNoSuchDigestMethodException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPValidationException;
import de.fuberlin.wiwiss.ng4j.swp.utils.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import junit.framework.TestCase;

/**
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
	protected final static String keystore = "/home/erw01r/software/certificates/erw01r.p12";
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
		String digest1 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha1 );
		String digest2 = SWPSignatureUtilities.calculateDigest( g1, SWP.JjcRdfC14N_sha1 );
		String digest3 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha1 );
		String digest4 = SWPSignatureUtilities.calculateDigest( g2, SWP.JjcRdfC14N_sha1 );
		
		assertEquals( digest1, digest2 );
		assertEquals( digest3, digest4 );
		assertNotSame( digest1, digest3 );
		assertNotSame( digest2, digest3 );
		assertNotSame( digest2, digest4 );
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
	SWPValidationException 
	{
		g1signature = SWPSignatureUtilities.calculateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha1, 
															PKCS12Utils.decryptPrivateKey( keystore, password ) );
		testValidateSignatureNamedGraphNodeStringString();
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
		setSignature = SWPSignatureUtilities.calculateSignature( this.set, 
															SWP.JjcRdfC14N_rsa_sha1, 
															PKCS12Utils.decryptPrivateKey( keystore, password ) );
		testValidateSignatureNamedGraphNodeStringString();
	}

	/*
	 * Class under test for boolean validateSignature(NamedGraph, Node, String, String)
	 */
	public void testValidateSignatureNamedGraphNodeStringString() 
	throws SWPInvalidKeyException, 
	SWPSignatureException, 
	SWPNoSuchAlgorithmException, 
	SWPValidationException 
	{
		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha1, g1signature, 
															( X509Certificate )PKCS12Utils.getCertChain( keystore, password )[0] ) );
	}

	/*
	 * Class under test for boolean validateSignature(NamedGraph, Node, String, X509Certificate, ArrayList)
	 */
	/*
	public void testValidateSignatureNamedGraphNodeStringX509CertificateArrayList() 
	{
		//TODO Implement validateSignature().
	}
	*/
	/*
	 * Class under test for boolean validateSignature(NamedGraph, Node, String, X509Certificate, ArrayList, ArrayList)
	 */
	/*
	public void testValidateSignatureNamedGraphNodeStringX509CertificateArrayListArrayList() 
	{
		//TODO Implement validateSignature().
	}
	*/
	/*
	public void testVerifyCertificate() 
	{
		//TODO Implement verifyCertificate().
	}
	*/
	/*
	public void testVerifyCertificationChain() 
	{
		//TODO Implement verifyCertificationChain().
	}
	*/
	/**
	 * 
	 */
	public SWPAuthority getAuthority( String keystore, String password )
	{
		SWPAuthority auth = new SWPAuthorityImpl();
		auth.setEmail( "mailto:rowland@grid.cx" );
		auth.setID(Node.createURI( "http://grid.cx/rowland" ) );
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

}
