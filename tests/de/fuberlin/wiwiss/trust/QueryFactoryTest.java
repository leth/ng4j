package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;

/**
 * @version $Id: QueryFactoryTest.java,v 1.3 2005/03/22 01:01:21 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QueryFactoryTest extends FixtureWithLotsOfNodes {

	public void testTrustEverything() {
		NamedGraphSet source = new NamedGraphSetImpl();
		Triple findMe = new Triple(node1, node2, node3);
		QueryFactory factory = new QueryFactory(
		        source, findMe, TrustPolicy.TRUST_EVERYTHING, new VariableBinding());
		TriQLQuery query = factory.buildQuery();
		assertTrue(query.getConstraints().isEmpty());
		assertEquals(source, query.getSource());
		assertEquals(1, query.getGraphPatterns().size());
		GraphPattern pattern = (GraphPattern) query.getGraphPatterns().get(0);
		assertEquals(TrustPolicy.GRAPH, pattern.getName());
		assertEquals(1, pattern.getTripleCount());
		assertEquals(
		        new Triple(TrustPolicy.SUBJ, TrustPolicy.PRED, TrustPolicy.OBJ),
		        pattern.getTriple(0));		
	}

	public void testTrustEverythingFindAnyTriple() {
		NamedGraphSet source = new NamedGraphSetImpl();
		QueryFactory factory = new QueryFactory(
		        source, anyTriple, TrustPolicy.TRUST_EVERYTHING, new VariableBinding());
		TriQLQuery query = factory.buildQuery();
		assertEquals(4, query.getResultVars().size());
		assertTrue(query.getResultVars().contains("GRAPH"));
		assertTrue(query.getResultVars().contains("SUBJ"));
		assertTrue(query.getResultVars().contains("PRED"));
		assertTrue(query.getResultVars().contains("OBJ"));
		assertTrue(query.getPreboundVariableValues().isEmpty());
	}

	public void testTrustEverythingFindWithWildcards() {
		NamedGraphSet source = new NamedGraphSetImpl();
		QueryFactory factory = new QueryFactory(source, new Triple(
				Node.ANY, knows, Node.ANY), TrustPolicy.TRUST_EVERYTHING, new VariableBinding());
		TriQLQuery query = factory.buildQuery();
		assertEquals(3, query.getResultVars().size());
		assertTrue(query.getResultVars().contains("GRAPH"));
		assertTrue(query.getResultVars().contains("SUBJ"));
		assertTrue(query.getResultVars().contains("OBJ"));
		assertEquals(1, query.getPreboundVariableValues().keySet().size());
		assertEquals(knows, query.getPreboundVariableValues().get("PRED"));
	}

	public void testTrustEverythingFindWithoutWildcards() {
		NamedGraphSet source = new NamedGraphSetImpl();
		QueryFactory factory = new QueryFactory(source, new Triple(
				richard, knows, joe), TrustPolicy.TRUST_EVERYTHING, new VariableBinding());
		TriQLQuery query = factory.buildQuery();
		assertEquals(1, query.getResultVars().size());
		assertTrue(query.getResultVars().contains("GRAPH"));
		assertEquals(3, query.getPreboundVariableValues().keySet().size());
		assertEquals(richard, query.getPreboundVariableValues().get("SUBJ"));
		assertEquals(knows, query.getPreboundVariableValues().get("PRED"));
		assertEquals(joe, query.getPreboundVariableValues().get("OBJ"));
	}

	public void testPolicyWithOnePattern() {
		Triple findMe = new Triple(node1, node2, node3);
		TriQLQuery query = buildQuery(findMe, getPolicyTrustOnlyFOAFProfiles());
		assertEquals(2, query.getGraphPatterns().size());
		GraphPattern pattern = (GraphPattern) query.getGraphPatterns().get(1);
		assertEquals(TrustPolicy.GRAPH, pattern.getName());
		assertEquals(1, pattern.getTripleCount());
		assertEquals(new Triple(TrustPolicy.GRAPH, RDF.Nodes.type, PPD),
				pattern.getTriple(0));		
	}

	public void testPolicyWithMultipleTriplePatterns() {
		Triple findMe = new Triple(node1, node2, node3);
		TriQLQuery query = buildQuery(findMe, getPolicyTrustOnlySelfAssertedInformation());
		assertEquals(2, query.getGraphPatterns().size());
		GraphPattern pattern = (GraphPattern) query.getGraphPatterns().get(1);
		assertEquals(varWarrant, pattern.getName());
		assertEquals(3, pattern.getTripleCount());
		assertEquals(new Triple(TrustPolicy.GRAPH, assertedBy, varWarrant),
				pattern.getTriple(0));
		assertEquals(new Triple(varWarrant, assertedBy, varWarrant),
				pattern.getTriple(1));
		assertEquals(new Triple(varWarrant, authority, varAnyAuthority),
				pattern.getTriple(2));
	}

	public void testPolicyWithMultipleGraphPatterns() {
		Triple findMe = new Triple(node1, node2, node3);
		TriQLQuery query = buildQuery(findMe, getPolicyTrustOnlyPeopleIKnow());
		assertEquals(4, query.getGraphPatterns().size());
		assertEquals(varOtherWarrant, ((GraphPattern) query.getGraphPatterns().get(1)).getName());
		assertEquals(varUserProfile, ((GraphPattern) query.getGraphPatterns().get(2)).getName());
		assertEquals(varUserWarrant, ((GraphPattern) query.getGraphPatterns().get(3)).getName());
		// we trust the triple patterns to be OK ...
	}

	public void testPolicyWithNamespaces() {
	    PrefixMapping prefixes = new PrefixMappingImpl();
	    prefixes.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	    prefixes.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
	    TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
	    	policy.setPrefixMapping(prefixes);
	    TriQLQuery query = buildQuery(anyTriple, policy);
	    assertEquals("http://xmlns.com/foaf/0.1/",
	            query.getPrefixMapping().getNsPrefixURI("foaf"));
	    assertEquals("http://purl.org/dc/elements/1.1/",
	            query.getPrefixMapping().getNsPrefixURI("dc"));
	    assertNull(query.getPrefixMapping().getNsPrefixURI("foo"));
	}
	
	public void testSystemVariables() {
		NamedGraphSet source = new NamedGraphSetImpl();
		VariableBinding sysVars = new VariableBinding();
		Node richardNode = Node.createURI("http://example.org/#Richard");
		sysVars.setValue("USER", richardNode);
		QueryFactory factory = new QueryFactory(source, new Triple(
				richard, knows, joe), TrustPolicy.TRUST_EVERYTHING, sysVars);
		TriQLQuery query = factory.buildQuery();
		assertEquals(richardNode, query.getPreboundVariableValues().get("USER"));
	}

	private TriQLQuery buildQuery(Triple findMe, TrustPolicy policy) {
		NamedGraphSet source = new NamedGraphSetImpl();
		QueryFactory factory = new QueryFactory(source, findMe, policy, new VariableBinding());
		return factory.buildQuery();
	}
}
