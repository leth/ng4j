// $Id: AllTests.java,v 1.2 2008/09/04 07:37:51 hartig Exp $
package de.fuberlin.wiwiss.ng4j.swp;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for SWP classes.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.fuberlin.wiwiss.ng4j.swp");
		//$JUnit-BEGIN$
		suite.addTestSuite(SWPNamedGraphSetTest.class);
		//$JUnit-END$
		return suite;
	}
}
