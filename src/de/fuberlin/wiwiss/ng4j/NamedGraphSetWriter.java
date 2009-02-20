// $Id: NamedGraphSetWriter.java,v 1.4 2009/02/20 08:09:51 hartig Exp $
package de.fuberlin.wiwiss.ng4j;

import java.io.OutputStream;
import java.io.Writer;


/**
 * <p>Serializes a {@link NamedGraphSet} into a Writer or OutputStream.
 * An Implementation will provide support for a single serialization syntax,
 * such as TriX or TriG.</p>
 * 
 * <p>A NamedGraphSetWriter instance can only be used to write one file.
 * To write another file, a new instance must be created.</p>
 * 
 * <p>NamedGraphSetWriters are used through NamedGraphSet's <tt>write</tt>
 * methods.</p>
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface NamedGraphSetWriter {

	/**
	 * Serializes a NamedGraphSet into a Writer.
	 * @param set The NamedGraphSet to be serialized
	 * @param out The destination
	 * @param baseURI A base URI, or <tt>null</tt> if none is known or needed
	 */
	public abstract void write(NamedGraphSet set, Writer out, String baseURI);

	/**
	 * Serializes a NamedGraphSet into an OutputStream.
	 * @param set The NamedGraphSet to be serialized
	 * @param out The destination
	 * @param baseURI A base URI, or <tt>null</tt> if none is known or needed
	 */
	public abstract void write(NamedGraphSet set, OutputStream out, String baseURI);
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