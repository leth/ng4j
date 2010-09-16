// $Id: AllTests.java,v 1.4 2010/09/16 13:38:18 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for NamedGraphSetDB and its helper classes. 
 * Connection data must be provided inside class DBConnectionHelper.
 * Defaults to in-memory HSQLDB, but can be changed to another
 * database such as PostgreSQL or MySQL.  If changed, do so only locally,
 * and include the appropriate database driver as a library on the .classpath.
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
		suite.addTestSuite(QuadDBTest.class);
//		suite.addTestSuite(TriQLAgainstDBTest.class);
		suite.addTestSuite(SPARQLAgainstDBTest.class);
		suite.addTestSuite(NamedGraphSetDBTest.class);
		//$JUnit-END$
		return suite;
	}
}
