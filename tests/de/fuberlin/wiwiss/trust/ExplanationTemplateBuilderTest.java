package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * @version $Id: ExplanationTemplateBuilderTest.java,v 1.2 2005/03/15 08:57:14 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationTemplateBuilderTest extends TestCase {
    private List patterns;
    private GraphPattern pattern1;
    private GraphPattern pattern2;
    private GraphPattern pattern3;
    private GraphPattern pattern4;
    
    public void setUp() {
        this.patterns = new ArrayList();
        this.pattern1 = new FakePattern(new String[] {"GRAPH"});
        this.pattern2 = new FakePattern(new String[] {"GRAPH", "foo"});
        this.pattern3 = new FakePattern(new String[] {"foo", "bar"});
        this.pattern4 = new FakePattern(new String[] {"foo", "baz"});
        this.patterns.add(this.pattern1);
        this.patterns.add(this.pattern2);
        this.patterns.add(this.pattern3);
        this.patterns.add(this.pattern4);
    }
    
    public void testNoExplanations() {
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                this.patterns, null, new HashMap()).explanationTemplate();
        
        assertNull(template);
    }

    public void testOnlyRootExplanations() {
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                this.patterns, Node.createLiteral("root explanation"),
                new HashMap()).explanationTemplate();
        
        assertNotNull(template);
        assertEquals("Explanation: 'root explanation'", template.toString());
    }
    
    public void testRootAndPattern2Explanations() {
        Map patternExplanations = new HashMap();
        patternExplanations.put(this.pattern2, Node.createLiteral("pattern2 explanation"));
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                this.patterns, Node.createLiteral("root explanation"),
                patternExplanations).explanationTemplate();
        
        assertNotNull(template);
        assertEquals(
                "Explanation: 'root explanation' {Explanation: 'pattern2 explanation'}",
                template.toString());
    }
    
    public void testTwoChildExplanations() {
        Map patternExplanations = new HashMap();
        patternExplanations.put(this.pattern1, Node.createLiteral("pattern1 explanation"));
        patternExplanations.put(this.pattern2, Node.createLiteral("pattern2 explanation"));
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                this.patterns, null, patternExplanations).explanationTemplate();
        
        assertNotNull(template);
        assertEquals(
                "Explanation: {Explanation: 'pattern1 explanation', Explanation: 'pattern2 explanation'}",
                template.toString());
    }

    public void testRootAndPattern3Explanations() {
        Map patternExplanations = new HashMap();
        patternExplanations.put(this.pattern3, Node.createLiteral("pattern3 explanation"));
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                this.patterns, Node.createLiteral("root explanation"),
                patternExplanations).explanationTemplate();
        
        assertNotNull(template);
        assertEquals(
                "Explanation: 'root explanation' {Explanation: 'pattern3 explanation'}",
                template.toString());
    }

    public void testPattern2And3() {
        Map patternExplanations = new HashMap();
        patternExplanations.put(this.pattern2, Node.createLiteral("pattern2 explanation"));
        patternExplanations.put(this.pattern3, Node.createLiteral("pattern3 explanation"));
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                this.patterns, null, patternExplanations).explanationTemplate();
        
        assertNotNull(template);
        assertEquals(
                "Explanation: {Explanation: 'pattern2 explanation' {Explanation: 'pattern3 explanation'}}",
                template.toString());
    }

    public void testRootAndPattern3And4() {
        Map patternExplanations = new HashMap();
        patternExplanations.put(this.pattern3, Node.createLiteral("pattern3 explanation"));
        patternExplanations.put(this.pattern4, Node.createLiteral("pattern4 explanation"));
        ExplanationTemplate template = new ExplanationTemplateBuilder(
                this.patterns, Node.createLiteral("root explanation"),
                patternExplanations).explanationTemplate();
        
        assertNotNull(template);
        assertEquals(
                "Explanation: 'root explanation' {Explanation: 'pattern3 explanation', Explanation: 'pattern4 explanation'}",
                template.toString());
    }

    private class FakePattern extends GraphPattern {
        private Set variables = new HashSet();
        
        FakePattern(String[] variableNames) {
            super(Node.ANY);
            for (int i = 0; i < variableNames.length; i++) {
                this.variables.add(Node.createVariable(variableNames[i]));
            }
        }
        
        public Set getAllVariables() {
            return this.variables;
        }
    }
}
