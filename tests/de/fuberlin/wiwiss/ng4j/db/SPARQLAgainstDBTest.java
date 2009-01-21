// $Id: SPARQLAgainstDBTest.java,v 1.1 2009/01/21 01:37:39 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.sparql.SPARQLTest;

/** Runs the SPARQL query tests with a database backend.
 * 
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class SPARQLAgainstDBTest extends SPARQLTest {

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.sparql.SPARQLTest#createNamedGraphSet()
	 */
	public NamedGraphSet createNamedGraphSet() throws Exception {
		return DBConnectionHelper.createNamedGraphSetDB();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.sparql.SPARQLTest#tearDown()
	 */
	public void tearDown() throws Exception {
		super.tearDown();
		DBConnectionHelper.deleteNamedGraphSetTables();
	}
}
