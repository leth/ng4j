// $Id: DirectoryReader.java,v 1.1 2004/09/13 14:37:29 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs.util;

import java.io.File;

import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.namedgraphs.NamedGraphSet;
import de.fuberlin.wiwiss.namedgraphs.impl.GraphReaderService;

/**
 * Helper class for reading several files from a directory into
 * a NamedGraphSet. The "file:" URIs will be used as graph names.
 * <p>
 * TODO: Write tests for DirectoryReader
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class DirectoryReader {

	/**
	 * Reads files from a directory into a NamedGraphSet. For each file,
	 * a new graph will be created in the graph set. Its name is
	 * the "file:" URI of the file.
	 * 
	 * @param set The NamedGraphSet to which the contents of the files will be added
	 * @param directory A directory
	 * @param lang "RDF/XML", "N-TRIPLE", "N3", "TRIX" or <tt>null</tt>
	 * 		for file extension based auto detection
	 */
	public void read(NamedGraphSet set, String directory, String lang) {
		File dir = new File(directory);
		if (!dir.isDirectory()) {
			throw new JenaException("No directory: " + directory);
		}
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			GraphReaderService service = new GraphReaderService();
			service.setSourceFile(files[i]);
			service.setLanguage(lang);
			service.readInto(set);
		}
	}
}
