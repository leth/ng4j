// $Id: NamedGraphImpl.java,v 1.1 2004/09/13 14:37:26 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs.impl;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.namedgraphs.NamedGraph;
import de.fuberlin.wiwiss.namedgraphs.NamedGraphSet;

/**
 * NamedGraph implementation that wraps a {@link Graph} instance
 * and a name for that graph. All methods inherited from the
 * Graph interface are delegated to the underlying Graph
 * instance.
 * <p>
 * NamedGraph instances can be created from any Jena {@link Graph}
 * instance, or by calling {@link NamedGraphSet#createGraph(Node)}
 * on a NamedGraphSet.
 * <p>
 * TODO: Implement equals, but with what semantics?
 * 
 * @author Chris Bizer
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @see Graph
 * @see NamedGraphSet#createGraph(Node)
 */
public class NamedGraphImpl implements NamedGraph {
	private Node name;
	private Graph graph;

	/**
	 * Creates a NamedGraph from a Graph instance and a name.
	 * The name must be an URI.
	 * @param graphName a name for the graph; must be an URI Node
	 * @param graph a graph instance
	 * @see NamedGraphSet#createGraph(Node)
	 */
	public NamedGraphImpl(Node graphName, Graph graph) {
		this.graph = graph;
		this.name = graphName;
	}

	/**
	 * Creates a NamedGraph from a Graph instance and a name.
	 * @param graphNameURI a name for the graph; must be an URI
	 * @param graph a graph instance
	 * @see NamedGraphSet#createGraph(Node)
	 */
	public NamedGraphImpl(String graphNameURI, Graph graph) {
		this(Node.createURI(graphNameURI), graph);
	}

	/**
	 * Returns the URI of the named graph. The returned Node
	 * instance is always an URI and cannot be a blank node
	 * or literal.
	 */
	public Node getGraphName() {
		return this.name;
	}

	// === Delegations to the underlying Graph ===

	public void close() {
		this.graph.close();
	}

	public boolean contains(Node s, Node p, Node o) {
		return this.graph.contains(s, p, o);
	}

	public boolean contains(Triple t) {
		return this.graph.contains(t);
	}

	public void delete(Triple t) throws DeleteDeniedException {
		this.graph.delete(t);
	}

	public boolean dependsOn(Graph other) {
		return this.graph.dependsOn(other);
	}

	public ExtendedIterator find(Node s, Node p, Node o) {
		return this.graph.find(s, p, o);
	}

	public ExtendedIterator find(TripleMatch m) {
		return this.graph.find(m);
	}
	
	public BulkUpdateHandler getBulkUpdateHandler() {
		return this.graph.getBulkUpdateHandler();
	}

	public Capabilities getCapabilities() {
		return this.graph.getCapabilities();
	}

	public GraphEventManager getEventManager() {
		return this.graph.getEventManager();
	}

	public PrefixMapping getPrefixMapping() {
		return this.graph.getPrefixMapping();
	}

	public Reifier getReifier() {
		return this.graph.getReifier();
	}

	public TransactionHandler getTransactionHandler() {
		return this.graph.getTransactionHandler();
	}

	public boolean isEmpty() {
		return this.graph.isEmpty();
	}

	public boolean isIsomorphicWith(Graph g) {
		return this.graph.isIsomorphicWith(g);
	}

	public QueryHandler queryHandler() {
		return this.graph.queryHandler();
	}

	public int size() {
		return this.graph.size();
	}

	public void add(Triple t) throws AddDeniedException {
		this.graph.add(t);
	}
}
