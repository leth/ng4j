// $Id: TriQLTest.java,v 1.1 2004/10/26 07:17:40 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Tests for {@link TriQLQuery}
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriQLTest extends TestCase {
	private static final Node aURI = Node.createURI("http://example.org/data#a");
	private static final Node bURI = Node.createURI("http://example.org/data#b");
	private static final Node cURI = Node.createURI("http://example.org/data#c");
	private static final Node graph1 = Node.createURI("http://example.com/graph1");
	private static final Node graph2 = Node.createURI("http://example.com/graph2");
	private static final Node graph3 = Node.createURI("http://example.com/graph3");
	private String query;
	private TriQLQuery q;
	private NamedGraphSet ngs;
	private List results;
	private Map expectedBinding;
	
	public void setUp() {
		this.ngs = new NamedGraphSetImpl();
		this.expectedBinding = new HashMap();
	}

	/**
	 * Test if the TriQLQuery object is set up correctly
	 */
	public void testSimpleParsing() {
		this.query = "SELECT * WHERE (?a, ?b, ?c)";
		addQuad(graph1, aURI, bURI, cURI);
		executeQuery();
		assertTrue(this.q.getResultVars().contains("a"));
		assertTrue(this.q.getResultVars().contains("b"));
		assertTrue(this.q.getResultVars().contains("c"));
		assertEquals(3, this.q.getResultVars().size());
		assertTrue(this.q.getBoundVars().contains("a"));
		assertTrue(this.q.getBoundVars().contains("b"));
		assertTrue(this.q.getBoundVars().contains("c"));
		assertEquals(3, this.q.getBoundVars().size());
		assertNull(this.q.getSourceURL());
		assertTrue(this.q.getConstraints().isEmpty());
		assertEquals(1, this.q.getGraphPatterns().size());
		GraphPattern gp = (GraphPattern) this.q.getGraphPatterns().get(0);
		assertEquals(Node.ANY, gp.getName());
		assertEquals(1, gp.getTripleCount());
		assertEquals(new Triple(Node.createVariable("a"),
				Node.createVariable("b"),
				Node.createVariable("c")), gp.getTriple(0));
	}
	
	/**
	 * Check for correct results with a minimal query
	 */
	public void testSimpleResult() {
		this.query = "SELECT * WHERE (?a, ?b, ?c)";
		addQuad(graph1, aURI, bURI, cURI);
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(1);
		expectBinding("a", aURI);
		expectBinding("b", bURI);
		expectBinding("c", cURI);
		assertExpectedBinding();
	}

	/**
	 * ?a is not bound and must not be returned
	 */
	public void testUnboundVariable() {
		this.query = "SELECT ?b WHERE (?a, ?b, <" + cURI + ">)";
		addQuad(graph1, aURI, bURI, cURI);
		addQuad(graph1, aURI, bURI, aURI);
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(1);
		expectBinding("b", bURI);
		assertExpectedBinding();
	}
	
	/**
	 * Graph name specified; graph2 does not match and must not be returned
	 */
	public void testGraphSelection() {
		this.query = "SELECT ?a WHERE <" + graph1 + "> (?a, ?b, ?c)";
		addQuad(graph1, aURI, aURI, aURI);
		addQuad(graph2, bURI, bURI, bURI);
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(1);
		expectBinding("a", aURI);
		assertExpectedBinding();
	}

	/**
	 * Check if graph names are returned as results
	 */
	public void testFetchGraphName() {
		this.query = "SELECT ?a, ?c WHERE ?a (<" + aURI + ">, ?b, ?c)";
		addQuad(graph1, aURI, aURI, aURI);
		addQuad(graph2, bURI, bURI, bURI);
		addQuad(graph3, aURI, bURI, cURI);
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(2);
		expectBinding("a", graph1);
		expectBinding("c", aURI);
		assertExpectedBinding();
		expectBinding("a", graph3);
		expectBinding("c", cURI);
		assertExpectedBinding();
	}

	/**
	 * Only graph3 contains both required patterns
	 */
	public void testGraphWithTwoTriples() {
		this.query = "SELECT ?g WHERE ?g (?a ?b ?c . ?a ?c ?b)";
		addQuad(graph1, aURI, bURI, cURI);
		addQuad(graph2, aURI, cURI, bURI);
		addQuad(graph3, aURI, bURI, cURI);
		addQuad(graph3, aURI, cURI, bURI);
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(2);
		expectBinding("g", graph3);
		assertExpectedBinding();
		expectBinding("g", graph3);
		assertExpectedBinding();
	}

	private void executeQuery() {
		this.q = new TriQLQuery(this.ngs, this.query);
		this.q.setPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		// ... could add more namespaces here
		this.results = this.q.getResultsAsList();
	}
	
	private void addQuad(Node graphName, Node s, Node p, Node o) {
		this.ngs.addQuad(new Quad(graphName, s, p, o));
	}
	
	private void assertExpectedBindingCount(int count) {
		assertEquals(count, this.results.size());
	}
	
	private void expectBinding(String variableName, Node value) {
		this.expectedBinding.put(variableName, value);
	}
	
	private void assertExpectedBinding() {
		assertTrue(this.results.contains(this.expectedBinding));
		this.expectedBinding.clear();
	}
	
	private void dumpResults() {
		System.out.println("result bindings: " + this.results.size());
		int i = 0;
		Iterator it = this.results.iterator();
		while (it.hasNext()) {
			Map binding = (Map) it.next();
			System.out.println(i + ": " + binding);
			i++;
		}
	}
}
