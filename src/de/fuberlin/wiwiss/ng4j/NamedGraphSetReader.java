// $Id: NamedGraphSetReader.java,v 1.1 2004/11/25 22:14:39 cyganiak Exp $
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