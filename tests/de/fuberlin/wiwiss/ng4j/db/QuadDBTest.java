// $Id: QuadDBTest.java,v 1.3 2004/12/14 13:30:15 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.db;

import java.util.Iterator;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Tests for {@link QuadDB}.
 *
 * TODO: Add tests for QuadDB table management
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QuadDBTest extends TestCase {
	private final static Node graph1 = Node.createURI("http://example.org/graph1");
	private final static Node graph2 = Node.createURI("http://example.org/graph2");
	private final static Node graph3 = Node.createURI("http://example.org/graph3");
	private final static Node node1 = Node.createURI("http://example.org/node1");
	private final static Node node2 = Node.createURI("http://example.org/node2");
	private final static Node node3 = Node.createURI("http://example.org/node3");
	private final static Node node4 = Node.createURI("http://example.org/node4");
	private final static Node blank1 = Node.createAnon(new AnonId("blank1"));
	private final static Node blank2 = Node.createAnon(new AnonId("blank2"));
	private QuadDB db;

	protected void setUp() throws Exception {
		this.db = new QuadDB(DBConnectionHelper.getConnection(), "ng4j_test");
		this.db.createTables();
	}

	protected void tearDown() throws Exception {
		this.db.deleteTables();
		this.db.close();
	}

	public void testGraphNamesEmpty() {
		assertEquals(0, this.db.countGraphNames());
		assertFalse(this.db.containsGraphName(graph1));
		assertFalse(this.db.containsGraphName(Node.ANY));
		assertFalse(this.db.listGraphNames().hasNext());
	}
	
	public void testAddGraphName() {
		this.db.insertGraphName(graph1);
		assertEquals(1, this.db.countGraphNames());
		assertTrue(this.db.containsGraphName(graph1));
		assertTrue(this.db.containsGraphName(Node.ANY));
		assertTrue(this.db.listGraphNames().hasNext());
		assertEquals(graph1, this.db.listGraphNames().next());
	}
	
	public void testDeleteGraphName() {
		this.db.insertGraphName(graph1);
		this.db.insertGraphName(graph2);
		assertEquals(2, this.db.countGraphNames());
		assertTrue(this.db.containsGraphName(graph2));
		this.db.deleteGraphName(graph2);
		assertEquals(1, this.db.countGraphNames());
		assertFalse(this.db.containsGraphName(graph2));
		assertTrue(this.db.containsGraphName(graph1));
		assertTrue(this.db.listGraphNames().hasNext());
		assertEquals(graph1, this.db.listGraphNames().next());
	}
	
	public void testDeleteAllGraphs() {
		this.db.insertGraphName(graph1);
		this.db.insertGraphName(graph2);
		this.db.insertGraphName(graph3);
		assertEquals(3, this.db.countGraphNames());
		this.db.deleteGraphName(Node.ANY);
		assertEquals(0, this.db.countGraphNames());
		assertFalse(this.db.containsGraphName(graph1));
		assertFalse(this.db.containsGraphName(graph2));
		assertFalse(this.db.containsGraphName(graph3));
		assertFalse(this.db.listGraphNames().hasNext());
	}
	
	public void testQuadsEmpty() {
		assertEquals(0, this.db.count());
		assertFalse(this.db.find(Node.ANY, Node.ANY, Node.ANY, Node.ANY).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, node3).hasNext());
	}
	
	public void testAddQuad() {
		this.db.insert(graph1, node1, node2, node3);
		assertEquals(1, this.db.count());
		assertTrue(this.db.find(Node.ANY, Node.ANY, Node.ANY, Node.ANY).hasNext());
		assertEquals(new Quad(graph1, node1, node2, node3),
				this.db.find(Node.ANY, Node.ANY, Node.ANY, Node.ANY).next());
		assertTrue(this.db.find(graph1, node1, node2, node3).hasNext());
		assertEquals(new Quad(graph1, node1, node2, node3),
				this.db.find(graph1, node1, node2, node3).next());
	}
	
	public void testFindPositionsNegative() {
		this.db.insert(graph1, node1, node2, node3);
		assertFalse(this.db.find(graph2, node1, node2, node3).hasNext());
		assertFalse(this.db.find(graph1, node2, node2, node3).hasNext());
		assertFalse(this.db.find(graph1, node1, node1, node3).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, node2).hasNext());
		assertFalse(this.db.find(graph2, Node.ANY, Node.ANY, Node.ANY).hasNext());
		assertFalse(this.db.find(Node.ANY, node2, Node.ANY, Node.ANY).hasNext());
		assertFalse(this.db.find(Node.ANY, Node.ANY, node3, Node.ANY).hasNext());
		assertFalse(this.db.find(Node.ANY, Node.ANY, Node.ANY, node1).hasNext());
	}

	public void testFindPositionsPositive() {
		this.db.insert(graph1, node1, node2, node3);
		assertTrue(this.db.find(Node.ANY, node1, node2, node3).hasNext());
		assertTrue(this.db.find(graph1, Node.ANY, node2, node3).hasNext());
		assertTrue(this.db.find(graph1, node1, Node.ANY, node3).hasNext());
		assertTrue(this.db.find(graph1, node1, node2, Node.ANY).hasNext());
	}
	
	public void testFindBlankNode() {
		this.db.insert(graph1, blank1, node1, blank2);
		assertTrue(this.db.find(Node.ANY, blank1, Node.ANY, blank2).hasNext());
		assertFalse(this.db.find(Node.ANY, blank2, Node.ANY, Node.ANY).hasNext());
		assertFalse(this.db.find(Node.ANY, node2, Node.ANY, Node.ANY).hasNext());
	}
	
	public void testFindPlainLiteral() {
		this.db.insert(graph1, node1, node2, Node.createLiteral("foo", null, null));
		assertTrue(this.db.find(graph1, node1, node2, Node.createLiteral("foo", null, null)).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, Node.createLiteral("bar", null, null)).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, Node.createLiteral("foo", "en", null)).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, Node.createLiteral("foo", null, XSDDatatype.XSDfloat)).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, Node.createAnon(new AnonId("foo"))).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, node3).hasNext());
	}
	
	public void testFindLangLiteral() {
		this.db.insert(graph1, node1, node2, Node.createLiteral("foo", "en", null));
		assertTrue(this.db.find(graph1, node1, node2, Node.createLiteral("foo", "en", null)).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, Node.createLiteral("bar", null, null)).hasNext());
	}
	
	public void testFindTypedLiteral() {
		this.db.insert(graph1, node1, node2, Node.createLiteral("2004", null, XSDDatatype.XSDgYear));
		assertTrue(this.db.find(graph1, node1, node2, Node.createLiteral("2004", null, XSDDatatype.XSDgYear)).hasNext());
		assertFalse(this.db.find(graph1, node1, node2, Node.createLiteral("2004", null, null)).hasNext());
	}
	
	public void testFindIterator() {
		this.db.insert(graph1, node1, node2, node3);
		Iterator it = this.db.find(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertNotNull(it.next());
		assertFalse(it.hasNext());
		assertFalse(it.hasNext());
	}
	
	public void testEscape() {
		this.db.insert(graph1, node1, node2, Node.createLiteral("\"'\\", null, null));
		assertTrue(this.db.find(Node.ANY, Node.ANY, Node.ANY, Node.createLiteral("\"'\\", null, null)).hasNext());
	}
	
	public void testDelete() {
		this.db.insert(graph1, node1, node2, node3);
		this.db.insert(graph2, node1, node2, node3);
		assertEquals(2, this.db.count());
		this.db.delete(graph1, node1, node2, node3);
		assertEquals(1, this.db.count());
		assertFalse(this.db.find(graph1, node1, node2, node3).hasNext());
		assertTrue(this.db.find(graph2, node1, node2, node3).hasNext());
	}
	
	public void testDeleteAll() {
		this.db.insert(graph1, node1, node2, node3);
		this.db.insert(graph2, node3, node1, node2);
		this.db.delete(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
		assertEquals(0, this.db.count());
	}
	
	public void testSameSubjectAndPredicate() {
		this.db.insert(graph1, node1, node2, node3);
		this.db.insert(graph1, node1, node2, node4);
		assertEquals(2, this.db.count());
	}
	
	public void testDontInsertDuplicates() {
		this.db.insert(graph1, node1, node2, node3);
		this.db.insert(graph1, node1, node2, node3);
		assertEquals(1, this.db.count());
	}
}
