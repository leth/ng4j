// $Id: AllTests.java,v 1.2 2004/11/25 22:14:38 cyganiak Exp $
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
		suite.addTestSuite(TriGReaderTest.class);
		//$JUnit-END$
		return suite;
	}
}
