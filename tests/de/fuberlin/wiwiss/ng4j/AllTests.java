/*
 * $Id: AllTests.java,v 1.9 2005/02/24 09:11:16 cyganiak Exp $
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
		suite.addTestSuite(UnionGraphTest.class);
		suite.addTestSuite(NamedGraphSetTest.class);
		suite.addTestSuite(NamedGraphModelTest.class);
		suite.addTestSuite(NamedGraphStatementIteratorTest.class);
		//$JUnit-END$
		suite.addTest(de.fuberlin.wiwiss.ng4j.db.AllTests.suite());
		suite.addTest(de.fuberlin.wiwiss.ng4j.trix.AllTests.suite());
		suite.addTest(de.fuberlin.wiwiss.ng4j.triql.AllTests.suite());
		suite.addTest(de.fuberlin.wiwiss.ng4j.trig.AllTests.suite());
		suite.addTest(de.fuberlin.wiwiss.ng4j.swp.AllTests.suite());
		suite.addTest(de.fuberlin.wiwiss.ng4j.swp.util.AllTests.suite());
		return suite;
	}
}