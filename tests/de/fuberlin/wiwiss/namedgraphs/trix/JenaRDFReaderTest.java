/*
 * $Id: JenaRDFReaderTest.java,v 1.2 2004/09/13 22:45:13 cyganiak Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

import java.io.InputStream;

import junit.framework.TestCase;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.namedgraphs.trix.JenaRDFReader;

/**
 * Unit tests for {@link JenaRDFReader}.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class JenaRDFReaderTest extends TestCase {
	private Model model;
	
	public void setUp() {
		this.model = ModelFactory.createDefaultModel();
		this.model.setReaderClassName("TRIX", JenaRDFReader.class.getName());		
	}

	public void testFixture() {
		assertTrue(this.model.isEmpty());		
	}

	public void testMinimal() {
		readIntoModel("minimal.xml");
		assertTrue(this.model.isEmpty());
	}
	
	public void testSingleTriple() {
		readIntoModel("singleTriple.xml");
		assertSameStatements("singleTriple.nt");
	}
	
	public void testMalformed() {
		try {
			readIntoModel("malformed.xml");
			fail("should have failed");
		} catch (JenaException jex) {
			// is expected
		}
	}

	public void testLiteralSubject() {
		try {
			readIntoModel("literalSubject.xml");
		} catch (JenaException jex) {
			// is expected
		}
	}

	public void testObjectNodeTypes() {
		readIntoModel("objectNodeTypes.xml");
		assertSameStatements("objectNodeTypes.nt");		
	}

	public void testBlankNodes() {
		readIntoModel("blankNodes.xml");
		assertSameStatements("blankNodes.nt");		
	}

	public void testBlankNodes2() {
		readIntoModel("blankNodes2.xml");
		Model expected = ModelFactory.createDefaultModel();
		InputStream stream = this.getClass().getResourceAsStream("tests/blankNodes.nt");
		expected.read(stream, "file:/test", "N-TRIPLE");
		assertFalse(expected.isIsomorphicWith(this.model));
	}

	public void testIgnoreMultipleGraphs() {
		readIntoModel("ignoreMultipleGraphs.xml");
		assertSameStatements("ignoreMultipleGraphs.nt");
		assertEquals(2, this.model.size());
	}

	private void readIntoModel(String triXFile) {
		InputStream stream = this.getClass().getResourceAsStream("tests/" + triXFile);
		assertNotNull("tests/" + triXFile + " not found", stream);
		this.model.read(stream, "file:/test", "TRIX");
	}

	private void assertSameStatements(String nTriplesFile) {
		Model expected = ModelFactory.createDefaultModel();
		InputStream stream = this.getClass().getResourceAsStream("tests/" + nTriplesFile);
		assertNotNull("tests/" + nTriplesFile + " not found", stream);
		expected.read(stream, "file:/test", "N-TRIPLE");
		if (!expected.isIsomorphicWith(this.model)) {
			System.out.println("=== Expected ===");
			StmtIterator it = expected.listStatements();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
			System.out.println("=== Actual ===");
			it = this.model.listStatements();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
			fail("models are not isomorphic; see stdout");
		}
	}
}
