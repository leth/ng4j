// $Id: NamedGraphExample.java,v 1.1 2004/10/23 13:31:28 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.examples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphStatement;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Example showing how to work with NamedGraphs
 */
public class NamedGraphExample {

	public static void main(String[] args) throws IOException {
		////////////////////////////////////////////////
		//		 Operations on GraphSet Level
		////////////////////////////////////////////////

		// Create a new graphset
		NamedGraphSet graphset = new NamedGraphSetImpl();

		// Create a new NamedGraph in the NamedGraphSet
		NamedGraph graph = graphset.createGraph("http://example.org/persons/123");

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

		// Find information about Richard across all graphs in the graphset
		Iterator it = graphset.findQuads( 
				Node.ANY, 
				Node.createURI("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak"),
				Node.ANY,
				Node.ANY);

		while (it.hasNext()) {
			Quad q = (Quad) it.next();
			System.out.println("Source: " + q.getGraphName());
			System.out.println("Statement: " + q.getTriple());
			// (This will output the two statements created above)
		}

		// Count all graphs in the graphset (2)
		System.out.println("The graphset contains " + graphset.countGraphs() + " graphs.");

		// Serialize the graphset to a TriX file
		OutputStream out = new FileOutputStream("C:/graphset.trix");
		graphset.write(out, "TRIX"); 

		////////////////////////////////////////////////
		//		 Operations on Model Level
		////////////////////////////////////////////////

		// Get a Jena Model view on the GraphSet
		Model model = graphset.asJenaModel("http://example.org/defaultgraph");

		// Add provenance information about a graph
		Resource informationAboutRichard = model.getResource("http://www.bizer.de/InformationAboutRichard");
		informationAboutRichard.addProperty(model.createProperty("http://purl.org/dc/elements/1.1/author"), "Chris Bizer");
		informationAboutRichard.addProperty(model.createProperty("http://purl.org/dc/elements/1.1/date"), "09/15/2004");

		// Get a Jena resource and statement
		Resource richard = model.getResource("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak");
			
		NamedGraphStatement mboxStmt = 
			(NamedGraphStatement) richard.getProperty(model.getProperty("http://xmlns.com/foaf/0.1/mbox"));

		// Get an iterator over all graphs which contain the statement.
		it = mboxStmt.listGraphNames();

		// So who has published my email address all over the Web??!?
		while (it.hasNext()) {
			Resource g = (Resource) it.next();
			System.out.println("GraphName: " + g.toString());
			System.out.println("Author: " + 
				g.getProperty(model.getProperty("http://purl.org/dc/elements/1.1/author")).getString());
			System.out.println("Date: " + 
				g.getProperty(model.getProperty("http://purl.org/dc/elements/1.1/date")).getString());
		}
		
		// Serialize the model to a TriX file
		out = new FileOutputStream("C:/model.trix");
		model.write(out, "TRIX"); 
	}
}