//$Id: SWPNamedGraphSetTest.java,v 1.8 2005/03/15 18:44:40 erw Exp $
package de.fuberlin.wiwiss.ng4j.swp;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPSignatureException;
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
	throws SWPSignatureException, 
	SWPCertificateException 
	{
		set.swpAssert( getAuthority( keystore, password ), null );  
		
		Iterator it = set.getAllAssertedGraphs( getAuthority( keystore, password ) );
		assertTrue( it.hasNext() );
		
		Iterator it1 = set.getAllQuotedGraphs( getAuthority( keystore, password ) );
		assertFalse( it1.hasNext() );
		
		Iterator it2 = set.getAllWarrants( getAuthority( keystore, password ) );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = ( SWPWarrant )it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			Iterator itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr2 = warrant.getGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr3 = warrant.getQuotedGraphs();
			assertFalse( itr3.hasNext() );
		}
	}

	/*
	 * Class under test for boolean swpAssert(SWPAuthority)
	 */
	public void testSwpAssertSWPAuthority() 
	throws SWPSignatureException, 
	SWPCertificateException 
	{
		set.swpAssert( getAuthority( keystore, password ) ); 
		
		Iterator it = set.getAllAssertedGraphs( getAuthority( keystore, password ) );
		assertTrue( it.hasNext() );
		
		Iterator it1 = set.getAllQuotedGraphs( getAuthority( keystore, password ) );
		assertFalse( it1.hasNext() );
		
		Iterator it2 = set.getAllWarrants( getAuthority( keystore, password ) );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = ( SWPWarrant )it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			Iterator itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr2 = warrant.getGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr3 = warrant.getQuotedGraphs();
			assertFalse( itr3.hasNext() );
		}
	}

	/*
	 * Class under test for boolean swpQuote(SWPAuthority, ArrayList)
	 */
	public void testSwpQuoteSWPAuthorityArrayList() 
	throws SWPSignatureException, 
	SWPCertificateException 
	{
		set.swpQuote( getAuthority( keystore, password ), null ); 
		
		Iterator it = set.getAllQuotedGraphs( getAuthority( keystore, password ) );
		assertTrue( it.hasNext() );
		
		//Don't forget the warrant graph asserts itself.
		Iterator it1 = set.getAllAssertedGraphs( getAuthority( keystore, password ) );
		assertTrue( it1.hasNext() );
		
		Iterator it2 = set.getAllWarrants( getAuthority( keystore, password ) );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = ( SWPWarrant )it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			Iterator itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr2 = warrant.getGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr3 = warrant.getQuotedGraphs();
			assertTrue( itr3.hasNext() );
		}
	}

	/*
	 * Class under test for boolean swpQuote(SWPAuthority)
	 */
	public void testSwpQuoteSWPAuthority() 
	throws SWPSignatureException, 
	SWPCertificateException 
	{
		set.swpQuote( getAuthority( keystore, password ) ); 
		
		Iterator it = set.getAllQuotedGraphs( getAuthority( keystore, password ) );
		assertTrue( it.hasNext() );
		
		// Don't forget the warrant graph asserts itself.
		Iterator it1 = set.getAllAssertedGraphs( getAuthority( keystore, password ) );
		assertTrue( it1.hasNext() );
		
		Iterator it2 = set.getAllWarrants( getAuthority( keystore, password ) );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = ( SWPWarrant )it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			Iterator itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr2 = warrant.getGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr3 = warrant.getQuotedGraphs();
			assertTrue( itr3.hasNext() );
		}
	}
	/*
	 *  Class under test for boolean assertWithSignature(SWPAuthority, Node, Node, ArrayList, String, String)
	 */
	public void testAssertWithSignature() 
	throws SWPBadSignatureException, 
	SWPBadDigestException, 
	SWPSignatureException, 
	SWPCertificateException 
	{
		assertTrue( set.assertWithSignature( getAuthority( keystore, password ), 
				SWP.JjcRdfC14N_rsa_sha384, 
				SWP.JjcRdfC14N_sha384, 
				null, 
				keystore, 
				password ) );
		
		assertTrue( set.verifyAllSignatures() );
		
		assertFalse( set.assertWithSignature( getAuthority( keystore, password ), 
				SWP.JjcRdfC14N_rsa_sha384, 
				SWP.JjcRdfC14N_sha384, 
				null, 
				keystore, 
				password ) );
		
		
		Iterator it = set.getAllAssertedGraphs( getAuthority( keystore, password ) );
		assertTrue( it.hasNext() );
		
		Iterator it1 = set.getAllQuotedGraphs( getAuthority( keystore, password ) );
		assertFalse( it1.hasNext() );
		
		Iterator wit = set.getAllWarrants( getAuthority( keystore, password ) );
		assertTrue( wit.hasNext() );
		while ( wit.hasNext() )
		{
			SWPWarrant warrant =  ( SWPWarrant ) wit.next();
			Iterator itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr1 = warrant.getQuotedGraphs();
			assertFalse( itr1.hasNext() );
			
			Iterator itr2 = warrant.getGraphs();
			assertTrue( itr2.hasNext() );
			
			assertNotNull( warrant.getSignature() );
			assertNotNull( warrant.getAuthority() );
			
			assertTrue( warrant.isSigned() );
			
		}
	}

	/*
	 * Class under test for boolean quoteWithSignature(SWPAuthority, Node, Node, ArrayList, String, String)
	 */
	public void testQuoteWithSignature() 
	throws SWPBadSignatureException, 
	SWPBadDigestException 
	{
		assertTrue( set.quoteWithSignature( getAuthority( keystore, password ), 
				SWP.JjcRdfC14N_rsa_sha512, 
				SWP.JjcRdfC14N_sha512, 
				null, 
				keystore, 
				password ) );
		
		assertTrue( set.verifyAllSignatures() );
		
		Iterator it = set.getAllQuotedGraphs( getAuthority( keystore, password ) );
		assertTrue( it.hasNext() );
		
		//Never forget the warrant graph asserts itself!
		Iterator it1 = set.getAllAssertedGraphs( getAuthority( keystore, password ) );
		assertTrue( it1.hasNext() );
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

	
	public void testAssertGraphsWithSignature() 
	throws SWPBadSignatureException,
	SWPBadDigestException, 
	SWPSignatureException, 
	SWPCertificateException 
	{
		ArrayList list = new ArrayList();
		list.add(uri1);
		list.add(uri2);
		list.add(uri3);
		list.add(uri4);
		
		set.assertGraphsWithSignature( list, 
									getAuthority( keystore, password ), 
									SWP.JjcRdfC14N_rsa_sha256, 
									SWP.JjcRdfC14N_sha384, 
									null, 
									keystore, 
									password );
		
		Iterator it = set.getAllAssertedGraphs( getAuthority( keystore, password ) );
		assertTrue( it.hasNext() );
		
		Iterator it1 = set.getAllQuotedGraphs( getAuthority( keystore, password ) );
		assertFalse( it1.hasNext() );
		
		Iterator wit = set.getAllWarrants( getAuthority( keystore, password ) );
		assertTrue( wit.hasNext() );
		while ( wit.hasNext() )
		{
			SWPWarrant warrant =  ( SWPWarrant ) wit.next();
			Iterator itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			Iterator itr1 = warrant.getQuotedGraphs();
			assertFalse( itr1.hasNext() );
			
			Iterator itr2 = warrant.getGraphs();
			assertTrue( itr2.hasNext() );
			
			assertNotNull( warrant.getSignature() );
			assertNotNull( warrant.getAuthority() );
			
			assertTrue( warrant.isSigned() );
			
		}
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