//$Id: SWPNamedGraphSetTest.java,v 1.6 2005/02/24 13:29:50 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.swp;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.util.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;

import junit.framework.TestCase;

/**
 * @author Rowland Watkins
 */
public class SWPNamedGraphSetTest extends TestCase 
{

	protected final static String uri1 = "http://example.org/graph1";
	protected final static String uri2 = "http://example.org/graph2";
	protected final static String uri3 = "http://example.org/graph3";
	protected final static String uri4 = "http://example.org/graph4";
	protected final static Node foo = Node.createURI("http://example.org/#foo");
	protected final static Node bar = Node.createURI("http://example.org/#bar");
	protected final static Node baz = Node.createURI("http://example.org/#baz");
	protected final static String keystore = "tests/test.p12";
	protected final static String password = "dpuser";
	
	protected SWPNamedGraphSet set;
	protected ArrayList list = new ArrayList();
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		this.set = createSWPNamedGraphSet();
		NamedGraph g1 = this.set.createGraph( uri1 );
		NamedGraph g2 = this.set.createGraph( uri2 );
		NamedGraph g3 = this.set.createGraph( uri3 );
		NamedGraph g4 = this.set.createGraph( uri4 );
		g1.add( new Triple( foo, bar, baz ) );
		g2.add( new Triple( bar, baz, foo ) );
		g3.add( new Triple( baz, bar, foo ) );
		g4.add( new Triple( bar, foo, baz ) );
		list.add( g3.getGraphName() );
		list.add( g4.getGraphName() );
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception 
	{
		this.set.close();
	}

	/*
	 * Class under test for boolean swpAssert(SWPAuthority, ArrayList)
	 */
	public void testSwpAssertSWPAuthorityArrayList() 
	{
		//TODO Implement swpAssert().
	}

	/*
	 * Class under test for boolean swpAssert(SWPAuthority)
	 */
	public void testSwpAssertSWPAuthority() 
	{
		//TODO Implement swpAssert().
	}

	/*
	 * Class under test for boolean swpQuote(SWPAuthority, ArrayList)
	 */
	public void testSwpQuoteSWPAuthorityArrayList() 
	{
		//TODO Implement swpQuote().
	}

	/*
	 * Class under test for boolean swpQuote(SWPAuthority)
	 */
	public void testSwpQuoteSWPAuthority() 
	{
		//TODO Implement swpQuote().
	}
	/*
	 *  Class under test for boolean assertWithSignature(SWPAuthority, Node, Node, ArrayList, String, String)
	 */
	public void testAssertWithSignature() 
	throws SWPBadSignatureException, 
	SWPBadDigestException 
	{
		assertTrue( set.assertWithSignature( getAuthority( keystore, password ), 
				SWP.JjcRdfC14N_rsa_sha1, 
				SWP.JjcRdfC14N_sha1, 
				null, 
				keystore, 
				password ) );
//	TODO actual JUnit asserts
//		set.write( System.out, "TRIG", "" );
		assertTrue( set.verifyAllSignatures() );
//		set.write( System.out, "TRIG", "" );
	}

	/*
	 * Class under test for boolean quoteWithSignature(SWPAuthority, Node, Node, ArrayList, String, String)
	 */
	public void testQuoteWithSignature() 
	throws SWPBadSignatureException, 
	SWPBadDigestException 
	{
		assertTrue( set.quoteWithSignature( getAuthority( keystore, password ), 
				SWP.JjcRdfC14N_rsa_sha1, 
				SWP.JjcRdfC14N_sha1, 
				null, 
				keystore, 
				password ) );
		assertTrue( set.verifyAllSignatures() );
	}

	
	/*
	 *	Class under test for boolean  
	 */
	public void testAssertGraphs() 
	{
		assertTrue( set.assertGraphs( list, getAuthority( keystore, password ), null ) );
	}

	public void testQuoteGraphs() 
	{
		assertTrue( set.quoteGraphs( list, getAuthority( keystore, password ), null ) );
	}

	/*
	public void testAssertGraphsWithSignature() 
	{
		//TODO Implement assertGraphsWithSignature().
	}
	*/
	
	public SWPAuthority getAuthority( String keystore, String password )
	{
		SWPAuthority auth = new SWPAuthorityImpl();
		auth.setEmail("mailto:rowland@grid.cx");
		auth.setID(Node.createURI( "http://grid.cx/rowland" ) );
		Certificate[] chain = PKCS12Utils.getCertChain( keystore, password );
		auth.setCertificate( (X509Certificate)chain[0] );
		
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
