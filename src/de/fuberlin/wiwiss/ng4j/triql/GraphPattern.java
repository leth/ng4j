// $Id: GraphPattern.java,v 1.1 2004/10/26 07:17:39 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * TODO: Describe this type
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPattern {
	private Node name;
	private List triples = new ArrayList();

	public GraphPattern(Node name) {
		this.name = name;
	}
	
	public void addTriplePattern(Node s, Node p, Node o) {
		this.triples.add(new Triple(s, p, o));
	}
	
	public Node getName() {
		return this.name;
	}
	
	public int getTripleCount() {
		return this.triples.size();
	}
	
	public Triple getTriple(int index) {
		return (Triple) this.triples.get(index);
	}
}
