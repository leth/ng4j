// $Id: NamedGraphSetDBTest.java,v 1.6 2009/04/22 17:25:35 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphSetTest;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;

/**
 * Tests {@link NamedGraphSetDB} by reusing the common NamedGraphSetTest
 * tests and adding some additional tests.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphSetDBTest extends NamedGraphSetTest {

	public static final String NL = System.getProperty("line.separator") ;
	
	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSetTest#createNamedGraphSet()
	 */
	@Override
	public NamedGraphSet createNamedGraphSet() throws Exception {
		return DBConnectionHelper.createNamedGraphSetDB();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSetTest#tearDown()
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		DBConnectionHelper.deleteNamedGraphSetTables();
	}
	
	public void testIsEmpty() {
		assertTrue(this.set.isEmpty());
		this.set.addQuad(new Quad(node1, node1, node1, node1));
		assertFalse(this.set.isEmpty());
	}
	
	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSetTest#testAddGraph()
	 */
	@Override
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
		Iterator<NamedGraph> it = this.set.listGraphs();
		assertTrue(it.hasNext());
		assertEquals(node1, (it.next()).getGraphName());
		assertFalse(it.hasNext());
		assertFalse(this.set.isEmpty());
		it.remove();
		assertTrue(this.set.isEmpty());
	}
	
	public void testSPARQL() {
		List<Quad> l = new ArrayList<Quad>();
		l.add(new Quad(node1, foo, bar, baz));
		l.add(new Quad(node1, foo, foo, foo));
		l.add(new Quad(node2, foo, foo, foo));
		Iterator<Quad> it = l.iterator();
		while (it.hasNext()) {
			Quad q = it.next();
			this.set.addQuad(q);
		}
		String queryString = "SELECT ?graph ?subject ?predicate ?object" + NL
			+ "WHERE { GRAPH ?graph {" + NL
			+ "?subject ?predicate ?object" + NL
			+ " } }";
		QueryExecution qe = QueryExecutionFactory.create( queryString, localDS );
		ResultSet results = ResultSetFactory.copyResults( qe.execSelect() );
		List<Object> actual = new ArrayList<Object>();
		assertTrue(results.hasNext());
		actual.add(results.next());
		assertTrue(results.hasNext());
		actual.add(results.next());
		assertTrue(results.hasNext());
		actual.add(results.next());
		assertFalse(results.hasNext());
	}
}