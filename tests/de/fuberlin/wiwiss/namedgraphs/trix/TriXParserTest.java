/*
 * $Id: TriXParserTest.java,v 1.1 2004/09/13 14:37:32 cyganiak Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.xml.sax.SAXParseException;

import de.fuberlin.wiwiss.namedgraphs.trix.TriXParser;

import junit.framework.TestCase;

/**
 * Unit tests for {@link TriXParserTest}
 *
 * TODO: Missing tests: whitespace normalization on bNodes and URIs;
 * bNodes shared between graphs (forbidden!); what are legal bNodeIDs?
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 */
public class TriXParserTest extends TestCase {
	private TriXParser parser;
	private MockParserCallback callback;
	
	protected void setUp() {
		this.parser = new TriXParser();
		this.callback = new MockParserCallback();
	}

	public void testMalformedXML() throws Exception {
		try {
			String malformed = "<foo";
			this.parser.parse(toInputStream(malformed), new URI("file:/test"),
					this.callback);
			fail();
		} catch (Exception ex) {
			// is expected
		}
	}

	public void testMissingNamespace() throws Exception {
		try {
			parseFile("missingNamespace.xml");
			fail();
		} catch (SAXParseException spex) {
			// is expected
		}
	}

	public void testWrongPredicateType() throws Exception {
		this.callback.addWildcardExpectation();
		try {
			parseFile("wrongPredicateType.xml");
			fail();
		} catch (SAXParseException spex) {
			// is expected
		}
	}

	public void testDatatypeRequired() throws Exception {
		this.callback.addWildcardExpectation();
		try {
			parseFile("datatypeRequired.xml");
			fail();
		} catch (SAXParseException spex) {
			// is expected
		}
	}

	public void testMalformedSubjectURI() throws Exception {
		this.callback.addWildcardExpectation();
		try {
			parseFile("malformedSubjectURI.xml");
			fail();
		} catch (Exception ex) {
			// is expected
		}
	}

	public void testMalformedPredicateURI() throws Exception {
		this.callback.addWildcardExpectation();
		try {
			parseFile("malformedPredicateURI.xml");
			fail();
		} catch (Exception ex) {
			// is expected
		}
	}

	public void testMalformedObjectURI() throws Exception {
		this.callback.addWildcardExpectation();
		try {
			parseFile("malformedObjectURI.xml");
			fail();
		} catch (Exception ex) {
			// is expected
		}
	}

	public void testMalformedGraphURI() throws Exception {
		this.callback.addWildcardExpectation();
		try {
			parseFile("malformedGraphURI.xml");
			fail();
		} catch (Exception ex) {
			// is expected
		}
	}

	public void testMinimal() throws Exception {
		testFromFiles("minimal");
	}

	public void testSingleTriple() throws Exception {
		testFromFiles("singleTriple");
	}

	public void testIgnoreXMLComments() throws Exception {
		testFromFiles("ignoreXMLComments");		
	}

	public void testSubjectNodeTypes() throws Exception {
		testFromFiles("subjectNodeTypes");
	}

	public void testObjectNodeTypes() throws Exception {
		testFromFiles("objectNodeTypes");		
	}

	public void testTwoEmptyGraphs() throws Exception {
		testFromFiles("twoEmptyGraphs");
	}

	public void testEmptyNamedGraph() throws Exception {
		testFromFiles("emptyNamedGraph");
	}

	public void testNamedGraphs() throws Exception {
		testFromFiles("namedGraphs");
	}

	public void testRelativeURIs() throws Exception {
		testFromFiles("relativeURIs");
	}

	public void testSpecialChars() throws Exception {
		testFromFiles("specialChars");
	}

	public void testAlternateEncoding() throws Exception {
		testFromFiles("alternateEncoding");
	}

	public void testJavaURIs() throws Exception {
		assertEquals(new URI("http://example.org/#foo"),
				new URI("http://example.org/").resolve("#foo"));
		assertEquals(new URI("http://example.org/foo/bar"),
				new URI("http://example.org/foo/").resolve("bar"));
	}

	private InputStream toInputStream(String in) {
		return new ByteArrayInputStream(in.getBytes());
	}
	
	private void testFromFiles(String fileBase) throws Exception {
		InputStream in = this.getClass().getResourceAsStream(
				"tests/" + fileBase + ".txt");
		assertNotNull("Not found: tests/" + fileBase + ".txt", in);
		BufferedReader expectations = new BufferedReader(new InputStreamReader(in, "utf-8"));
		while (true) {
			String line = expectations.readLine();
			if (line == null) {
				break;
			}
			this.callback.addExpectation(line);
		}
		parseFile(fileBase + ".xml");
		this.callback.verify();
	}
	
	private void parseFile(String fileName) throws Exception {
		InputStream in = this.getClass().getResourceAsStream("tests/" + fileName);
		assertNotNull("Not found: tests/" + fileName, in);
		this.parser.parse(in, new URI("file:/test"), this.callback);		
	}
}
