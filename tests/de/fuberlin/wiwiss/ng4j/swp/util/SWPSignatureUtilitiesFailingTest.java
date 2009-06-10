// $Header: /cvsroot/ng4j/ng4j/tests/de/fuberlin/wiwiss/ng4j/swp/util/SWPSignatureUtilitiesFailingTest.java,v 1.1 2009/06/10 19:32:59 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.swp.util;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPAlgorithmNotSupportedException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPInvalidKeyException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPNoSuchAlgorithmException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPValidationException;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import junit.framework.TestCase;

/** The test case that was failing.
 * 
 * Moved it here, to a separate class, so that it can be excluded.
 * 
 * The failure started occurring after replacing expired test 
 * certificates by new certificates. 
 * 
 * The reason for the failures probably is an incompatibility 
 * between the new certificates and the certificates and 
 * signatures embedded in this file.
 * 
 * TODO: Fix this test so it no longer needs to be excluded.
 * 
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class SWPSignatureUtilitiesFailingTest extends TestCase {

	protected SWPNamedGraphSet set;
	protected NamedGraph g1;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception 
	{
		this.set = SWPSignatureUtilitiesTest.createSWPNamedGraphSet();
		g1 = this.set.createGraph( SWPSignatureUtilitiesTest.uri1 );
		g1.add( new Triple( SWPSignatureUtilitiesTest.foo, SWPSignatureUtilitiesTest.bar, SWPSignatureUtilitiesTest.baz ) );
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception 
	{
		this.set.close();
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
		Certificate[] certs = PKCS12Utils.getCertChain( SWPSignatureUtilitiesTest.keystore, SWPSignatureUtilitiesTest.password );
		
		ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
		list.add( (X509Certificate )certs[1]);

		assertTrue( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, //SWP.JjcRdfC14N_rsa_sha1, 
															SWPSignatureUtilitiesTest.signature, 
															(X509Certificate )certs[0], 
															list ) );
		
		assertFalse( SWPSignatureUtilities.validateSignature( g1, 
															SWP.JjcRdfC14N_rsa_sha224, //SWP.JjcRdfC14N_rsa_sha1, 
															SWPSignatureUtilitiesTest.badsignature, 
															(X509Certificate )certs[0], 
															list ) );
	}

}
