// $Id: QueryResultsMem2.java,v 1.1 2004/10/26 07:17:40 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.io.FileNotFoundException;

import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.QueryResultsMem;

/**
 * Work around some bug in Jena to make tests suite reporting work
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QueryResultsMem2 extends QueryResultsMem {

	public QueryResultsMem2(QueryResults qr) {
		super(qr);
	}

	public QueryResultsMem2(String s) throws FileNotFoundException {
		super(s);
	}

	public void close() {
		// don't close
	}
}
