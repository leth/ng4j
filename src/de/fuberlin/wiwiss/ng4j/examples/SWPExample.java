// $Id: SWPExample.java,v 1.3 2005/02/22 11:40:47 erw Exp $
package de.fuberlin.wiwiss.ng4j.examples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphStatement;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraphSet;
import de.fuberlin.wiwiss.ng4j.swp.SWPNamedGraph;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadDigestException;
import de.fuberlin.wiwiss.ng4j.swp.exceptions.SWPBadSignatureException;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPNamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.SWPAuthority;
import de.fuberlin.wiwiss.ng4j.swp.impl.SWPAuthorityImpl;

import de.fuberlin.wiwiss.ng4j.swp.vocabulary.FOAF;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;

/**
 * Example showing how to work with NamedGraphs
 */
public class SWPExample {

	public static void main(String[] args) throws IOException, SWPBadSignatureException, SWPBadDigestException {
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

		// Serialize the model to a TriG file +++++++++ Doesn't work :-(
		//OutputStream out = new FileOutputStream("C:/model.trig");
		//Model model = graphset.asJenaModel("http://example.org") ;
		//model.write(out, "TRIG");

		// Serialize the model to a TriG file +++++++++ Doesn't work :-(
		//OutputStream out = new FileOutputStream("C:/graphset.trig");
		//graphset.write(out, "TRIG");

        graphset.write(System.out, "TRIX", "");
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

		// Add private and public key to Rowland
        // Has be wait for Rowland to be implemented :-)
		//rowland.setPrivateKey();
		//rowland.setPublicKey();

        // Publish Rowland's public key with the warrant
        // RowlandsPropertiestoBePublished.add(SWP.RSAKeyNode);
        // or this certificate: RowlandsPropertiestoBePublished.add(SWP.X509CertificateNode);

        graphset2.assertWithSignature(rowland, SWP.JjcRdfC14N_rsa_sha1, SWP.JjcRdfC14N_sha1, RowlandsPropertiestoBePublished, "/home/erw01r/software/certificates/erw01r.p12", "dpuser");

        graphset2.write(System.out, "TRIX", "");

		// Next step would be verification

        // 1. We would have to fill the http://localhost/trustedinformation
        //    graph with the keys and vertificates which we trust.
        //    Maybe we should have a special object for this with methods like addTrustedPublicKey()
        //    which is serialized at the end as the http://localhost/trustedinformation graph.
        //	  Rowland: What do you think?
		// 2. put it into the graphset
        // 3. call verifyAllSignatures() :-)

        System.out.println();
        System.out.println("Finished :-)");

	}

}
