/*
 * $Id: AllTests.java,v 1.2 2004/12/13 02:05:52 cyganiak Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test suite for TriX parsing and serializing
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.fu_berlin.wiwiss.ng.trix");
		//$JUnit-BEGIN$
		suite.addTestSuite(JenaRDFWriterTest.class);
		suite.addTestSuite(SyntacticExtensionProcessorTest.class);
		suite.addTestSuite(TriXParserTest.class);
		suite.addTestSuite(JenaRDFReaderTest.class);
		//$JUnit-END$
		return suite;
	}
}