// $Id: TriQLQuery.java,v 1.4 2004/12/17 01:44:29 cyganiak Exp $
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

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdql.QueryException;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.legacy.Constraint;
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

	/**
	 * Creates a new query instance whose data source is a NamedGraphSet.
	 * @param dataSource The NamedGraphSet on which to execute the query
	 * @param queryString The TriQL query
	 */
	public TriQLQuery(NamedGraphSet dataSource, String queryString) {
		this(queryString);
		this.source = dataSource;
	}

	/**
	 * Creates a new query instance whose data source is an URL given in
	 * the query's FROM clause, or is passed later to the {@link #setSource}
	 * or {@link #setSourceURL} method. 
	 * @param queryString The TriQL query
	 */
	public TriQLQuery(String queryString) {
		this();
		this.queryString = queryString;
		this.mustParse = true;
	}
	
	/**
	 * Constructor for assembling a TriQL query programmatically. All components
	 * of the query (bound variables, source URL or NamedGraphSet, GraphPatterns,
	 * Constraints, and namespace prefixes) must be passed to the instance using
	 * the methods of the class.
	 */
	public TriQLQuery() {
		this.mustParse = false;
		this.baseURL = getDefaultBaseURL();
		this.prefixes.put("rdf",  "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		this.prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		this.prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#"); 
		this.prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
	}
	
	/**
	 * <p>Convenience method for quick query execution. A NamedGraphSet
	 * is the data source.
	 * 
	 * <pre>Iterator results = TriQLQuery.exec(mySet, "SELECT ?x, ?y FROM ...")</pre>
	 * 
	 * <p>is equivalent to</p>
	 * 
	 * <pre>TriQLQuery query = new TriQLQuery(mySet, "SELECT ?x, ?y FROM ...");
	 * Iterator results = query.getResults();</pre>
	 * 
	 * @param queryString The TriQL query to be executed
	 * @return An iterator over the results
	 * @see #getResults()
	 */
	public static Iterator exec(NamedGraphSet dataSource, String queryString) {
		TriQLQuery query = new TriQLQuery(dataSource, queryString);
		return query.getResults();
	}

	/**
	 * <p>Convenience method for quick query execution. The data source
	 * is an URL which must be given in the query's FROM clause.<p>
	 * 
	 * <pre>Iterator results = TriQLQuery.exec("SELECT ?x, ?y FROM ...")</pre>
	 * 
	 * <p>is equivalent to</p>
	 * 
	 * <pre>TriQLQuery query = new TriQLQuery("SELECT ?x, ?y FROM ...");
	 * Iterator results = query.getResults();</pre>
	 * 
	 * @param queryString The TriQL query to be executed
	 * @return An iterator over the results
	 * @see #getResults()
	 */
	public static Iterator exec(String queryString) {
		TriQLQuery query = new TriQLQuery(queryString);
		return query.getResults();
	}

	/**
	 * Executes the query and delivers results as an iterator over maps.
	 * The keys of each map are the variable names. The values are the
	 * result {@link Node}s. 
	 * @return An iterator over the query results
	 */
	public Iterator getResults() {
		return getResultsAsList().iterator();
	}
	
	/**
	 * Executes the query and delivers results as a list of maps.
	 * The keys of each map are the variable names. The values are the
	 * result {@link Node}s. 
	 * @return A list of the query results
	 */
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

	/**
	 * Programmatically sets the NamedGraphSet on which the query will run.
	 * @param source
	 */
	public void setSource(NamedGraphSet source) {
		this.source = source;
	}

	public NamedGraphSet getSource() {
		return this.source;
	}

	/**
	 * Programmatically sets the source URL. On query execution, the URL
	 * will be fetched and the query will run on the RDF data found there.
	 * @param sourceURL
	 */
	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public String getSourceURL() {
		return this.sourceURL;
	}

	/**
	 * Programmatically adds a result variable. Calling <tt>addResultVar("foo")</tt>
	 * is equivalent to having "?foo" in the SELECT clause.
	 * @param name
	 */
	public void addResultVar(String name) {
		if (this.resultVars.contains(name)) {
			return;
		}
		this.resultVars.add(name);
	}

	public List getResultVars() {
		return this.resultVars;
	}

	/**
	 * TODO: Is this necessary? We can find them all by going throug the patterns.
	 * @param name
	 */
	public void addBoundVar(String name) {
		if (this.boundVars.contains(name)) {
			return;
		}
		this.boundVars.add(name);
	}

	public List getBoundVars() {
		return this.boundVars;
	}

	/**
	 * Programmatically adds a graph pattern to the query. This is equivalent
	 * to having the pattern in the WHERE clause.
	 * @param graphPattern
	 */
	public void addGraphPattern(GraphPattern graphPattern) {
		this.graphPatterns.add(graphPattern);
	}

	public List getGraphPatterns() {
		return this.graphPatterns;
	}

	/**
	 * Programmatically adds a constraint to the query. This is equivalent
	 * to having the constraint in the AND clause.
	 * @param constraint
	 */
	public void addConstraint(Constraint constraint) {
		this.constraints.add(constraint);
	}

	public List getConstraints() {
		return this.constraints;
	}

	/**
	 * Programmatically adds a prefix mapping. This is equivalent to the
	 * USING ... FOR clause.
	 * @param prefix The namespace prefix to be added
	 * @param expansion The full namespace URI
	 */
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