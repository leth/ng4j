// $Id: DirectoryReader.java,v 1.1 2004/10/23 13:31:24 cyganiak Exp $

package de.fuberlin.wiwiss.ng4j.util;



import java.io.File;



import com.hp.hpl.jena.shared.JenaException;




import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;



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

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
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
