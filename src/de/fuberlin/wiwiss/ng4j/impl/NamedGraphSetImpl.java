// $Id: NamedGraphSetImpl.java,v 1.9 2006/05/02 19:56:10 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.graph.impl.SimpleBulkUpdateHandler;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;
import com.hp.hpl.jena.util.iterator.NullIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;


/**
 * Implementation of the {@link NamedGraphSet} interface based on a
 * set of in-memory {@link NamedGraph}s. For details about Named Graphs see
 * <a href="http://www.w3.org/2004/03/trix/">http://www.w3.org/2004/03/trix/</a>.
 *
 * @author Chris Bizer
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphSetImpl extends NamedGraphSetIO implements NamedGraphSet {
	/** Map from names (Node) to NamedGraphs */
	private Map namesToGraphsMap = new HashMap();
	
	/**
	 * List of all NamedGraphs that backs the UnionGraphs handed
	 * out by {@link #asJenaGraph(Node)}. This is always in sync
	 * with namesToGraphsMap.values(), but it's a List, not a
	 * Collection. This whole graphs List affair is probably rather slow.
	 */
	private List graphs = new ArrayList();

	public void addGraph(NamedGraph graph) {
		this.graphs.remove(this.namesToGraphsMap.get(graph.getGraphName()));
		if (!this.graphs.contains(graph)) {
			this.graphs.add(graph);
		}
		this.namesToGraphsMap.put(graph.getGraphName(), graph);
	}

	public boolean containsGraph(Node graphName) {
		if (Node.ANY.equals(graphName)) {
			return !isEmpty();
		}
		return this.namesToGraphsMap.containsKey(graphName);
	}

	public boolean containsGraph(String graphNameURI) {
		return this.namesToGraphsMap.containsKey(
				Node.createURI(graphNameURI));
	}

	public long countGraphs() {
		return this.namesToGraphsMap.size();
	}

	public NamedGraph createGraph(Node graphName) {
		NamedGraph newGraph = createNamedGraphInstance(graphName);
		addGraph(newGraph);
		return newGraph;
	}

	public NamedGraph createGraph(String graphNameURI) {
		return createGraph(Node.createURI(graphNameURI));
	}

	public NamedGraph getGraph(Node graphName) {
		return (NamedGraph) this.namesToGraphsMap.get(graphName);
	}

	public NamedGraph getGraph(String graphNameURI) {
		return getGraph(Node.createURI(graphNameURI));
	}

	public boolean isEmpty() {
		return this.namesToGraphsMap.isEmpty();
	}

	public Iterator listGraphs() {
		return this.namesToGraphsMap.values().iterator();
	}

	public void removeGraph(Node graphName) {
		if (Node.ANY.equals(graphName)) {
			this.namesToGraphsMap.clear();
			this.graphs.clear();
		} else {
			this.graphs.remove(this.namesToGraphsMap.get(graphName));
			this.namesToGraphsMap.remove(graphName);
		}
	}

	public void removeGraph(String graphNameURI) {
		removeGraph(Node.createURI(graphNameURI));
	}

	public void clear() {
		this.namesToGraphsMap.clear();
	}

	public void addQuad(Quad quad) {
		if (!quad.isConcrete()) {
			throw new IllegalArgumentException("Cannot add quads with wildcards");
		}
		if (containsGraph(quad.getGraphName())) {
			getGraph(quad.getGraphName()).add(quad.getTriple());
		} else {
			createGraph(quad.getGraphName()).add(quad.getTriple());
		}
	}

	public boolean containsQuad(Quad pattern) {
		if (!Node.ANY.equals(pattern.getGraphName())) {
			if (!containsGraph(pattern.getGraphName())) {
				return false;
			}
			return getGraph(pattern.getGraphName()).contains(pattern.getTriple());
		}
		Iterator it = listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = (NamedGraph) it.next();
			if (graph.contains(pattern.getTriple())) {
				return true;
			}
		}
		return false;
	}

	public int countQuads() {
		int sum = 0;
		Iterator it = listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = (NamedGraph) it.next();
			sum += graph.size();
		}
		return sum;
	}

	public Iterator findQuads(Node graphName, Node subject,
			Node predicate, Node object) {
		return findQuads(new Quad(graphName, subject, predicate, object));
	}

	public Iterator findQuads(Quad pattern) {
		if (!containsGraph(pattern.getGraphName())) {
			return new NullIterator();
		}
		if (Node.ANY.equals(pattern.getGraphName())) {
			return getQuadIteratorOverAllGraphs(pattern.getTriple());
		}
		return getQuadIteratorOverGraph(
				getGraph(pattern.getGraphName()),
				pattern.getTriple());
	}

	public void removeQuad(Quad pattern) {
		Iterator it = findQuads(pattern);
		// Read the entire iterator into a collection first to avoid
		// ConcurrentModificationException
		Collection quadsToDelete = new ArrayList();
		while (it.hasNext()) {
			quadsToDelete.add(it.next());
		}
		it = quadsToDelete.iterator();
		while (it.hasNext()) {
			Quad quad = (Quad) it.next();
			getGraph(quad.getGraphName()).delete(quad.getTriple());
		}
	}

	public Graph asJenaGraph(Node defaultGraphForAdding) {
		if (defaultGraphForAdding != null && !containsGraph(defaultGraphForAdding)) {
			createGraph(defaultGraphForAdding);
		}
		MultiUnion unionGraph = new UnionGraph(this.graphs);
		if (defaultGraphForAdding != null) {
			unionGraph.setBaseGraph(getGraph(defaultGraphForAdding));
		}
		return unionGraph;
	}

	public NamedGraphModel asJenaModel(String defaultGraphForAdding) {
		return new NamedGraphModel(this, defaultGraphForAdding);
	}

	public void close() {
		Iterator it = listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = (NamedGraph) it.next();
			graph.close();
		}
	}

	protected NamedGraph createNamedGraphInstance(Node graphName) {
		if (!graphName.isURI()) {
			throw new IllegalArgumentException("Graph names must be URIs");
		}
		return new NamedGraphImpl(graphName, Factory.createGraphMem(ReificationStyle.Standard));
	}
	
	private ExtendedIterator getQuadIteratorOverGraph(
			final NamedGraph graph, Triple triple) {
		final ExtendedIterator triples = graph.find(triple);
		return new NiceIterator() {

			public boolean hasNext() {

				return triples.hasNext();

			}

			public Object next() {

				Triple t = (Triple) triples.next();

				return new Quad(graph.getGraphName(), t);

			}

		};
	}
	
	private ExtendedIterator getQuadIteratorOverAllGraphs(Triple triple) {
	    return new FindQuadsIterator(triple);
	}

	/**
	 * Subclass of {@link MultiUnion} that allows the list of member
	 * graphs to be directly passed to the constructor. When we later
	 * change the list (add or remove graphs from the NamedGraphSet),
	 * the member list of the MultiUnion is automatically updated.
	 * <p>
	 * Note: This is a hack.
	 */
	private class UnionGraph extends MultiUnion {
		public UnionGraph (List members) {
			super();
			this.m_subGraphs = members;
		}

		/**
		 * MultiUnion deletes from the baseGraph only; we want to
		 * delete from all member graphs
		 */
		public void performDelete(Triple t) {
			Iterator it = this.m_subGraphs.iterator();
			while (it.hasNext()) {
				Graph member = (Graph) it.next();
				member.delete(t);
			}
		}

		/**
		 * <p>We create our own bulk update handler because the superclass
		 * uses only the base graph's update handler.</p>
		 * 
		 * TODO: Add a test!
		 */
        public BulkUpdateHandler getBulkUpdateHandler() {
            if (this.bulkHandler == null)
                this.bulkHandler = new SimpleBulkUpdateHandler(this);
            return this.bulkHandler;
        }
	}

	private class FindQuadsIterator extends NiceIterator {
	    private Iterator graphIt;
	    private Iterator currentIt;
	    private Triple findMe;
	    private Node currentGraphName;
	    FindQuadsIterator(Triple findMe) {
	        this.findMe = findMe;
	        this.graphIt = listGraphs();
	        this.currentIt = new NullIterator();
	    }
	    public boolean hasNext() {
	        while (!this.currentIt.hasNext()) {
	            if (!this.graphIt.hasNext()) {
	                return false;
	            }
	            NamedGraph graph = (NamedGraph) this.graphIt.next();
	            this.currentGraphName = graph.getGraphName();
	            this.currentIt = graph.find(this.findMe);
	        }
	        return true;
	    }
	    public Object next() {
	        if (!hasNext()) {
	            throw new NoSuchElementException();
	        }
	        Triple found = (Triple) this.currentIt.next();
	        return new Quad(this.currentGraphName, found);
	    }
	    public void remove() {
	        this.currentIt.remove();
	    }
	}
}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
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