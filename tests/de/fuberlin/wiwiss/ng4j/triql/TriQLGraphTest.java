// $Id: TriQLGraphTest.java,v 1.1 2004/11/02 02:00:24 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.vocabulary.DC_11;

/**
 * Tests for {@link TriQLQuery}
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriQLGraphTest extends TriQLTest {
	private static final Node aURI = Node.createURI("http://example.org/data#a");
	private static final Node bURI = Node.createURI("http://example.org/data#b");
	private static final Node cURI = Node.createURI("http://example.org/data#c");
	private static final Node graph1 = Node.createURI("http://example.com/graph1");
	private static final Node graph2 = Node.createURI("http://example.com/graph2");
	private static final Node graph3 = Node.createURI("http://example.com/graph3");

	/**
	 * Test if the TriQLQuery object is set up correctly
	 */
	public void testSimpleParsing() {
		setQuery("SELECT * WHERE (?a, ?b, ?c)");
		addQuad(graph1, aURI, bURI, cURI);
		executeQuery();
		TriQLQuery q = getQuery();
		assertTrue(q.getResultVars().contains("a"));
		assertTrue(q.getResultVars().contains("b"));
		assertTrue(q.getResultVars().contains("c"));
		assertEquals(3, q.getResultVars().size());
		assertTrue(q.getBoundVars().contains("a"));
		assertTrue(q.getBoundVars().contains("b"));
		assertTrue(q.getBoundVars().contains("c"));
		assertEquals(3, q.getBoundVars().size());
		assertNull(q.getSourceURL());
		assertTrue(q.getConstraints().isEmpty());
		assertEquals(1, q.getGraphPatterns().size());
		GraphPattern gp = (GraphPattern) q.getGraphPatterns().get(0);
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
		setQuery("SELECT * WHERE (?a, ?b, ?c)");
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
		setQuery("SELECT ?b WHERE (?a, ?b, <" + cURI + ">)");
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
		setQuery("SELECT ?a WHERE <" + graph1 + "> (?a, ?b, ?c)");
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
		setQuery("SELECT ?a, ?c WHERE ?a (<" + aURI + ">, ?b, ?c)");
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
		setQuery("SELECT ?g WHERE ?g (?a ?b ?c . ?a ?c ?b)");
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

	public void testLiteralInPattern() {
		setQuery("SELECT * WHERE (?x ?y 5)");
		addQuad(graph1, aURI, bURI, Node.createLiteral("5", null, XSDDatatype.XSDint));
		addQuad(graph1, aURI, bURI, Node.createLiteral("5.7", null, null));
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(1);
		expectBinding("x", aURI);
		expectBinding("y", bURI);
		assertExpectedBinding();		
	}

	public void testLiteralInPattern2() {
		setQuery("SELECT * WHERE (?x ?y \"5\")");
		addQuad(graph1, aURI, bURI, Node.createLiteral("5", null, null));
		addQuad(graph1, aURI, bURI, Node.createLiteral("5.7", null, null));
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(1);
		expectBinding("x", aURI);
		expectBinding("y", bURI);
	}

	public void testLiteralInPattern3() {
		setQuery("SELECT * WHERE (?x ?y \"5\"^^xsd:integer)");
		addQuad(graph1, aURI, bURI, Node.createLiteral("5", null, XSDDatatype.XSDint));
		addQuad(graph1, aURI, bURI, Node.createLiteral("5.7", null, null));
		executeQuery();
//		dumpResults();
		assertExpectedBindingCount(1);
		expectBinding("x", aURI);
		expectBinding("y", bURI);
	}

	public void testRDQLDate() {
		Model m = ModelFactory.createDefaultModel();
		m.createResource("http://example.org/r").addProperty(DC_11.date, m.createTypedLiteral("2004-01-01", XSDDatatype.XSDdate));
		QueryResults qr = Query.exec("SELECT ?x WHERE (?x ?y ?z) AND ?z < \"2004-07-15\"^^xsd:date", m);
		assertTrue(qr.hasNext());
	}
}
