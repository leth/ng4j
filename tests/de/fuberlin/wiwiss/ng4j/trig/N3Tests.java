// $Id: N3Tests.java,v 1.2 2004/11/25 22:14:38 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Runs some of the test cases included with Jena's N3 parser. Reads N3 test
 * files and N-Triples result files from a directory, parses both, and checks
 * if they are isomorphic. Fails and prints both to System.out if not.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class N3Tests extends TestSuite {
	private static final String dirName = "./etc/N3-tests";
	private static final String base = "file:///base/";

	public N3Tests() {
		File dir = new File(N3Tests.dirName);
		File[] testCases = dir.listFiles(new FilenameFilter() {
			public boolean accept(File f, String name) {
				return name.startsWith("rdf-test-") && name.endsWith(".n3");
			}
		});
		for (int i = 0; i < testCases.length; i++) {
			try {
				String testName = testCases[i].getCanonicalPath();
				String resultName = toResultName(testName);
				addTest(new N3Test(testName, resultName, base + testCases[i].getName()));
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	public static TestSuite suite() {
		return new N3Tests();
	}
	
	private String toResultName(String testName) {
		return testName.replaceAll("-test-", "-result-").replaceAll(".n3", ".nt");
	}

	private class N3Test extends TestCase {
		private String testFile;
		private String resultFile;
		private String baseURI;

		public N3Test(String testName) {
			super(testName);
		}

		public N3Test(String testFile, String resultFile, String baseURI) {
			super(testFile);
			this.testFile = testFile;
			this.resultFile = resultFile;
			this.baseURI = baseURI;
		}
		
		protected void runTest() throws Throwable {
			Graph test = readTestGraph();
			Graph result = readResultGraph();
			if (!test.isIsomorphicWith(result)) {
				System.out.println("=== " + this.testFile + " ===");
				System.out.println("--- Expected ---");
				System.out.println(result);
				System.out.println("--- Actual -----");
				System.out.println(test);
				fail();
			}
		}
		
		private Graph readTestGraph() throws Exception {
			Reader in = new InputStreamReader(new FileInputStream(this.testFile), "UTF-8");
			NamedGraphSet ngs = new NamedGraphSetImpl();
			NamedGraphSetPopulator handler = new NamedGraphSetPopulator(
					ngs, this.baseURI, this.baseURI);
			new TriGParser(in, handler).parse();
			return ngs.getGraph(this.baseURI);
		}
		
		private Graph readResultGraph() throws Exception {
			Model m = ModelFactory.createDefaultModel();
			m.read(new FileReader(this.resultFile), this.baseURI, "N-TRIPLE");
			return m.getGraph();
		}
	}
}
