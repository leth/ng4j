// $Id: NamedGraphModelTest.java,v 1.2 2004/09/13 22:26:04 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.mem.ModelMem;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;

import de.fuberlin.wiwiss.namedgraphs.impl.NamedGraphSetImpl;

/**
 * Unit tests for {@link NamedGraphModel}. We do not test the whole
 * Model interface because it is so large. We left out methods which
 * likely are uneffected by our changes, or which just call other methods
 * that we already have tested.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphModelTest extends TestCase {
	private final static String uri1 = "http://example.org/#graph1";
	private final static Node node1 = Node.createURI("http://example.org/#graph1");
	private final static Resource foo = new ResourceImpl("http://example.org/#foo");
	private final static Property bar = new PropertyImpl("http://example.org/#bar");
	private final static Resource baz = new ResourceImpl("http://example.org/#baz");
	private final static Node fooNode = Node.createURI("http://example.org/#foo");
	private final static Node barNode = Node.createURI("http://example.org/#bar");
	private final static Node bazNode = Node.createURI("http://example.org/#baz");
	private final static String fooString = "http://example.org/#foo";

	private NamedGraphSet set;
	private NamedGraphModel model;

	protected void setUp() throws Exception {
		this.set = new NamedGraphSetImpl();
		this.model = this.set.asJenaModel(uri1);
		super.setUp();
	}

	public void testGetDefaultGraphName() {
		assertEquals(uri1, this.model.getDefaultGraphName().getURI());
	}
	
	public void testAddStatementList() {
		this.model.add(twoStatementsList());
		assertTwoStatementsAdded();
	}
	
	public void testAddModel() {
		Model other = new ModelMem();
		other.add(twoStatementsList());
		this.model.add(other);
		assertTwoStatementsAdded();
	}
	
	public void testAddStatement() {
		this.model.add(new StatementImpl(foo, bar, baz));
		assertTrue(this.set.containsQuad(new Quad(node1, fooNode, barNode, bazNode)));
		assertTrue(this.model.listStatements().next() instanceof NamedGraphStatement);
	}
	
	public void testAddStatementArray() {
		this.model.add((Statement[]) twoStatementsList().toArray(new Statement[]{}));
		assertTwoStatementsAdded();
	}
	
	public void testAddStatementIterator() {
		this.model.add(new StmtIteratorImpl(twoStatementsList().iterator()));
		assertTwoStatementsAdded();
	}
	
	public void testContains() {
		this.model.add(twoStatementsList());
		assertTrue(this.model.contains(foo, bar, baz));
		assertTrue(this.model.contains(baz, bar, foo));
		assertFalse(this.model.contains(foo, bar, foo));
		assertTrue(this.model.contains(new StatementImpl(foo, bar, baz)));
		assertTrue(this.model.contains(new StatementImpl(baz, bar, foo)));
		assertFalse(this.model.contains(new StatementImpl(foo, bar, foo)));
		assertTrue(this.model.contains(new NamedGraphStatement(foo, bar, baz, this.model)));
		assertTrue(this.model.contains(new NamedGraphStatement(baz, bar, foo, this.model)));
		assertFalse(this.model.contains(new NamedGraphStatement(foo, bar, foo, this.model)));
		// what about containsAll and containsAny?
	}

	public void testCreateStatement() {
		Statement stmt = this.model.createStatement(foo, bar, baz);
		assertTrue(stmt instanceof NamedGraphStatement);
		assertTrue(this.model.isEmpty());
	}

	public void testListStatementsAll() {
		this.model.add(twoStatementsList());
		assertAllNamedGraphStatements(this.model.listStatements());
		Collection stmts = toCollection(this.model.listStatements());
		assertTrue(stmts.contains(new NamedGraphStatement(foo, bar, baz, this.model)));
		assertTrue(stmts.contains(new NamedGraphStatement(baz, bar, foo, this.model)));
		assertEquals(2, stmts.size());
	}

	public void testListStatementsWithSelector() {
		this.model.add(twoStatementsList());
		assertAllNamedGraphStatements(this.model.listStatements(new SimpleSelector()));
		Collection stmts = toCollection(this.model.listStatements(new SimpleSelector()));
		assertTrue(stmts.contains(new NamedGraphStatement(foo, bar, baz, this.model)));
		assertTrue(stmts.contains(new NamedGraphStatement(baz, bar, foo, this.model)));
		assertEquals(2, stmts.size());
	}
	
	private Collection toCollection(Iterator it) {
		Collection result = new ArrayList();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}

	private List twoStatementsList() {
		List statements = new ArrayList();
		statements.add(new StatementImpl(foo, bar, baz));
		statements.add(new NamedGraphStatement(baz, bar, foo, this.model));
		return statements;
	}

	private void assertTwoStatementsAdded() {
		assertTrue(this.set.containsQuad(new Quad(node1, fooNode, barNode, bazNode)));
		assertTrue(this.set.containsQuad(new Quad(node1, bazNode, barNode, fooNode)));
		assertEquals(2, this.set.countQuads());
		assertTrue(this.model.contains(foo, bar, baz));
		assertTrue(this.model.contains(baz, bar, foo));
		assertEquals(2, this.model.size());
		assertAllNamedGraphStatements(this.model.getResource(fooString).listProperties());
	}

	private void assertAllNamedGraphStatements(StmtIterator it) {
		while (it.hasNext()) {
			Object o = it.next();
			if (!(o instanceof NamedGraphStatement)) {
				fail("Expected NamedGraphStatement, was " + o.getClass().getName() + ": " + o);
			}
		}
	}
}
