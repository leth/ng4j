package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.util.Utils;

public class CommandLineQuery {

	private SemanticWebClient client = null;

	private String sparqlQuery = null;

	private String sparqlQueryFromFile = null;

	private Triple queryTriple = null;

	private boolean outputRetrievedURIs = false;

	private boolean outputFailedURIs = false;

	private boolean outputRedirectedURIs = false;

	private List graphsToAdd = new ArrayList();

	private String writeGraphSetDestination = null;

	private String writeGraphSetFormat = null;

	private String loadGraphSetSource = null;

	private String loadGraphSetFormat = null;

	private int maxsteps = -1;

	private long timeout = -1;

	private int maxthreads = -1;
	
	private int maxfilesize = -1;

        private boolean enablegrddl = false;

	private boolean enableSindiceSearch = false;

	private boolean verbose = false;


	public CommandLineQuery() {
		this.client = new SemanticWebClient();
	}

	/**
	 * Sets a SPARQL query to be executed against the Semantic Web.
	 * 
	 * @param query
	 */
	public void setSPARQLQuery(String query) {
		this.sparqlQuery = query;

	}

	/**
	 * A SPARQL query is loaded from a file and executed against the Semantic
	 * Web.
	 * 
	 * @param filename
	 */
	public void setSPARQLFile(String filename) {
		this.sparqlQueryFromFile = filename;
	}

	/**
	 * Sets a triple pattern to be used as a query against the Semantic Web.
	 * 
	 * @param triple
	 *            An RDF triple, may contain Node.ANY wildcards
	 */
	public void setFindTriple(Triple triple) {
		this.queryTriple = triple;
	}

	/**
	 * Sets the maximal number of iterations of the retrieval algorithm. The
	 * default value is 3.
	 * 
	 * @param maxSteps
	 */
	public void setMaxSteps(int maxSteps) {
		this.maxsteps = maxSteps;
	}
	
	/**
	 * Sets the maximal number of iterations of the retrieval algorithm. The
	 * default value is 3.
	 * 
	 * @param maxSteps
	 */
	public void setMaxFilesize(int maxFilesize) {
		this.maxfilesize = maxFilesize;
	}

	/**
	 * Sets the timeout of the query in milliseconds. The default value is
	 * 60000.
	 * 
	 * @param timeoutMilliseconds
	 */
	public void setTimeout(long timeoutMilliseconds) {
		this.timeout = timeoutMilliseconds;
	}

	/**
	 * Sets the maximal number of parallel threads for retrieving URIs. The
	 * default is 10.
	 * 
	 * @param maxThreads
	 */
	public void setMaxThreads(int maxThreads) {
		this.maxthreads = maxThreads;
	}

	/**
	 * Enables the GRDDL transformations. The
	 * default is false.
	 * 
	 * @param enableGrddl
	 */
	public void setEnableGrddl(boolean enableGrddl) {
		this.enablegrddl = enableGrddl;
	}

	/**
	 * Enables a Sindice-based URI search during query execution.
	 * The default is false.
	 * 
	 * @param enableSindiceSearch
	 */
	public void setEnableSindiceSearch ( boolean enableSindiceSearch ) {
		this.enableSindiceSearch = enableSindiceSearch;
	}

	/**
	 * Enables verbose output.
	 * The default is false.
	 * 
	 * @param verbose
	 */
	public void setVerbose ( boolean verbose ) {
		this.verbose = verbose;
	}

	/**
	 * Loads a file from the Web before the query is executed.
	 * 
	 * @param uri
	 */
	public void addSourceURI(String uri) { // Can be called multiple times
		this.graphsToAdd.add(uri);
	}

	/**
	 * Loads a set of graphs from a file before the query is executed.
	 * 
	 * @param file
	 *            Filename of the graph set file
	 * @param format
	 *            "TRIG" or "TRIX"
	 */
	public void setLoadGraphSet(String file, String format) { // format is
																// "TRIG" or
																// "TRIX"
		this.loadGraphSetSource = file;
		this.loadGraphSetFormat = format;
	}

	/**
	 * Saves all graphs that have been retrieved into a file after query
	 * execution has finished.
	 * 
	 * @param file
	 *            Filename of the destination file
	 * @param format
	 *            "TRIG" or "TRIX"
	 */
	public void setWriteGraphSet(String file, String format) { // format is
																// "TRIG" or
																// "TRIX"
		this.writeGraphSetDestination = file;
		this.writeGraphSetFormat = format;
	}

