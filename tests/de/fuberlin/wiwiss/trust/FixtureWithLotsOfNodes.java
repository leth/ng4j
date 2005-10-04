package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;
import de.fuberlin.wiwiss.trust.ExplanationTemplate;
import de.fuberlin.wiwiss.trust.TrustPolicy;

import junit.framework.TestCase;

/**
 * Provides lots of test data to several test cases which extend this class
 *
 * @version $Id: FixtureWithLotsOfNodes.java,v 1.5 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public abstract class FixtureWithLotsOfNodes extends TestCase {
    protected static final String ex = "http://example.org/";
    
	protected static final Node graph1 = Node.createURI(ex + "graph1");
	protected static final Node graph2 = Node.createURI(ex + "graph2");
	protected static final Node graph3 = Node.createURI(ex + "graph3");
	protected static final Node node1 = Node.createURI(ex + "#node1");
	protected static final Node node2 = Node.createURI(ex + "#node2");
	protected static final Node node3 = Node.createURI(ex + "#node3");
	protected static final Node authority1 = Node.createURI(ex + "#authority1");
	protected static final Node authority2 = Node.createURI(ex + "#authority2");
	protected static final Node warrant1 = Node.createURI(ex + "#warrant1");
	protected static final Node warrant2 = Node.createURI(ex + "#warrant2");

	protected static final Node assertedBy = Node.createURI("http://www.w3.org/2004/03/trix/swp-2/assertedBy");
	protected static final Node authority = Node.createURI("http://www.w3.org/2004/03/trix/swp-2/authority");
	protected static final Node mbox = Node.createURI("http://xmlns.com/foaf/0.1/mbox");
	protected static final Node homepage = Node.createURI("http://xmlns.com/foaf/0.1/homepage");
	protected static final Node knows = Node.createURI("http://xmlns.com/foaf/0.1/knows");
	protected static final Node PPD = Node.createURI("http://xmlns.com/foaf/0.1/PersonalProfileDocument");

	protected static final Node varWarrant = Node.createVariable("warrant");
	protected static final Node varAuthority = Node.createVariable("authority");
	protected static final Node varAnyAuthority = Node.createVariable("anyAuthority");
	protected static final Node varOtherWarrant = Node.createVariable("otherWarrant");
	protected static final Node varUserWarrant = Node.createVariable("userWarrant");
	protected static final Node varUserProfile = Node.createVariable("userProfile");
	protected static final Node varKnownPerson = Node.createVariable("knownPerson");

	protected static final Node richard = Node.createAnon(new AnonId("Richard"));
	protected static final Node richardsMbox = Node.createURI("mailto:richard@cyganiak.de");
	protected static final Node richardsHomepage = Node.createURI("http://richard.cyganiak.de/");
	protected static final Node richardsProfile = Node.createURI("http://richard.cyganiak.de/foaf.rdf");

	protected static final Node joe = Node.createAnon(new AnonId("Joe"));

    protected static final Node rating = Node.createURI(ex + "vocab#rating");

    protected static final Triple anyTriple = new Triple(Node.ANY, Node.ANY, Node.ANY);

    public static TrustPolicy getPolicyTrustOnlyFOAFProfiles() {
		GraphPattern pattern = new GraphPattern(TrustPolicy.GRAPH);
		pattern.addTriplePattern(new Triple(
				TrustPolicy.GRAPH, RDF.Nodes.type, PPD));
		TrustPolicy result = new TrustPolicy(ex + "policies#TrustOnlyFOAFProfiles");
		result.addPattern(pattern);
		ExplanationTemplate template = new ExplanationTemplate(
		        "The information is trusted because it is stated in ");
		template.addChild(new ExplanationTemplate("@@?GRAPH@@, which is a FOAF profile"));
		result.setExplanationTemplate(template);
		return result;
	}

	public static TrustPolicy getPolicyTrustOnlySelfAssertedInformation() {
		TrustPolicy result = new TrustPolicy(
		        ex + "policies#TrustOnlySelfAssertedInformation");
		result.addPattern(createSelfAssertedWarrant(
				TrustPolicy.GRAPH, varWarrant, varAnyAuthority));
		ExplanationTemplate template = new ExplanationTemplate(
				"The information is trusted because it is stated in ");
		template.addChild(new ExplanationTemplate("@@?GRAPH@@, which is self-asserted."));
		result.setExplanationTemplate(template);
		return result;
	}

	public static TrustPolicy getPolicyTrustOnlyPeopleIKnow() {
		TrustPolicy policy = new TrustPolicy(ex + "policies#TrustOnlyPeopleIKnow");
		policy.addPattern(createSelfAssertedWarrant(
				TrustPolicy.GRAPH, varOtherWarrant, varKnownPerson));
		GraphPattern pattern = new GraphPattern(varUserProfile);
		// TODO: USER should be on TrustPolicy
		Node USER = Node.createVariable("USER");
		pattern.addTriplePattern(new Triple(
				USER, knows, varKnownPerson));
		policy.addPattern(pattern);
		policy.addPattern(createSelfAssertedWarrant(
				varUserProfile, varUserWarrant, USER));
		return policy;
	}

	public static TrustPolicy getPolicyTrustIfAssertedByFredAndBob() {
		TrustPolicy policy = new TrustPolicy(ex + "policies#TrustIfAssertedByFredAndBob");
		Node varWarrant1 = Node.createVariable("warrant1");
		Node varWarrant2 = Node.createVariable("warrant2");
		Node varPerson1 = Node.createVariable("person1");
		Node varPerson2 = Node.createVariable("person2");
		GraphPattern pattern = new GraphPattern(graph1);
		pattern.addTriplePattern(new Triple(TrustPolicy.SUBJ, TrustPolicy.PRED, TrustPolicy.OBJ));		
		policy.addPattern(pattern);
		policy.addPattern(createSelfAssertedWarrant(
				graph1, varWarrant1, varPerson1));
		pattern = new GraphPattern(graph2);
		pattern.addTriplePattern(new Triple(TrustPolicy.SUBJ, TrustPolicy.PRED, TrustPolicy.OBJ));		
		policy.addPattern(pattern);
		policy.addPattern(createSelfAssertedWarrant(
				graph2, varWarrant2, varPerson2));
		pattern = new GraphPattern(Node.ANY);
		pattern.addTriplePattern(new Triple(varPerson1, mbox, Node.createURI("mailto:fred@example.com")));
		policy.addPattern(pattern);
		pattern = new GraphPattern(Node.ANY);
		pattern.addTriplePattern(new Triple(varPerson2, mbox, Node.createURI("mailto:bob@example.com")));
		policy.addPattern(pattern);
		return policy;		
	}

	public static TrustPolicy getPolicyTrustIfRatingGreaterThanThree() {
	    Node varRating = Node.createVariable("rating");
	    TrustPolicy policy = new TrustPolicy(ex + "policies#TrustIfRatingGreaterThanThree");
	    GraphPattern pattern = new GraphPattern(Node.ANY);
	    pattern.addTriplePattern(new Triple(TrustPolicy.GRAPH, rating, varRating));
	    policy.addPattern(pattern);
	    policy.addExpressionConstraint(ConstraintFixture.getConstraint("?rating > 3"));
	    return policy;
	}

	public static TrustPolicy getPolicyTwoSources() {
		TrustPolicy result = new TrustPolicy(
		        ex + "policies#TrustOnlyInformationAssertedByTwoSources");
		result.addPattern(createSelfAssertedWarrant(
				TrustPolicy.GRAPH, varWarrant, varAuthority));
		result.addCountConstraint(new CountConstraint(varAuthority.getName(), ">=", 2));
		ExplanationTemplate template = new ExplanationTemplate(
				"The information is trusted because it is asserted by two sources:");
		template.addChild(new ExplanationTemplate("@@?authority@@"));
		result.setExplanationTemplate(template);
		return result;
	}

	private static GraphPattern createSelfAssertedWarrant(Node assertedGraph,
			Node warrantGraph, Node authorityNode) {
		GraphPattern pattern = new GraphPattern(warrantGraph);
		pattern.addTriplePattern(new Triple(
				assertedGraph, assertedBy, warrantGraph));
		pattern.addTriplePattern(new Triple(
				warrantGraph, assertedBy, warrantGraph));
		pattern.addTriplePattern(new Triple(
				warrantGraph, authority, authorityNode));
		return pattern;
	}
}
