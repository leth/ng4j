// $Id: TriGWriterTest.java,v 1.1 2004/12/13 22:56:31 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.io.StringWriter;

import com.hp.hpl.jena.graph.Node;

import junit.framework.TestCase;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
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
	private final static Node foo = Node.createURI(EX + "foo");
	private final static Node bar = Node.createURI(EX + "bar");
	private final static Node baz = Node.createURI(EX + "baz");
	private NamedGraphSet set;
	
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
		assertEquals("<" + EX + "graph1> { }\n\n", s.toString());
	}
	
	public void testTwoEmptyGraphs() {
		this.set.createGraph(graph1);
		this.set.createGraph(graph2);
		TriGWriter writer = new TriGWriter();
		StringWriter s = new StringWriter();
		writer.write(this.set, s, null);
		assertEquals("<" + EX + "graph1> { }\n\n<" + EX + "graph2> { }\n\n",
				s.toString());
	}
	
	public void testOneGraphOneStatement() {
		this.set.addQuad(new Quad(graph1, foo, bar, baz));
		this.set.addQuad(new Quad(graph1, bar, foo, foo));
		TriGWriter writer = new TriGWriter();
		StringWriter s = new StringWriter();
		writer.write(this.set, s, null);
		System.out.println(s);
	}
}
