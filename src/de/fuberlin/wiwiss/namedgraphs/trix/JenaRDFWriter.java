/*
 * $Id: JenaRDFWriter.java,v 1.2 2004/09/15 08:22:00 bizer Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;

/**
 * Simple RDFWriter that adds support for the TriX syntax (see
 * <a href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56">TriX
 * specification</a>) to the Jena framework. It writes all statements
 * from a Jena model into a single trix:graph element, ignoring
 * the named graph features of TriX.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class JenaRDFWriter implements RDFWriter {
	private String encoding = null;
	private Writer writer;

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFWriter#write(com.hp.hpl.jena.rdf.model.Model, java.io.Writer, java.lang.String)
	 */
	public void write(Model model, Writer out, String base) {
		this.writer = new BufferedWriter(out);
		try {
			if (this.encoding != null) {
				write("<?xml version=\"1.0\" encoding=\"" + this.encoding + "\"?>\n");
			}
			write("<TriX xmlns=\"http://www.w3.org/2004/03/trix/trix-1/\">\n");
			write("  <graph>\n");
			StmtIterator it = model.listStatements();
			while (it.hasNext()) {
				Statement stmt = (Statement) it.next();
				writeTriple(stmt);
			}
			write("  </graph>\n");
			write("</TriX>");
			this.writer.flush();
		} catch (IOException ioex) {
			throw new JenaException(ioex);
		}
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFWriter#write(com.hp.hpl.jena.rdf.model.Model, java.io.OutputStream, java.lang.String)
	 */
	public void write(Model model, OutputStream out, String base) {
		try {
			this.encoding = "utf-8";
			write(model, new OutputStreamWriter(out, "utf-8"), base);
		} catch (UnsupportedEncodingException ueex) {
			throw new JenaException(ueex);
		}
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFWriter#setProperty(java.lang.String, java.lang.Object)
	 */
	public Object setProperty(String propName, Object propValue) {
		// TODO: Figure out what RDFWriter.setProperty is good for
		return null;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFWriter#setErrorHandler(com.hp.hpl.jena.rdf.model.RDFErrorHandler)
	 */
	public RDFErrorHandler setErrorHandler(RDFErrorHandler errHandler) {
		// TODO: Figure out what RDFWriter.setErrorHandler is good for
		return null;
	}
	
	private void writeTriple(Statement stmt) throws IOException {
		write("    <triple>\n");
		writeNode(stmt.getSubject());
		writeNode(stmt.getPredicate());
		writeNode(stmt.getObject());
		write("    </triple>\n");
	}

	private void writeNode(RDFNode node) throws IOException {
		Node n = node.asNode();
		if (n.isURI()) {
			write("      <uri>" + escape(n.getURI()) + "</uri>\n");
			return;
		}
		if (n.isBlank()) {
			write("      <id>" + escape(n.getBlankNodeId().toString()) + "</id>\n");
			return;
		}
		if (!n.isLiteral()) {
			throw new JenaException("Don't know how to serialize node " + n);
		}
		if (n.getLiteral().getDatatypeURI() != null) {
			write("      <typedLiteral datatype=\"" +
					escape(n.getLiteral().getDatatypeURI()) + "\">" +
					escape(n.getLiteral().getLexicalForm()) + "</typedLiteral>\n");
			return;
		}
		if (n.getLiteral().language() == null || "".equals(n.getLiteral().language())) {
			write("      <plainLiteral>" + escape(n.getLiteral().getLexicalForm()) +
					"</plainLiteral>\n");
			return;
		}
		write("      <plainLiteral xml:lang=\"" + n.getLiteral().language() +
				"\">" + escape(n.getLiteral().getLexicalForm()) + "</plainLiteral>\n");
	}

	private void write(String output) throws IOException {
		this.writer.write(output);
	}
	
	private String escape(String value) {
		StringBuffer buffer = new StringBuffer(value);
		replaceAll(buffer, "&", "&amp;");
		replaceAll(buffer, "\"", "&quot;");
		replaceAll(buffer, "<", "&lt;");
		return buffer.toString();
	}
	
	private void replaceAll(StringBuffer buffer, String search, String replace) {
		int index = buffer.indexOf(search);
		while (index >= 0) {
			buffer.replace(index, index + search.length(), replace);
			index = buffer.indexOf(search, index + search.length());
		}
	}
}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
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
