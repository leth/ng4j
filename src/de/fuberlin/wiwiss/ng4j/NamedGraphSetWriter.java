// $Id: NamedGraphSetWriter.java,v 1.1 2004/11/26 01:50:30 cyganiak Exp $
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