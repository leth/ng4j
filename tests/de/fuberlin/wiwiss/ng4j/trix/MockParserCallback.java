/*
 * $Id: MockParserCallback.java,v 1.2 2009/02/11 01:55:26 jenpc Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.fuberlin.wiwiss.ng4j.trix.ParserCallback;

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
	private List<String> expectedCalls = new LinkedList<String>();

	public void addExpectation(String expectation) {
		this.expectedCalls.add(expectation);
	}

	public void addWildcardExpectation() {
		this.expectedCalls.add("*");
	}

	public void expectStartGraph() {
		expectStartGraph(new ArrayList<String>(0));
	}

	public void expectStartGraph(Collection<String> uris) {
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

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#startGraph(java.util.List)
	 */
	public void startGraph(List<String> uris) {
		assertNext(getStartGraphCall(uris));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#endGraph()
	 */
	public void endGraph() {
		assertNext("endGraph");
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectURI(java.lang.String)
	 */
	public void subjectURI(String uri) {
		assertNext(getURICall(uri));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectBNode(java.lang.String)
	 */
	public void subjectBNode(String id) {
		assertNext(getIDCall(id));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectPlainLiteral(java.lang.String, java.lang.String)
	 */
	public void subjectPlainLiteral(String value, String lang) {
		assertNext(getPlainLiteralCall(value, lang));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectTypedLiteral(java.lang.String, java.lang.String)
	 */
	public void subjectTypedLiteral(String value, String datatypeURI) {
		assertNext(getTypedLiteralCall(value, datatypeURI));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#predicate(java.lang.String)
	 */
	public void predicate(String uri) {
		assertNext(getURICall(uri));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectURI(java.lang.String)
	 */
	public void objectURI(String uri) {
		assertNext(getURICall(uri));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectBNode(java.lang.String)
	 */
	public void objectBNode(String id) {
		assertNext(getIDCall(id));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectPlainLiteral(java.lang.String, java.lang.String)
	 */
	public void objectPlainLiteral(String value, String lang) {
		assertNext(getPlainLiteralCall(value, lang));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectTypedLiteral(java.lang.String, java.lang.String)
	 */
	public void objectTypedLiteral(String value, String datatypeURI) {
		assertNext(getTypedLiteralCall(value, datatypeURI));
	}

	private String getStartGraphCall(Collection<String> uris) {
		List<String> sortedURIs = new ArrayList<String>(uris);
		Collections.sort(sortedURIs);
		StringBuffer call = new StringBuffer("startGraph");
		Iterator<String> it = sortedURIs.iterator();
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

/*
 *  (c) Copyright 2004, 2005, 2006, 2007, 2008 Christian Bizer (chris@bizer.de)
 *   All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
