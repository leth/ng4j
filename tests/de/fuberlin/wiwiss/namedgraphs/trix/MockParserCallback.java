/*
 * $Id: MockParserCallback.java,v 1.1 2004/09/13 14:37:31 cyganiak Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.fuberlin.wiwiss.namedgraphs.trix.ParserCallback;

import junit.framework.Assert;

/**
 * Mock implementation of {@link ParserCallback} for testing purposes.
 * The {@link #addExpectation} method can be used to feed expectations
 * from an external file. See <tt>getXXX</tt> private methods for
 * expected format of the expectation strings.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MockParserCallback implements ParserCallback {
	private List expectedCalls = new LinkedList();

	public void addExpectation(String expectation) {
		this.expectedCalls.add(expectation);
	}

	public void addWildcardExpectation() {
		this.expectedCalls.add("*");
	}

	public void expectStartGraph() {
		expectStartGraph(new ArrayList(0));
	}

	public void expectStartGraph(Collection uris) {
		addExpectation(getStartGraphCall(uris));
	}

	public void expectEndGraph() {
		addExpectation("endGraph");
	}
	
	public void expectURI(String uri) {
		addExpectation(getURICall(uri));
	}

	public void expectBNode(String id) {
		addExpectation(getIDCall(id));
	}

	public void expectPlainLiteral(String value, String lang) {
		addExpectation(getPlainLiteralCall(value, lang));
	}

	public void expectTypedLiteral(String value, String datatypeURI) {
		addExpectation(getTypedLiteralCall(value, datatypeURI));
	}

	public void verify() {
		if (!this.expectedCalls.isEmpty()) {
			Assert.fail("At end, but expected " + this.expectedCalls.remove(0));
		}
	}

	public void startGraph(List uris) {
		assertNext(getStartGraphCall(uris));
	}

	public void endGraph() {
		assertNext("endGraph");
	}

	public void subjectURI(String uri) {
		assertNext(getURICall(uri));
	}

	public void subjectBNode(String id) {
		assertNext(getIDCall(id));
	}

	public void subjectPlainLiteral(String value, String lang) {
		assertNext(getPlainLiteralCall(value, lang));
	}

	public void subjectTypedLiteral(String value, String datatypeURI) {
		assertNext(getTypedLiteralCall(value, datatypeURI));
	}

	public void predicate(String uri) {
		assertNext(getURICall(uri));
	}

	public void objectURI(String uri) {
		assertNext(getURICall(uri));
	}

	public void objectBNode(String id) {
		assertNext(getIDCall(id));
	}

	public void objectPlainLiteral(String value, String lang) {
		assertNext(getPlainLiteralCall(value, lang));
	}

	public void objectTypedLiteral(String value, String datatypeURI) {
		assertNext(getTypedLiteralCall(value, datatypeURI));
	}

	private String getStartGraphCall(Collection uris) {
		List sortedURIs = new ArrayList(uris);
		Collections.sort(sortedURIs);
		StringBuffer call = new StringBuffer("startGraph");
		Iterator it = sortedURIs.iterator();
		if (it.hasNext()) {
			call.append(":");
		}
		while (it.hasNext()) {
			String uri = (String) it.next();
			call.append(" <");
			call.append(uri + ">");
		}
		return call.toString();
	}

	private String getURICall(String uri) {
		return "uri: <" + uri + ">";
	}

	private String getIDCall(String id) {
		return "id: \"" + id + "\"";
	}

	private String getPlainLiteralCall(String value, String lang) {
		String expectation = "plainLiteral: \"" + value + "\"";
		if (lang != null) {
			expectation += "@" + lang;
		}
		return expectation;
	}

	private String getTypedLiteralCall(String value, String datatypeURI) {
		return "typedLiteral: \"" + value + "\"^^<" + datatypeURI + ">";
	}

	private void assertNext(String actual) {
		Assert.assertFalse("no more expectations, but actual: " + actual,
				this.expectedCalls.isEmpty());
		if ("*".equals(this.expectedCalls.get(0))) {
			return;
		}
		Assert.assertEquals(this.expectedCalls.remove(0), actual);
	}
}
