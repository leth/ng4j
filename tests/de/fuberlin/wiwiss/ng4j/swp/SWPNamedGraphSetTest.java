/*
 * Created on 01-Feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.fuberlin.wiwiss.ng4j.swp;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.signature.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.signature.keystores.pkcs12.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import junit.framework.TestCase;

/**
 * @author erw01r
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SWPNamedGraphSetTest extends TestCase 
{
	protected final static String uri1 = "http://example.org/graph1";
	protected final static String uri2 = "http://example.org/graph2";
	protected final static Node node1 = Node.createURI("http://example.org/graph1");
	protected final static Node node2 = Node.createURI("http://example.org/graph2");
	protected final static Node foo = Node.createURI("http://example.org/#foo");
	protected final static Node bar = Node.createURI("http://example.org/#bar");
	protected final static Node baz = Node.createURI("http://example.org/#baz");
	protected final static Node variableA = Node.createVariable("a");
	protected final static String keystore = "/home/erw01r/software/certificates/erw01r.p12";
	protected final static String password = "dpuser";
	
	
	protected SWPNamedGraphSet set;

	public void setUp() throws Exception 
	{
		this.set = createSWPNamedGraphSet();
		NamedGraph g1 = this.set.createGraph( uri1 );
		NamedGraph g2 = this.set.createGraph( uri2 );
		g1.add( new Triple( foo, bar, baz ) );
		g2.add( new Triple( bar, baz, foo ) );
		
		set.assertWithSignature( getAuthority( keystore, password ), 
								SWP.JjcRdfC14N_rsa_sha1.asNode(), 
								SWP.JjcRdfC14N_sha1.asNode(), 
								null, 
								keystore, 
								password );
		set.write(System.out, "TRIG", "");
	}
	
	public void testAssertedWithSignature() throws SWPBadSignatureException, SWPBadDigestException
	{
		assertTrue( set.assertWithSignature( getAuthority( keystore, password ), 
				SWP.JjcRdfC14N_rsa_sha1.asNode(), 
				SWP.JjcRdfC14N_sha1.asNode(), 
				null, 
				keystore, 
				password ) );
		set.write(System.out, "TRIG", "");
	}
	
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
	
	public void tearDown() throws Exception 
	{
		this.set.close();
	}
	
	public void setUpNGS()
	{
		
	}
	
	
}
