// $Id: AllTests.java,v 1.1 2004/11/02 02:00:25 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.db;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.fuberlin.wiwiss.ng4j.db");
		//$JUnit-BEGIN$
		suite.addTestSuite(MySQLTest.class);
		suite.addTestSuite(QuadDBTest.class);
		//$JUnit-END$
		return suite;
	}
}
