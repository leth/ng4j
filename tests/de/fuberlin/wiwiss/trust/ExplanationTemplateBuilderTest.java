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
import de.fuberlin.wiwiss.trust.ExplanationTemplate;
import de.fuberlin.wiwiss.trust.ExplanationTemplateBuilder;

/**
 * @version $Id: ExplanationTemplateBuilderTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationTemplateBuilderTest extends TestCase {
    private List patterns;
    private GraphPattern pattern1;
    private GraphPattern pattern2;
    private GraphPattern pattern3;
    
    public void setUp() {
        this.patterns = new ArrayList();
        this.pattern1 = new FakePattern(new String[] {"GRAPH"});
        this.pattern2 = new FakePattern(new String[] {"GRAPH", "foo"});
        this.pattern3 = new FakePattern(new String[] {"foo", "bar"});
        this.patterns.add(this.pattern1);
        this.patterns.add(this.pattern2);
        this.patterns.add(this.pattern3);
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
