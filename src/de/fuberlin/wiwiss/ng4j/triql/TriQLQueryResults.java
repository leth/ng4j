// $Id: TriQLQueryResults.java,v 1.3 2004/12/12 17:30:29 cyganiak Exp $
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