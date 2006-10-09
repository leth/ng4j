package de.fuberlin.wiwiss.ng4j.sparql;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.LockMutex;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * Implementation of ARQ's Dataset interface on top of an NG4J NamedGraphSet
 */
public class NamedGraphDataset implements Dataset {
	private NamedGraphSet set;
	private Graph defaultGraph;
	private Lock lock;

	/**
	 * Creates a new instance whose default graph is the merge of
	 * all named graphs. 
	 */
	public NamedGraphDataset(NamedGraphSet baseNamedGraphSet) {
		this(baseNamedGraphSet, baseNamedGraphSet.asJenaGraph(null));
	}
	
	/**
	 * Creates a new instance where one of the named graph is
	 * used as the default graph. The graph must already exist
	 * in the NamedGraphSet.
	 */
	public NamedGraphDataset(NamedGraphSet baseNamedGraphSet,
			Node defaultGraphName) {
		this(baseNamedGraphSet, baseNamedGraphSet.getGraph(defaultGraphName));
	}
	
	/**
	 * Creates a new instance with a given default graph.
	 */
	public NamedGraphDataset(NamedGraphSet baseNamedGraphSet,
			Graph defaultGraph) {
		this.set = baseNamedGraphSet;
		this.defaultGraph = defaultGraph;
	}
	
	public boolean containsNamedModel(String uri) {
		return this.set.containsGraph(uri);
	}

	public Model getDefaultModel() {
		return new ModelCom(this.defaultGraph);
	}

	public Model getNamedModel(String graphName) {
		return new ModelCom(this.set.getGraph(graphName));
	}

	public Iterator listNames() {
		final Iterator it = this.set.listGraphs();
		return new Iterator() {
			public Object next() {
				return ((NamedGraph) it.next()).getGraphName().getURI();
			}
			public boolean hasNext() {
				return it.hasNext();
			}
			public void remove() {
				it.remove();
			}
		};
	}

	/**
	 * Returns a LockMutex. I've no idea if this works or what
	 * it is supposed to do.
	 */
	public Lock getLock() {
		if (this.lock == null) {
			this.lock = new LockMutex();
		}
		return this.lock;
	}
}
