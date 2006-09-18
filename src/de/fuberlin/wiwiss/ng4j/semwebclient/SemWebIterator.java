package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * A Semantic Web Client Iterator which is returned from the find(TripleMatch
 * pattern) method of the SemWebClientImpl. If the hasNext() method is called
 * and there are no matching triples the Interator waits until new graphs are
 * retrieved. If the retrieval is finished and there are no more matching
 * triples the hasNext() method returns false.
 * 
 * @author Tobias Gauß
 * 
 */
public class SemWebIterator implements Iterator, FindListener {

	/**
	 * The corresponding client.
	 */
	private SemanticWebClientImpl client;

	/**
	 * The current graph.
	 */
	private Node graph = null;

	/**
	 * A LinkedList which contains all graphs to inspect.
	 */
	private LinkedList graphList = new LinkedList();

	/**
	 * The current iterator.
	 */
	private Iterator iter;

	/**
	 * The object node.
	 */
	private Node o;

	/**
	 * The predicate node.
	 */
	private Node p;

	/**
	 * A List wich contains graphs which has already been inspected.
	 */
	private List removedList = Collections.synchronizedList(new ArrayList());

	/**
	 * The subject node.
	 */
	private Node s;

	/**
	 * Creates a SemWebIterator.
	 * 
	 * @param client
	 *            The corresponding SemanticWebClient.
	 * @param s
	 *            The subject node.
	 * @param p
	 *            The predicate node.
	 * @param o
	 *            The object node.
	 */
	public SemWebIterator(SemanticWebClientImpl client, Node s, Node p, Node o) {
		this.client = client;
		this.s = s;
		this.p = p;
		this.o = o;
		this.fillGraphList();
		this.initIterator();
		this.client.addFindListener(this);
	}

	/**
	 * Fills the graphlist with graphs wich have been in the NamedGraphSet
	 * before the retrieval started.
	 */
	synchronized private void fillGraphList() {
		Iterator iter = this.client.getNamedGraphSet().listGraphs();
		while (iter.hasNext()) {
			NamedGraph g = (NamedGraph) iter.next();
			this.graphList.add(g.getGraphName().getURI());
		}
	}

	/**
	 * Returns the graphlist.
	 * 
	 * @return List
	 */
	public List getGraphList() {
		return this.graphList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.FindListener#graphAdded(de.fuberlin.wiwiss.ng4j.semWebClient.GraphAddedEvent)
	 */
	synchronized public void graphAdded(GraphAddedEvent e) {
		if (!this.removedList.contains(e.getGraphUri())
				&& !this.graphList.contains(e.getGraphUri())) {
			this.graphList.add(e.getGraphUri());
		}
		this.notify();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	synchronized public boolean hasNext() {
		boolean finished = false;
		while (!finished) {
			if (!this.iter.hasNext()) {
				if (this.client.retrievalFinished)
					return false;
				try {
					this.wait();
					this.replaceIterator(finished);
				} catch (Exception e) {
					return false;
				}
			} else {
				return true;
			}
		}
		return this.iter.hasNext();

	}

	/**
	 * Initiates an Iterator.
	 */
	synchronized private void initIterator() {
		String graphname = (String) this.graphList.getFirst();
		NamedGraph g = this.client.getNamedGraphSet().getGraph(graphname);
		this.graph = g.getGraphName();
		this.iter = g.find(this.s, this.p, this.o);
		this.removedList.add(this.graphList.getFirst());
		this.graphList.removeFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	synchronized public Object next() {
		if (this.hasNext()) {
			SemWebTriple triple = null;

			if (this.graph == null) {
				Quad q = (Quad) this.iter.next();
				triple = new SemWebTriple(q.getSubject(), q.getPredicate(), q
						.getObject());
				triple.setSource(q.getGraphName());
			} else {
				Triple t = (Triple) this.iter.next();
				triple = new SemWebTriple(t.getSubject(), t.getPredicate(), t
						.getObject());
				triple.setSource(this.graph);
			}
			return triple;
		} else {
			return new NoSuchElementException();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		this.iter.remove();

	}

	/**
	 * If the hasNext() method returns false the graphlist is checked and the
	 * current Iterator is replaced by a new one.
	 * 
	 * @param finished
	 *            boolean
	 */
	synchronized public void replaceIterator(boolean finished) {
		if (!this.graphList.isEmpty()) {
			NamedGraph graph = null;
			boolean loop = true;
			boolean replace = false;

			while (loop) {
				if (this.getGraphList().isEmpty()) {
					finished = true;
					return;
				}
				String graphname = (String) this.graphList.getFirst();
				graph = this.client.getGraph(graphname);
				if (graph == null) {
					finished = true;
				} else if (graph.size() > 0) {
					replace = true;
					loop = false;
				}
				this.removedList.add(this.graphList.getFirst());
				this.graphList.removeFirst();
			}
			if (replace) {
				this.iter = graph.find(this.s, this.p, this.o);
				this.graph = graph.getGraphName();
			}
		} else {
			finished = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.FindListener#uriRetrievalFininshed(de.fuberlin.wiwiss.ng4j.semWebClient.GraphAddedEvent)
	 */
	synchronized public void uriRetrievalFininshed(GraphAddedEvent e) {
		this.notify();
	}

}
