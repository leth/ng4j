package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.trust.ExplanationPart;
import de.fuberlin.wiwiss.trust.ExplanationTemplate;
import de.fuberlin.wiwiss.trust.VariableBinding;

/**
 * @version $Id: ExplanationTemplateTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationTemplateTest extends TestCase {
    private static final Node FOO = Node.createLiteral("FOO");
    private static final Node BAR = Node.createLiteral("BAR");
    private static final Node URI = Node.createURI("http://example.com/");
    
    private VariableBinding binding;
    
    public void setUp() {
        this.binding = new VariableBinding();
        this.binding.setValue("foo", FOO);
        this.binding.setValue("bar", BAR);
        this.binding.setValue("uri", URI);
    }
    
    public void testOnlyText() {
        assertInstantiation("", new Node[] {});
        assertInstantiation("foo", new Node[] {Node.createLiteral("foo")});
        assertInstantiation("?foo", new Node[] {Node.createLiteral("?foo")});
        assertInstantiation("?foo@", new Node[] {Node.createLiteral("?foo@")});
        assertInstantiation("?foo@@", new Node[] {Node.createLiteral("?foo@@")});
        assertInstantiation("@?foo", new Node[] {Node.createLiteral("@?foo")});
        assertInstantiation("@@?foo", new Node[] {Node.createLiteral("@@?foo")});
        assertInstantiation("@@foo@@", new Node[] {Node.createLiteral("@@foo@@")});
        assertInstantiation("@@?@@", new Node[] {Node.createLiteral("@@?@@")});
    }
    
    public void testOneVariable() {
        assertInstantiation("@@?foo@@", new Node[] {FOO});
        assertInstantiation("@@?foo@@bar", new Node[] {FOO, Node.createLiteral("bar")});
        assertInstantiation("bar@@?foo@@", new Node[] {Node.createLiteral("bar"), FOO});
        assertInstantiation("bar@@?foo@@bar",
                new Node[] {Node.createLiteral("bar"), FOO, Node.createLiteral("bar")});
        assertInstantiation("@@?foo@@?bar@@",
                new Node[] {FOO, Node.createLiteral("?bar@@")});
        assertInstantiation("@@?foo@@@?bar@@",
                new Node[] {FOO, Node.createLiteral("@?bar@@")});
    }

    public void testTwoVariables() {
        assertInstantiation("@@?foo@@@@?bar@@", new Node[] {FOO, BAR});
        assertInstantiation("@@?foo@@aa@@?bar@@",
                new Node[] {FOO, Node.createLiteral("aa"), BAR});
    }
    
    public void testThreeVariables() {
        assertInstantiation("0@@?foo@@1@@?bar@@2@@?uri@@3", new Node[] {
                Node.createLiteral("0"), FOO,
                Node.createLiteral("1"), BAR,
                Node.createLiteral("2"), URI,
                Node.createLiteral("3")});
    }
    
    public void testUndefinedVariable() {
        ExplanationTemplate template = new ExplanationTemplate("@@?undef@@");
        try {
            template.instantiate(this.binding);
            fail("Expected failure because ?undef is not defined");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().indexOf("?undef") >= 0);
        }
    }
    
    public void testLang() {
        ExplanationTemplate template = new ExplanationTemplate(
                "@@?foo@@ gefingerpoken blinkenlichten", "de");
        assertEquals(createExplanationPart(new Node[] {
                FOO, Node.createLiteral(
                        " gefingerpoken blinkenlichten", "de", (RDFDatatype) null)}),
                template.instantiate(this.binding));
    }

    public void testMatchTwice() {
        ExplanationTemplate template = new ExplanationTemplate("abc@@?foo@@");
        assertEquals(
                createExplanationPart(new Node[] {Node.createLiteral("abc"), FOO}),
                template.instantiate(this.binding));
        assertEquals(
                createExplanationPart(new Node[] {Node.createLiteral("abc"), FOO}),
                template.instantiate(this.binding));
    }
    
    public void testUsedVariables() {
        Node var1 = Node.createVariable("var1");
        Node var2 = Node.createVariable("var2");
        assertEquals(Collections.EMPTY_SET,
                new ExplanationTemplate("foo").usedVariables());
        assertEquals(new HashSet(Arrays.asList(new Node[] {var1})),
                new ExplanationTemplate("@@?var1@@").usedVariables());
        assertEquals(new HashSet(Arrays.asList(new Node[] {var1, var2})),
                new ExplanationTemplate("@@?var1@@@@?var2@@").usedVariables());
        
        ExplanationTemplate template = new ExplanationTemplate();
        template.addChild(new ExplanationTemplate("@@?var1@@"));
        assertEquals(new HashSet(Arrays.asList(new Node[] {var1})),
                template.usedVariables());
    }
    
    private ExplanationPart createExplanationPart(Node[] explanation) {
        return new ExplanationPart(Arrays.asList(explanation));
    }
    
    private void assertInstantiation(String pattern, Node[] instantiation) {
        ExplanationTemplate template = new ExplanationTemplate(pattern);
        assertEquals(
                createExplanationPart(instantiation),
                template.instantiate(this.binding));
    }
}
