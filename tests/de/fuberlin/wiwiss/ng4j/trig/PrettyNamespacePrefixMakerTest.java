// $Id: PrettyNamespacePrefixMakerTest.java,v 1.1 2004/12/17 05:06:31 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.mem.GraphMem;

/**
 * Tests for {@link PrettyNamespacePrefixMaker}
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class PrettyNamespacePrefixMakerTest extends TestCase {
	private Graph graph;
	private PrettyNamespacePrefixMaker maker;
	private Map actualPrefixes;
	private Map expectedPrefixes;

	public void setUp() {
		this.graph = new GraphMem();
		this.maker = new PrettyNamespacePrefixMaker(this.graph);
		this.expectedPrefixes = new HashMap();
	}

	public void tearDown() {
		this.actualPrefixes = this.maker.getPrefixMap();
		Iterator it = this.expectedPrefixes.keySet().iterator();
		while (it.hasNext()) {
			String expectedPrefix = (String) it.next();
			assertTrue("Expected prefix: '" + expectedPrefix + "'",
					this.actualPrefixes.containsKey(expectedPrefix));
			assertEquals("Namespace URI for prefix '" + expectedPrefix + "':",
					this.expectedPrefixes.get(expectedPrefix),
					this.actualPrefixes.get(expectedPrefix));
		}
		it = this.actualPrefixes.keySet().iterator();
		while (it.hasNext()) {
			String actualPrefix = (String) it.next();
			assertTrue("Unexpected prefix: '" + actualPrefix + "'",
					this.expectedPrefixes.containsKey(actualPrefix));
		}
	}

	public void testEmpty() {
		// defaults to no prefixes expected
	}

	public void testOneAutomatic() {
		addURI("http://example.org/#x");
		expect("ns0", "http://example.org/#");
	}

	public void testOneAutomaticWithTwoURIs() {
		addURI("http://example.org/#x");
		addURI("http://example.org/#y");
		expect("ns0", "http://example.org/#");
	}

	public void testEndingWithSlash() {
		addURI("http://example.org/x");
		expect("ns0", "http://example.org/");		
	}
	
	public void testBaseURIWithoutOccurrence() {
		this.maker.setBaseURI("http://example.org/foo");
		expect("", "http://example.org/foo#");				
	}

	public void testBaseURIWithOccurrence() {
		this.maker.setBaseURI("http://example.org/foo");
		addURI("http://example.org/foo#x");
		expect("", "http://example.org/foo#");				
	}

	public void testBaseURIWithHash() {
		this.maker.setBaseURI("http://example.org/#");
		addURI("http://example.org/#graph1");
		expect("", "http://example.org/#");			
	}

	public void testDeclaredNSWithoutOccurrence() {
		this.maker.addNamespace("ex", "http://example.org/#");
		expect("ex", "http://example.org/#");
	}

	public void testDeclaredNSWithOccurrence() {
		this.maker.addNamespace("ex", "http://example.org/#");
		addURI("http://example.org/#x");
		expect("ex", "http://example.org/#");
	}

	public void testDefaultNSWithoutOccurrence() {
		this.maker.addDefaultNamespace("ex", "http://example.org/#");
	}

	public void testDefaultNSWithOccurrence() {
		this.maker.addDefaultNamespace("ex", "http://example.org/#");
		addURI("http://example.org/#x");
		expect("ex", "http://example.org/#");
	}

	public void testBaseOverridesDeclared() {
		this.maker.setBaseURI("http://example.org/foo");
		this.maker.addNamespace("ex", "http://example.org/foo#");
		addURI("http://example.org/foo#x");		
		expect("", "http://example.org/foo#");				
		expect("ex", "http://example.org/foo#");				
	}

	public void testDeclaredOverridesDefault() {
		this.maker.addNamespace("ex", "http://example.org/foo#");
		this.maker.addDefaultNamespace("def", "http://example.org/foo#");
		addURI("http://example.org/foo#x");		
		expect("ex", "http://example.org/foo#");						
	}

	public void testNoNamespace() {
		addURI("mailto:foo@example.org");
	}
	
	public void testURIEndsWithSlash() {
		addURI("http://example.org/");
		expect("ns0", "http://example.org/");
	}

	private void expect(String prefix, String uri) {
		this.expectedPrefixes.put(prefix, uri);
	}

	private void addURI(String uri) {
		this.graph.add(new Triple(Node.createURI("http://example.org/#s"),
				Node.createURI(uri),
				Node.createURI("http://example.org/#o")));
	}
}