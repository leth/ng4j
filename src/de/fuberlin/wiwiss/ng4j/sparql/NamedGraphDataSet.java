package de.fuberlin.wiwiss.ng4j.sparql;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
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
	
	public NamedGraphDataSet(NamedGraphSet baseNamedGraphSet,
			Node defaultGraphName) {
		this.set = baseNamedGraphSet;
		this.defaultGraphName = defaultGraphName;
	}
	
	public Model getModel() {
		return getDefaultModel();
	}

	public Model getDefaultModel() {
		Graph defaultGraph = getDefaultGraph();
		if (defaultGraph == null) {
			return null;
		}
		return new ModelCom(defaultGraph);
	}

	public Graph getDefaultGraph() {
		return this.set.getGraph(this.defaultGraphName);
	}

	public Graph getGraph() {
		return getDefaultGraph();
	}

	public Model getNamedModel(String graphName) {
		return new ModelCom(getNamedGraph(graphName));
	}

	public Graph getNamedGraph(String graphName) {
		return this.set.getGraph(graphName);
	}

	public boolean containsNamedGraph(String uri) {
		return this.set.containsGraph(uri);
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

	public Iterator listNamedModels() {
		final Iterator it = this.set.listGraphs();
		return new Iterator() {
			public Object next() {
				return new ModelCom((Graph) it.next());
			}
			public boolean hasNext() {
				return it.hasNext();
			}
			public void remove() {
				it.remove();
			}
		};
	}

	public Iterator listNamedGraphs() {
		return this.set.listGraphs();
	}
}
