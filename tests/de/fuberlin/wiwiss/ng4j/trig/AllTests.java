// $Id: AllTests.java,v 1.6 2004/12/17 05:06:31 cyganiak Exp $
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
		suite.addTestSuite(TriGWriterTest.class);
		suite.addTestSuite(SpecExamplesTest.class);
		suite.addTestSuite(TriGParserTest.class);
		suite.addTestSuite(PrettyNamespacePrefixMakerTest.class);
		//$JUnit-END$
		return suite;
	}
}
