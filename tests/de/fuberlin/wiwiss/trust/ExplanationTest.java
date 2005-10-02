package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.trust.Explainer;
import de.fuberlin.wiwiss.trust.Explanation;
import de.fuberlin.wiwiss.trust.ExplanationPart;
import de.fuberlin.wiwiss.trust.ExplanationTemplate;
import de.fuberlin.wiwiss.trust.ResultTable;
import de.fuberlin.wiwiss.trust.TrustPolicy;
import de.fuberlin.wiwiss.trust.VariableBinding;

/**
 * @version $Id: ExplanationTest.java,v 1.3 2005/10/02 21:59:28 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationTest extends TestCase {
    private static final String ex = "http://example.org/#";
    private static final Node graph1 = Node.createURI(ex + "graph1");
    private static final Node graph2 = Node.createURI(ex + "graph2");
    private static final Node a = Node.createURI(ex + "a");
    private static final Node b = Node.createURI(ex + "b");
    private static final Node c1 = Node.createURI(ex + "c1");
    private static final Node c2 = Node.createURI(ex + "c2");
    private static final Triple abc1 = new Triple(a, b, c1);
    private static final Triple abc2 = new Triple(a, b, c2);
    private static final Node fooValue1 = Node.createLiteral("fooValue1");
    private static final Node fooValue2 = Node.createLiteral("fooValue2");
    private static final Node fooValue3 = Node.createLiteral("fooValue3");

    private ResultTable results;
    private VariableBinding binding1;
    private VariableBinding binding2;
    private VariableBinding binding3;
    
    public void setUp() {
        this.results = new ResultTable();

        this.binding1 = new VariableBinding();
        this.binding1.setValue(TrustPolicy.SUBJ.getName(), a);
        this.binding1.setValue(TrustPolicy.PRED.getName(), b);
        this.binding1.setValue(TrustPolicy.OBJ.getName(), c1);
        this.binding1.setValue(TrustPolicy.GRAPH.getName(), graph1);
        this.binding1.setValue("foo", fooValue1);
        this.results.addBinding(this.binding1);

        this.binding2 = new VariableBinding();
        this.binding2.setValue(TrustPolicy.SUBJ.getName(), a);
        this.binding2.setValue(TrustPolicy.PRED.getName(), b);
        this.binding2.setValue(TrustPolicy.OBJ.getName(), c1);
        this.binding2.setValue(TrustPolicy.GRAPH.getName(), graph2);
        this.binding2.setValue("foo", fooValue2);
        this.results.addBinding(this.binding2);

        this.binding3 = new VariableBinding();
        this.binding3.setValue(TrustPolicy.SUBJ.getName(), a);
        this.binding3.setValue(TrustPolicy.PRED.getName(), b);
        this.binding3.setValue(TrustPolicy.OBJ.getName(), c2);
        this.binding3.setValue(TrustPolicy.GRAPH.getName(), graph1);
        this.binding3.setValue("foo", fooValue3);
        this.results.addBinding(this.binding3);
    }
    
    public void testGenerateEmptyTextExplanation() {
        Explainer explainer = new Explainer(this.results, abc1,
                TrustPolicy.TRUST_EVERYTHING);
        Explanation explanation = explainer.explain();
        assertTrue(explanation.parts().isEmpty());
    }
    
    public void testExplainTripleNotInResults() {
        Triple triple = new Triple(b, b, b);
        try {
	        Explainer explainer = new Explainer(this.results, triple,
	                TrustPolicy.TRUST_EVERYTHING);
            explainer.explain();
            fail("Expected exception because triple (b b b) is not in result table");
        } catch (IllegalArgumentException e) {
            // is expected
        }
    }
    
    public void testTemplateOnRootNoVariables() {
        TrustPolicy policy = new TrustPolicy(ex + "Policy");
        policy.setExplanationTemplate(new ExplanationTemplate("text"));
        Explanation part = new Explainer(this.results, abc1, policy).explain();

        Explanation expected = new Explanation(abc1, policy);
        expected.addPart(new ExplanationPart(
                Arrays.asList(new Node[] {Node.createLiteral("text")})));
        
        assertEquals(expected, part);
    }
    
    public void testInstantiateEmptyTemplate() {
        ExplanationTemplate template = new ExplanationTemplate();
        assertEquals(Collections.singletonList(new ExplanationPart()),
                template.instantiateTree(this.results));
    }

    public void testInstantiateTemplateFromResultTable() {
        ExplanationTemplate template = new ExplanationTemplate("@@?foo@@");
        Collection expectedParts = Arrays.asList(new ExplanationPart[] {
                createExplanationPart(fooValue1),
                createExplanationPart(fooValue2),
                createExplanationPart(fooValue3)});
        assertEquals(expectedParts, template.instantiateTree(this.results));
    }
    
    public void testInstantiateTemplateFromResultTableNoVariables() {
        ExplanationTemplate template = new ExplanationTemplate("text");
        Collection expectedParts = Arrays.asList(new ExplanationPart[] {
                createExplanationPart(Node.createLiteral("text"))});
        assertEquals(expectedParts, template.instantiateTree(this.results));
    }
    
    public void testInstantiateTemplateWithChild() {
        ExplanationTemplate template = new ExplanationTemplate("text");
        template.addChild(new ExplanationTemplate("@@?foo@@"));
        ExplanationPart expectedPart = createExplanationPart(Node.createLiteral("text"));
        expectedPart.addPart(new ExplanationPart(Collections.singletonList(fooValue1)));
        expectedPart.addPart(new ExplanationPart(Collections.singletonList(fooValue2)));
        expectedPart.addPart(new ExplanationPart(Collections.singletonList(fooValue3)));
        assertEquals(Collections.singletonList(expectedPart),
                template.instantiateTree(this.results));
    }
    
    public void testInstantiateTemplateWithChild2() {
        ExplanationTemplate template = new ExplanationTemplate("@@?foo@@");
        template.addChild(new ExplanationTemplate("@@?GRAPH@@"));
        ExplanationPart part1 = createExplanationPart(fooValue1);
        part1.addPart(new ExplanationPart(Collections.singletonList(graph1)));
        ExplanationPart part2 = createExplanationPart(fooValue2);
        part2.addPart(new ExplanationPart(Collections.singletonList(graph2)));
        ExplanationPart part3 = createExplanationPart(fooValue3);
        part3.addPart(new ExplanationPart(Collections.singletonList(graph1)));
        assertEquals(Arrays.asList(new ExplanationPart[] {part1, part2, part3}),
                template.instantiateTree(this.results));
    }
    
    public void testInstantiateTemplateWithTwoChildren() {
        ExplanationTemplate template = new ExplanationTemplate("text");
        template.addChild(new ExplanationTemplate("@@?GRAPH@@"));
        template.addChild(new ExplanationTemplate("@@?OBJ@@"));
        ExplanationPart expectedPart = createExplanationPart(Node.createLiteral("text"));
        expectedPart.addPart(new ExplanationPart(Collections.singletonList(graph1)));
        expectedPart.addPart(new ExplanationPart(Collections.singletonList(graph2)));
        expectedPart.addPart(new ExplanationPart(Collections.singletonList(c1)));
        expectedPart.addPart(new ExplanationPart(Collections.singletonList(c2)));
        assertEquals(Collections.singletonList(expectedPart),
                template.instantiateTree(this.results));
    }
    
    public void testTemplateOnRootSeveralMatches() {
        TrustPolicy policy = new TrustPolicy(ex + "Policy");
        policy.setExplanationTemplate(new ExplanationTemplate("@@?foo@@"));
        Explanation explanation = new Explainer(this.results, abc1, policy).explain();

        Explanation expected = new Explanation(abc1, policy);
        expected.addPart(new ExplanationPart(Arrays.asList(new Node[] {fooValue1})));
        expected.addPart(new ExplanationPart(Arrays.asList(new Node[] {fooValue2})));
        assertEquals(expected, explanation);
    }
    
    public void testResultTableContainsBinding() {
        assertTrue(this.results.containsBinding(this.binding1));
        assertTrue(this.results.containsBinding(this.binding2));
        assertTrue(this.results.containsBinding(this.binding3));
        assertFalse(this.results.containsBinding(new VariableBinding()));
    }
    
    public void testSelectDistinct() {
        ResultTable distinct = this.results.selectDistinct(
        		Collections.singleton("foo"));
        ResultTable expected = ResultTableBuilder.build(
        		"foo", "fooValue1|fooValue2|fooValue3");
        assertEquals(expected, distinct);
    }
    
    public void testSelectDistinct2() {
        String[] varNames =
            new String[] {TrustPolicy.SUBJ.getName(), TrustPolicy.OBJ.getName()};
        ResultTable distinct = this.results.selectDistinct(Arrays.asList(varNames));
        ResultTableBuilder expected = new ResultTableBuilder(varNames);
        expected.addNodeRow(new Node[] {a, c1});
        expected.addNodeRow(new Node[] {a, c2});
        assertEquals(expected.table(), distinct);
    }
    
    public void testSelectMatchingAll() {
        ResultTable matching = this.results.selectMatching(new VariableBinding());
        assertEquals(this.results, matching);
    }
    
    public void testSelectMatchingNone() {
        VariableBinding binding = new VariableBinding();
        binding.setValue("undef", Node.createLiteral("a"));
        assertEquals(new ResultTable(), this.results.selectMatching(binding));
    }
    
    public void testSelectMatching() {
        VariableBinding binding = new VariableBinding();
        binding.setValue(TrustPolicy.OBJ.getName(), c1);
        binding.setValue("foo", fooValue1);
        ResultTable matching = this.results.selectMatching(binding);
        ResultTable expected = new ResultTable();
        expected.addBinding(this.binding1);
        assertEquals(expected, matching);
    }

    public void testSelectMatchingWithTriple() {
        assertEquals(2, this.results.selectMatching(abc1).countBindings());
        assertEquals(1, this.results.selectMatching(abc2).countBindings());
    }
    
    public void testTripleIterator() {
        Iterator it = this.results.tripleIterator();
        assertTrue(it.hasNext());
        assertEquals(abc1, it.next());
        assertTrue(it.hasNext());
        assertEquals(abc2, it.next());
        assertFalse(it.hasNext());
    }
    
    public void testCreateFromTriQLResult() {
        Map result1 = new HashMap();
        result1.put(TrustPolicy.OBJ.getName(), c1);
        result1.put(TrustPolicy.GRAPH.getName(), graph1);
        result1.put("foo", fooValue1);
        Map result2 = new HashMap();
        result2.put(TrustPolicy.OBJ.getName(), c2);
        result2.put(TrustPolicy.GRAPH.getName(), graph1);
        result2.put("foo", fooValue3);
        Iterator resultIt = Arrays.asList(new Map[] {result1, result2}).iterator();
        Triple triple = new Triple(a, b, Node.ANY);
        ResultTable table = ResultTable.createFromTriQLResult(resultIt, triple);
        
        ResultTable expected = new ResultTable();
        expected.addBinding(this.binding1);
        expected.addBinding(this.binding3);
        assertEquals(expected, table);
    }

    public void testExplanationGetTripleAndPolicyURI() {
        Explanation expl = new Explanation(abc1, new TrustPolicy(ex + "Policy"));
        assertEquals(abc1, expl.getExplainedTriple());
        assertEquals(Node.createURI(ex + "Policy"), expl.getPolicyURI());
    }
    
    private ExplanationPart createExplanationPart(Node explanation) {
        return new ExplanationPart(Collections.singletonList(explanation));
    }
}
