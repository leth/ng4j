package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @version $Id: TrustEngineTest.java,v 1.2 2005/03/21 00:23:24 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustEngineTest extends FixtureWithLotsOfNodes {
	private NamedGraphSet source;
	private TrustEngine trustEngine;

	public void setUp() {
		this.source = new NamedGraphSetImpl();
		this.trustEngine = new TrustEngine(this.source);
	}
		
	public void testCreation() {
		assertNotNull(this.trustEngine);
	}
	
	public void testCreationWithoutSource() {
		try {
			new TrustEngine(null);
			fail();
		} catch (IllegalArgumentException e) {
			// expected because there must be a source
		}
	}

	public void testFindOnEmptySource() {
		Iterator it = this.trustEngine.find(anyTriple).tripleIterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
	}
	
	public void testFind() {
		this.source.addQuad(new Quad(graph1, richard, mbox, richardsMbox));
		Iterator it = this.trustEngine.find(anyTriple).tripleIterator();
		assertTrue(it.hasNext());
		assertEquals(new Triple(richard, mbox, richardsMbox), it.next());
		assertFalse(it.hasNext());
	}

	public void testFindFromMultipleGraphs() {
		this.source.addQuad(new Quad(graph1, richard, mbox, richardsMbox));
		this.source.addQuad(new Quad(graph2, richard, homepage, richardsHomepage));
		List l = iteratorToList(this.trustEngine.find(anyTriple).tripleIterator());
		assertEquals(2, l.size());
		assertTrue(l.contains(new Triple(richard, mbox, richardsMbox)));
		assertTrue(l.contains(new Triple(richard, homepage, richardsHomepage)));
	}

	public void testFindSameTripleFromMultipleGraphs() {
		this.source.addQuad(new Quad(graph1, richard, mbox, richardsMbox));
		this.source.addQuad(new Quad(graph2, richard, mbox, richardsMbox));
		List l = iteratorToList(this.trustEngine.find(anyTriple).tripleIterator());
		assertEquals(1, l.size());
		assertTrue(l.contains(new Triple(richard, mbox, richardsMbox)));
	}

	public void testFetchOnEmtpySource() {
		Iterator it = this.trustEngine.fetch(richard).tripleIterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
	}

	public void testFetch() {
		this.source.addQuad(new Quad(graph1, richard, mbox, richardsMbox));
		this.source.addQuad(new Quad(graph1, Node.createAnon(), mbox, richardsMbox));
		Iterator it = this.trustEngine.fetch(richard).tripleIterator();
		assertTrue(it.hasNext());
		assertEquals(new Triple(richard, mbox, richardsMbox), it.next());
		assertFalse(it.hasNext());
	}
	
	public void testFindWithPolicy() {
		this.source.addQuad(new Quad(richardsProfile, richardsProfile, RDF.Nodes.type, PPD));
		this.source.addQuad(new Quad(richardsProfile, richard, mbox, richardsMbox));
		this.source.addQuad(new Quad(graph1, richard, mbox, Node.createURI("mailto:nope@example.com")));
		// following statement is *not* in graph1, therefore graph1 does not qualify
		this.source.addQuad(new Quad(graph2, graph1, RDF.Nodes.type, PPD));
		List results = iteratorToList(this.trustEngine.find(
				new Triple(richard, mbox, Node.ANY)).tripleIterator());
		assertEquals(2, results.size());
		results = iteratorToList(this.trustEngine.find(
				new Triple(richard, mbox, Node.ANY),
				getPolicyTrustOnlyFOAFProfiles()).tripleIterator());
		assertEquals(1, results.size());
		assertTrue(results.contains(new Triple(richard, mbox, richardsMbox)));
	}

	public void testExplanation() {
		this.source.addQuad(new Quad(richardsProfile, richardsProfile, RDF.Nodes.type, PPD));
		this.source.addQuad(new Quad(richardsProfile, richard, mbox, richardsMbox));
		this.source.addQuad(new Quad(graph1, richard, mbox, Node.createURI("mailto:nope@example.com")));
		// following statement is *not* in graph1, therefore graph1 does not qualify
		this.source.addQuad(new Quad(graph2, graph1, RDF.Nodes.type, PPD));
		QueryResult results = this.trustEngine.find(
		        new Triple(richard, mbox, Node.ANY),
		        getPolicyTrustOnlyFOAFProfiles());
		Explanation explanation = results.explain(
		        new Triple(richard, mbox, richardsMbox));

		Graph graph = explanation.toRDF();
		Node root = Node.createAnon(new AnonId("explanation"));
		Node statement = Node.createAnon(new AnonId("explainedStatement"));
		assertTrue(graph.contains(root, RDF.Nodes.type, EXPL.Explanation));
		assertTrue(graph.contains(statement, RDF.Nodes.type, RDF.Nodes.Statement));
		assertTrue(graph.contains(statement, RDF.Nodes.subject, richard));
		assertTrue(graph.contains(statement, RDF.Nodes.predicate, mbox));
		assertTrue(graph.contains(statement, RDF.Nodes.object, richardsMbox));
		assertTrue(graph.contains(root, EXPL.parts, Node.ANY));
	}

	public void testFindWithMetric() {
	    this.source.addQuad(new Quad(graph1, node1, node1, Node.createLiteral("bar")));
	    this.source.addQuad(new Quad(graph1, node2, node2, Node.createLiteral("foo")));

	    TrustPolicy policy = new TrustPolicy("http://example.org/policy1");
	    policy.addConstraint(new ConstraintParser(
	            "METRIC(<http://example.org/metrics#IsFoo>, ?OBJ)",
	            new PrefixMappingImpl(),
	            Collections.singletonList(new IsFooMetric())).parse());
	    
	    List results = iteratorToList(
	            this.trustEngine.find(anyTriple,
	                    policy).tripleIterator());
	    assertEquals(1, results.size());
	    assertTrue(results.contains(new Triple(node2, node2, Node.createLiteral("foo"))));
	}

	public void testFindWithPolicyUsingSUBJ() {
	    // What Fred says
	    this.source.addQuad(new Quad(graph1, node1, node1, node1));
	    this.source.addQuad(new Quad(graph1, node2, node2, node2));
	    this.source.addQuad(new Quad(graph1, node3, node3, node3));

	    // What Bob says
	    this.source.addQuad(new Quad(graph2, node1, node1, node3));
	    this.source.addQuad(new Quad(graph2, node2, node2, node2));
	    this.source.addQuad(new Quad(graph2, node3, node3, node3));

	    // What Joe says
	    this.source.addQuad(new Quad(graph3, node1, node1, node1));

	    // Warrant for Fred's graph
	    Node person1 = Node.createAnon();
	    this.source.addQuad(new Quad(graph1, person1, mbox, Node.createURI("mailto:fred@example.com")));
	    addWarrant(graph1, person1);

	    // Warrant for Bob's graph
	    Node person2 = Node.createAnon();
	    this.source.addQuad(new Quad(graph2, person2, mbox, Node.createURI("mailto:bob@example.com")));
	    addWarrant(graph2, person2);

	    // Warrant for Joe's graph
	    Node person3 = Node.createAnon();
	    this.source.addQuad(new Quad(graph1, person1, mbox, Node.createURI("mailto:joe@example.com")));
	    addWarrant(graph3, person3);

		List results = iteratorToList(
		        this.trustEngine.find(anyTriple,
		                getPolicyTrustIfAssertedByFredAndBob()).tripleIterator());
		assertEquals(2, results.size());
		assertTrue(results.contains(new Triple(node2, node2, node2)));
		assertTrue(results.contains(new Triple(node3, node3, node3)));
	}

	public void testFindWithCondition() {
	    this.source.addQuad(new Quad(graph1, node1, node1, node1));
	    this.source.addQuad(new Quad(graph2, node2, node2, node2));
	    this.source.addQuad(new Quad(graph3, graph1, rating, Node.createLiteral("2", null, XSDDatatype.XSDinteger)));
	    this.source.addQuad(new Quad(graph3, graph2, rating, Node.createLiteral("5", null, XSDDatatype.XSDinteger)));

	    List results = iteratorToList(
	            this.trustEngine.find(anyTriple,
	                    getPolicyTrustIfRatingGreaterThanThree()).tripleIterator());
	    assertEquals(1, results.size());
	    assertTrue(results.contains(new Triple(node2, node2, node2)));
	}

// Later
//	public void testCount() {
//	    TrustPolicy policy = new TrustPolicy();
//	    policy.addCountRestriction(TrustPolicy.GRAPH.getName(),
//	            TrustPolicy.COUNT_EQUALS, 1);
//
//	    this.source.addQuad(new Quad(graph1, node1, node1, node1));
//	    this.source.addQuad(new Quad(graph1, node2, node2, node2));
//	    this.source.addQuad(new Quad(graph2, node2, node2, node2));
//	    
//	    List results = iteratorToList(
//	            this.trustEngine.find(anyTriple, policy));
//
//	    assertEquals(1, results.size());
//	    assertTrue(results.contains(new Triple(node1, node1, node1)));
//	}

	private void addWarrant(Node graphName, Node authorityNode) {
	    Node warrantGraphName = Node.createURI("http://example.com/warrant#" + new Random(System.currentTimeMillis()).nextLong());
	    this.source.addQuad(new Quad(warrantGraphName, graphName, assertedBy, warrantGraphName));
	    this.source.addQuad(new Quad(warrantGraphName, warrantGraphName, assertedBy, warrantGraphName));
	    this.source.addQuad(new Quad(warrantGraphName, warrantGraphName, authority, authorityNode));
	}

	private List iteratorToList(Iterator it) {
		List results = new ArrayList();
		while (it.hasNext()) {
			results.add(it.next());
		}
		return results;
	}
}