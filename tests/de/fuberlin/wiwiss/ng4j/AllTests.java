/*
 * $Id: AllTests.java,v 1.3 2004/11/02 02:00:25 cyganiak Exp $
 */
package de.fuberlin.wiwiss.ng4j;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for the whole project
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
			"Test for de.fuberlin.wiwiss.namedgraphs");
		//$JUnit-BEGIN$
		suite.addTestSuite(NamedGraphSetTest.class);
		suite.addTestSuite(UnionGraphTest.class);
		suite.addTestSuite(NamedGraphModelTest.class);
		suite.addTestSuite(NamedGraphStatementIteratorTest.class);
		//$JUnit-END$
		suite.addTest(de.fuberlin.wiwiss.ng4j.db.AllTests.suite());
		suite.addTest(de.fuberlin.wiwiss.ng4j.trix.AllTests.suite());
		suite.addTest(de.fuberlin.wiwiss.ng4j.triql.AllTests.suite());
		return suite;
	}
}