	/**
	 * Output a list of all successfully retrieved URIs?
	 * 
	 * @param outputRetrievedURIs
	 */
	public void setOutputRetrievedURIs(boolean outputRetrievedURIs) {
		this.outputRetrievedURIs = outputRetrievedURIs;
	}

	/**
	 * Output a list of all URIs that could not be retrieved?
	 * 
	 * @param outputFailedURIs
	 */
	public void setOutputFailedURIs(boolean outputFailedURIs) {
		this.outputFailedURIs = outputFailedURIs;
	}

	/**
	 * Output a mapping of all redirected URIs?
	 * 
	 * @param outputRedirectedURIs
	 */
	public void setOutputRedirectedURIs(boolean outputRedirectedURIs) {
		this.outputRedirectedURIs = outputRedirectedURIs;
	}

	/**
	 * Executes the query specified using the other options.
	 * 
	 * @throws Exception
	 *             Indicates an error
	 */
	public void run() throws Exception {
		executeConfigure();
		executeLoadNamendGraphSet();
		executeWriteIntro();
		executeAddGraphs();
		executeSparqlFromFile();
		executeSparqlQuery();
		executeFindQuery();
		executeOutput();
		executeWriteGraphset();
		this.client.close();
	}

	private void executeAddGraphs() throws MalformedURLException, IOException {
		// TODO same code as in dereferencing thread (accept) ?
		Iterator it = this.graphsToAdd.iterator();
		while (it.hasNext()) {
			String graphuri = (String) it.next();
			URL url = null;
			HttpURLConnection connection = null;
			url = new URL(graphuri);
			if (url != null) {
				connection = (HttpURLConnection) url.openConnection();
			connection
			.addRequestProperty(
					"accept",
					"application/rdf+xml ; q=1, "
							+ "text/xml ; q=0.6 , text/rdf+n3 ; q=0.9 , "
							+ "application/octet-stream ; q=0.5 , "
							+ "application/xml q=0.5, application/rss+xml ; q=0.5 , "
							+ "text/plain ; q=0.5, application/x-turtle ; q=0.5, "
							+ "application/x-trig ; q=0.5");
				this.client.read(connection.getInputStream(),
						guessLanguage(connection), url.toString());
				System.out.println("Successfully added: " + graphuri);
			}
		}
	}

	private void executeLoadNamendGraphSet() {
		if (this.loadGraphSetSource != null) {
			this.client.read(this.loadGraphSetSource, this.loadGraphSetFormat);
			System.out.println("Successfully loaded: "
					+ this.loadGraphSetSource);
		}
	}

