package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * Tests for {@link PolicySuiteFromRDFBuilder}
 *
 * TODO: tpl:graphExplanation
 * TODO: Warn when unknown term from the tpl namespace are used
 * 
 * @version $Id: PolicySuiteFromRDFBuilderTest.java,v 1.4 2005/03/21 21:51:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class PolicySuiteFromRDFBuilderTest extends TestCase {
    private static final String policy1URI = "http://example.com/suite#policy1";
    private static final String policy2URI = "http://example.com/suite#policy2";
    private static final Node suiteNode = Node.createURI("http://example.com/suite");
    private static final Node policy1 = Node.createURI(policy1URI);
    private static final Node policy2 = Node.createURI(policy2URI);
    private static final Node pattern1 = Node.createAnon(new AnonId("pattern1"));
    private static final Node pattern2 = Node.createAnon(new AnonId("pattern2"));

    private Graph graph;
    private PolicySuite suite;
    private int expectedWarnings;
    
    public void setUp() {
        this.graph = new GraphMem();
        this.expectedWarnings = 0; 
    }

    public void testMinimalSuite() {
        this.graph.add(new Triple(suiteNode, RDF.Nodes.type, TPL.TrustPolicySuite));
        expectWarning();	// no policies
        buildSuite();

        assertNull(this.suite.getSuiteName());
        assertTrue(this.suite.getAllPolicyURIs().isEmpty());
    }
    
    public void testNoSuite() {
        try {
            buildSuite();
            fail("Expected failure because graph doesn't contain any test suites");
        } catch (TPLException ex) {
            // expected
        }
    }
    
    public void testInferSuiteFromSuiteNameTriple() {
        this.graph.add(new Triple(suiteNode, TPL.suiteName, Node.createLiteral("Test suite")));
        expectWarning();	// no policies
        buildSuite();
        assertEquals("Test suite", this.suite.getSuiteName());
    }
    
    public void testInferSuiteFromIncludesPolicyTriple() {
        this.graph.add(new Triple(suiteNode, TPL.includesPolicy, policy1));
        buildSuite();
    }
    
    public void testInferenceDoesntModifyOriginalGraph() {
        this.graph.add(new Triple(suiteNode, TPL.suiteName, Node.createLiteral("Test suite")));
        expectWarning();	// no policies
        buildSuite();
        assertEquals(1, this.graph.size());
    }

    public void testWarnOnMultipleSuites() {
        this.graph.add(new Triple(suiteNode, RDF.Nodes.type, TPL.TrustPolicySuite));
        this.graph.add(new Triple(Node.createAnon(), RDF.Nodes.type, TPL.TrustPolicySuite));
        expectWarning();	// no policies
        expectWarning();	// multiple suites
        buildSuite();
    }

    public void testSuiteName() {
        this.graph.add(new Triple(suiteNode, TPL.suiteName, Node.createLiteral("Test suite")));
        this.graph.add(new Triple(suiteNode, RDF.Nodes.type, TPL.TrustPolicySuite));
        expectWarning();	// no policies
        buildSuite();

        assertEquals("Test suite", this.suite.getSuiteName());
    }
    
    public void testIncludesPolicy() {
        this.graph.add(new Triple(suiteNode, TPL.suiteName, Node.createLiteral("Test suite")));
        this.graph.add(new Triple(suiteNode, RDF.Nodes.type, TPL.TrustPolicySuite));
        this.graph.add(new Triple(suiteNode, TPL.includesPolicy, policy1));
        this.graph.add(new Triple(suiteNode, TPL.includesPolicy, policy2));
        buildSuite();

        assertEquals(2, this.suite.getAllPolicyURIs().size());
        assertTrue(this.suite.getAllPolicyURIs().contains(policy1.getURI()));
        assertTrue(this.suite.getAllPolicyURIs().contains(policy2.getURI()));
        
        assertTrue(this.suite.getTrustPolicy(policy1URI).getGraphPatterns().isEmpty());
    }
    
    public void testPolicyNodesMustBeURIs() {
        this.graph.add(new Triple(suiteNode, TPL.suiteName, Node.createLiteral("Test suite")));
        this.graph.add(new Triple(suiteNode, RDF.Nodes.type, TPL.TrustPolicySuite));
        this.graph.add(new Triple(suiteNode, TPL.includesPolicy, Node.createAnon()));
        try {
            buildSuite();
            fail("Expected failure because the policy node is not an URI");
        } catch (TPLException ex) {
            // expected
        }
    }

    public void testPolicyNameAndDescription() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.policyName, Node.createLiteral("Policy 1")));
        this.graph.add(new Triple(policy1, TPL.policyDescription, Node.createLiteral("Policy description")));
        buildSuite();

        assertEquals("Policy 1", this.suite.getPolicyName(policy1.getURI()));
        assertEquals("Policy description", this.suite.getPolicyDescription(policy1.getURI()));
    }

    public void testUnincludedPolicy() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy2, RDF.Nodes.type, TPL.TrustPolicy));
        expectWarning();	// policy2 is not linked with tpl:includesPolicy to suite
        buildSuite();
    }
    
    public void testGraphPatterns() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern1));
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern2));
        this.graph.add(new Triple(pattern1, TPL.pattern, Node.createLiteral("(?a ?a ?a)")));
        this.graph.add(new Triple(pattern2, TPL.pattern, Node.createLiteral("(?a ?a ?a)")));
        buildSuite();
        
        Collection patterns = this.suite.getTrustPolicy(policy1URI).getGraphPatterns();
        assertEquals(2, patterns.size());
    }
    
    public void testNamespaces() {
        this.graph.getPrefixMapping().setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        this.graph.getPrefixMapping().setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");

        addSuiteAndPolicy1();
        this.graph.add(new Triple(suiteNode, TPL.includesPolicy, policy2));
        buildSuite();
        
	    assertEquals(this.graph.getPrefixMapping(),
	            this.suite.getTrustPolicy(policy1URI).getPrefixMapping());
	    assertEquals(this.graph.getPrefixMapping(),
	            this.suite.getTrustPolicy(policy2URI).getPrefixMapping());
    }

    public void testExamplePolicy() {
        String swp = "http://www.w3.org/2004/03/trix/swp-1/";
        this.graph.getPrefixMapping().setNsPrefix("swp", swp);
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern1));
        this.graph.add(new Triple(pattern1, TPL.pattern, Node.createLiteral(
                "?graph1 (?GRAPH swp:assertedBy ?y . ?y swp:authority ?z )")));
        buildSuite();

        Collection patterns = this.suite.getTrustPolicy(policy1URI).getGraphPatterns();
        assertEquals(1, patterns.size());
        GraphPattern p = (GraphPattern) this.suite.getTrustPolicy(policy1URI)
                .getGraphPatterns().iterator().next();
        assertEquals(Node.createVariable("graph1"), p.getName());
        assertEquals(2, p.getTripleCount());

        assertEquals(Node.createVariable("GRAPH"), p.getTriple(0).getSubject());
        assertEquals(Node.createURI(swp + "assertedBy"), p.getTriple(0).getPredicate());
        assertEquals(Node.createVariable("y"), p.getTriple(0).getObject());

        assertEquals(Node.createVariable("y"), p.getTriple(1).getSubject());
        assertEquals(Node.createURI(swp + "authority"), p.getTriple(1).getPredicate());
        assertEquals(Node.createVariable("z"), p.getTriple(1).getObject());
    }

    public void testOneCondition() {
        addSuiteAndPolicy1();
        this.graph.getPrefixMapping().setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern1));
        this.graph.add(new Triple(pattern1, TPL.pattern, Node.createLiteral("?GRAPH (?GRAPH dc:date ?date)")));
        // Cheating -- we don't support real xsd:dates yet
        this.graph.add(new Triple(policy1, TPL.condition, Node.createLiteral("?date >= 2005 && ?date < 2006")));
        buildSuite();
        
        assertEquals(1, this.suite.getTrustPolicy(policy1URI).getConstraints().size());
        Constraint condition = (Constraint) this.suite.getTrustPolicy(policy1URI)
        			.getConstraints().iterator().next();
        VariableBinding binding = new VariableBinding();
        binding.setValue("date", Node.createLiteral("2005", null, XSDDatatype.XSDinteger));
        assertTrue(condition.evaluate(binding).getResult());
        binding.setValue("date", Node.createLiteral("2004", null, XSDDatatype.XSDinteger));
        assertFalse(condition.evaluate(binding).getResult());
        binding.setValue("date", Node.createLiteral("2006", null, XSDDatatype.XSDinteger));
        assertFalse(condition.evaluate(binding).getResult());
    }

    public void testTwoConditions() {
        addSuiteAndPolicy1();
        this.graph.getPrefixMapping().setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern1));
        this.graph.add(new Triple(pattern1, TPL.pattern, Node.createLiteral("?GRAPH (?GRAPH dc:date ?date)")));
        // Cheating -- we don't support real xsd:dates yet
        this.graph.add(new Triple(policy1, TPL.condition, Node.createLiteral("?date >= 2005")));
        this.graph.add(new Triple(policy1, TPL.condition, Node.createLiteral("?date < 2006")));
        buildSuite();

        assertEquals(2, this.suite.getTrustPolicy(policy1URI).getConstraints().size());
        Iterator it = this.suite.getTrustPolicy(policy1URI).getConstraints().iterator();
        Constraint condition1 = (Constraint) it.next();
        Constraint condition2 = (Constraint) it.next();
        VariableBinding binding = new VariableBinding();
        binding.setValue("date", Node.createLiteral("2005", null, XSDDatatype.XSDinteger));
        assertTrue(condition1.evaluate(binding).getResult() && condition2.evaluate(binding).getResult());
        binding.setValue("date", Node.createLiteral("2004", null, XSDDatatype.XSDinteger));
        assertFalse(condition1.evaluate(binding).getResult() && condition2.evaluate(binding).getResult());
        binding.setValue("date", Node.createLiteral("2006", null, XSDDatatype.XSDinteger));
        assertFalse(condition1.evaluate(binding).getResult() && condition2.evaluate(binding).getResult());
    }

    public void testMetric() {
        addSuiteAndPolicy1();
        this.graph.getPrefixMapping().setNsPrefix("ex", "http://example.org/metrics#");
        this.graph.add(new Triple(policy1, TPL.condition, Node.createLiteral("METRIC(ex:IsFoo, ?a)")));
        PolicySuiteFromRDFBuilder builder = new PolicySuiteFromRDFBuilder(
                this.graph, Arrays.asList(new Metric[] {new IsFooMetric()}));
        this.suite = builder.buildPolicySuite();
        
        assertEquals(1, this.suite.getTrustPolicy(policy1URI).getConstraints().size());
        Constraint condition = (Constraint) this.suite.getTrustPolicy(policy1URI)
        			.getConstraints().iterator().next();
        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("foo"));
        assertTrue(condition.evaluate(binding).getResult());
        binding.setValue("a", Node.createLiteral("bar"));
        assertFalse(condition.evaluate(binding).getResult());
    }

    public void testPolicyWithoutExplanation() {
        addSuiteAndPolicy1();
        buildSuite();
        
        TrustPolicy policy = this.suite.getTrustPolicy(policy1URI);
        assertNull(policy.getExplanationTemplate());
    }
    
    public void testPolicyWithExplanation() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.textExplanation,
                Node.createLiteral("Foo @@?GRAPH@@ @@?SUBJ@@ @@?PRED@@ @@?OBJ@@")));
        buildSuite();
        
        TrustPolicy policy = this.suite.getTrustPolicy(policy1URI);
        assertEquals("Explanation: 'Foo @@?GRAPH@@ @@?SUBJ@@ @@?PRED@@ @@?OBJ@@'",
                policy.getExplanationTemplate().toString());
    }
    
    public void testWarnOnPolicyWithMultipleExplanations() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.textExplanation,
                Node.createLiteral("Foo @@?GRAPH@@")));
        this.graph.add(new Triple(policy1, TPL.textExplanation,
                Node.createLiteral("Bar @@?GRAPH@@")));
        expectWarning();	// multiple textExplanations
        buildSuite();
    }

    public void testGraphPatternExplanation() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern1));
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern2));
        this.graph.add(new Triple(pattern1, TPL.pattern, Node.createLiteral("?GRAPH (?a ?a ?a)")));
        this.graph.add(new Triple(pattern2, TPL.pattern, Node.createLiteral("?GRAPH (?b ?b ?b)")));
        this.graph.add(new Triple(pattern1, TPL.textExplanation,
                Node.createLiteral("Foo @@?a@@")));
        buildSuite();
        
        TrustPolicy policy = this.suite.getTrustPolicy(policy1URI);
        assertEquals("Explanation: {Explanation: 'Foo @@?a@@'}",
                policy.getExplanationTemplate().toString());
    }
    
    public void testUndefinedVarInGraphPatternExplanation() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern1));
        this.graph.add(new Triple(pattern1, TPL.pattern, Node.createLiteral("?GRAPH (?a ?a ?a)")));
        this.graph.add(new Triple(pattern1, TPL.textExplanation,
                Node.createLiteral("Foo @@?undefined@@")));
        try {
            buildSuite();
            fail("Expected TPLException because ?undefined isn't defined anywhere");
        } catch (TPLException ex) {
            // is expected
        }
    }
    
    public void testUndefinedVarInRootExplanation() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.textExplanation,
                Node.createLiteral("Foo @@?undefined@@")));
        try {
            buildSuite();
            fail("Expected TPLException because ?undefined isn't defined anywhere");
        } catch (TPLException ex) {
            // is expected
        }
    }
    
    public void testWarnOnGraphPatternWithMultipleExplanations() {
        addSuiteAndPolicy1();
        this.graph.add(new Triple(policy1, TPL.graphPattern, pattern1));
        this.graph.add(new Triple(pattern1, TPL.pattern, Node.createLiteral("?GRAPH (?a ?a ?a)")));
        this.graph.add(new Triple(pattern1, TPL.textExplanation,
                Node.createLiteral("Foo @@?a@@")));
        this.graph.add(new Triple(pattern1, TPL.textExplanation,
                Node.createLiteral("Bar @@?a@@")));
        expectWarning();	// multiple textExplanations on pattern1
        buildSuite();
    }

    private void addSuiteAndPolicy1() {
        this.graph.add(new Triple(suiteNode, TPL.suiteName, Node.createLiteral("Test suite")));
        this.graph.add(new Triple(suiteNode, RDF.Nodes.type, TPL.TrustPolicySuite));
        this.graph.add(new Triple(suiteNode, TPL.includesPolicy, policy1));
    }

    private void buildSuite() {
        PolicySuiteFromRDFBuilder builder = new PolicySuiteFromRDFBuilder(
                this.graph, Collections.EMPTY_LIST);
        this.suite = builder.buildPolicySuite();
        if (this.expectedWarnings != builder.getWarnings().size()) {
            StringBuffer warnings = new StringBuffer();
            Iterator it = builder.getWarnings().iterator();
            while (it.hasNext()) {
                warnings.append("\n    ");
                warnings.append(it.next());
            }
            fail("Expected and actual warnings don't match; expected "
                    + this.expectedWarnings + ", but received:" + warnings);
        }
    }
    
    private void expectWarning() {
        this.expectedWarnings++;
    }
}
