// $Id: MySQLTest.java,v 1.1 2004/11/02 02:00:25 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.mem.GraphMem;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSetTest;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;

/**
 * Tests for DB persistence. Needs a MySQL database. Connection data must be
 * provided within this file.
 *
 * TODO: Put DB access data somewhere more safe!
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MySQLTest extends NamedGraphSetTest {
	private static final String URL = "jdbc:mysql://localhost/ng4j";
	private static final String USER = "root";
	private static final String PW = "";
	private static final Node node1 = Node.createURI("http://example.org/node1");
	private static final Node node2 = Node.createURI("http://example.org/node2");

	public void setUp() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(URL, USER, PW);
		this.set = NamedGraphSetDB.open(connection);
	}

	protected void tearDown() throws Exception {
		this.set.close();
		Connection connection = DriverManager.getConnection(URL, USER, PW);
		NamedGraphSetDB.delete(connection);
	}
	
	public void testIsEmpty() {
		assertTrue(this.set.isEmpty());
		this.set.addQuad(new Quad(node1, node1, node1, node1));
		assertFalse(this.set.isEmpty());
	}
	
	public void testAddGraph() {
		NamedGraph ng = new NamedGraphImpl(node1, new GraphMem());
		assertFalse(this.set.containsGraph(node1));
		assertEquals(0, this.set.countGraphs());
		this.set.addGraph(ng);
		assertEquals(1, this.set.countGraphs());
		assertTrue(this.set.containsGraph(node1));
		assertFalse(this.set.containsGraph(node2));
		assertTrue(this.set.getGraph(node1).isEmpty());
		assertNull(this.set.getGraph(node2));
		Iterator it = this.set.listGraphs();
		assertTrue(it.hasNext());
		assertEquals(node1, ((NamedGraph) it.next()).getGraphName());
		assertFalse(it.hasNext());
		assertFalse(this.set.isEmpty());
		it.remove();
		assertTrue(this.set.isEmpty());
	}
}
