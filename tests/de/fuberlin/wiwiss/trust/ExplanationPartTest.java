package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.trust.EXPL;
import de.fuberlin.wiwiss.trust.Explanation;
import de.fuberlin.wiwiss.trust.ExplanationPart;
import de.fuberlin.wiwiss.trust.ExplanationTemplate;
import de.fuberlin.wiwiss.trust.TrustPolicy;
import de.fuberlin.wiwiss.trust.VariableBinding;

/**
 * @version $Id: ExplanationPartTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationPartTest extends TestCase {
    private static final String ex = "http://example.org/#";
    private static final Node a = Node.createURI(ex + "a");
    private static final Node b = Node.createURI(ex + "b");
    private static final Node c = Node.createURI(ex + "c");
    private static final Triple triple = new Triple(a, b, c);
    
    public void testEmptyPart() {
        ExplanationPart part = new ExplanationPart();
        assertEquals("Part[]", part.toString());
    }
    
    public void testListConstructor() {
        List list = new ArrayList();
        list.add(Node.createLiteral("foo"));
        ExplanationPart part = new ExplanationPart(list);
        assertEquals("Part[\"foo\"]", part.toString());
    }
    
    public void testExplanation() {
        ExplanationTemplate template = new ExplanationTemplate("@@?foo@@");
        VariableBinding binding = new VariableBinding();
        binding.setValue("foo", Node.createLiteral("bar"));
        ExplanationPart part = template.instantiate(binding);
        assertEquals("Part[\"bar\"]", part.toString());
    }
    
    public void testChildren() {
        ExplanationPart part = new ExplanationPart();
        ExplanationPart child = new ExplanationPart();
        part.addPart(child);
        assertEquals("Part[] <Part[]>", part.toString());
    }
    
    public void testEqualityOfExplanation() {
        List expl1 = Arrays.asList(new Node[] {Node.createLiteral("value1")});
        List expl2 = Arrays.asList(new Node[] {Node.createLiteral("value2")});
        assertTrue(new ExplanationPart(expl1).equals(
                new ExplanationPart(expl1)));
        assertFalse(new ExplanationPart(expl1).equals(
                new ExplanationPart(expl2)));
    }
    
    public void testEqualityOfChildren() {
        List expl1 = Arrays.asList(new Node[] {Node.createLiteral("value1")});
        List expl2 = Arrays.asList(new Node[] {Node.createLiteral("value2")});
        ExplanationPart child1 = new ExplanationPart(expl1);
        ExplanationPart child2 = new ExplanationPart(expl2);
        ExplanationPart part1a = new ExplanationPart();
        part1a.addPart(child1);
        ExplanationPart part1b = new ExplanationPart();
        part1b.addPart(child1);
        ExplanationPart part2 = new ExplanationPart();
        part2.addPart(child2);
        ExplanationPart part12 = new ExplanationPart();
        part12.addPart(child1);
        part12.addPart(child2);
        ExplanationPart part21 = new ExplanationPart();
        part21.addPart(child2);
        part21.addPart(child1);
        assertTrue(part1a.equals(part1b));
        assertFalse(part1a.equals(part2));
        assertFalse(part1a.equals(part12));
        assertTrue(part12.equals(part21));
    }
    
    public void testHashCode() {
        List expl1 = Arrays.asList(new Node[] {Node.createLiteral("value1")});
        List expl2 = Arrays.asList(new Node[] {Node.createLiteral("value2")});
        ExplanationPart child1 = new ExplanationPart(expl1);
        ExplanationPart child1b = new ExplanationPart(expl1);
        ExplanationPart child2 = new ExplanationPart(expl2);
        ExplanationPart part12 = new ExplanationPart();
        part12.addPart(child1);
        part12.addPart(child2);
        ExplanationPart part21 = new ExplanationPart();
        part21.addPart(child2);
        part21.addPart(child1);

        assertEquals(new ExplanationPart().hashCode(),
                new ExplanationPart().hashCode());
        assertEquals(child1.hashCode(), child1b.hashCode());
        assertEquals(part12.hashCode(), part21.hashCode());
    }

    public void testExplanationEqualityOfChildren() {
        List expl1 = Arrays.asList(new Node[] {Node.createLiteral("value1")});
        List expl2 = Arrays.asList(new Node[] {Node.createLiteral("value2")});
        ExplanationPart child1 = new ExplanationPart(expl1);
        ExplanationPart child2 = new ExplanationPart(expl2);
        Explanation part1a = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        part1a.addPart(child1);
        Explanation part1b = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        part1b.addPart(child1);
        Explanation part2 = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        part2.addPart(child2);
        Explanation part12 = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        part12.addPart(child1);
        part12.addPart(child2);
        Explanation part21 = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        part21.addPart(child2);
        part21.addPart(child1);
        assertTrue(part1a.equals(part1b));
        assertFalse(part1a.equals(part2));
        assertFalse(part1a.equals(part12));
        assertTrue(part12.equals(part21));
    }
    
    public void testExplanationHashCode() {
        ExplanationPart child1 = new ExplanationPart();
        ExplanationPart child2 = new ExplanationPart();
        Explanation expl12 = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        expl12.addPart(child1);
        expl12.addPart(child2);
        Explanation expl21 = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        expl21.addPart(child2);
        expl21.addPart(child1);

        assertEquals(new Explanation(triple, TrustPolicy.TRUST_EVERYTHING).hashCode(),
                new Explanation(triple, TrustPolicy.TRUST_EVERYTHING).hashCode());
        assertEquals(expl12.hashCode(), expl21.hashCode());
    }
    
    public void testEmptyExplanationToRDF() {
        Explanation explanation = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        Graph graph = explanation.toRDF();
        
        Graph expected = ModelFactory.createDefaultModel().getGraph();
        Node root = Node.createAnon();
        Node statement = Node.createAnon();
        expected.add(new Triple(root, RDF.Nodes.type, EXPL.Explanation));
        expected.add(new Triple(root, EXPL.policy, Node.createURI(
                TrustPolicy.TRUST_EVERYTHING.getURI())));
        expected.add(new Triple(root, EXPL.statement, statement));
        expected.add(new Triple(statement, RDF.Nodes.type, RDF.Nodes.Statement));
        expected.add(new Triple(statement, RDF.Nodes.subject, a));
        expected.add(new Triple(statement, RDF.Nodes.predicate, b));
        expected.add(new Triple(statement, RDF.Nodes.object, c));
        assertTrue(expected.isIsomorphicWith(graph));
    }
    
    public void testExplanationToRDF() {
        Explanation explanation = new Explanation(triple, TrustPolicy.TRUST_EVERYTHING);
        explanation.addPart(new ExplanationPart(Arrays.asList(new Node[] {a, b})));
        Graph graph = explanation.toRDF();

        assertTrue(graph.contains(
                Node.createAnon(new AnonId("explanation")), EXPL.parts, Node.ANY));
        assertTrue(graph.contains(Node.ANY, RDF.Nodes.type, EXPL.ExplanationPart));
        assertTrue(graph.contains(Node.ANY, Node.createURI(RDF.getURI() + "_1"), a));
        assertTrue(graph.contains(Node.ANY, Node.createURI(RDF.getURI() + "_2"), b));
    }
}