	private void executeSparqlFromFile() throws FileNotFoundException,
			IOException {
		if (this.sparqlQueryFromFile != null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(this.sparqlQueryFromFile)));
			String line;
			this.sparqlQuery = "";
			while ((line = in.readLine()) != null) {
				this.sparqlQuery += line + "\n";
			}
			in.close();
		}
	}

	private void executeSparqlQuery() {
		if (this.sparqlQuery != null) {
			System.out.println("\nExecuting SPARQL query: \n");
			System.out.println(this.sparqlQuery);
			Query query;
			query = QueryFactory.create(this.sparqlQuery);
			QueryExecution qe = QueryExecutionFactory.create(query, this.client
					.asJenaModel("default"));
			ResultSet results = qe.execSelect();
			ResultSetFormatter.out(System.out, results, query);
		}

	}

	private void executeFindQuery() {
		if (this.queryTriple != null) {
			System.out.println("\nExecuting find query: \n");
			System.out.println(this.queryTriple);
			System.out.println("--------------------------------");
			System.out.println("Query Results :\n");
			SemWebIterator iter = this.client.find(this.queryTriple);
			if ( verbose ) {
				int i = 0;
				while (iter.hasNext()) {
					SemWebTriple triple = (SemWebTriple) iter.next();
					System.out.println( triple.toString() +
					                    " (result#: " + String.valueOf(++i) +
					                    " succ.URIs: " + String.valueOf(client.successfullyDereferencedURIs().size()) +
					                    " unsucc.URIs: " + String.valueOf(client.unsuccessfullyDereferencedURIs().size()) + ")" );
				}
			}
			else  {
				while (iter.hasNext()) {
					SemWebTriple triple = (SemWebTriple) iter.next();
					System.out.println(triple.toString());
				}
			}
			System.out.println("--------------------------------");

		}
	}

	private void executeOutput() {
		if (this.outputRetrievedURIs) {
			System.out.println("Successfully dereferenced URIs: \n");
			Iterator it = this.client.successfullyDereferencedURIs().iterator();
			while (it.hasNext()) {
				String uri = (String) it.next();
				System.out.println(uri);
			}
			System.out.println("--------------------------------");
		}
		if (this.outputRedirectedURIs) {
			System.out.println("Redirected URIs: \n");
			Iterator it = this.client.redirectedURIs().iterator();
			while (it.hasNext()) {
				String uri = (String) it.next();
				String redirect = this.client.getRedirectURI(uri);
				System.out.println( uri + " -> " + redirect );
			}
			System.out.println("--------------------------------");
		}
		if (this.outputFailedURIs) {
			System.out.println("Unsuccessfully dereferenced URIs: \n");

			Map reasons = new HashMap ();
			int count = 0;

			Iterator it = this.client.unsuccessfullyDereferencedURIs().iterator();
			while (it.hasNext()) {
				String uri = (String) it.next();
				if ( verbose ) {
					++count;
					Exception reason = client.getReasonForFailedDereferencing( uri );
					Class reasonType = reason.getClass();
					if ( ! reasons.containsKey(reasonType) ) {
						reasons.put( reasonType, Integer.valueOf(1) );
					}
					else {
						int i = ((Integer) reasons.get(reasonType)).intValue() + 1;
						reasons.put( reasonType, Integer.valueOf(i) );
					}

					System.out.println( uri + " (" + Utils.classShortName(reasonType) + ": " + reason.getMessage() + ")" );
				}
				else {
					System.out.println(uri);
				}
			}

			if ( verbose && (count > 0) ) {
				System.out.println(" Reason statistics: " + String.valueOf(count) + " unsuccessfully dereferenced URIs");
				Iterator itR = reasons.entrySet().iterator();
				while ( itR.hasNext() ) {
					Map.Entry r = (Map.Entry) itR.next();
					int percent = ( ((Integer) r.getValue()).intValue() * 100 ) / count;
					System.out.println(" - " + String.valueOf(percent) + "% " + Utils.classShortName((Class) r.getKey()) );
				}
			}

			System.out.println("--------------------------------");
		}
	}

	private void executeWriteGraphset() throws FileNotFoundException,
			IOException {
		if (this.writeGraphSetDestination != null) {
			FileOutputStream out = new FileOutputStream(
					this.writeGraphSetDestination);
			this.client.write(out, this.writeGraphSetFormat, null);
			System.out.println("Graphset written to: "
					+ this.writeGraphSetDestination);
			out.close();
		}
	}

	private String guessLanguage(HttpURLConnection con) {
		String type = con.getContentType();
		if (type == null)
			return null;

		if (type.startsWith("application/rdf+xml")
				|| type.startsWith("text/xml")
				|| type.startsWith("application/xml")
				|| type.startsWith("application/rss+xml")
				|| type.startsWith("text/plain"))
			return "RDF/XML";
		if (type.startsWith("application/n3")
				|| type.startsWith("application/x-turtle")
				|| type.startsWith("text/rdf+n3"))
			return "N3";
		if (type.startsWith("application/xhtml")
				|| type.startsWith("text/html"))
			return "HTML";

		return type;
	}

	private void executeConfigure() {
		if (this.maxsteps != -1)
			this.client.setConfig("maxsteps", Integer.toString(this.maxsteps));
		if (this.timeout != -1)
			this.client.setConfig("timeout", Long.toString(this.timeout));
		if (this.maxthreads != -1)
			this.client.setConfig("maxthreads", Long.toString(this.maxthreads));
		if (this.maxfilesize != -1)
			this.client.setConfig("maxfilesize", Integer.toString(this.maxfilesize));
		if (this.enablegrddl != false)
		    this.client.setConfig("enablegrddl", Boolean.toString(this.enablegrddl));

		client.setConfig( SemanticWebClient.CONFIG_ENABLE_SINDICE, Boolean.toString(enableSindiceSearch) );
	}

	private void executeWriteIntro() {
		System.out.println("--------------------------------");
		System.out.println("Semantic Web Client Library V0.2");
		System.out.println("--------------------------------");
	}
}
