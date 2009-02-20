// $Id: PrettyNamespacePrefixMaker.java,v 1.6 2009/02/20 08:09:52 hartig Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Analyzes a graph and generates a pretty namespace mapping from it.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class PrettyNamespacePrefixMaker {
	private Graph graph;
	private Map<String,String> defaultNamespaces = new HashMap<String,String>();
	private Map<String,String> foundNamespaces = new HashMap<String,String>();
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
	public Map<String,String> getPrefixMap() {
		if (!this.hasAnalyzed) {
			analyzeGraph();
		}
		return this.foundNamespaces;
	}

	private void analyzeGraph() {
		ExtendedIterator it = this.graph.find(Node.ANY, Node.ANY, Node.ANY);
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

/*
 *  (c) Copyright 2004 - 2009 Christian Bizer (chris@bizer.de)
 *   All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */