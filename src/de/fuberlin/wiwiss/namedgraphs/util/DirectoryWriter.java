// $Id: DirectoryWriter.java,v 1.1 2004/09/13 14:37:29 cyganiak Exp $
package de.fuberlin.wiwiss.namedgraphs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.rdf.model.impl.RDFWriterFImpl;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.namedgraphs.NamedGraph;
import de.fuberlin.wiwiss.namedgraphs.NamedGraphSet;
import de.fuberlin.wiwiss.namedgraphs.trix.JenaRDFWriter;

/**
 * Helper class for writing a {@link NamedGraphSet} as a directory of
 * files. This works only with graph names that are "file:" URIs in that
 * directory. All other graphs will be ignored. This class is most
 * useful for writing to disk NamedGraphSets that have been read
 * by a {@link DirectoryReader}.
 * <p>
 * TODO: Write tests for DirectoryReader
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class DirectoryWriter {

	/**
	 * Writes graphs from a NamedGraphSets as files into a directory.
	 * Only graphs named with "file:" URIs matching the directory will
	 * be written. Other graphs will be ignored.
	 * 
	 * @param set The NamedGraphSet to be written
	 * @param directory A directory
	 * @param lang "RDF/XML", "N-TRIPLE", "N3" or "TRIX"
	 */
	public void write(NamedGraphSet set, String directory, String lang) {
		File dir = new File(directory);
		if (!dir.isDirectory()) {
			throw new JenaException("No directory: " + directory);
		}
		RDFWriterFImpl factory = new RDFWriterFImpl();
		factory.setWriterClassName("TRIX", JenaRDFWriter.class.getName());
		Iterator it = set.listGraphs();
		while (it.hasNext()) {
			NamedGraph graph = (NamedGraph) it.next();
			try {
				String name = new URI(graph.getGraphName().getURI()).toString();
				if (!name.startsWith(dir.toURI().toString())) {
					continue;
				}
				RDFWriter writer = factory.getWriter(lang);
				writer.write(new ModelCom(graph), new FileOutputStream(new File(graph.getGraphName().getURI())), graph.getGraphName().getURI());
			} catch (URISyntaxException ex) {
				throw new JenaException(ex);
			} catch (FileNotFoundException ex) {
				throw new JenaException(ex);
			}
		}
	}
}