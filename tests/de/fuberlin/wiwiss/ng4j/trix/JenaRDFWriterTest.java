/*
 * $Id: JenaRDFWriterTest.java,v 1.1 2004/10/23 13:31:23 cyganiak Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import de.fuberlin.wiwiss.ng4j.trix.JenaRDFReader;
import de.fuberlin.wiwiss.ng4j.trix.JenaRDFWriter;

/**
 * Unit tests for {@link JenaRDFWriter}
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class JenaRDFWriterTest extends TestCase {

	public void testWrite() {
		Model model = createModelFromNTriples("tests/writerTest.nt");
		assertEquals(6, model.size());
		String written = modelToString(model, new JenaRDFWriter(), "file:/test");
		Model actual = createModelFromString(written, new JenaRDFReader(), "file:/test");
		assertEquals(6, actual.size());
		assertTrue(actual.isIsomorphicWith(model));
	}
	
	public void testEscapeSpecialCharacters() throws IOException {
		Model model = ModelFactory.createDefaultModel();
		model.add(model.createResource("http://example.org/#foo"),
				model.createProperty("http://example.org/#bar"),
				model.createLiteral("<\"&"));
		String actual = modelToString(model, new JenaRDFWriter(), "file:/test");
		String expected = TestHelpers.getFileContents("tests/escape.xml");
		assertEquals(expected, actual);
	}

	private String modelToString(Model model, RDFWriter writer, String baseURI) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		writer.write(model, bytes, baseURI);
		String actual = new String(bytes.toByteArray());
		return actual;
	}
	
	private Model createModelFromString(String input, RDFReader reader, String baseURI) {
		ByteArrayInputStream bytes = new ByteArrayInputStream(input.getBytes());
		Model result = ModelFactory.createDefaultModel();
		reader.read(result, bytes, baseURI);
		return result;
	}
	
	private Model createModelFromNTriples(String fileName) {
		Model result = ModelFactory.createDefaultModel();
		result.read(this.getClass().getResource(fileName).toString(), "N-TRIPLE");
		return result;
	}
}
