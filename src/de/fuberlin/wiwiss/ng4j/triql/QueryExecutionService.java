// $Id: QueryExecutionService.java,v 1.7 2005/01/30 22:08:58 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.rdql.QueryException;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.triql.legacy.Constraint;

/**
 * <p>Executes a TriQL query and returns results. This class is used by
 * {@link TriQLQuery#getResults()} and is usually not used explicitly.</p>
 *
 * <p>The class implements the TriQL query algorithm. It's a na•ve
 * implementation. The first triple pattern is matched against the
 * proper graph(s), then for every match, a recursive call matches
 * the second pattern, etc. Finally, the constraint clauses are evaluated.
 * For this part, the RDQL implementation is used.</p>
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QueryExecutionService {
	private TriQLQuery query;
	private NamedGraphSet source;
	private List results;

	/**
	 * Constructs a new QueryExecutionService from a TriQL query.
	 * @param query a TriQL query object
	 */
	public QueryExecutionService(TriQLQuery query) {
		this.query = query;
	}
	
	/**
	 * Executes the query and returns a list of results.
	 * @return a list of maps from variable names to result {@link Node}s
	 */
	public List execute() {
		this.source = initSource();
		this.results = new ArrayList();
		Map matchedVars = cloneMap(this.query.getPreboundVariableValues());
		matchGraphPattern(0, matchedVars);
		return this.results;
	}

	private void matchGraphPattern(int index, Map matchedVars) {
		if (index >= this.query.getGraphPatterns().size()) {
			matchConstraints(matchedVars);
			return;
		}
		GraphPattern gp =
				(GraphPattern) this.query.getGraphPatterns().get(index);
		Node graphName = getPossiblyMatchedNode(gp.getName(), matchedVars);
		if (!this.source.containsGraph(varToAny(graphName))) {
			return;
		}
		if (graphName.isConcrete()) {
			matchGraph(index, this.source.getGraph(graphName), matchedVars);
		} else {
			Iterator it = this.source.listGraphs();
			while (it.hasNext()) {
				NamedGraph graph = (NamedGraph) it.next();
				if (graphName.isVariable()) {
					Map matchedVarsCopy = cloneMap(matchedVars);
					matchedVarsCopy.put(graphName.getName(), graph.getGraphName());
					matchGraph(index, graph, matchedVarsCopy);
				} else {
					matchGraph(index, graph, matchedVars);
				}
			}
		}
	}
	
	private void matchGraph(int index, NamedGraph graph, Map matchedVars) {
		matchTriplePattern(0, index, graph, matchedVars);
	}
	
	private void matchTriplePattern(int tripleIndex, int graphIndex, NamedGraph graph,
			Map matchedVars) {
		GraphPattern gp =
			(GraphPattern) this.query.getGraphPatterns().get(graphIndex);
		if (tripleIndex >= gp.getTripleCount()) {
			matchGraphPattern(graphIndex + 1, matchedVars);
			return;
		}
		Triple tp = gp.getTriple(tripleIndex);
		Iterator it = graph.find(getTripleMatch(tp, matchedVars));
		while (it.hasNext()) {
			Triple match = (Triple) it.next();
			if (containsNonMatchingDuplicates(tp, match)) {
				continue;
			}
			Map matchedVarsCopy = cloneMap(matchedVars);
			if (tp.getSubject().isVariable()) {
				matchedVarsCopy.put(tp.getSubject().getName(), match.getSubject());
			}
			if (tp.getPredicate().isVariable()) {
				matchedVarsCopy.put(tp.getPredicate().getName(), match.getPredicate());
			}
			if (tp.getObject().isVariable()) {
				matchedVarsCopy.put(tp.getObject().getName(), match.getObject());
			}
			matchTriplePattern(tripleIndex + 1, graphIndex, graph, matchedVarsCopy);
		}
	}

	private boolean containsNonMatchingDuplicates(Triple pattern, Triple potentialMatch) {
		if (areSameVariable(pattern.getSubject(), pattern.getPredicate())) {
			return !potentialMatch.getSubject().matches(potentialMatch.getPredicate());
		}
		if (areSameVariable(pattern.getSubject(), pattern.getObject())) {
			return !potentialMatch.getSubject().matches(potentialMatch.getObject());
		}
		if (areSameVariable(pattern.getObject(), pattern.getPredicate())) {
			return !potentialMatch.getObject().matches(potentialMatch.getPredicate());
		}
		return false;
	}
	
	private boolean areSameVariable(Node v1, Node v2) {
		return v1.isVariable() && v2.isVariable() && v1.getName().equals(v2.getName());
	}

	private Node varToAny(Node node) {
		return node.isVariable() ? Node.ANY : node;
	}

	private Node getPossiblyMatchedNode(Node node, Map matchedVars) {
		if (node.isVariable() && matchedVars.containsKey(node.getName())) {
			return (Node) matchedVars.get(node.getName());
		}
		return node;
	}

	private Node eliminateVariables(Node node, Map matchedVars) {
		if (node.isVariable()) {
			if (matchedVars.containsKey(node.getName())) {
				return (Node) matchedVars.get(node.getName());
			}
			return Node.ANY;
		}
		return node;
	}

	private Triple getTripleMatch(Triple triple, Map matchedVars) {
		return new Triple(
				eliminateVariables(triple.getSubject(), matchedVars),
				eliminateVariables(triple.getPredicate(), matchedVars),
				eliminateVariables(triple.getObject(), matchedVars));
	}

	private void matchConstraints(Map matchedVars) {
		Iterator it = this.query.getConstraints().iterator();
		while (it.hasNext()) {
			Constraint constraint = (Constraint) it.next();
			if (!constraint.isSatisfied(null, toResultBinding(matchedVars))) {
				return;
			}
		}
		addResultBinding(matchedVars);
	}

	private void addResultBinding(Map matchedVars) {
		Map binding = new HashMap();
		Iterator it = this.query.getResultVars().iterator();
		while (it.hasNext()) {
			String varName = (String) it.next();
			binding.put(varName, matchedVars.get(varName));
		}
		this.results.add(binding);		
	}

	private ResultBinding toResultBinding(Map matchedVars) {
		ResultBinding result = new ResultBinding();
		Iterator it = matchedVars.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String varName = (String) entry.getKey();
			Node varValue = (Node) entry.getValue();
			result.add(varName, convertNodeToRDFNode(varValue));
		}
		return result;
	}

	private NamedGraphSet initSource() {
		if (this.query.getSource() != null) {
			return this.query.getSource();
		}
		NamedGraphSet result = new NamedGraphSetImpl();
		GraphReaderService reader = new GraphReaderService();
		URL url;
		try {
			url = new URL(this.query.getBaseURL(), this.query.getSourceURL());
		} catch (MalformedURLException ex) {
			try {
				url = new URL(this.query.getBaseURL(), "file:" + this.query.getSourceURL());
			} catch (MalformedURLException ex2) {
				throw new QueryException("Query Exception: " + ex2);
			}
		}
		reader.setSourceURL(url.toString());
		reader.readInto(result);
		return result;
	}

	private RDFNode convertNodeToRDFNode(Node n) {
		return StatementImpl.createObject(n, null);
	}
	
	private Map cloneMap(Map original) {
		Map result = new HashMap();
		Iterator it = original.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
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