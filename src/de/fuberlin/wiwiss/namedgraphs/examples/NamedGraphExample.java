// $Id: NamedGraphExample.java,v 1.1 2004/09/13 14:37:25 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs.examples;


/**
 * Example showing how to work with NamedGraphs
 */
public class NamedGraphExample {

	public static void main(String[] args) {
//TODO: Make this work
/*		
		////////////////////////////////////////////////
		// Operations on GraphSet Level
		////////////////////////////////////////////////
		
		// Create a new GraphSet
		NamedGraphSet graphset = new NamedGraphSetImpl();
		
		// Read a TriX file
		graphset.read("file://c:/bla.trix", "TriX");

		// Create a new NamedGraph
		NamedGraph namedgraph1 = new NamedGraphImpl("http://graphname1.org", new GraphMem());
		namedgraph1.add(new Triple( ... ));
		
		// Add to the graphset
		graphset.addGraph(namedgraph1);
		graphset.addQuad(new Quad( ... ));
		
		// Find information in the graphset
		ExtendedIterator it = graphset.findQuads(new Quad( ... ));

		
		////////////////////////////////////////////////
		// Operations on Model Level
		////////////////////////////////////////////////
		
		// Get a Jena Model view on the GraphSet
		Model model = graphset.viewAsJenaModel("http://defaultgraph.org/default");
		
		// Use the Jena Resource interface 
		Resource Person1 = model.getResource("http://examle.org/Person1");
		
		// Get provenance information about a property
		NamedGraphStatement stmt = (NamedGraphStatement) Person1.getProperty(new Property("http://examle.org/name"));
		
		// Get an iterator over all graphs which contain the statement.
		ExtendedIterator it = stmt.listGraphNames();
		
*/		
	}
}
