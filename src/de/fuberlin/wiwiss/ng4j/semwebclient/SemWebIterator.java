package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;

/**
 * A Semantic Web Client Iterator which is returned from the find(TripleMatch
 * pattern) method of the SemWebClientImpl. If the hasNext() method is called
 * and there are no matching triples the Interator waits until new graphs are
 * retrieved. If the retrieval is finished and there are no more matching
 * triples the hasNext() method returns false.
 * 
 * @author Tobias Gau�
 * 
 */
public class SemWebIterator implements Iterator<SemWebTriple> {
	private LinkedList<NamedGraph> graphQueue = new LinkedList<NamedGraph>();
	private Iterator currentIterator = null;
	private Node currentGraphName = null;
	private Triple pattern;
	private SemWebTriple nextTriple = null;
	private boolean noMoreGraphs = false;
	private FindQuery findQuery;
	private Log log = LogFactory.getLog(SemWebIterator.class);
	
	public SemWebIterator(FindQuery observer, Triple pattern) {
		this.findQuery = observer;
		this.pattern = pattern;
	}

	public synchronized void queueNamedGraphs(Iterator<NamedGraph> graphs) {
		while (graphs.hasNext()) {
			NamedGraph graph = graphs.next();
			this.graphQueue.addLast(graph);
			this.log.debug("Queue result graph: <" + graph.getGraphName() + "> (" + graph.size() + " triples)");
		}
		notify();
	}
	
	public synchronized void noMoreGraphs() {
		this.noMoreGraphs = true;
		notify();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (this.nextTriple == null) {
			this.nextTriple = tryFetchNextTriple();
		}
		return this.nextTriple != null;
	}
	
	private synchronized SemWebTriple tryFetchNextTriple() {
		while (true) {
			if (this.currentIterator != null && this.currentIterator.hasNext()) {
				return createSemWebTriple((Triple) this.currentIterator.next());
			}
			if (!this.graphQueue.isEmpty()) {
				NamedGraph graph = this.graphQueue.removeFirst();
				this.currentIterator = graph.find(this.pattern);
				this.currentGraphName = graph.getGraphName();
				this.log.debug("Searching <" + this.currentGraphName + ">");
				continue;
			}
			if (this.noMoreGraphs) {
				return null;
			}
			try {
				this.wait();
			} catch (InterruptedException ex) {
				// We don't know when this happens
				throw new RuntimeException(ex);
			}
		}
	}

	private SemWebTriple createSemWebTriple(Triple t) {
		return new SemWebTriple(t, this.currentGraphName);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public SemWebTriple next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		}
		SemWebTriple result = this.nextTriple;
		this.nextTriple = null;
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Triple getTriple() {
		return this.pattern;
	}
	
	public synchronized void close() {
		if (this.findQuery != null) {
			this.findQuery.close();
			this.findQuery = null;
		}
		this.noMoreGraphs = true;
		notify();
	}
	
	public String toString() {
		return "SemWebIterator(" + this.pattern + ")";
	}
}
