package de.fuberlin.wiwiss.trust;

import java.util.Collections;
import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;
import de.fuberlin.wiwiss.trust.Condition;
import de.fuberlin.wiwiss.trust.TrustPolicy;
import de.fuberlin.wiwiss.trust.VariableBinding;

/**
 * @version $Id: TrustPolicyTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustPolicyTest extends FixtureWithLotsOfNodes {

	public void testCreation() {
		TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
		assertTrue(policy.getGraphPatterns().isEmpty());
	}
	
	public void testCreationWithPattern() {
		GraphPattern pattern = new GraphPattern(TrustPolicy.GRAPH);
		pattern.addTriplePattern(new Triple(
				TrustPolicy.GRAPH, RDF.Nodes.type, PPD));
		TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
		policy.addPattern(pattern);
		assertEquals(1, policy.getGraphPatterns().size());
		assertEquals(pattern, policy.getGraphPatterns().iterator().next());
	}
	
	public void testTrustEverything() {
		assertNotNull(TrustPolicy.TRUST_EVERYTHING);
		assertTrue(TrustPolicy.TRUST_EVERYTHING.getGraphPatterns().isEmpty());
	}
	
	public void testMultiplePatterns() {
		TrustPolicy policy = getPolicyTrustOnlyPeopleIKnow();
		assertEquals(3, policy.getGraphPatterns().size());
	}
	
	public void testNamespaces() {
	    PrefixMapping prefixes = new PrefixMappingImpl();
	    prefixes.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	    prefixes.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
	    TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
	    policy.setPrefixMapping(prefixes);

	    assertEquals(prefixes, policy.getPrefixMapping());
	}
	
	public void testConditions() {
	    TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
	    policy.addCondition(ConditionFixture.getCondition("true"));
	    assertEquals(1, policy.getConditions().size());
	    assertTrue(((Condition) policy.getConditions().iterator().next()).isSatisfiedBy(new HashMap()));
	    
	    policy = new TrustPolicy("http://example.org/policies#Policy1");
	    policy.addCondition(ConditionFixture.getCondition("false"));
	    assertFalse(((Condition) policy.getConditions().iterator().next()).isSatisfiedBy(new HashMap()));
	}
	
	public void testAddMetricConstraint() {
	    Node var1 = Node.createVariable("var1");
	    Node var2 = Node.createVariable("var2");
	    TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
	    policy.addMetricConstraint(new IsFooMetric(), Collections.singletonList(var1));
	    policy.addMetricConstraint(new IsFooMetric(), Collections.singletonList(var2));

	    VariableBinding vb = new VariableBinding();
	    vb.setValue("var1", Node.createLiteral("foo"));
	    vb.setValue("var2", Node.createLiteral("foo"));
	    assertTrue(policy.matchesMetricConstraints(vb));

	    vb = new VariableBinding();
	    vb.setValue("var1", Node.createLiteral("foo"));
	    vb.setValue("var2", Node.createLiteral("bar"));
	    assertFalse(policy.matchesMetricConstraints(vb));
	}
}
