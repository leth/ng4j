// $Id: AllTests.java,v 1.5 2004/11/26 02:42:56 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit Test suite for NG4J's TriG support
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.fuberlin.wiwiss.ng4j.trig");
		//$JUnit-BEGIN$
		suite.addTestSuite(TriGReaderTest.class);
		suite.addTestSuite(TriGParserTest.class);
		suite.addTestSuite(SpecExamplesTest.class);
		//$JUnit-END$
		return suite;
	}
}
