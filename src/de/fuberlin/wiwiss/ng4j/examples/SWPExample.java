// $Id: SWPExample.java,v 1.5 2005/03/15 15:57:38 erw Exp $
package de.fuberlin.wiwiss.ng4j.examples;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph;

import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.util.PKCS12Utils;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.FOAF;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;

/**
 * Example showing how to work with NamedGraphs
 */
public class SWPExample {

	public static void main(String[] args) throws SWPBadSignatureException, SWPBadDigestException {
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

        ArrayList RowlandsPropertiestoBePublished = new ArrayList();
        RowlandsPropertiestoBePublished.add(Node.createURI("http://www.w3.org/2000/01/rdf-schema#label"));
        RowlandsPropertiestoBePublished.add(FOAF.mboxNode);

		graphset.swpAssert(rowland, RowlandsPropertiestoBePublished);

        ArrayList graphs = new ArrayList();
        graphs.add(Node.createURI("http://example.org/persons/123"));
        graphs.add(Node.createURI("http://www.bizer.de/InformationAboutRichard"));
		graphset.quoteGraphs(graphs, chris, new ArrayList());

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
		Certificate[] chain = PKCS12Utils.getCertChain( "tests/test.p12", "dpuser" );
		rowland.setCertificate( (X509Certificate)chain[0] );

        graphset2.assertWithSignature(rowland,
                SWP.JjcRdfC14N_rsa_sha512,
                SWP.JjcRdfC14N_sha224,
                RowlandsPropertiestoBePublished,
                "tests/test.p12",
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
		// 1. List all assertedGraphs

        System.out.println();
        System.out.println("Finished :-)");

	}

}
