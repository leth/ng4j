// $Id: TriQLQuery.java,v 1.2 2004/11/02 02:00:24 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdql.Constraint;
import com.hp.hpl.jena.rdql.QueryException;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_Query;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;

/**
 * <p>A <a href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriQL/">TriQL</a>
 * query. The data source can be a {@link NamedGraphSet}, or any RDF document
 * given in the query's FROM clause.</p>
 * 
 * <p>A query is executed by creating a
 * new TriQLQuery instance and calling its {@link #getResults()} method.
 * The static {@link #exec(String)} and {@link #exec(NamedGraphSet, String)} methods
 * combine both steps into a single call.</p>
 * 
 * <p>TriQL query results are iterators over a list of maps. The map's keys are
 * the names of the result variables. The map's values are the corresponding
 * result values.</p> 
 *
 * <p>The class provides methods for assembling a query programmatically. This
 * is an alternative to creating the query from a String. For example, one can
 * add prefix bindings using the {@link #setPrefix(String, String)} method,
 * instead of the ususal USING ... FOR clauses.</p>
 * 
 * TODO: Documentation
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriQLQuery {
	private NamedGraphSet source = null;
	private String queryString = null;
	private boolean mustParse;
	private URL baseURL;

	private String sourceURL = null;
	private List resultVars = new ArrayList(); // [String]
	private List boundVars = new ArrayList(); // [String]
	private List graphPatterns = new ArrayList(); // [GraphPattern]
	private List constraints = new ArrayList(); // [Constraint]
	private Map prefixes = new HashMap(); // {String => String}

	public TriQLQuery(NamedGraphSet dataSource, String queryString) {
		this(queryString);
		this.source = dataSource;
	}

	public TriQLQuery(String queryString) {
		this();
		this.queryString = queryString;
		this.mustParse = true;
	}
	
	public TriQLQuery() {
		this.mustParse = false;
		this.baseURL = getDefaultBaseURL();
		this.prefixes.put("rdf",  "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		this.prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		this.prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#"); 
		this.prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
	}
	
	public static Iterator exec(NamedGraphSet dataSource, String queryString) {
		TriQLQuery query = new TriQLQuery(dataSource, queryString);
		return query.getResults();
	}
	
	public static Iterator exec(String queryString) {
		TriQLQuery query = new TriQLQuery(queryString);
		return query.getResults();
	}

	public Iterator getResults() {
		return getResultsAsList().iterator();
	}
	
	public List getResultsAsList() {
		if (this.mustParse) {
			parse();
			this.mustParse = false;
		}
		if (this.sourceURL == null && this.source == null) {
			throw new QueryException("Missing FROM clause or source");
		}
		return new QueryExecutionService(this).execute();
	}

	/**
	 * If the FROM URI of the query is relative, it will be evaluated relative
	 * to this base URI.
	 * @param baseURL
	 */
	public void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}
	
	public URL getBaseURL() {
		return this.baseURL;
	}

	public void setSource(NamedGraphSet source) {
		this.source = source;
	}

	public NamedGraphSet getSource() {
		return this.source;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public String getSourceURL() {
		return this.sourceURL;
	}

	public void addResultVar(String name) {
		if (this.resultVars.contains(name)) {
			return;
		}
		this.resultVars.add(name);
	}

	public List getResultVars() {
		return this.resultVars;
	}

	public void addBoundVar(String name) {
		if (this.boundVars.contains(name)) {
			return;
		}
		this.boundVars.add(name);
	}

	public List getBoundVars() {
		return this.boundVars;
	}

	public void addGraphPattern(GraphPattern graphPattern) {
		this.graphPatterns.add(graphPattern);
	}

	public List getGraphPatterns() {
		return this.graphPatterns;
	}

	public void addConstraint(Constraint constraint) {
		this.constraints.add(constraint);
	}

	public List getConstraints() {
		return this.constraints;
	}

	public void setPrefix(String prefix, String expansion) {
		this.prefixes.put(prefix, expansion);
	}

	public String getPrefix(String prefix) {
		return (String) this.prefixes.get(prefix);
	}

	private void parse() {
		Q_Query queryNode = null ;
		try {
			TriQLParser parser = new TriQLParser(new StringReader(this.queryString));
			parser.CompilationUnit();
			queryNode = (Q_Query)parser.top();
			queryNode.buildQuery(this);
		} catch (QueryException qEx) {
			throw qEx;
		} catch (Exception e) {
            throw new QueryException("Parse error: "+e) ;
		}
	}
	
	private URL getDefaultBaseURL() {
		try {
			return new File(".").toURL();
		} catch (MalformedURLException ex) {
			return null;
		}
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