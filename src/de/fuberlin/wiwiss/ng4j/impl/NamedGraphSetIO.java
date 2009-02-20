// $Id: NamedGraphSetIO.java,v 1.6 2009/02/20 08:09:51 hartig Exp $
package de.fuberlin.wiwiss.ng4j.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.trig.TriGWriter;
import de.fuberlin.wiwiss.ng4j.trix.TriXWriter;

/**
 * <p>Abstract {@link NamedGraphSet} implementation providing implementations for
 * NamedGraphSet's various <tt>read</tt> and <tt>write</tt> methods.</p> 
 *
 * TODO: Factor out graph writing into a GraphWriterService, use that also in
 * NamedGraphModel 
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public abstract class NamedGraphSetIO implements NamedGraphSet {

	public void read(InputStream source, String lang, String baseURI) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceInputStream(source, baseURI);
		service.setLanguage(lang);
		service.readInto(this);
	}

	public void read(Reader source, String lang, String baseURI) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceReader(source, baseURI);
		service.setLanguage(lang);
		service.readInto(this);
	}

	public void read(String url, String lang) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceURL(url);
		service.setLanguage(lang);
		service.readInto(this);
	}

	public void write(OutputStream out, String lang, String baseURI) {
		if ("TRIX".equals(lang)) {
			new TriXWriter().write(this, out, baseURI);
		} else if ("TRIG".equals(lang)) {
			new TriGWriter().write(this, out, baseURI);
		} else {
			// can fail if no graph in set
			NamedGraph firstGraph = (NamedGraph) listGraphs().next();
			asJenaModel(firstGraph.getGraphName().toString()).write(out, lang, baseURI);
		}
	}

	public void write(Writer out, String lang, String baseURI) {
		if ("TRIX".equals(lang)) {
			new TriXWriter().write(this, out, baseURI);
		} else if ("TRIG".equals(lang)) {
			new TriGWriter().write(this, out, baseURI);
		} else {
			// can fail if no graph in set
			NamedGraph firstGraph = (NamedGraph) listGraphs().next();
			asJenaModel(firstGraph.getGraphName().toString()).write(out, lang, baseURI);
		}
	}
}

/*
 *  (c) Copyright 2004 - 2009 Christian Bizer (chris@bizer.de)
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