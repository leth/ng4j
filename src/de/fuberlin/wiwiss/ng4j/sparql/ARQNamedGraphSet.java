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
	
	public NamedGraph getGraph(String graphNameURI) {
		Graph graph = this.source.getNamedGraph(graphNameURI);
		if (graph == null) {
			return null;
		}
		if (graph instanceof NamedGraph) {
			return (NamedGraph) graph;
		} else {
			return new NamedGraphImpl(graphNameURI, graph);
		}
	}

	public NamedGraph getGraph(Node graphName) {
		return getGraph(graphName.getURI());
	}

	public void addGraph(NamedGraph graph) {
		if (this.source.containsNamedGraph(graph.getGraphName().getURI())) {
			this.source.replaceNamedGraph(graph.getGraphName().getURI(), graph);
		} else {
			this.source.addNamedGraph(graph.getGraphName().getURI(), graph);
		}
	}

	public void removeGraph(Node graphName) {
		removeGraph(graphName.getURI());
	}

	public void removeGraph(String graphNameURI) {
		this.source.removeNamedGraph(graphNameURI);
	}

	public boolean containsGraph(Node graphName) {
		return containsGraph(graphName.getURI());
	}

	public boolean containsGraph(String graphNameURI) {
		return this.source.containsNamedGraph(graphNameURI);
	}

	public NamedGraph createGraph(Node graphName) {
		return createGraph(graphName.getURI());
	}

	public NamedGraph createGraph(String graphNameURI) {
		Graph graph = ModelFactory.createDefaultModel().getGraph();
		if (this.source.containsNamedGraph(graphNameURI)) {
			this.source.replaceNamedGraph(graphNameURI, graph);
		} else {
			this.source.addNamedGraph(graphNameURI, graph);
		}
		return getGraph(graphNameURI);
	}

	public long countGraphs() {
		Iterator it = this.source.listNames();
		long count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		return count;
	}

	public Iterator listGraphs() {
		return new Iterator() {
			private Iterator it = source.listNames();
			public boolean hasNext() {
				return it.hasNext();
			}
			public Object next() {
				return getGraph((String) it.next());
			}
			public void remove() {
				it.remove();
			}
		};
	}

	// === Misc functions ===
	
	public Graph asJenaGraph(Node defaultGraphForAdding) {
		throw new UnsupportedOperationException("Sorry, not implemented");
	}

	public NamedGraphModel asJenaModel(String defaultGraphForAdding) {
		throw new UnsupportedOperationException("Sorry, not implemented");
	}

	public void clear() {
		Iterator it = listGraphs();
		while (it.hasNext()) {
			NamedGraph g = (NamedGraph) it.next();
			removeGraph(g.getGraphName());
		}
	}

	public void close() {
		// do nothing
	}

	public boolean isEmpty() {
		return this.countGraphs() == 0;
	}
}
