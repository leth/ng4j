// $Id: AllTests.java,v 1.4 2004/11/26 00:48:38 cyganiak Exp $
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
		suite.addTestSuite(TriGReaderTest.class);
		suite.addTestSuite(TriGParserTest.class);
		suite.addTestSuite(SpecExamplesTest.class);
		//$JUnit-END$
		return suite;
	}
}
