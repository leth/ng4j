package de.fuberlin.wiwiss.ng4j.semwebclient;

import com.hp.hpl.jena.graph.Triple;

public class CommandLineClient {

	/**
	 * Sets a SPARQL query to be executed against the Semantic Web.
	 */
	public void setSPARQLQuery(String query) {
		
	}

	/**
	 * A SPARQL query is loaded from a file and executed against the Semantic Web.
	 */
	public void setSPARQLFile(String filename) {
		
	}
	
	/**
	 * Sets a triple pattern to be used as a query against the Semantic Web. 
	 * @param triple An RDF triple, may contain Node.ANY wildcards
	 */
	public void setFindTriple(Triple triple) {
		
	}
	
	/**
	 * Sets the maximal number of iterations of the retrieval algorithm. The default value is 3.
	 */
	public void setMaxSteps(int maxSteps) {
		
	}
	
	/**
	 * Sets the timeout of the query in milliseconds. The default value is 10000.
	 */
	public void setTimeout(long timeoutMilliseconds) {
		
	}

	/**
	 * Sets the maximal number of parallel threads for retrieving URIs. The default is 10.
	 */
	public void setMaxThreads(int maxThreads) {
		
	}
	
	/**
	 * Loads a file from the Web before the query is executed.
	 */
	public void addSourceURI(String uri) {	// Can be called multiple times

	}
	
	/**
	 * Loads a set of graphs from a file before the query is executed.
	 * @param file Filename of the graph set file
	 * @param format "TRIG" or "TRIX"
	 */
	public void setLoadGraphSet(String file, String format) {	// format is "TRIG" or "TRIX"
		
	}
	
	/**
	 * Saves all graphs that have been retrieved into a file after query execution has finished.
	 * @param file Filename of the destination file
	 * @param format "TRIG" or "TRIX"
	 */
	public void setWriteGraphSet(String file, String format) {	// format is "TRIG" or "TRIX"
		
	}
	
	/**
	 * Output a list of all successfully retrieved URIs?
	 */
	public void setOutputRetrievedURIs(boolean outputRetrievedURIs) {
		
	}
	
	/**
	 * Output a list of all URIs that could not be retrieved?
	 */
	public void setOutputFailedURIs(boolean outputFailedURIs) {
		
	}
	
	/**
	 * Executes the query specified using the other options.
	 * @throws Exception Indicates an error
	 */
	public void run() throws Exception {
		
	}
}
