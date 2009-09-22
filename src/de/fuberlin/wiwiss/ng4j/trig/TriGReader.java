// $Id: TriGReader.java,v 1.5 2009/09/22 16:24:51 timp Exp $
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
			// UTF-8 is always supported - so this should never happen
		  throw new JenaException(ex);
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