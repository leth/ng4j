// $Id: NamedGraphSetReader.java,v 1.5 2010/02/25 14:28:21 hartig Exp $
package de.fuberlin.wiwiss.ng4j;

import java.io.InputStream;
import java.io.Reader;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;

/**
 * <p>Reads a serialized set of Named Graphs from a Reader, InputStream, or URL
 * into a {@link NamedGraphSet}. An Implementation will provide support for
 * a single serialization syntax, such as TriX or TriG.</p>
 * 
 * <p>A NamedGraphSetReader instance can only be used to read one file.
 * To read another file, a new instance must be created.</p>
 * 
 * <p>NamedGraphSetReaders are used through NamedGraphSet's <tt>read</tt>
 * methods, or through a {@link GraphReaderService}.</p>
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface NamedGraphSetReader {

	/**
	 * Reads Named Graphs from a Reader into a NamedGraphSet. 
	 * If some of the graph names from the source are already used
	 * in the NamedGraphSet, then the statements from the old
	 * graphs will be replaced by those from the source.
	 * @param namedGraphSet Graphs read from the source will be stored
	 * 			into this NamedGraphSet
	 * @param source The source of the input serialization
	 * @param baseURI The URI from where the input was read
	 * @param defaultGraphName If a graph in the input has no name attached,
	 * 			then this will be used. When in doubt, use the baseURI.
	 */
	public abstract void read(NamedGraphSet namedGraphSet, Reader source,
			String baseURI, String defaultGraphName);

	/**
	 * Reads Named Graphs from an InputStream into a NamedGraphSet. 
	 * If some of the graph names from the source are already used
	 * in the NamedGraphSet, then the statements from the old
	 * graphs will be replaced by those from the source.
	 * @param namedGraphSet Graphs read from the source will be stored
	 * 			into this NamedGraphSet
	 * @param source The source of the input serialization
	 * @param baseURI The URI from where the input was read
	 * @param defaultGraphName If a graph in the input has no name attached,
	 * 			then this will be used. When in doubt, use the baseURI.
	 */
	public abstract void read(NamedGraphSet namedGraphSet, InputStream source,
			String baseURI, String defaultGraphName);
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