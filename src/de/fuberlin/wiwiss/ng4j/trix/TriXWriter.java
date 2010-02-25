/*
 * $Id: TriXWriter.java,v 1.6 2010/02/25 14:28:22 hartig Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphSetWriter;

/**
 * Serializes a {@link NamedGraphSet} as a TriX file (see
 * <a href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56">TriX
 * specification</a>).
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriXWriter implements NamedGraphSetWriter {
	private String encoding = null;
	private Writer writer;

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSetWriter#write(de.fuberlin.wiwiss.ng4j.NamedGraphSet, java.io.Writer, java.lang.String)
	 */
	public void write(NamedGraphSet set, Writer out, String baseURI) {
		this.writer = new BufferedWriter(out);
		try {
			if (this.encoding != null) {
				write("<?xml version=\"1.0\" encoding=\"" + this.encoding + "\"?>\n");
			}
			write("<TriX xmlns=\"http://www.w3.org/2004/03/trix/trix-1/\">\n");
			Iterator<NamedGraph> it = set.listGraphs();
			while (it.hasNext()) {
				NamedGraph graph = it.next();
				writeGraph(graph);
			}
			write("</TriX>");
			this.writer.flush();
		} catch (IOException ioex) {
			throw new JenaException(ioex);
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSetWriter#write(de.fuberlin.wiwiss.ng4j.NamedGraphSet, java.io.OutputStream, java.lang.String)
	 */
	public void write(NamedGraphSet set, OutputStream out, String baseURI) {
		try {
			this.encoding = "utf-8";
			write(set, new OutputStreamWriter(out, "utf-8"), baseURI);
		} catch (UnsupportedEncodingException ueex) {
			throw new JenaException(ueex);
		}
	}

	private void writeGraph(NamedGraph graph) throws IOException {
		String graphName = graph.getGraphName().getURI();
		write("  <graph>\n");
		write("    <uri>" + escape(graphName) + "</uri>\n");
		ExtendedIterator it = graph.find(Node.ANY, Node.ANY, Node.ANY);
		while (it.hasNext()) {
			Triple triple = (Triple) it.next();
			writeTriple(triple);
		}
		write("  </graph>\n");
		
	}

	private void writeTriple(Triple triple) throws IOException {
		write("    <triple>\n");
		writeNode(triple.getSubject());
		writeNode(triple.getPredicate());
		writeNode(triple.getObject());
		write("    </triple>\n");
	}

	private void writeNode(Node n) throws IOException {
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
 *  (c) Copyright 2004 - 2010 Christian Bizer (chris@bizer.de)
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
