// $Id: AllTests.java,v 1.1 2004/11/22 02:48:52 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TODO: Describe this type
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.fuberlin.wiwiss.ng4j.trig");
		//$JUnit-BEGIN$
		suite.addTestSuite(TriGParserTest.class);
		suite.addTest(N3Tests.suite());
		//$JUnit-END$
		return suite;
	}
}
