package de.fuberlin.wiwiss.ng4j.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;
import com.hp.hpl.jena.util.iterator.NullIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * 
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public abstract class NamedGraphSetBase extends NamedGraphSetIO implements
		NamedGraphSet {


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