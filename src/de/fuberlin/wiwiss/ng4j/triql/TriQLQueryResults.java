// $Id: TriQLQueryResults.java,v 1.1 2004/10/26 07:17:39 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdql.QueryException;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.QueryResultsMem;
import com.hp.hpl.jena.rdql.ResultBinding;

/**
 * <p>Delivers results from a TriQL query as RDQL {@link QueryResults} (an iterator
 * over {@link ResultBinding}s). This class exists for compatibility with applications
 * that use RDQL.</p>
 * 
 * <p>Typical usage:</p>
 * 
 * <pre>QueryResults results = new TriQLQueryResults(new TriQLQuery(queryString));</pre>
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriQLQueryResults implements QueryResults {
	private TriQLQuery query;
	private Iterator iterator = null;
	int row = 0;

	public TriQLQueryResults(TriQLQuery query) {
		this.query = query;
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		if (this.iterator == null) {
			this.iterator = this.query.getResults();
		}
		return this.iterator.hasNext();
	}

	public Object next() {
		this.row++;
		return toResultBinding((Map) this.iterator.next());
	}

	public void close() {
		// ignore
	}

	public int getRowNumber() {
		return this.row;
	}

	public List getResultVars() {
		return this.query.getResultVars();
	}

	public List getAll() {
		return this.query.getResultsAsList();
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

	private RDFNode convertNodeToRDFNode(Node n) {
		Model model = null;
		if (n == null) {
			return null;
		}
		if (n.isLiteral()) {
			return new LiteralImpl(n, model);
		}
		if (n.isURI() || n.isBlank()) {
			return new ResourceImpl(n, model);
		}
		throw new QueryException("Variable unbound: " + n);
	}
}
