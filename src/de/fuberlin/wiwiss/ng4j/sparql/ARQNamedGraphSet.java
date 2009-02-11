package de.fuberlin.wiwiss.ng4j.sparql;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetBase;

/**
 * <p>Wraps an ARQ DataSource into the NamedGraphSet interface.</p>
 * 
 * <p><strong>Note:</strong> The DataSource's default graph is not
 * visible through the NamedGraphSet interface. Only the named graphs
 * are exposed.</p>
 * 
 * <p>The {@link #asJenaGraph(Node)} and {@link #asJenaModel(String)}
 * methods are not implemented.</p>
 * 
 * <p>Useful to read and write TriX and TriG from DataSources.</p>
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ARQNamedGraphSet extends NamedGraphSetBase {
	private DataSource source;
	
	public ARQNamedGraphSet(DataSource source) {
		this.source = source;
	}

	// === Graphset functions ===
	
	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#getGraph(java.lang.String)
	 */
	public NamedGraph getGraph(String graphNameURI) {
		Graph graph = this.source.getNamedModel(graphNameURI).getGraph();
		if (graph == null) {
			return null;
		}
		if (graph instanceof NamedGraph) {
			return (NamedGraph) graph;
		} else {
			return new NamedGraphImpl(graphNameURI, graph);
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#getGraph(com.hp.hpl.jena.graph.Node)
	 */
	public NamedGraph getGraph(Node graphName) {
		return getGraph(graphName.getURI());
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addGraph(de.fuberlin.wiwiss.ng4j.NamedGraph)
	 */
	public void addGraph(NamedGraph graph) {
		if (this.source.containsNamedModel(graph.getGraphName().getURI())) {
			this.source.replaceNamedModel(
					graph.getGraphName().getURI(),
					ModelFactory.createModelForGraph(graph));
		} else {
			this.source.addNamedModel(
					graph.getGraphName().getURI(),
					ModelFactory.createModelForGraph(graph));
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeGraph(com.hp.hpl.jena.graph.Node)
	 */
	public void removeGraph(Node graphName) {
		removeGraph(graphName.getURI());
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeGraph(java.lang.String)
	 */
	public void removeGraph(String graphNameURI) {
		this.source.removeNamedModel(graphNameURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsGraph(com.hp.hpl.jena.graph.Node)
	 */
	public boolean containsGraph(Node graphName) {
		return containsGraph(graphName.getURI());
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsGraph(java.lang.String)
	 */
	public boolean containsGraph(String graphNameURI) {
		return this.source.containsNamedModel(graphNameURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#createGraph(com.hp.hpl.jena.graph.Node)
	 */
	public NamedGraph createGraph(Node graphName) {
		return createGraph(graphName.getURI());
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#createGraph(java.lang.String)
	 */
	public NamedGraph createGraph(String graphNameURI) {
		Graph graph = ModelFactory.createDefaultModel().getGraph();
		if (this.source.containsNamedModel(graphNameURI)) {
			this.source.replaceNamedModel(graphNameURI,
					ModelFactory.createModelForGraph(graph));
		} else {
			this.source.addNamedModel(
					graphNameURI,
					ModelFactory.createModelForGraph(graph));
		}
		return getGraph(graphNameURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#countGraphs()
	 */
	public long countGraphs() {
		Iterator<String> it = this.source.listNames();
		long count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#listGraphs()
	 */
	public Iterator<NamedGraph> listGraphs() {
		return new Iterator<NamedGraph>() {
			private Iterator<String> it = source.listNames();
			
			/* (non-Javadoc)
			 * @see java.util.Iterator#hasNext()
			 */
			public boolean hasNext() {
				return it.hasNext();
			}
			
			/* (non-Javadoc)
			 * @see java.util.Iterator#next()
			 */
			public NamedGraph next() {
				return getGraph( it.next());
			}
			
			/* (non-Javadoc)
			 * @see java.util.Iterator#remove()
			 */
			public void remove() {
				it.remove();
			}
		};
	}

	// === Misc functions ===
	
	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#asJenaGraph(com.hp.hpl.jena.graph.Node)
	 */
	public Graph asJenaGraph(Node defaultGraphForAdding) {
		throw new UnsupportedOperationException("Sorry, not implemented");
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#asJenaModel(java.lang.String)
	 */
	public NamedGraphModel asJenaModel(String defaultGraphForAdding) {
		throw new UnsupportedOperationException("Sorry, not implemented");
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#clear()
	 */
	public void clear() {
		Iterator<NamedGraph> it = listGraphs();
		while (it.hasNext()) {
			NamedGraph g = it.next();
			removeGraph(g.getGraphName());
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#close()
	 */
	public void close() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#isEmpty()
	 */
	public boolean isEmpty() {
		return this.countGraphs() == 0;
	}
}
