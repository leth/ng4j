// $Id: TriQLQuery.java,v 1.1 2004/10/26 07:17:39 cyganiak Exp $
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
 * TODO: Describe this type
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
