// $Id: NamedGraphSetIO.java,v 1.1 2004/11/02 02:00:25 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.trix.NamedGraphSetWriter;

/**
 * Abstract {@link NamedGraphSet} implementation providing implementations for
 * NamedGraphSet's various <tt>read</tt> and <tt>write</tt> methods. 
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public abstract class NamedGraphSetIO implements NamedGraphSet {

	public void read(InputStream source, String baseURI, String lang) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceInputStream(source, baseURI);
		service.setLanguage(lang);
		service.readInto(this);
	}

	public void read(Reader source, String baseURI, String lang) {
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

	public void write(OutputStream out, String lang) {
		// TODO: Ugly! Fix this
		if ("TRIX".equals(lang)) {
			new NamedGraphSetWriter().write(this, out);
		} else {
			// can fail if no graph in set
			NamedGraph firstGraph = (NamedGraph) listGraphs().next();
			asJenaModel(firstGraph.getGraphName().toString()).write(out, lang);
		}
	}

	public void write(Writer out, String lang) {
		// TODO: Ugly! Fix this
		if ("TRIX".equals(lang)) {
			new NamedGraphSetWriter().write(this, out);
		} else {
			// can fail if no graph in set
			NamedGraph firstGraph = (NamedGraph) listGraphs().next();
			asJenaModel(firstGraph.getGraphName().toString()).write(out, lang);
		}
	}

}
