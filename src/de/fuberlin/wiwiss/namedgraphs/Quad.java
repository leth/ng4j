// $Id: Quad.java,v 1.1 2004/09/13 14:37:25 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * A Triple in a NamedGraph, consisting of four Jena Nodes: graphName,
 * subject, predicate, and object. Any of the four fields may be a wildcard
 * (Node.ANY). In this case, the Quad represents a quad pattern and not
 * a specific quad.
 *
 * @author Chris Bizer
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Quad {
	private final Node graphName;
	private final Triple triple;

	/**
	 * Creates a Quad from four Nodes. <tt>null</tt> arguments are not
	 * allowed. <tt>Node.ANY</tt> can be used to create a quad pattern
	 * that matches any node at that position. 
	 */
	public Quad(Node graphName, Node subject, Node predicate, Node object) {
		this(graphName, Triple.create(subject, predicate, object));
		if (!graphName.isURI() && graphName.isConcrete()) {
			throw new IllegalArgumentException("Graph names must be URIs or Node.ANY");
		}
		if (subject == null) {
			throw new IllegalArgumentException("null subjects are not allowed; use Node.ANY to match all subjets");
		}
		if (predicate == null) {
			throw new IllegalArgumentException("null predicates are not allowed; use Node.ANY to match all predicates");
		}
		if (object == null) {
			throw new IllegalArgumentException("null objects are not allowed; use Node.ANY to match all objets");
		}
	}
	
	/**
	 * Creates a Quad a triple and a graph name. <tt>null</tt> arguments
	 * are not allowed. <tt>Node.ANY</tt> can be used to create a quad
	 * pattern that matches any graph name. 
	 */
	public Quad(Node graphName, Triple triple) {
		if (!graphName.isURI() && graphName.isConcrete()) {
			throw new IllegalArgumentException("Graph names must be URIs or Node.ANY");
		}
		if (triple == null) {
			throw new IllegalArgumentException("null triples are not allowed");
		}
		this.graphName = graphName;
		this.triple = triple;
	}

	/**
	 * Return a human-readable (sort of) string "graphname { s p o . }"
	 * describing the quad.
	 * @return human-readable representation of this quad
	 */
	public String toString() {
		return toString(PrefixMapping.Standard);
	}
	
	/**
	 * Return a human-readable (sort of) string "graphname { s p o . }"
	 * describing the quad.
	 * @param prefixMapping a prefix mapping for making URIs shorter
	 * @return human-readable representation of this quad
	 */
	public String toString(PrefixMapping prefixMapping) {
		return this.graphName.toString(prefixMapping, true) + " { " +
				this.triple.getSubject().toString(prefixMapping, true) + " " +
				this.triple.getPredicate().toString(prefixMapping, true) + " " +
				this.triple.getObject().toString(prefixMapping, true) + " . }";
	}

	public Node getGraphName() {
		return this.graphName;
	}
	
	public Node getSubject() {
		return this.triple.getSubject();
	}
	
	public Node getPredicate() {
		return this.triple.getPredicate();
	}
	
	public Node getObject() {
		return this.triple.getObject();
	}
	
	public Triple getTriple() {
		return this.triple;
	}

	/**
	 * @return <tt>true</tt> if this is a single quad; <tt>false</tt> if it
	 * is a quad pattern
	 */
	public boolean isConcrete() {
		return this.graphName.isConcrete() && this.triple.isConcrete();
	}
	        
    /**
     * Quads are equal iff both have four equal components. 
     */
	public boolean equals(Object o) {
		return o instanceof Quad && ((Quad) o).sameAs(this.graphName, this.triple);
	}

	private boolean sameAs(Node otherGraphName, Triple otherTriple) {
		return this.graphName.equals(otherGraphName) &&
				this.triple.equals(otherTriple);
	}

	public boolean matches(Quad other) {
		return this.graphName.matches(other.getGraphName()) &&
				this.triple.matches(other.getTriple());
	}

	public boolean matches(Node gn, Triple t) {
		return this.graphName.matches(gn) && this.triple.matches(t);
	}

	public boolean matches(Node gn, Node s, Node p, Node o) {
		return this.graphName.matches(gn) && this.triple.matches(s, p, o);
	}

	public boolean graphNameMatches(Node gn) {
		return this.graphName.matches(gn);
	}

	public boolean subjectMatches(Node s) {
		return this.triple.getSubject().matches(s);
	}

	public boolean predicateMatches(Node p) {
		return this.triple.getPredicate().matches(p);
	}
	
	public boolean objectMatches(Node o) {
		return this.triple.getObject().matches(o);
	}

	/**
	 * The hash code of a quad is calculated in a way similar to Triple.
	 */
	public int hashCode() {
		return (this.graphName.hashCode() >> 2) ^ this.triple.hashCode();
	}
}