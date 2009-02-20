// $Id: Quad.java,v 1.3 2009/02/20 08:09:51 hartig Exp $
package de.fuberlin.wiwiss.ng4j;

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