// $Id: TriGParserTest.java,v 1.2 2004/11/22 02:48:52 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.io.Reader;
import java.io.StringReader;

import com.hp.hpl.jena.graph.Node;

import junit.framework.TestCase;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Tests for TriG parsing
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriGParserTest extends TestCase {
	private static final String EX = "http://example.com/ex#";
	private static final String BASE = "http://example.com/base";
	private static final Node baseNode = Node.createURI(BASE);
	private static final Node aNode = Node.createURI(EX + "a");
	private static final Node bNode = Node.createURI(EX + "b");
	private static final Node cNode = Node.createURI(EX + "c");
	private static final Node graph1Node = Node.createURI(EX + "graph1");
	private static final Node graph2Node = Node.createURI(EX + "graph2");
	private static final Node graph3Node = Node.createURI(EX + "graph3");

	private NamedGraphSet parseTriG(String triG) throws Exception {
		Reader r = new StringReader(triG);
		NamedGraphSetImpl ngs = new NamedGraphSetImpl();
		NamedGraphSetPopulator h = 
				new NamedGraphSetPopulator(ngs, BASE);
		TriGParser p = new TriGParser(r, h);
		p.parse();
		return ngs;
	}

	public void testSimpleN3() throws Exception {
		String n3 = "@prefix ex: <http://example.com/ex#> .\n" +
				"ex:a ex:b ex:c .";
		NamedGraphSet ngs = parseTriG(n3);
		assertTrue(ngs.containsQuad(new Quad(baseNode, aNode, bNode, cNode)));
		assertEquals(1, ngs.countQuads());
	}
	
	public void testUnlabelledGraph() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
				"{ ex:a ex:b ex:c }";
		NamedGraphSet ngs = parseTriG(triG);
		assertTrue(ngs.containsQuad(new Quad(baseNode, aNode, bNode, cNode)));
		assertEquals(1, ngs.countQuads());
	}
	
	public void testQuadFormula() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
				"ex:graph1 :- { ex:a ex:b ex:c } .";
		NamedGraphSet ngs = parseTriG(triG);
		assertTrue(ngs.containsQuad(new Quad(graph1Node, aNode, bNode, cNode)));
		assertEquals(1, ngs.countQuads());
	}
	
	public void testIllegalNamingOperator() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
		"ex:graph ex:foo { ex:a ex:b ex:c } .";
		try {
			parseTriG(triG);
			fail();
		} catch (TriGException ex) {
			// is expected since ex:foo is not the graph naming operator :-
			// TODO: Better error message
		}
	}
	
	public void testIllegalGraphName() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
		"foo :- { ex:a ex:b ex:c } .";
		try {
			parseTriG(triG);
			fail();
		} catch (TriGException ex) {
			// is expected since "foo" is not a legal graph name
		}
	}
	
	public void testNoNestedGraphs() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
		"ex:graph :- { ex:graph :- { ex:a ex:b ex:c } } .";
		try {
			parseTriG(triG);
			fail();
		} catch (TriGException ex) {
			// is expected because nesting graphs is not allowed
		}		
	}
	
	public void testMultipleGraphs() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
				"ex:graph1 :- { ex:a ex:b ex:c } .\n" +
				"ex:graph2 :- { ex:b ex:c ex:a } .\n" +
				"ex:graph3 :- { ex:c ex:a ex:b } .\n";
		NamedGraphSet ngs = parseTriG(triG);
		assertTrue(ngs.containsQuad(new Quad(graph1Node, aNode, bNode, cNode)));
		assertTrue(ngs.containsQuad(new Quad(graph2Node, bNode, cNode, aNode)));
		assertTrue(ngs.containsQuad(new Quad(graph3Node, cNode, aNode, bNode)));
		assertEquals(3, ngs.countQuads());
	}	

	public void testDefaultGraph() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
				"ex:a ex:a ex:a .\n" +
				"ex:graph1 :- { ex:a ex:b ex:c } .\n" +
				"ex:b ex:b ex:b .\n" +
				"ex:graph2 :- { ex:b ex:c ex:a } .\n" +
				"ex:c ex:c ex:c .\n";
		NamedGraphSet ngs = parseTriG(triG);
		assertTrue(ngs.containsQuad(new Quad(graph1Node, aNode, bNode, cNode)));
		assertTrue(ngs.containsQuad(new Quad(graph2Node, bNode, cNode, aNode)));
		assertTrue(ngs.containsQuad(new Quad(baseNode, aNode, aNode, aNode)));
		assertTrue(ngs.containsQuad(new Quad(baseNode, bNode, bNode, bNode)));
		assertTrue(ngs.containsQuad(new Quad(baseNode, cNode, cNode, cNode)));
		assertEquals(5, ngs.countQuads());
	}
	
	public void testWithoutGraphNamingOperator() throws Exception {
		String triG = "@prefix ex: <http://example.com/ex#> .\n" +
				"ex:graph1 { ex:a ex:b ex:c } .";
		NamedGraphSet ngs = parseTriG(triG);
		assertTrue(ngs.containsQuad(new Quad(graph1Node, aNode, bNode, cNode)));
		assertEquals(1, ngs.countQuads());
	}
}