// $Id: TriGReader.java,v 1.1 2004/11/25 22:14:38 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphSetReader;

/**
 * Reads TriG files (see
 * <a href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriG/">TriG
 * specification</a>) into {@link NamedGraphSet}s.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriGReader implements NamedGraphSetReader {

	public void read(NamedGraphSet namedGraphSet, Reader source,
			String baseURI, String defaultGraphName) {
		try {
			new TriGParser(source, new NamedGraphSetPopulator(
					namedGraphSet, baseURI, defaultGraphName)).parse();
		} catch (TokenStreamException ex) {
			throw new JenaException(ex);
		} catch (RecognitionException ex) {
			throw new JenaException(ex);
		}
	}

	public void read(NamedGraphSet namedGraphSet, InputStream source,
			String baseURI, String defaultGraphName) {
		try {
			read(namedGraphSet, new InputStreamReader(source, "UTF-8"), baseURI,
					defaultGraphName);
		} catch (UnsupportedEncodingException ex) {
			// UTF-8 is always supported
		}
	}
}
