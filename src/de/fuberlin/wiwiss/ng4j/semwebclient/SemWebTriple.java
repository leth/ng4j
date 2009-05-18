package de.fuberlin.wiwiss.ng4j.semwebclient;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * A Triple which contains information about it's source.
 * 
 * @author Tobias Gauß
 * 
 */
public class SemWebTriple extends Triple {
	/**
	 * The source graph node.
	 */
	private Node source;

	/**
	 * Creates a SemWebTriple.
	 * 
	 * @param s The subject Node.
	 * @param p The predicate Node.
	 * @param o The object Node.
	 */
	public SemWebTriple(Node s, Node p, Node o) {
		super(s, p, o);
	}

	public SemWebTriple(Triple t, Node sourceURI) {
		this(t.getSubject(), t.getPredicate(), t.getObject());
		setSource(sourceURI);
	}
	
	/**
	 * Sets the triple's source.
	 * 
	 * @param n The source Node.
	 */
	public void setSource(Node n) {
		this.source = n;
	}

	/**
	 * Returns the triple's source.
	 * 
	 * @return Node
	 */
	public Node getSource() {
		// TODO return String ?
		return this.source;
	}

}
