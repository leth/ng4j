// $Id: NamedGraphImpl.java,v 1.7 2009/03/08 19:47:15 hartig Exp $
package de.fuberlin.wiwiss.ng4j.impl;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;

import com.hp.hpl.jena.graph.Capabilities;

import com.hp.hpl.jena.graph.Graph;

import com.hp.hpl.jena.graph.GraphEventManager;

import com.hp.hpl.jena.graph.Node;

import com.hp.hpl.jena.graph.Reifier;

import com.hp.hpl.jena.graph.TransactionHandler;

import com.hp.hpl.jena.graph.Triple;

import com.hp.hpl.jena.graph.TripleMatch;

import com.hp.hpl.jena.graph.query.QueryHandler;

import com.hp.hpl.jena.shared.AddDeniedException;

import com.hp.hpl.jena.shared.DeleteDeniedException;

import com.hp.hpl.jena.shared.PrefixMapping;

import com.hp.hpl.jena.util.iterator.ExtendedIterator;




import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * NamedGraph implementation that wraps a {@link Graph} instance
 * and a name for that graph. All methods inherited from the
 * Graph interface are delegated to the underlying Graph
 * instance.
 * <p>
 * NamedGraph instances can be created from any Jena {@link Graph}
 * instance, or by calling {@link NamedGraphSet#createGraph(Node)}
 * on a NamedGraphSet.
 * <p>
 * TODO: Implement equals, but with what semantics?
 * 
 * @author Chris Bizer
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @see Graph
 * @see NamedGraphSet#createGraph(Node)
 */
public class NamedGraphImpl implements NamedGraph {
	private Node name;
	final protected Graph graph;

	/**
	 * Creates a NamedGraph from a Graph instance and a name.
	 * The name must be an URI.
	 * @param graphName a name for the graph; must be an URI Node
	 * @param graph a graph instance
	 * @see NamedGraphSet#createGraph(Node)
	 */
	public NamedGraphImpl(Node graphName, Graph graph) {
		this.graph = graph;
		this.name = graphName;
	}

	/**
	 * Creates a NamedGraph from a Graph instance and a name.
	 * @param graphNameURI a name for the graph; must be an URI
	 * @param graph a graph instance
	 * @see NamedGraphSet#createGraph(Node)
	 */
	public NamedGraphImpl(String graphNameURI, Graph graph) {
		this(Node.createURI(graphNameURI), graph);
	}

	/**
	 * Returns the URI of the named graph. The returned Node
	 * instance is always an URI and cannot be a blank node
	 * or literal.
	 */
	public Node getGraphName() {
		return this.name;
	}

	// === Delegations to the underlying Graph ===

	public void close() {
		this.graph.close();
	}
        public boolean isClosed() {
                return this.graph.isClosed();
        }


	public boolean contains(Node s, Node p, Node o) {
		return this.graph.contains(s, p, o);
	}

	public boolean contains(Triple t) {
		return this.graph.contains(t);
	}

	public void delete(Triple t) throws DeleteDeniedException {
		this.graph.delete(t);
	}

	public boolean dependsOn(Graph other) {
		return this.graph.dependsOn(other);
	}

	public ExtendedIterator find(Node s, Node p, Node o) {
		return this.graph.find(s, p, o);
	}

	public ExtendedIterator find(TripleMatch m) {
		return this.graph.find(m);
	}
	
	public BulkUpdateHandler getBulkUpdateHandler() {
		return this.graph.getBulkUpdateHandler();
	}

	public Capabilities getCapabilities() {
		return this.graph.getCapabilities();
	}

	public GraphEventManager getEventManager() {
		return this.graph.getEventManager();
	}

	public PrefixMapping getPrefixMapping() {
		return this.graph.getPrefixMapping();
	}

	public Reifier getReifier() {
		return this.graph.getReifier();
	}

	public TransactionHandler getTransactionHandler() {
		return this.graph.getTransactionHandler();
	}

	public boolean isEmpty() {
		return this.graph.isEmpty();
	}

	public boolean isIsomorphicWith(Graph g) {
		return this.graph.isIsomorphicWith(g);
	}

	public QueryHandler queryHandler() {
		return this.graph.queryHandler();
	}

	public int size() {
		return this.graph.size();
	}

	public void add(Triple t) throws AddDeniedException {
		this.graph.add(t);
	}
	
	public String toString() {
		return this.name + this.graph.toString();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.Graph#getStatisticsHandler()
	 */
	public GraphStatisticsHandler getStatisticsHandler() {
		return graph.getStatisticsHandler();
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
