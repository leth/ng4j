/*
 * $Id: TriGWriter.java,v 1.1 2004/11/26 01:50:32 cyganiak Exp $
 */
package de.fuberlin.wiwiss.ng4j.trig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphSetWriter;

/**
 * Serializes a {@link NamedGraphSet} as a TriG file (see
 * <a href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriG/">TriG
 * specification</a>).
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriGWriter implements NamedGraphSetWriter {
	private Writer writer;

	public void write(NamedGraphSet set, Writer out, String baseURI) {
		this.writer = new BufferedWriter(out);
		try {
			Iterator it = set.listGraphs();
			while (it.hasNext()) {
//				NamedGraph graph = (NamedGraph) it.next();
// TODO @@@				writeGraph(graph);
			}
			this.writer.flush();
		} catch (IOException ioex) {
			throw new JenaException(ioex);
		}
	}

	public void write(NamedGraphSet set, OutputStream out, String baseURI) {
		try {
			write(set, new OutputStreamWriter(out, "utf-8"), baseURI);
		} catch (UnsupportedEncodingException ueex) {
			// UTF-8 is always supported
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