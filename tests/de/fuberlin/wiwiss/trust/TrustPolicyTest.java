package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @version $Id: TrustPolicyTest.java,v 1.5 2005/06/22 21:21:23 maresch Exp $
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
	    policy.addExpressionConstraint(ConstraintFixture.getConstraint("true"));
	    assertEquals(1, policy.getExpressionConstraints().size());
	    assertTrue(((ExpressionConstraint) policy.getExpressionConstraints().iterator().next()).evaluate(new VariableBinding()).getResult());
	    
	    policy = new TrustPolicy("http://example.org/policies#Policy1");
	    policy.addExpressionConstraint(ConstraintFixture.getConstraint("false"));
	    assertFalse(((ExpressionConstraint) policy.getExpressionConstraints().iterator().next()).evaluate(new VariableBinding()).getResult());
	}
	
	public void testAddMetricConstraint() {
	    TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
	    policy.addExpressionConstraint(new ConstraintParser(
	            "METRIC(<http://example.org/metrics#IsFoo>, ?var1)",
	            new PrefixMappingImpl(),
	            Collections.singletonList(new IsFooMetric()),
                Collections.EMPTY_LIST).parseExpressionConstraint());
	    policy.addExpressionConstraint(new ConstraintParser(
	            "METRIC(<http://example.org/metrics#IsFoo>, ?var2)",
	            new PrefixMappingImpl(),
	            Collections.singletonList(new IsFooMetric()),
                Collections.EMPTY_LIST).parseExpressionConstraint());

	    VariableBinding vb = new VariableBinding();
	    vb.setValue("var1", Node.createLiteral("foo"));
	    vb.setValue("var2", Node.createLiteral("foo"));
	    assertTrue(policy.matchesConstraints(vb));

	    vb = new VariableBinding();
	    vb.setValue("var1", Node.createLiteral("foo"));
	    vb.setValue("var2", Node.createLiteral("bar"));
	    assertFalse(policy.matchesConstraints(vb));
	}
    
    public void testAddRankBasedMetricConstraint(){
        RankBasedMetric metric = new AlwaysFirstRankBasedMetric();
	    TrustPolicy policy = new TrustPolicy("http://example.org/policies#Policy1");
	    policy.addRankBasedConstraint(new ConstraintParser(
	            "METRIC(<" + AlwaysFirstRankBasedMetric.URI + ">, ?var1, 10)",
	            new PrefixMappingImpl(),
                Collections.EMPTY_LIST,
                Collections.singletonList(metric)).parseRankBasedConstraint());
        assertEquals(1, policy.getRankBasedConstraints().size());
        assertTrue(metric.equals(((RankBasedConstraint) 
            policy.getRankBasedConstraints().iterator().next()).getRankBasedMetric()));
   }
}
