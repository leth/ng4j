// $Id: N3SanityCheckTest.java,v 1.1 2004/12/12 17:30:30 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.io.StringReader;

import com.hp.hpl.jena.n3.N3JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import junit.framework.TestCase;

/**
 * TODO: Describe this type
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class N3SanityCheckTest extends TestCase {

	public void testBaseURIForProperty1() {
		String n3 = "<#a> <#a> <#a> .";
		N3JenaReader reader = new N3JenaReader();
		Model m = ModelFactory.createDefaultModel();
		reader.read(m, new StringReader(n3), "http://example.com/base");
		Statement stmt = m.listStatements().nextStatement();
		System.out.println(stmt);
		assertEquals("http://example.com/base#a", stmt.getPredicate().getURI());
	}

	public void testBaseURIForProperty1b() {
		String n3 = "<#> <#> <#> .";
		N3JenaReader reader = new N3JenaReader();
		Model m = ModelFactory.createDefaultModel();
		reader.read(m, new StringReader(n3), "http://example.com/base");
		Statement stmt = m.listStatements().nextStatement();
		System.out.println(stmt);
		assertEquals("http://example.com/base#", stmt.getPredicate().getURI());
	}

	public void testBaseURIForProperty2() {
		String n3 = "<> <> <> .";
		N3JenaReader reader = new N3JenaReader();
		Model m = ModelFactory.createDefaultModel();
		reader.read(m, new StringReader(n3), "http://example.com/base");
		Statement stmt = m.listStatements().nextStatement();
		System.out.println(stmt);
		assertEquals("http://example.com/base", stmt.getPredicate().getURI());
	}
}
