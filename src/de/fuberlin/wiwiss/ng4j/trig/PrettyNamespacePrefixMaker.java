// $Id: PrettyNamespacePrefixMaker.java,v 1.1 2004/12/17 05:06:31 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * Analyzes a graph and generates a pretty namespace mapping from it.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class PrettyNamespacePrefixMaker {
	private Graph graph;
	private Map defaultNamespaces = new HashMap();
	private Map foundNamespaces = new HashMap();
	private int namespaceCount = 0;
	private boolean hasAnalyzed = false;

	public PrettyNamespacePrefixMaker(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Default namespaces are only declared if an URI of that namespace
	 * is actually used in the graph.
	 */
	public void addDefaultNamespace(String prefix, String namespaceURI) {
		this.defaultNamespaces.put(namespaceURI, prefix);
	}
	
	/**
	 * Declares a namespace that will be part of the mapping.
	 */
	public void addNamespace(String prefix, String namespaceURI) {
		this.foundNamespaces.put(prefix, namespaceURI);
	}
	
	/**
	 * Sets a base URI. It will be used as the default namespace.
	 */
	public void setBaseURI(String baseURI) {
		if (baseURI == null) {
			return;
		}
		this.foundNamespaces.put("", removeFinalHash(baseURI) + "#");
	}

	/**
	 * Returns the finished prefix map. Keys are string prefixes, values are
	 * string URIs.
	 */
	public Map getPrefixMap() {
		if (!this.hasAnalyzed) {
			analyzeGraph();
		}
		return this.foundNamespaces;
	}

	private void analyzeGraph() {
		Iterator it = this.graph.find(Node.ANY, Node.ANY, Node.ANY);
		while (it.hasNext()) {
			Triple triple = (Triple) it.next();
			analyzeURI(triple.getPredicate().getURI());
		}
		this.hasAnalyzed = true;
	}

	private void analyzeURI(String uri) {
		String namespaceURI = getNamespacePart(uri);
		if (namespaceURI == null) {
			return;
		}
		if (this.foundNamespaces.containsValue(namespaceURI)) {
			return;
		}
		if (this.defaultNamespaces.containsKey(namespaceURI)) {
			this.foundNamespaces.put(
					this.defaultNamespaces.get(namespaceURI), namespaceURI);
			return;
		}
		this.foundNamespaces.put("ns" + this.namespaceCount, namespaceURI);
		this.namespaceCount++;
	}

	private String getNamespacePart(String uri) {
		int lastHash = uri.lastIndexOf("#");
		int lastSlash = uri.lastIndexOf("/");
		if (lastHash >= 0 && lastHash > lastSlash) {
			return uri.substring(0, lastHash + 1);
		}
		if (lastSlash >= 0) {
			return uri.substring(0, lastSlash + 1);
		}
		return null;
	}

	private String removeFinalHash(String uri) {
		if (uri.endsWith("#")) {
			return uri.substring(0, uri.length() - 1);
		}
		return uri;
	}
}
