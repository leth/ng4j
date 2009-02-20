// $Id: SWPExample.java,v 1.10 2009/02/20 08:09:51 hartig Exp $
package de.fuberlin.wiwiss.ng4j.examples;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.ng4j.Quad;

import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.SWPWarrant;

import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPCertificateException;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.util.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.FOAF;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;

/**
 * Example showing how to work with NamedGraphs
 */
public class SWPExample {

	public static void main(String[] args) 
	throws SWPBadSignatureException, 
	SWPBadDigestException, 
	SWPCertificateException 
	{
		////////////////////////////////////////////////
		//		 Do some asserting and quoting
		////////////////////////////////////////////////

		// Create a new graphset
		SWPNamedGraphSet graphset = new SWPNamedGraphSetImpl();

		// Create a new NamedGraph in the NamedGraphSet
		SWPNamedGraph graph = (SWPNamedGraph) graphset.createGraph("http://example.org/persons/123");

		// Add information to the NamedGraph
		graph.add(new Triple(Node.createURI("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak"),
		                     Node.createURI("http://xmlns.com/foaf/0.1/name") ,
		                     Node.createLiteral("Richard Cyganiak", null, null)));

		// Create a quad
		Quad quad = new Quad(Node.createURI("http://www.bizer.de/InformationAboutRichard"),
		                     Node.createURI("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak"),
		                     Node.createURI("http://xmlns.com/foaf/0.1/mbox") ,
		                     Node.createURI("mailto:richard@cyganiak.de"));

		// Add the quad to the graphset. This will create a new NamedGraph in the
		// graphset.
		graphset.addQuad(quad);

        // Create a SWP authority for Chris
        //SWPAuthority chris = new SWPAuthorityImpl(Node.createURI("http://www.bizer.de#chris"));
        SWPAuthority chris = new SWPAuthorityImpl(Node.createAnon());
        chris.setLabel("Chris Bizer");
        chris.setEmail("chris@bizer.de");

        // Assert the first graph
		graph.swpAssert(chris);

        // Create a SWP authority for Rowland
        SWPAuthority rowland = new SWPAuthorityImpl(Node.createURI("http://www.ecs.soton.ac.uk#rowland"));
        rowland.setLabel("Rowland Watkins");
        rowland.setEmail("erw01r@ecs.soton.ac.uk");

        ArrayList<Node> RowlandsPropertiestoBePublished = new ArrayList<Node>();
        RowlandsPropertiestoBePublished.add(Node.createURI("http://www.w3.org/2000/01/rdf-schema#label"));
        RowlandsPropertiestoBePublished.add(FOAF.mboxNode);

		graphset.swpAssert(rowland, RowlandsPropertiestoBePublished);

        ArrayList<Node> graphs = new ArrayList<Node>();
        graphs.add(Node.createURI("http://example.org/persons/123"));
        graphs.add(Node.createURI("http://www.bizer.de/InformationAboutRichard"));
		graphset.quoteGraphs(graphs, chris, new ArrayList<Node>());

        graphset.write(System.out, "TRIG", "");
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println();

		////////////////////////////////////////////////////
		// Second graphset. This time with signatures
		////////////////////////////////////////////////////

        // Create a new graphset
		SWPNamedGraphSet graphset2 = new SWPNamedGraphSetImpl();

		quad = new Quad(Node.createURI("http://www.bizer.de/ExampleGraph1"),
		                     Node.createURI("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak"),
		                     Node.createURI("http://xmlns.com/foaf/0.1/mbox") ,
		                     Node.createURI("mailto:richard@cyganiak.de"));
		graphset2.addQuad(quad);

		quad = new Quad(Node.createURI("http://www.bizer.de/ExampleGraph2"),
		                     Node.createURI("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak"),
		                     Node.createURI("http://xmlns.com/foaf/0.1/mbox") ,
		                     Node.createURI("mailto:richard@cyganiak.de"));
		graphset2.addQuad(quad);

		// Add public key to Rowland
        // Has be wait for Rowland to be implemented :-)
		//rowland.setPublicKey();

        // Publish Rowland's public key with the warrant
        //RowlandsPropertiestoBePublished.add(SWP.RSAKey);
        RowlandsPropertiestoBePublished.add(SWP.X509Certificate);
		
		//Add certificate from PKCS12 keystore
		Certificate[] chain = PKCS12Utils.getCertChain( "tests/ng4jtest.p12", "dpuser" );
		rowland.setCertificate( (X509Certificate)chain[0] );

        graphset2.assertWithSignature(rowland,
                SWP.JjcRdfC14N_rsa_sha512,
                SWP.JjcRdfC14N_sha224,
                RowlandsPropertiestoBePublished,
                "tests/ng4jtest.p12",
                "dpuser");

		// Next step would be verification
		graphset2.verifyAllSignatures();
		graphset2.write(System.out, "TRIG", "");
		
        // 1. We would have to fill the http://localhost/trustedinformation
        //    graph with the keys and certificates which we trust.
        //    Maybe we should have a special object for this with methods like addTrustedPublicKey()
        //    which is serialized at the end as the http://localhost/trustedinformation graph.
        //	  Rowland: What do you think?
		// 2. put it into the graphset
        // 3. call verifyAllSignatures() :-)
		
		//Let's do some fancy things:
		// 1. Get all warrant graphs my this authority
		// 2. Test to see if warrant is signed
		// 3. Get the warrant's SWPAuthority
		// 4. List all graphs asserted by this warrant
		ExtendedIterator it = graphset2.getAllWarrants( rowland );
		while ( it.hasNext() )
		{
			SWPWarrant warrant = ( SWPWarrant ) it.next();
			// Let's see if this warrant is signed:
			if ( warrant.isSigned() )
			{
				System.out.println( "This warrant has been signed!" );
			}
			else System.out.println( "This warrant has NOT been signed!" );
			System.out.println();
			
			// Get the warrant's SWPAuthority
			SWPAuthority authority = warrant.getAuthority();
			System.out.println( "Authority's ID: "+authority.getID() );
			System.out.println();
			
			// Get all asserted graphs in the warrant
			// This *will* include the warrant graph itself.
			// Chris, Richard: I assume this should be the case?
			System.out.println( "Asserted Graphs in Warrant:" );
			System.out.println();
			ExtendedIterator itr = warrant.getAssertedGraphs();
			while ( itr.hasNext() )
			{
				System.out.println( itr.next() );
			}
		}

        System.out.println();
        System.out.println("Finished :-)");

	}

}

/*
 *  (c) Copyright 2004 - 2009 Christian Bizer (chris@bizer.de)
 *   All rights reserved.
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