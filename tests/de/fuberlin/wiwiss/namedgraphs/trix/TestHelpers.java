/*
 * $Id: TestHelpers.java,v 1.1 2004/09/13 14:37:31 cyganiak Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Helper methods for unit tests.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TestHelpers {

	/**
	 * Reads a file into a String.
	 * @param fileName a file name relative to the location of this class
	 * @return the contents of the file in the system's default encoding
	 * @throws IOException
	 */
	public static String getFileContents(String fileName) throws IOException {
		Reader reader = new InputStreamReader(
						TestHelpers.class.getResourceAsStream(fileName));
		StringBuffer result = new StringBuffer();
		char[] chars = new char[1000];
		while (true) {
			int read = reader.read(chars);
			result.append(chars, 0, read);
			if (read < chars.length) {
				return result.toString();
			}
		}
	}
}
