/*
 * $Id: JenaRDFReaderTest.java,v 1.3 2005/06/21 09:25:35 cyganiak Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.io.InputStream;

import junit.framework.TestCase;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.ng4j.trix.JenaRDFReader;

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
		this.model.setReaderClassName("TRIX-EXT", JenaRDFReaderWithExtensions.class.getName());		
	}

	public void testFixture() {
		assertTrue(this.model.isEmpty());		
	}

	public void testMinimal() {
		readIntoModel("minimal.xml", "TRIX");
		assertTrue(this.model.isEmpty());
	}
	
	public void testSingleTriple() {
		readIntoModel("singleTriple.xml", "TRIX");
		assertSameStatements("singleTriple.nt");
	}
	
	public void testMalformed() {
		try {
			readIntoModel("malformed.xml", "TRIX");
			fail("should have failed");
		} catch (JenaException jex) {
			// is expected
		}
	}

	public void testLiteralSubject() {
		try {
			readIntoModel("literalSubject.xml", "TRIX");
		} catch (JenaException jex) {
			// is expected
		}
	}

	public void testObjectNodeTypes() {
		readIntoModel("objectNodeTypes.xml", "TRIX");
		assertSameStatements("objectNodeTypes.nt");		
	}

	public void testBlankNodes() {
		readIntoModel("blankNodes.xml", "TRIX");
		assertSameStatements("blankNodes.nt");		
	}

	public void testBlankNodes2() {
		readIntoModel("blankNodes2.xml", "TRIX");
		Model expected = ModelFactory.createDefaultModel();
		InputStream stream = this.getClass().getResourceAsStream("tests/blankNodes.nt");
		expected.read(stream, "file:/test", "N-TRIPLE");
		assertFalse(expected.isIsomorphicWith(this.model));
	}

	public void testIgnoreMultipleGraphs() {
		readIntoModel("ignoreMultipleGraphs.xml", "TRIX");
		assertSameStatements("ignoreMultipleGraphs.nt");
		assertEquals(2, this.model.size());
	}

	public void testSyntacticExtension() {
		readIntoModel("extended.xml", "TRIX-EXT");
		assertSameStatements("extended.nt");
		assertEquals(1, this.model.size());
	}

	private void readIntoModel(String triXFile, String lang) {
		InputStream stream = this.getClass().getResourceAsStream("tests/" + triXFile);
		assertNotNull("tests/" + triXFile + " not found", stream);
		this.model.read(stream, "file:/test", lang);
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
