// $Id: NamedGraphSetTest.java,v 1.1 2004/09/13 14:37:30 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.mem.GraphMem;

import de.fuberlin.wiwiss.namedgraphs.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.namedgraphs.impl.NamedGraphSetImpl;

/**
 * Tests for {@link NamedGraphSet}.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphSetTest extends TestCase {
	private final static String uri1 = "http://example.org/graph1";
	private final static String uri2 = "http://example.org/graph2";
	private final static Node node1 = Node.createURI("http://example.org/graph1");
	private final static Node node2 = Node.createURI("http://example.org/graph2");
	private final static Node foo = Node.createURI("http://example.org/#foo");
	private final static Node bar = Node.createURI("http://example.org/#bar");
	private final static Node baz = Node.createURI("http://example.org/#baz");

	private NamedGraphSet set;

	public void setUp() {
		this.set = new NamedGraphSetImpl();
	}

	public void testFixture() {
		assertFalse(uri1.equals(uri2));
		assertFalse(node1.equals(node2));
		assertFalse(foo.equals(bar));
		assertFalse(foo.equals(baz));
	}

	public void testCreation() {
		assertTrue(this.set.isEmpty());
		assertEquals(0, this.set.countGraphs());
		assertEquals(0, this.set.countQuads());
	}
	
	public void testAddGraph() {
		NamedGraph graph = createGraph(uri1);
		graph.add(new Triple(foo, bar, baz));
		this.set.addGraph(graph);
		assertFalse(this.set.isEmpty());
		assertEquals(1, this.set.countGraphs());
		assertEquals(1, this.set.countQuads());
		assertTrue(this.set.containsGraph(uri1));
		assertTrue(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
	}

	public void testAddGraphReplaceExisting() {
		NamedGraph graph1 = createGraph(uri1);
		graph1.add(new Triple(foo, bar, baz));
		NamedGraph graph2 = createGraph(uri1);
		graph2.add(new Triple(baz, foo, bar));
		assertNotSame(graph1, graph2);
		this.set.addGraph(graph1);
		assertSame(graph1, this.set.getGraph(uri1));
		assertTrue(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
		assertFalse(this.set.containsQuad(new Quad(node1, baz, foo, bar)));
		this.set.addGraph(graph2);
		assertSame(graph2, this.set.getGraph(uri1));
		assertFalse(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
		assertTrue(this.set.containsQuad(new Quad(node1, baz, foo, bar)));
	}

	public void testRemoveGraph() {
		NamedGraph graph = createGraph(uri1);
		graph.add(new Triple(foo, bar, baz));
		this.set.addGraph(graph);
		this.set.removeGraph(uri1);
		assertTrue(this.set.isEmpty());
		assertEquals(0, this.set.countGraphs());
		assertFalse(this.set.containsGraph(uri1));
		assertFalse(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
	}
	
	public void testRemoveGraphNonexisting() {
		// should have no effect and should not cause an exception
		this.set.removeGraph(uri1);
	}
	
	public void testRemoveGraphAny() {
		this.set.addGraph(createGraph(uri1));
		this.set.addGraph(createGraph(uri2));
		assertTrue(this.set.containsGraph(uri1));
		assertTrue(this.set.containsGraph(uri2));
		this.set.removeGraph(Node.ANY);
		assertFalse(this.set.containsGraph(uri1));
		assertFalse(this.set.containsGraph(uri2));
	}
	
	public void testContainsGraph() {
		assertFalse(this.set.containsGraph(uri1));
		assertFalse(this.set.containsGraph(Node.ANY));
		this.set.addGraph(createGraph(uri1));
		assertTrue(this.set.containsGraph(uri1));
		assertTrue(this.set.containsGraph(Node.ANY));
		this.set.removeGraph(uri1);
		assertFalse(this.set.containsGraph(uri1));
		assertFalse(this.set.containsGraph(Node.ANY));
	}

	public void testGetGraph() {
		assertNull(this.set.getGraph(uri1));
		NamedGraph graph = createGraph(uri1);
		this.set.addGraph(graph);
		assertSame(graph, this.set.getGraph(uri1));
		this.set.getGraph(uri1).add(new Triple(foo, bar, baz));
		assertTrue(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
	}

	public void testGetGraphConnectedToSet() {
		this.set.addGraph(createGraph(uri1));
		this.set.getGraph(uri1).add(new Triple(foo, bar, baz));
		assertTrue(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
	}

	public void testCreateGraph() {
		NamedGraph graph = this.set.createGraph(uri1);
		assertNotNull(graph);
		assertEquals(node1, graph.getGraphName());
		assertTrue(graph.isEmpty());
		assertTrue(this.set.containsGraph(uri1));
	}

	public void testCreateGraphReplacesExistingGraph() {
		NamedGraph graph1 = createGraph(uri1);
		graph1.add(new Triple(foo, bar, baz));
		this.set.addGraph(graph1);
		assertTrue(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
		NamedGraph graph2 = this.set.createGraph(uri1);
		assertNotNull(graph2);
		assertNotSame(graph1, graph2);
		assertEquals(node1, graph2.getGraphName());
		assertTrue(graph2.isEmpty());
		assertFalse(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
	}

	public void testCreateGraphNoURI() {
		try {
			this.set.createGraph(Node.createAnon());
			fail();
		} catch (IllegalArgumentException iex) {
			// expected since graph names must be URIs
		}
	}

	public void testListGraphs() {
		assertNotNull(this.set.listGraphs());
		assertFalse(this.set.listGraphs().hasNext());
		NamedGraph graph1 = this.set.createGraph(uri1);
		assertTrue(this.set.listGraphs().hasNext());
		assertSame(graph1, this.set.listGraphs().next());
		Collection graphs = new ArrayList();
		NamedGraph graph2 = this.set.createGraph(uri2);
		Iterator it = this.set.listGraphs();
		assertTrue(it.hasNext());
		graphs.add(it.next());
		assertTrue(it.hasNext());
		graphs.add(it.next());
		assertFalse(it.hasNext());
		assertTrue(graphs.contains(graph1));
		assertTrue(graphs.contains(graph2));
		this.set.removeGraph(Node.ANY);
		assertFalse(this.set.listGraphs().hasNext());
	}

	public void testAddQuadToExistingGraph() {
		NamedGraph graph = this.set.createGraph(uri1);
		this.set.addQuad(new Quad(node1, foo, bar, baz));
		assertTrue(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
		assertTrue(graph.contains(foo, bar, baz));
		assertEquals(1, this.set.countQuads());
	}

	public void testAddQuadAutomaticallyCreateNewGraph() {
		this.set.addQuad(new Quad(node1, foo, bar, baz));
		assertTrue(this.set.containsGraph(uri1));
		assertTrue(this.set.containsQuad(new Quad(node1, foo, bar, baz)));
		assertEquals(1, this.set.countQuads());
	}

	public void testAddQuadThatAlreadyExists() {
		this.set.addQuad(new Quad(node1, foo, bar, baz));
		this.set.addQuad(new Quad(node1, foo, bar, baz));
		assertEquals(1, this.set.countQuads());
	}

	public void testAddGraphMustBeConcrete() {
		try {
			this.set.addQuad(new Quad(Node.ANY, foo, bar, baz));
			fail();
		} catch (IllegalArgumentException iaex) {
			// expected because added quad contains wildcard
		}
		try {
			this.set.addQuad(new Quad(node1, Node.ANY, bar, baz));
			fail();
		} catch (IllegalArgumentException iaex) {
			// expected because added quad contains wildcard
		}
	}

	public void testContainsQuads() {
		Quad quad____ = new Quad(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
		Quad quad_spo = new Quad(Node.ANY, foo, bar, baz);
		Quad quad1_po = new Quad(node1, Node.ANY, bar, baz);
		Quad quad1spo = new Quad(node1, foo, bar, baz);
		Quad quad2_po = new Quad(node2, Node.ANY, bar, baz);
		Quad quad2spo = new Quad(node2, foo, bar, baz);
		assertFalse(this.set.containsQuad(quad____));
		assertFalse(this.set.containsQuad(quad_spo));
		assertFalse(this.set.containsQuad(quad1_po));
		assertFalse(this.set.containsQuad(quad1spo));
		this.set.addQuad(quad1spo);
		assertTrue(this.set.containsQuad(quad____));
		assertTrue(this.set.containsQuad(quad_spo));
		assertTrue(this.set.containsQuad(quad1_po));
		assertTrue(this.set.containsQuad(quad1spo));
		assertFalse(this.set.containsQuad(quad2_po));
		assertFalse(this.set.containsQuad(quad2spo));
	}

	public void testRemoveQuads() {
		addSomeQuads();
		this.set.removeQuad(new Quad(Node.ANY, Node.ANY, Node.ANY, Node.ANY));
		assertFalse(this.set.containsQuad(new Quad(Node.ANY, Node.ANY, Node.ANY, Node.ANY)));
		assertEquals(0, this.set.countQuads());
	}

	public void testRemoveQuadsAllFromOneGraph() {
		addSomeQuads();
		this.set.removeQuad(new Quad(node1, Node.ANY, Node.ANY, Node.ANY));
		assertFalse(this.set.containsQuad(new Quad(node1, Node.ANY, Node.ANY, Node.ANY)));
		assertTrue(this.set.containsQuad(new Quad(node2, Node.ANY, Node.ANY, Node.ANY)));
		assertEquals(2, this.set.countQuads());
	}

	public void testRemoveQuadsOneTripleFromAllGraphs() {
		addSomeQuads();
		this.set.removeQuad(new Quad(Node.ANY, foo, bar, baz));
		assertFalse(this.set.containsQuad(new Quad(Node.ANY, foo, bar, baz)));
		assertTrue(this.set.containsQuad(new Quad(node1, foo, foo, foo)));
		assertTrue(this.set.containsQuad(new Quad(node2, foo, foo, foo)));
		assertEquals(2, this.set.countQuads());		
	}

	public void testRemoveQuadsKeepEmptyGraphs() {
		addSomeQuads();
		this.set.removeQuad(new Quad(Node.ANY, Node.ANY, Node.ANY, Node.ANY));
		assertEquals(2, this.set.countGraphs());
		assertTrue(this.set.containsGraph(uri1));
		assertTrue(this.set.containsGraph(uri2));
	}

	public void testCountQuads() {
		assertEquals(0, this.set.countQuads());
		this.set.addQuad(new Quad(node1, foo, bar, baz));
		assertEquals(1, this.set.countQuads());
		this.set.addQuad(new Quad(node2, foo, bar, baz));
		assertEquals(2, this.set.countQuads());
		this.set.addQuad(new Quad(node1, foo, foo, foo));
		assertEquals(3, this.set.countQuads());
	}

	public void testFindQuadsAny() {
		addSomeQuads();
		Collection quads = toCollection(
				this.set.findQuads(Node.ANY, Node.ANY, Node.ANY, Node.ANY));
		assertTrue(quads.contains(new Quad(node1, foo, bar, baz)));
		assertTrue(quads.contains(new Quad(node1, foo, foo, foo)));
		assertTrue(quads.contains(new Quad(node2, foo, bar, baz)));
		assertTrue(quads.contains(new Quad(node2, foo, foo, foo)));
		assertEquals(4, quads.size());
	}

	public void testFindQuadsOneGraph() {
		addSomeQuads();
		Collection quads = toCollection(
				this.set.findQuads(node1, Node.ANY, Node.ANY, Node.ANY));
		assertTrue(quads.contains(new Quad(node1, foo, bar, baz)));
		assertTrue(quads.contains(new Quad(node1, foo, foo, foo)));
		assertEquals(2, quads.size());
	}

	public void testFindQuadsOneTriple() {
		addSomeQuads();
		Collection quads = toCollection(
				this.set.findQuads(Node.ANY, foo, bar, Node.ANY));
		assertTrue(quads.contains(new Quad(node1, foo, bar, baz)));
		assertTrue(quads.contains(new Quad(node2, foo, bar, baz)));
		assertEquals(2, quads.size());
	}

	public void testFindQuadsSpecific() {
		addSomeQuads();
		Collection quads = toCollection(
				this.set.findQuads(node1, foo, bar, baz));
		assertTrue(quads.contains(new Quad(node1, foo, bar, baz)));
		assertEquals(1, quads.size());
	}

	public void testFindQuadsEmptySet() {
		Iterator it = this.set.findQuads(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
		assertFalse(it.hasNext());
	}

	public void testAsJenaGraph() {
		assertNotNull(this.set.asJenaGraph(node1));
	}

	public void testAsJenaGraphNull() {
		try {
			this.set.asJenaGraph(null);
			fail();
		} catch (NullPointerException npex) {
			// We don't really care what kind of exception is thrown,
		} catch (IllegalArgumentException iaex) {
			// but it should fail
		}
	}

	private NamedGraph createGraph(String uri) {
		return new NamedGraphImpl(uri, new GraphMem());
	}
	
	private void addSomeQuads() {
		this.set.addQuad(new Quad(node1, foo, bar, baz));
		this.set.addQuad(new Quad(node1, foo, foo, foo));
		this.set.addQuad(new Quad(node2, foo, bar, baz));
		this.set.addQuad(new Quad(node2, foo, foo, foo));
	}
	
	private Collection toCollection(Iterator it) {
		Collection result = new ArrayList();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}
}
