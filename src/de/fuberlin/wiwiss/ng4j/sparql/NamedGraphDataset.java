package de.fuberlin.wiwiss.ng4j.sparql;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.LockMutex;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * Implementation of ARQ's Dataset interface on top of an NG4J NamedGraphSet
 */
public class NamedGraphDataset implements Dataset, DatasetGraph {
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
	
	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.Dataset#containsNamedModel(java.lang.String)
	 */
	public boolean containsNamedModel(String uri) {
		return this.set.containsGraph(uri);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.Dataset#getDefaultModel()
	 */
	public Model getDefaultModel() {
		return new ModelCom(this.defaultGraph);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.Dataset#getNamedModel(java.lang.String)
	 */
	public Model getNamedModel(String graphName) {
		return new ModelCom(this.set.getGraph(graphName));
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.Dataset#listNames()
	 */
	public Iterator<String> listNames() {
		final Iterator<NamedGraph> it = this.set.listGraphs();
		return new Iterator<String>() {
			
			/* (non-Javadoc)
			 * @see java.util.Iterator#next()
			 */
			public String next() {
				return (it.next()).getGraphName().getURI();
			}
			
			/* (non-Javadoc)
			 * @see java.util.Iterator#hasNext()
			 */
			public boolean hasNext() {
				return it.hasNext();
			}
			
			/* (non-Javadoc)
			 * @see java.util.Iterator#remove()
			 */
			public void remove() {
				it.remove();
			}
		};
	}


	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.Dataset#getLock()
	 */
	public Lock getLock() {
		// Returns a LockMutex. 
		// TODO Revisit: I've no idea if this works or what it is supposed to do.
		if (this.lock == null) {
			this.lock = new LockMutex();
		}
		return this.lock;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.Dataset#asDatasetGraph()
	 */
	public DatasetGraph asDatasetGraph() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#containsGraph(com.hp.hpl.jena.graph.Node)
	 */
	public boolean containsGraph(Node graphNode) {
		return set.containsGraph(graphNode);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#getDefaultGraph()
	 */
	public Graph getDefaultGraph() {
		return defaultGraph;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#getGraph(com.hp.hpl.jena.graph.Node)
	 */
	public Graph getGraph(Node graphNode) {
		return set.getGraph(graphNode);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#listGraphNodes()
	 */
	public Iterator<Node> listGraphNodes() {
		Set<Node> graphNodes = new HashSet<Node>();
		for ( Iterator<NamedGraph> it = set.listGraphs(); it.hasNext(); ) {
			NamedGraph ng = (NamedGraph) it.next();
			graphNodes.add(ng.getGraphName());
		}
		return graphNodes.iterator();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.sparql.core.DatasetGraph#size()
	 */
	public int size() {
		long numGraphs = set.countGraphs();
		int graphNum = (int) numGraphs;
		return graphNum;
	}

	/**
	 * @see com.hp.hpl.jena.query.Dataset#close()
	 */
	public void close() {
		set.close();
		defaultGraph.close();
	}

}
