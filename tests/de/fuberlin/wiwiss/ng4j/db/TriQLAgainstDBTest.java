// $Id: TriQLAgainstDBTest.java,v 1.1 2004/12/12 17:30:29 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.db;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.triql.TriQLGraphTest;

/**
 * Runs TriQLGraphTest against a NamedGraphSetDB.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriQLAgainstDBTest extends TriQLGraphTest {

	public NamedGraphSet createNamedGraphSet() throws Exception {
		return DBConnectionHelper.createNamedGraphSetDB();
	}

	public void tearDown() throws Exception {
		super.tearDown();
		DBConnectionHelper.deleteNamedGraphSetTables();
	}
}
