// $Id: SpecExamplesTest.java,v 1.2 2004/11/26 00:51:07 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import junit.framework.TestCase;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Test parsing the example TriG files from the spec
 *
 * TODO: Actually compare the parsed data to the expectation
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SpecExamplesTest extends TestCase {
	private final static String BASE = "http://example.com/base";
	private final static String DEFAULT = "http://example.com/default";

	public void testExample1() {
		NamedGraphSet set = new NamedGraphSetImpl();
		new TriGReader().read(set,
				this.getClass().getResourceAsStream("tests/spec_example1.trig"),
				BASE, DEFAULT);
	}

	public void testExample2() {
		NamedGraphSet set = new NamedGraphSetImpl();
		new TriGReader().read(set,
				this.getClass().getResourceAsStream("tests/spec_example2.trig"),
				BASE, DEFAULT);
	}
}
