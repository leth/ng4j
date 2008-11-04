// $Id: QueryExecutionService.java,v 1.10 2008/11/04 13:00:21 hartig Exp $
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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.query.QueryException;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
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
 * TODO: Refactor containsNonMatchingDuplicates(); it should check
 * for duplicate variables in a single graph pattern before the query
 * executes, and do the runtime checks only if any were found
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QueryExecutionService {
	private TriQLQuery query;
	private NamedGraphSet source;
	private List results;
	private List quadPatterns = new ArrayList();

	/**
	 * Constructs a new QueryExecutionService from a TriQL query.
	 * @param query a TriQL query object
	 */
	public QueryExecutionService(TriQLQuery query) {
		this.query = query;
		buildQuadPatternList();
	}
	
	/**
	 * Executes the query and returns a list of results.
	 * @return a list of maps from variable names to result {@link Node}s
	 */
	public List execute() {
		this.source = initSource();
		this.results = new ArrayList();
		Map matchedVars = cloneMap(this.query.getPreboundVariableValues());
		matchQuadPattern(0, matchedVars);
		return this.results;
	}

	private void buildQuadPatternList() {
		Iterator it = this.query.getGraphPatterns().iterator();
		while (it.hasNext()) {
            GraphPattern graphPattern = (GraphPattern) it.next();
            this.quadPatterns.addAll(graphPattern.getQuads());
        }
	}
	
	private void matchQuadPattern(int quadIndex, Map matchedVars) {
		if (quadIndex >= this.quadPatterns.size()) {
			matchConstraints(matchedVars);
			return;
		}
		Quad quadPattern = (Quad) this.quadPatterns.get(quadIndex);
		Iterator it = this.source.findQuads(replaceVariables(quadPattern, matchedVars));
		while (it.hasNext()) {
			Quad match = (Quad) it.next();
			if (containsNonMatchingDuplicates(quadPattern, match)) {
				continue;
			}
			Map matchedVarsCopy = cloneMap(matchedVars);
			if (quadPattern.getGraphName().isVariable()) {
				matchedVarsCopy.put(quadPattern.getGraphName().getName(), match.getGraphName());
			}
			if (quadPattern.getSubject().isVariable()) {
				matchedVarsCopy.put(quadPattern.getSubject().getName(), match.getSubject());
			}
			if (quadPattern.getPredicate().isVariable()) {
				matchedVarsCopy.put(quadPattern.getPredicate().getName(), match.getPredicate());
			}
			if (quadPattern.getObject().isVariable()) {
				matchedVarsCopy.put(quadPattern.getObject().getName(), match.getObject());
			}
			matchQuadPattern(quadIndex + 1, matchedVarsCopy);
		}
	}

	private Quad replaceVariables(Quad quadPattern, Map matchedVars) {
		return new Quad(
				eliminateVariables(quadPattern.getGraphName(), matchedVars),
				eliminateVariables(quadPattern.getSubject(), matchedVars),
				eliminateVariables(quadPattern.getPredicate(), matchedVars),
				eliminateVariables(quadPattern.getObject(), matchedVars));
	}

	private boolean containsNonMatchingDuplicates(Quad pattern, Quad potentialMatch) {
		if (areSameVariable(pattern.getSubject(), pattern.getPredicate())) {
			return !potentialMatch.getSubject().matches(potentialMatch.getPredicate());
		}
		if (areSameVariable(pattern.getSubject(), pattern.getObject())) {
			return !potentialMatch.getSubject().matches(potentialMatch.getObject());
		}
		if (areSameVariable(pattern.getObject(), pattern.getPredicate())) {
			return !potentialMatch.getObject().matches(potentialMatch.getPredicate());
		}
		if (areSameVariable(pattern.getGraphName(), pattern.getSubject())) {
			return !potentialMatch.getGraphName().matches(potentialMatch.getSubject());
		}
		if (areSameVariable(pattern.getGraphName(), pattern.getPredicate())) {
			return !potentialMatch.getGraphName().matches(potentialMatch.getPredicate());
		}
		if (areSameVariable(pattern.getGraphName(), pattern.getObject())) {
			return !potentialMatch.getGraphName().matches(potentialMatch.getObject());
		}
		return false;
	}
	
	private boolean areSameVariable(Node v1, Node v2) {
		return v1.isVariable() && v2.isVariable() && v1.getName().equals(v2.getName());
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
 *  (c) Copyright 2004, 2005, 2006, 2007, 2008 Christian Bizer (chris@bizer.de)
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