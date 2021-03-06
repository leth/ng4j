// $Id: NamedGraphStatementIteratorTest.java,v 1.2 2009/02/11 15:15:25 jenpc Exp $
package de.fuberlin.wiwiss.ng4j;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;

import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.NamedGraphStatement;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphStatementIterator;

/**
 * Unit tests for {@link NamedGraphStatementIterator}
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphStatementIteratorTest extends TestCase {
	private final static String uri1 = "http://example.com/#graph1";
	private final static String foo = "http://example.com/#foo";
	private final static String bar = "http://example.com/#bar";
	private final static String baz = "http://example.com/#baz";

	public void testIterator() {
		NamedGraphModel model = new NamedGraphModel(new NamedGraphSetImpl(), uri1);
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(new StatementImpl(model.createResource(foo),
				model.createProperty(bar), model.createResource(baz)));
		statements.add(new NamedGraphStatement(model.createResource(baz),
				model.createProperty(bar), model.createResource(foo),
				model));
		NamedGraphStatementIterator it = new NamedGraphStatementIterator(
				new StmtIteratorImpl(statements.iterator()), model);
		assertTrue(it.hasNext());
		NamedGraphStatement stmt = (NamedGraphStatement) it.next();
		assertEquals(foo, stmt.getSubject().getURI());
		assertEquals(bar, stmt.getPredicate().getURI());
		assertEquals(baz, stmt.getObject().asNode().getURI());
		assertSame(model, stmt.getModel());
		assertTrue(it.hasNext());
		stmt = (NamedGraphStatement) it.next();
		assertEquals(baz, stmt.getSubject().getURI());
		assertEquals(bar, stmt.getPredicate().getURI());
		assertEquals(foo, stmt.getObject().asNode().getURI());
		assertSame(model, stmt.getModel());
	}
}
