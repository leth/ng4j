// $Id: AllTests.java,v 1.1 2005/02/18 18:44:02 erw Exp $
package de.fuberlin.wiwiss.ng4j.swp.signature;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for NamedGraphSetDB and its helper classes. Needs a MySQL database.
 * Connection data must be provided inside this file.
 *
 * TODO: Database access data shouldn't be here
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.fuberlin.wiwiss.ng4j.swp.signature");
		//$JUnit-BEGIN$
		suite.addTestSuite(SWPSignatureUtilitiesTest.class);
		//$JUnit-END$
		return suite;
	}
}
