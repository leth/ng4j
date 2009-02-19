// $Id: AllTests.java,v 1.3 2009/02/19 22:09:39 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.swp.util;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for SWP util classes.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.fuberlin.wiwiss.ng4j.swp.util");
		//$JUnit-BEGIN$
		suite.addTestSuite(SWPSignatureUtilitiesTest.class);
		//$JUnit-END$
		return suite;
	}
}
