// $Id: NamedGraphSetImpl.java,v 1.3 2004/09/13 23:33:26 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;
import com.hp.hpl.jena.util.iterator.NullIterator;

import de.fuberlin.wiwiss.namedgraphs.NamedGraph;
import de.fuberlin.wiwiss.namedgraphs.NamedGraphModel;
import de.fuberlin.wiwiss.namedgraphs.NamedGraphSet;
import de.fuberlin.wiwiss.namedgraphs.Quad;
import de.fuberlin.wiwiss.namedgraphs.trix.NamedGraphSetWriter;


/**
 * Implementation of the {@link NamedGraphSet} interface based on a
 * set of in-memory {@link NamedGraph}s. For details about Named Graphs see
 * <a href="http://www.w3.org/2004/03/trix/">http://www.w3.org/2004/03/trix/</a>.
 *
 * @author Chris Bizer
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphSetImpl implements NamedGraphSet {
	/** Map from names (Node) to NamedGraphs */
	private Map namesToGraphsMap = new HashMap();
	
	/**
	 * List of all NamedGraphs that backs the UnionGraphs handed
	 * out by {@link #asJenaGraph(Node)}.
	 * This whole graphs List affair is probably rather slow.
	 */
	private List graphs = new ArrayList();

	public void addGraph(NamedGraph graph) {
		this.graphs.remove(this.namesToGraphsMap.get(graph.getGraphName()));
		if (!this.graphs.contains(graph)) {
			this.graphs.add(graph);
		}
		this.namesToGraphsMap.put(graph.getGraphName(), graph);
	}

	public boolean containsGraph(Node graphName) {
		if (!graphName.isConcrete()) {
			return !isEmpty();
		}
		return this.namesToGraphsMap.containsKey(graphName);
	}

	public boolean containsGraph(String graphNameURI) {
		return this.namesToGraphsMap.containsKey(
				Node.createURI(graphNameURI));
	}

	public long countGraphs() {
		return this.namesToGraphsMap.size();
	}

	public NamedGraph createGraph(Node graphName) {
		NamedGraph newGraph = createNamedGraphInstance(graphName);
		addGraph(newGraph);
		return newGraph;
	}

	public NamedGraph createGraph(String graphNameURI) {
		return createGraph(Node.createURI(graphNameURI));
	}

	public NamedGraph getGraph(Node graphName) {
		return (NamedGraph) this.namesToGraphsMap.get(graphName);
	}

	public NamedGraph getGraph(String graphNameURI) {
		return getGraph(Node.createURI(graphNameURI));
	}

	public boolean isEmpty() {
		return this.namesToGraphsMap.isEmpty();
	}

	public Iterator listGraphs() {
		return this.namesToGraphsMap.values().iterator();
	}

	public void removeGraph(Node graphName) {
		if (graphName.isConcrete()) {
			this.graphs.remove(this.namesToGraphsMap.get(graphName));
			this.namesToGraphsMap.remove(graphName);
		} else {
			this.namesToGraphsMap.clear();
			this.graphs.clear();
		}
	}

	public void removeGraph(String graphNameURI) {
		removeGraph(Node.createURI(graphNameURI));
	}

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
		if (pattern.getGraphName().isConcrete()) {
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
		if (!pattern.getGraphName().isConcrete()) {
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

	public Graph asJenaGraph(Node defaultGraphForAdding) {
		if (!containsGraph(defaultGraphForAdding)) {
			createGraph(defaultGraphForAdding);
		}
		MultiUnion unionGraph = new UnionGraph(this.graphs);
		unionGraph.setBaseGraph(getGraph(defaultGraphForAdding));
		return unionGraph;
	}

	public NamedGraphModel asJenaModel(String defaultGraphForAdding) {
		if (defaultGraphForAdding == null) {
			return new NamedGraphModel(this, null);
		}
		return new NamedGraphModel(this, defaultGraphForAdding);
	}

	public void close() {
		Iterator it = listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = (NamedGraph) it.next();
			graph.close();
		}
	}

	// TODO: Write tests for NamedGraphSet.read and .write
	public void read(InputStream source, String baseURI, String lang) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceInputStream(source, baseURI);
		service.setLanguage(lang);
		service.readInto(this);
	}

	public void read(Reader source, String baseURI, String lang) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceReader(source, baseURI);
		service.setLanguage(lang);
		service.readInto(this);
	}

	public void read(String url, String lang) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceURL(url);
		service.setLanguage(lang);
		service.readInto(this);
	}

	public void write(OutputStream out, String lang) {
		// TODO: Ugly! Fix this
		if ("TRIX".equals(lang)) {
			new NamedGraphSetWriter().write(this, out);
		} else {
			// can fail if no graph in set
			NamedGraph firstGraph = (NamedGraph) listGraphs().next();
			asJenaModel(firstGraph.getGraphName().toString()).write(out, lang);
		}
	}

	public void write(Writer out, String lang) {
		// TODO: Ugly! Fix this
		if ("TRIX".equals(lang)) {
			new NamedGraphSetWriter().write(this, out);
		} else {
			// can fail if no graph in set
			NamedGraph firstGraph = (NamedGraph) listGraphs().next();
			asJenaModel(firstGraph.getGraphName().toString()).write(out, lang);
		}
	}
	
	protected NamedGraph createNamedGraphInstance(Node graphName) {
		if (!graphName.isURI()) {
			throw new IllegalArgumentException("Graph names must be URIs");
		}
		return new NamedGraphImpl(graphName, new GraphMem());
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
		ExtendedIterator iteratorChain = new NullIterator();
		Iterator it = listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = (NamedGraph) it.next();
			ExtendedIterator itForGraph = getQuadIteratorOverGraph(graph, triple);
			iteratorChain = itForGraph.andThen(iteratorChain);
		}
		return iteratorChain;
	}

	/**
	 * Subclass of {@link MultiUnion} that allows the list of member
	 * graphs to be directly passed to the constructor. When we later
	 * change the list (add or remove graphs from the NamedGraphSet),
	 * the member list of the MultiUnion is automatically updated.
	 * <p>
	 * Note: This is a hack.
	 */
	private class UnionGraph extends MultiUnion {
		public UnionGraph (List members) {
			super();
			this.m_subGraphs = members;
		}

		/**
		 * MultiUnion deletes from the baseGraph only; we want to
		 * delete from all member graphs
		 */
		public void delete(Triple t) {
			Iterator it = this.m_subGraphs.iterator();
			while (it.hasNext()) {
				Graph member = (Graph) it.next();
				member.delete(t);
			}
		}
	}
}