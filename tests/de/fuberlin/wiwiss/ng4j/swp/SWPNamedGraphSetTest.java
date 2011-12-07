//$Id: SWPNamedGraphSetTest.java,v 1.21 2011/07/15 22:21:22 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.swp;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.util.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.util.SWPSignatureUtilities;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP_V;

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
	protected final static String keystore = "tests/ng4jtest.p12";
	protected final static String password = "dpuser";

	protected SWPNamedGraphSet set;
	protected ArrayList<Node> list = new ArrayList<Node>();
	protected SWPAuthority authority;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
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
		authority = getAuthority( keystore, password );
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception 
	{
		this.set.close();
	}

	/** Test that add description works. */
    public void testAddDescriptionToGraph() { 
		assertEquals("http://example.org/graph1 {http://example.org/#foo @http://example.org/#bar http://example.org/#baz}",set.getGraph(uri1).toString());
		assertEquals(1, set.getGraph(uri1).size());
    	ArrayList<Node> listOfAuthorityProperties = new ArrayList<Node>();
    	listOfAuthorityProperties.add(SWP.RSAKey);
    	listOfAuthorityProperties.add(SWP.X509Certificate);
    	try { 
    		authority.addDescriptionToGraph(set.getGraph(uri1), listOfAuthorityProperties);
    		fail("Should have bombed");
    	} catch (NullPointerException expected) { 
    		expected = null;
    	}
    	listOfAuthorityProperties = new ArrayList<Node>();
    	listOfAuthorityProperties.add(SWP.X509Certificate);
		authority.addDescriptionToGraph(set.getGraph(uri1), listOfAuthorityProperties);
		// NOTE Sun Base64Encoder formats with line ends.
		// REVISIT  Comparison failure after re-creating keystore
		// What needs to be done to find the correct value with which to replace?
		// Did by copying the expected value as shown by the error output of the test.
		assertEquals("http://example.org/graph1 {http://example.org/graph1 " + 
				"@http://www.w3.org/2004/03/trix/swp-2/authority http://grid.cx/rowland; http://grid.cx/rowland @http://www.w3.org/2004/03/trix/swp-2/X509Certificate " + 
				 "\"MIICEzCCAXygAwIBAgIGA" +
				 	"TEu1tWgMA0GCSqGSIb3DQEBBQUAMEgxGDAWBgNVBAMMD0NOPU5HNEog" + 
					"dGVzdCBDQTESMBAGA1UECgwJTkc0SiB0ZXN0MQswCQYDVQQIDAJTSDELMAkGA1UEBhMCREUwHhcN" + 
					"MTEwNzE1MTcyNzM2WhcNMTQwNDEwMTcyNzM2WjBUMRUwEwYDVQQDDAxDTj1ORzRKIHRlc3QxEjAQ" +  
					"BgNVBAoMCU5HNEogdGVzdDENMAsGA1UEBwwES2llbDELMAkGA1UECAwCU0gxCzAJBgNVBAYTAkRF" + 
					"MIGdMA0GCSqGSIb3DQEBAQUAA4GLADCBhwKBgQCe9DoT7WPK7+MxnLCg81cKD46/3Goxp5fO4oc8" + 
					"LO4DZA6JsPunSJ9rGC5LEM5HrPP11D22hbrVYILej0MM7bn0jHVNIERUyg4cek9m6JQJJWaVOK+M" + 
					"M8mFKqiQilCf5XvSKog1ECqNM/4adzoVPLoupJDdo8jGnS2TBqJRaxqAJQIBAzANBgkqhkiG9w0B" + 
					"AQUFAAOBgQCYsJWJgZcobaAUJOe6hd3cz/5jCUt6YyZ5JiNLYroZaku8MNyPrkFHILMWMEFZSAJ4" + 
					"dRnS+mG7T4oSmPDr0opBDtISXim1ro8ulDBcYyFN2FIABwDvKhljhRvWogAQYGcYcFpgW6+ZBrbh" + 
					"YTiIPoMe6doxDONBYWtqLLMtRS9yaA==" + 
					"\"^^http://www.w3.org/2001/XMLSchema#base64Binary; " + 
					"http://example.org/#foo @http://example.org/#bar http://example.org/#baz}",
					set.getGraph(uri1).toString());
		assertEquals(3,set.getGraph(uri1).size());
    }
	public void testNoAssertedGraphs() {
		assertFalse(set.getAllAssertedGraphs(authority).hasNext());
	}
	
	public void testNoQuotedGraphs() {
		assertFalse(set.getAllQuotedGraphs(authority).hasNext());
	}
	
	public void testGetAllAssertedGraphsReturnsNamedGraph() {
		((SWPNamedGraph) set.getGraph(uri1)).swpAssert(authority);
		ExtendedIterator<NamedGraph> it = set.getAllAssertedGraphs(authority);
		assertTrue(it.hasNext());
		assertSame(set.getGraph(uri1), it.next());
	}
	
	public void testAssertedGraphIsNotQuoted() {
		((SWPNamedGraph) set.getGraph(uri1)).swpAssert(authority);
		assertFalse(set.getAllQuotedGraphs(authority).hasNext());
	}
	
	public void testGetAllQuotedGraphsReturnsNamedGraph() {
		((SWPNamedGraph) set.getGraph(uri1)).swpQuote(authority);
		ExtendedIterator<NamedGraph> it = set.getAllQuotedGraphs(authority);
		assertTrue(it.hasNext());
		assertSame(set.getGraph(uri1), it.next());
	}
	
	public void testQuotedGraphIsNotAsserted() {
		((SWPNamedGraph) set.getGraph(uri1)).swpQuote(authority);
		assertFalse(set.getAllAssertedGraphs(authority).hasNext());
	}
	
	/*
	 * Class under test for boolean swpAssert(SWPAuthority, ArrayList)
	 */
	public void testSwpAssertSWPAuthorityArrayList() 
	throws SWPSignatureException, 
	SWPCertificateException 
	{
		set.swpAssert( authority, null );  
		
		ExtendedIterator<NamedGraph> it = set.getAllAssertedGraphs( authority );
		assertTrue( it.hasNext() );
		
		ExtendedIterator<NamedGraph> it1 = set.getAllQuotedGraphs( authority );
		assertFalse( it1.hasNext() );
		
		ExtendedIterator<SWPWarrant> it2 = set.getAllWarrants( authority );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			ExtendedIterator<String> itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			ExtendedIterator<String> itr2 = warrant.getGraphs();
			assertTrue( itr2.hasNext() );
			
			ExtendedIterator<String> itr3 = warrant.getQuotedGraphs();
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
		set.swpAssert( authority ); 
		
		ExtendedIterator<NamedGraph> it = set.getAllAssertedGraphs( authority );
		assertTrue( it.hasNext() );
		
		ExtendedIterator<NamedGraph> it1 = set.getAllQuotedGraphs( authority );
		assertFalse( it1.hasNext() );
		
		ExtendedIterator<SWPWarrant> it2 = set.getAllWarrants( authority );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			ExtendedIterator<String> itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			ExtendedIterator<String> itr2 = warrant.getGraphs();
			assertTrue( itr2.hasNext() );
			
			ExtendedIterator<String> itr3 = warrant.getQuotedGraphs();
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
		set.swpQuote( authority, null ); 
		
		ExtendedIterator<NamedGraph> it = set.getAllQuotedGraphs( authority );
		assertTrue( it.hasNext() );
		
		//Don't forget the warrant graph asserts itself.
		ExtendedIterator<NamedGraph> it1 = set.getAllAssertedGraphs( authority );
		assertTrue( it1.hasNext() );
		
		ExtendedIterator<SWPWarrant> it2 = set.getAllWarrants( authority );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			ExtendedIterator<String> itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			ExtendedIterator<String> itr2 = warrant.getGraphs();
			assertTrue( itr2.hasNext() );
			
			ExtendedIterator<String> itr3 = warrant.getQuotedGraphs();
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
		set.swpQuote( authority ); 
		
		ExtendedIterator<NamedGraph> it = set.getAllQuotedGraphs( authority );
		assertTrue( it.hasNext() );
		
		// Don't forget the warrant graph asserts itself.
		ExtendedIterator<NamedGraph> it1 = set.getAllAssertedGraphs( authority );
		assertTrue( it1.hasNext() );
		
		ExtendedIterator<SWPWarrant> it2 = set.getAllWarrants( authority );
		assertTrue( it2.hasNext() );
		while ( it2.hasNext() )
		{
			SWPWarrant warrant = it2.next();
			
			assertFalse( warrant.isSigned() );
			
			assertNull( warrant.getSignature() );
			
			assertNotNull( warrant.getAuthority() );
			
			ExtendedIterator<String> itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			ExtendedIterator<String> itr2 = warrant.getGraphs();
			assertTrue( itr2.hasNext() );
			
			ExtendedIterator<String> itr3 = warrant.getQuotedGraphs();
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
		assertTrue( set.assertWithSignature( authority, 
				SWP.JjcRdfC14N_rsa_sha384, 
				SWP.JjcRdfC14N_sha384, 
				null, 
				keystore, 
				password ) );
		
		assertTrue( set.verifyAllSignatures() );
		
		assertFalse( set.assertWithSignature( authority, 
				SWP.JjcRdfC14N_rsa_sha384, 
				SWP.JjcRdfC14N_sha384, 
				null, 
				keystore, 
				password ) );
		
		
		ExtendedIterator<NamedGraph> it = set.getAllAssertedGraphs( authority );
		assertTrue( it.hasNext() );
		
		ExtendedIterator<NamedGraph> it1 = set.getAllQuotedGraphs( authority );
		assertFalse( it1.hasNext() );
		
		ExtendedIterator<SWPWarrant> wit = set.getAllWarrants( authority );
		assertTrue( wit.hasNext() );
		while ( wit.hasNext() )
		{
			SWPWarrant warrant =  wit.next();
			ExtendedIterator<String> itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			ExtendedIterator<String> itr1 = warrant.getQuotedGraphs();
			assertFalse( itr1.hasNext() );
			
			ExtendedIterator<String> itr2 = warrant.getGraphs();
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
		assertTrue( set.quoteWithSignature( authority, 
				SWP.JjcRdfC14N_rsa_sha512, 
				SWP.JjcRdfC14N_sha512, 
				null, 
				keystore, 
				password ) );
		
		assertTrue( set.verifyAllSignatures() );
		
		ExtendedIterator<NamedGraph> it = set.getAllQuotedGraphs( authority );
		assertTrue( it.hasNext() );
		
		//Never forget the warrant graph asserts itself!
		ExtendedIterator<NamedGraph> it1 = set.getAllAssertedGraphs( authority );
		assertTrue( it1.hasNext() );
	}

	
	/**
	 *	Class under test for boolean  
	 */
	public void testAssertGraphs() 
	{
		assertTrue( set.assertGraphs( list, authority, null ) );
	}

	public void testQuoteGraphs() 
	{
		assertTrue( set.quoteGraphs( list, authority, null ) );
	}

	
	public void testAssertGraphsWithSignature() 
	throws SWPBadSignatureException,
	SWPBadDigestException, 
	SWPSignatureException, 
	SWPCertificateException 
	{
		ArrayList<String> uriList = new ArrayList<String>();
		uriList.add(uri1);
		uriList.add(uri2);
		uriList.add(uri3);
		uriList.add(uri4);
		
		set.assertGraphsWithSignature( uriList, 
									authority, 
									SWP.JjcRdfC14N_rsa_sha256, 
									SWP.JjcRdfC14N_sha384, 
									null, 
									keystore, 
									password );
		
		ExtendedIterator<NamedGraph> it = set.getAllAssertedGraphs( authority );
		assertTrue( it.hasNext() );
		
		ExtendedIterator<NamedGraph> it1 = set.getAllQuotedGraphs( authority );
		assertFalse( it1.hasNext() );
		
		ExtendedIterator<SWPWarrant> wit = set.getAllWarrants( authority );
		assertTrue( wit.hasNext() );
		while ( wit.hasNext() )
		{
			SWPWarrant warrant =  wit.next();
			ExtendedIterator<String> itr = warrant.getAssertedGraphs();
			assertTrue( itr.hasNext() );
			
			ExtendedIterator<String> itr1 = warrant.getQuotedGraphs();
			assertFalse( itr1.hasNext() );
			
			ExtendedIterator<String> itr2 = warrant.getGraphs();
			assertTrue( itr2.hasNext() );
			
			assertNotNull( warrant.getSignature() );
			assertEquals("Signature object: SHA256withRSA<not initialized>", warrant.getSignature().toString());
			assertNotNull( warrant.getAuthority() );
			
			assertTrue( warrant.isSigned() );
			
		}
	}
	
	
	public SWPAuthority getAuthority( String keystoreP, String passwordP )
	{
		SWPAuthority auth = new SWPAuthorityImpl();
		auth.setEmail("mailto:rowland@grid.cx");
		auth.setID(Node.createURI( "http://grid.cx/rowland" ) );
		Certificate[] chain = PKCS12Utils.getCertChain( keystoreP, passwordP );
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

    /**
     * Check that verifyAllSignatures adds the verifiedSignatures graph to the
     * graph set and all signatures are valid.
     * @throws Exception
     */
    public void testVerifyAllSignatures() throws Exception {
        final Certificate[] chain = PKCS12Utils
                .getCertChain(keystore, password);

        final SWPAuthority auth = new SWPAuthorityImpl(Node
                .createURI("http://zedlitz.de#jesper"));
        auth.setEmail("mailto:jesper@zedlitz.de");
        auth.setCertificate((X509Certificate) chain[0]);

        set.assertWithSignature(auth, SWP.JjcRdfC14N_rsa_sha1,
                SWP.JjcRdfC14N_sha1, null, keystore, password);

        set.verifyAllSignatures();

        final NamedGraph verifiedSignatures = set.getGraph(SWP_V.default_graph);
        assertNotNull("verifiedSignatures graph added", verifiedSignatures);

        assertTrue("every signature valid", SWPSignatureUtilities
                .isEverySignatureValid(verifiedSignatures));
    }

    /**
     * Checking a signature must also work after the graph as been serialized
     * and reread.
     * @throws Exception
     */
    public void testVerifyAllSignatures_Serialized() throws Exception {
        final String serializedGraph;

        {
            final Certificate[] chain = PKCS12Utils.getCertChain(keystore,
                    password);

            final SWPAuthority auth = new SWPAuthorityImpl(Node
                    .createURI("http://zedlitz.de#jesper"));
            auth.setEmail("mailto:jesper@zedlitz.de");
            auth.setCertificate((X509Certificate) chain[0]);

            set.assertWithSignature(auth, SWP.JjcRdfC14N_rsa_sha1,
                    SWP.JjcRdfC14N_sha1, null, keystore, password);
            final StringWriter sw = new StringWriter();
            set.write(sw, "TRIG", "");
            serializedGraph = sw.toString();
        }

        final SWPNamedGraphSet newGraphSet = new SWPNamedGraphSetImpl();
        newGraphSet.read(new StringReader(serializedGraph), "TRIG", "");

        final SWPAuthority auth = new SWPAuthorityImpl(Node
                .createURI("http://zedlitz.de#jesper"));
        auth.setEmail("mailto:jesper@zedlitz.de");

        newGraphSet.verifyAllSignatures();

        final NamedGraph verifiedSignatures = newGraphSet.getGraph(SWP_V.default_graph);
        assertTrue("every signature valid", SWPSignatureUtilities
                .isEverySignatureValid(verifiedSignatures));   
    }
    
    /**
     * When a signed graph has been manipulated the "verifiedSignatures" must
     * not contain valid signatures.
     * 
     * @throws Exception
     */
    public void testVerifyAllSignatures_SerializedManipulated()
            throws Exception {
        final String serializedGraph;

        {
            final Certificate[] chain = PKCS12Utils.getCertChain(keystore,
                    password);

            final SWPAuthority auth = new SWPAuthorityImpl(Node
                    .createURI("http://zedlitz.de#jesper"));
            auth.setEmail("mailto:jesper@zedlitz.de");
            auth.setCertificate((X509Certificate) chain[0]);

            set.assertWithSignature(auth, SWP.JjcRdfC14N_rsa_sha1,
                    SWP.JjcRdfC14N_sha1, null, keystore, password);
            final StringWriter sw = new StringWriter();
            set.write(sw, "TRIG", "");
            serializedGraph = sw.toString();
        }

        /* manipulate the serialized graph */
        final String manipulatedGraph = serializedGraph.replaceAll(
                "http://example.org/graph1", "http://example.org/graphA");

        final SWPNamedGraphSet newGraphSet = new SWPNamedGraphSetImpl();
        newGraphSet.read(new StringReader(manipulatedGraph), "TRIG", "");

        final SWPAuthority auth = new SWPAuthorityImpl(Node
                .createURI("http://zedlitz.de#jesper"));
        auth.setEmail("mailto:jesper@zedlitz.de");

        newGraphSet.verifyAllSignatures();

        final NamedGraph verifiedSignatures = newGraphSet
                .getGraph(SWP_V.default_graph);
        assertFalse("manipulated graph", SWPSignatureUtilities
                .isEverySignatureValid(verifiedSignatures));
    }

}

/*
 * (c) Copyright 2004 - 2010 Rowland Watkins (rowland@grid.cx) All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */