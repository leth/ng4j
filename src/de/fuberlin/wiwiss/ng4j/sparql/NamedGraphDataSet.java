package de.fuberlin.wiwiss.ng4j.sparql;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Lock;
import com.hp.hpl.jena.query.util.LockMutex;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * Implementation of ARQ's DataSet interface on top of an NG4J NamedGraphSet
 */
public class NamedGraphDataSet implements Dataset {
	private NamedGraphSet set;
	private Node defaultGraphName;
	private Lock lock;
	
	public NamedGraphDataSet(NamedGraphSet baseNamedGraphSet,
			Node defaultGraphName) {
		this.set = baseNamedGraphSet;
		this.defaultGraphName = defaultGraphName;
	}
	
	public boolean containsNamedModel(String uri) {
		return this.set.containsGraph(uri);
	}

	public Model getDefaultModel() {
		Graph defaultGraph = this.set.getGraph(this.defaultGraphName);
		if (defaultGraph == null) {
			return null;
		}
		return new ModelCom(defaultGraph);
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
