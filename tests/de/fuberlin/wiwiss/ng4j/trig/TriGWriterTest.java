// $Id: TriGWriterTest.java,v 1.5 2009/01/21 17:35:55 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.io.StringWriter;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * TODO: Describe this type
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriGWriterTest extends TestCase {
	private final static String EX = "http://example.org/#";
	private final static Node graph1 = Node.createURI(EX + "graph1");
	private final static Node graph2 = Node.createURI(EX + "graph2");
	private NamedGraphSet set;
	
	public static final String NL = System.getProperty("line.separator");
	
	public void setUp() {
		this.set = new NamedGraphSetImpl();
	}

	public void testEmptySet() {
		TriGWriter writer = new TriGWriter();
		StringWriter s = new StringWriter();
		writer.write(this.set, s, null);
		assertEquals("", s.toString());
	}
	
	public void testOneEmptyGraph() {
		this.set.createGraph(graph1);
		TriGWriter writer = new TriGWriter();
		StringWriter s = new StringWriter();
		writer.write(this.set, s, null);
		String string = s.toString();
		String comparisonString = "<" + EX + "graph1> { }" + NL + NL;
		assertEquals(comparisonString, string);
	}
	
	public void testTwoEmptyGraphs() {
		this.set.createGraph(graph1);
		this.set.createGraph(graph2);
		TriGWriter writer = new TriGWriter();
		StringWriter s = new StringWriter();
		writer.write(this.set, s, null);
		String string = s.toString();
		String comparisonString = "<" + EX + "graph1> { }" + NL + NL + "<" + EX + "graph2> { }" + NL + NL;
		assertEquals(comparisonString, string);
	}

	public void testAddNamespace() {
		this.set.createGraph(graph1);
		TriGWriter writer = new TriGWriter();
		writer.addNamespace("ex", EX);
		StringWriter s = new StringWriter();
		writer.write(this.set, s, null);
		String string = s.toString();
		String comparisonString = "@prefix ex:      <" + EX + "> ." + NL + NL + "ex:graph1 { }" + NL + NL;
		assertEquals(comparisonString, string);
	}
}
