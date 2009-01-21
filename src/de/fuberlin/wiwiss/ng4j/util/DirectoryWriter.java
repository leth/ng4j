// $Id: DirectoryWriter.java,v 1.3 2009/01/21 18:10:53 jenpc Exp $

package de.fuberlin.wiwiss.ng4j.util;



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





import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.trix.JenaRDFWriter;



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

		Iterator<NamedGraph> it = set.listGraphs();

		while (it.hasNext()) {

			NamedGraph graph = it.next();

			try {

				String name = new URI(graph.getGraphName().getURI()).toString();

				System.out.println(name);

				System.out.println(dir.toURI().toString());

				if (!name.startsWith(dir.toURI().toString())) {

					continue;

				}

				RDFWriter writer = factory.getWriter(lang);

				URI fileURI = new URI(graph.getGraphName().getURI());

				writer.write(new ModelCom(graph), new FileOutputStream(new File(fileURI)), graph.getGraphName().getURI());

			} catch (URISyntaxException ex) {

				throw new JenaException(ex);

			} catch (FileNotFoundException ex) {

				throw new JenaException(ex);

			}

		}

	}

}

/*
 *  (c) Copyright 2004, 2005, 2006, 2007, 2008 Christian Bizer (chris@bizer.de)
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