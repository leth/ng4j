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


	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addQuad(de.fuberlin.wiwiss.ng4j.Quad)
	 */
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

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsQuad(de.fuberlin.wiwiss.ng4j.Quad)
	 */
	public boolean containsQuad(Quad pattern) {
		if (!Node.ANY.equals(pattern.getGraphName())) {
			if (!containsGraph(pattern.getGraphName())) {
				return false;
			}
			return getGraph(pattern.getGraphName()).contains(pattern.getTriple());
		}
		Iterator<NamedGraph> it = listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = it.next();
			if (graph.contains(pattern.getTriple())) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#countQuads()
	 */
	public int countQuads() {
		int sum = 0;
		Iterator<NamedGraph> it = listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = it.next();
			sum += graph.size();
		}
		return sum;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#findQuads(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	public Iterator<Quad> findQuads(Node graphName, Node subject,
			Node predicate, Node object) {
		return findQuads(new Quad(graphName, subject, predicate, object));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#findQuads(de.fuberlin.wiwiss.ng4j.Quad)
	 */
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

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeQuad(de.fuberlin.wiwiss.ng4j.Quad)
	 */
	public void removeQuad(Quad pattern) {
		Iterator<Quad> it = findQuads(pattern);
		// Read the entire iterator into a collection first to avoid
		// ConcurrentModificationException
		Collection<Quad> quadsToDelete = new ArrayList<Quad>();
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
	    private Iterator<NamedGraph> graphIt;
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
	            NamedGraph graph = this.graphIt.next();
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