// $Id: NamedGraphExample.java,v 1.9 2010/02/25 14:28:21 hartig Exp $
package de.fuberlin.wiwiss.ng4j.examples;

import java.io.IOException;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
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
		Iterator<Quad> quads = graphset.findQuads( 
				Node.ANY, 
				Node.createURI("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak"),
				Node.ANY,
				Node.ANY);

		while (quads.hasNext()) {
			Quad q = quads.next();
			System.out.println("Source: " + q.getGraphName());
			System.out.println("Statement: " + q.getTriple());
			// (This will output the two statements created above)
		}

		// Count all graphs in the graphset (2)
		System.out.println("The graphset contains " + graphset.countGraphs() + " graphs.");

		// Serialize the graphset to System.out, using the TriX syntax
		graphset.write(System.out, "TRIX", null);

		////////////////////////////////////////////////
		//		 Operations on Model Level
		////////////////////////////////////////////////

		// Get a Jena Model view on the GraphSet
		NamedGraphModel model = graphset.asJenaModel("http://example.org/defaultgraph");

		// Add provenance information about a graph
		Resource informationAboutRichard = model.getResource("http://www.bizer.de/InformationAboutRichard");
		informationAboutRichard.addProperty(model.createProperty("http://purl.org/dc/elements/1.1/author"), "Chris Bizer");
		informationAboutRichard.addProperty(model.createProperty("http://purl.org/dc/elements/1.1/date"), "09/15/2004");

		// Get a Jena resource and statement
		Resource richard = model.getResource("http://richard.cyganiak.de/foaf.rdf#RichardCyganiak");
			
		NamedGraphStatement mboxStmt = getNamedGraphStatement(model, 
				richard.getProperty(model.getProperty("http://xmlns.com/foaf/0.1/mbox")));

		// Get an iterator over all graphs which contain the statement.
		Iterator<Resource> it = mboxStmt.listGraphNames();

		// So who has published my email address all over the Web??!?
		while (it.hasNext()) {
			Resource g = it.next();
			System.out.println();
			System.out.println("GraphName: " + g.toString());
			System.out.println("Author: " + 
				g.getProperty(model.getProperty("http://purl.org/dc/elements/1.1/author")).getString());
			System.out.println("Date: " + 
				g.getProperty(model.getProperty("http://purl.org/dc/elements/1.1/date")).getString());
		}
		
		// Serialize the model to System.out, using the TriG syntax
		model.write(System.out, "TRIG", "http://richard.cyganiak.de/foaf.rdf");
	}

	/** Converts a generic Statement into a NamedGraphStatement.
	 *
	 * TODO: Consider moving this utility method to a different package, or at least
	 *       a separate class within this package so it can be more easily used by
	 *       other example code.
	 *
	 * @param stmt A Jena model statement.
	 * @return the corresponding named graph statement. 
	 */
	public static NamedGraphStatement getNamedGraphStatement(NamedGraphModel model, Statement stmt) {
		if (stmt instanceof NamedGraphStatement) {
			return (NamedGraphStatement) stmt;
		}

		return new NamedGraphStatement( stmt.getSubject(),
		                                stmt.getPredicate(),
		                                stmt.getObject(),
		                                model );
	}

}

/*
 *  (c) Copyright 2004 - 2010 Christian Bizer (chris@bizer.de)
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