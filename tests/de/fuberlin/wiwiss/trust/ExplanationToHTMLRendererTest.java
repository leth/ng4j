package de.fuberlin.wiwiss.trust;

import java.util.Arrays;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

/**
 * @version $Id: ExplanationToHTMLRendererTest.java,v 1.1 2005/03/22 01:01:21 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationToHTMLRendererTest extends TestCase {
    private String foo = "http://example.org/foo";
    private Node fooNode = Node.createURI(this.foo);
    private Node literal = Node.createLiteral("literal");
    private Explanation expl;
    private ExplanationToHTMLRenderer renderer;
    
    public void setUp() {
        this.expl = new Explanation(
                new Triple(this.fooNode, this.fooNode, this.fooNode),
                TrustPolicy.TRUST_EVERYTHING);
        this.renderer = new ExplanationToHTMLRenderer(this.expl);
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
        assertEquals("<dl><dt>Triple:</dt><dd>"
                + fooLink + " " + fooLink + " " + fooLink + " .</dd>"
                + "<dt>Policy:</dt><dd>" + policyURILink + "</dd>"
                + "<dt>Explanation:</dt><dd>" + noExplanationHTML + "</dd></dl>",
                this.renderer.getExplanationAsHTML());
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
        this.renderer = new ExplanationToHTMLRenderer(this.expl);
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
                Arrays.asList(new Node[] {Node.createLiteral("foo < bar &")})));
        assertEquals(
                "<ul><li>foo &lt; bar &amp;</li></ul>",
                this.renderer.getExplanationPartsAsHTML());
    }
}