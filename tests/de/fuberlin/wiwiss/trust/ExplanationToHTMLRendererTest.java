package de.fuberlin.wiwiss.trust;

import java.util.Arrays;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
/**
 * @version $Id: ExplanationToHTMLRendererTest.java,v 1.2 2005/05/25 13:15:33 maresch Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationToHTMLRendererTest extends TestCase {
    private String foo = "http://example.org/foo";
    private Node fooNode = Node.createURI(this.foo);
    private Node literal = Node.createLiteral("literal");
    private TrustLayerGraph tlg;
    private Explanation expl;
    private ExplanationToHTMLRenderer renderer;
    
    public void setUp() {
        this.expl = new Explanation(
                new Triple(this.fooNode, this.fooNode, this.fooNode),
                TrustPolicy.TRUST_EVERYTHING);

        String tpl = "http://www.wiwiss.fu-berlin.de/suhl/bizer/TPL/";
        Node thisSuite = Node.createURI("http://example.com/test/");
        Node trustEverything = Node.createURI(tpl + "TrustEverything");
        Graph policy = ModelFactory.createMemModelMaker().getGraphMaker().createGraph();
        policy.add(new Triple( thisSuite, RDF.Nodes.type, Node.createURI(tpl + "TrustPolicySuite")));
        policy.add(new Triple( thisSuite, Node.createURI(tpl + "suiteName"), Node.createLiteral("Test Policies")));
        policy.add(new Triple( thisSuite, Node.createURI(tpl + "includesPolicy"), trustEverything));
        policy.add(new Triple( trustEverything, RDF.Nodes.type, Node.createURI(tpl + "TrustPolicy")));
        policy.add(new Triple( trustEverything, Node.createURI(tpl + "policyName"), Node.createLiteral("Trust everything")));
        
        this.tlg = new TrustLayerGraph(new NamedGraphSetImpl(), policy);
        this.renderer = new ExplanationToHTMLRenderer(this.expl, this.tlg);
    }
    
    public void testRenderEmptyExplanation() {
        String fooLink = "<a href=\"" + this.foo + "\">" + this.foo + "</a>";
        String policyURILink =
            "<a href=\"" + TrustPolicy.TRUST_EVERYTHING.getURI() + "\">"
            + TrustPolicy.TRUST_EVERYTHING.getURI() + "</a>";
        String noExplanationHTML =
            "<em>This policy does not generate explanations</em>";

        assertEquals(fooLink, this.renderer.getSubjectAsHTML());
        assertEquals(fooLink, this.renderer.getPredicateAsHTML());
        assertEquals(fooLink, this.renderer.getObjectAsHTML());
        assertEquals(policyURILink, this.renderer.getPolicyAsHTML());
        assertEquals(noExplanationHTML, this.renderer.getExplanationPartsAsHTML());
    }
    
    public void testUsePrefixes() {
        PrefixMapping prefixes = new PrefixMappingImpl();
        prefixes.setNsPrefix("ex", "http://example.org/");
        this.renderer.setPrefixes(prefixes);
        assertEquals("<a href=\"" + this.foo + "\">ex:foo</a>", this.renderer.getSubjectAsHTML());
    }
    
    public void testBlankNode() {
        this.expl = new Explanation(
                new Triple(Node.createAnon(new AnonId("anon")), this.fooNode, this.fooNode),
                TrustPolicy.TRUST_EVERYTHING);
        this.renderer = new ExplanationToHTMLRenderer(this.expl, this.tlg);
        assertEquals("<tt>_:anon</tt>", this.renderer.getSubjectAsHTML());
    }
    
    public void testTwoParts() {
        this.expl.addPart(new ExplanationPart());
        this.expl.addPart(new ExplanationPart());
        String emptyPart = "<em>empty ExplanationPart</em>";
        assertEquals(
                "<ul><li>" + emptyPart + "</li><li>" + emptyPart + "</li></ul>",
                this.renderer.getExplanationPartsAsHTML());
    }

    public void testPartWithLiteral() {
        this.expl.addPart(new ExplanationPart(Arrays.asList(new Node[] {this.literal})));
        assertEquals(
                "<ul><li>literal</li></ul>",
                this.renderer.getExplanationPartsAsHTML());
    }
    
    public void testPartWithLiteralAndURI() {
        this.expl.addPart(new ExplanationPart(
                Arrays.asList(new Node[] {this.literal, this.fooNode})));
        assertEquals(
                "<ul><li>literal<a href=\"" + this.foo + "\">"
                + this.foo + "</a></li></ul>",
                this.renderer.getExplanationPartsAsHTML());
    }
    
    public void testPartWithSubPart() {
        ExplanationPart subpart = new ExplanationPart(
                Arrays.asList(new Node[] {this.literal}));
        subpart.addPart(new ExplanationPart());
        this.expl.addPart(subpart);
        String emptyPart = "<em>empty ExplanationPart</em>";
        assertEquals(
                "<ul><li>literal<ul><li>" + emptyPart + "</li></ul></li></ul>",
                this.renderer.getExplanationPartsAsHTML());
    }
    
    public void testEscaping() {
        this.expl.addPart(new ExplanationPart(
                Arrays.asList(new Node[] {Node.createLiteral("foo ä Ä ö Ö ü Ü ß < bar &")})));
        assertEquals(
                "<ul><li>foo &auml; &Auml; &ouml; &Ouml; &uuml; &Uuml; &szlig; &lt; bar &amp;</li></ul>",
                this.renderer.getExplanationPartsAsHTML());
    }
}