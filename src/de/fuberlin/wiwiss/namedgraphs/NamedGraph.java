// $Id: NamedGraph.java,v 1.1 2004/09/13 14:37:23 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

/**
 * A collection of RDF triples which is named by an URI. 
 * For details about Named Graphs see the
 * <a href="http://www.w3.org/2004/03/trix/">Named Graphs homepage</a>.
 * <p>
 * The core interface is small (add, delete, find, contains) and
 * is augmented by additional classes to handle more complicated matters
 * such as reification, query handling, bulk update, event management,
 * and transaction handling.
 *
 * @author Chris Bizer
 */
public interface NamedGraph extends Graph {

	/**
	 * Returns the URI of the named graph. The returned Node
	 * instance is always an URI and cannot be a blank node
	 * or literal.
	 */
	public Node getGraphName();
}