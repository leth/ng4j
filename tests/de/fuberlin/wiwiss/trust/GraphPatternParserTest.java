package de.fuberlin.wiwiss.trust;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;
import de.fuberlin.wiwiss.trust.GraphPatternParser;
import de.fuberlin.wiwiss.trust.TPLException;

/**
 * Tests for {@link GraphPatternParser}
 *
 * @version $Id: GraphPatternParserTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPatternParserTest extends TestCase {

    public void testMinimalPattern() {
        GraphPattern pattern = new GraphPatternParser(
                "(?a ?b ?c)", new PrefixMappingImpl()).parse();
        
        assertEquals(Node.ANY, pattern.getName());
        assertEquals(1, pattern.getTripleCount());
        assertEquals(new Triple(Node.createVariable("a"), Node.createVariable("b"),
                Node.createVariable("c")), pattern.getTriple(0));
    }
    
    public void testVariableGraphName() {
        GraphPattern pattern = new GraphPatternParser(
                "?GRAPH (?a ?b ?c)", new PrefixMappingImpl()).parse();
        assertEquals(Node.createVariable("GRAPH"), pattern.getName());
    }
    
    public void testConcreteGraphName() {
        GraphPattern pattern = new GraphPatternParser(
                "<http://example.org/graph> (?a ?b ?c)", new PrefixMappingImpl())
                .parse();
        assertEquals(Node.createURI("http://example.org/graph"), pattern.getName());
    }
    
    public void testMultiplePatterns() {
        GraphPattern pattern = new GraphPatternParser(
                "(?a ?b ?c . ?d ?e ?f)", new PrefixMappingImpl()).parse();
        
        assertEquals(Node.ANY, pattern.getName());
        assertEquals(2, pattern.getTripleCount());
        assertEquals(new Triple(Node.createVariable("a"), Node.createVariable("b"),
                Node.createVariable("c")), pattern.getTriple(0));
        assertEquals(new Triple(Node.createVariable("d"), Node.createVariable("e"),
                Node.createVariable("f")), pattern.getTriple(1));
    }
    
    public void testLiteral() {
        GraphPattern pattern = new GraphPatternParser(
                "(?a ?b \"2005-02-02\"^^xsd:date)", PrefixMapping.Standard).parse();
        assertEquals(Node.createLiteral("2005-02-02", null, XSDDatatype.XSDdate),
                pattern.getTriple(0).getObject());
    }
    
    public void testQNames() {
        String ex = "http://example.com/";
        PrefixMapping prefixes = new PrefixMappingImpl();
        prefixes.setNsPrefix("ex", ex);
        GraphPattern pattern = new GraphPatternParser(
                "ex:graph (ex:a ex:b ex:c)", prefixes).parse();

        assertEquals(Node.createURI(ex + "graph"), pattern.getName());
        assertEquals(Node.createURI(ex + "a"), pattern.getTriple(0).getSubject());
        assertEquals(Node.createURI(ex + "b"), pattern.getTriple(0).getPredicate());
        assertEquals(Node.createURI(ex + "c"), pattern.getTriple(0).getObject());
    }
    
    public void testUndefinedNamespace() {
        try {
	        new GraphPatternParser("ex:graph (ex:a ex:b ex:c)",
	                new PrefixMappingImpl()).parse();
	        fail("Expected failure because namespace ex: is undefined");
        } catch (TPLException e) {
            // expected
        }
    }
}
