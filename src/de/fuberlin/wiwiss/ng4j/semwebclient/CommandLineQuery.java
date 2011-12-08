package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sparql.resultset.ResultsFormat;
import com.hp.hpl.jena.sparql.util.Utils;

public class CommandLineQuery {

	private SemanticWebClient client = null;

	private String sparqlQuery = null;

	private String sparqlQueryFromFile = null;

	private Triple queryTriple = null;

	private boolean outputRetrievedURIs = false;

	private boolean outputFailedURIs = false;

	private boolean outputRedirectedURIs = false;

	private List<String> graphsToAdd = new ArrayList<String> ();

	private String writeGraphSetDestination = null;

	private String writeGraphSetFormat = null;

	private String loadGraphSetSource = null;

	private String loadGraphSetFormat = null;

	private int maxsteps = -1;

	private long timeout = -1;

	private int maxthreads = -1;
	
	private int maxfilesize = -1;

	private boolean enableRDFa = true;

	private boolean enableSindiceSearch = false;

	private ResultsFormat resultFormat = null;

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
	 * Enables RDFa parsing. The default is true.
	 * 
	 * @param enableRDFa
	 */
	public void setEnableRDFa(boolean enableRDFa) {
		this.enableRDFa = enableRDFa;
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
	 * Enables a Sindice-based URI search during query execution.
	 * The default is false.
	 */
	public void setResultFormat ( String resFmtStr ) {
		String resFmtStrUC = resFmtStr.toUpperCase();
		if ( resFmtStrUC.equals("TXT") ) {
			resultFormat = ResultsFormat.FMT_TEXT;
		}
		else if ( resFmtStrUC.equals("XML") ) {
			resultFormat = ResultsFormat.FMT_RS_XML;
		}
		else if ( resFmtStrUC.equals("JSON") ) {
			resultFormat = ResultsFormat.FMT_RS_JSON;
		}
		else if ( resFmtStrUC.equals("RDF/XML") ) {
			resultFormat = ResultsFormat.FMT_RDF_XML;
		}
		else if ( resFmtStrUC.equals("N-TRIPLE") ) {
			resultFormat = ResultsFormat.FMT_RDF_NT;
		}
		else if ( resFmtStrUC.equals("TURTLE") ) {
			resultFormat = ResultsFormat.FMT_RDF_TURTLE;
		}
		else if ( resFmtStrUC.equals("N3") ) {
			resultFormat = ResultsFormat.FMT_RDF_N3;
		}
		else {
			throw new IllegalArgumentException( "Unsupported result format specified. For SELECT and ASK queries use TXT, XML, or JSON. For CONSTRUCT or DESCRIBE queries use RDF/XML, N-TRIPLE, TURTLE, or N3." );
		}
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
		if ( verbose ) {
			executeWriteIntro();
		}
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
		Iterator<String> it = this.graphsToAdd.iterator();
		while (it.hasNext()) {
			String graphuri = it.next();
			URL url = null;
			HttpURLConnection connection = null;
			url = new URL(graphuri);
			if ( url != null && url.getProtocol().equals("file") ) {
				client.read( url.openStream(), FileUtils.guessLang(url.toString()), url.toString() );
				System.out.println( "Successfully added: " + graphuri );
			}
			else if (url != null) {
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
			Query query;
			query = QueryFactory.create(this.sparqlQuery);
			if (    verbose
			     && ( query.isSelectType() || query.isAskType() )
			     && ( (resultFormat == null) || resultFormat.equals(ResultsFormat.FMT_TEXT) ) ) {
				System.out.println("\nExecuting SPARQL query: \n");
				System.out.println(this.sparqlQuery);
			}
			QueryExecution qe = QueryExecutionFactory.create(query, this.client
					.asJenaModel("default"));
			OutputStream out = System.out;
			if ( query.isSelectType() ) {
				ResultSet results = qe.execSelect();
				ResultsFormat resFmt = ( resultFormat == null ) ? ResultsFormat.FMT_TEXT : resultFormat;
				ResultSetFormatter.output( out, results, resFmt );
			}
			else if ( query.isAskType() ) {
				boolean result = qe.execAsk();
				if ( (resultFormat == null) || resultFormat.equals(ResultsFormat.FMT_TEXT) ) {
					ResultSetFormatter.out( out, result );
				}
				else if ( resultFormat.equals(ResultsFormat.FMT_RS_JSON) ) {
					ResultSetFormatter.outputAsJSON( out, result );
				}
				else if ( resultFormat.equals(ResultsFormat.FMT_RS_XML) ) {
					ResultSetFormatter.outputAsXML( out, result );
				}
				else {
					throw new IllegalArgumentException( "Unsupported result format specified. For ASK queries use TXT, XML, or JSON." );
				}
			}
			else {
				Model result;
				if ( query.isDescribeType() ) {
					result = qe.execDescribe();
				}
				else {
					result = qe.execConstruct();
				}
				String lang;
				if ( (resultFormat==null) || resultFormat.equals(ResultsFormat.FMT_RDF_XML) ) {
					lang = "RDF/XML-ABBREV";
				}
				else if ( resultFormat.equals(ResultsFormat.FMT_RDF_NT) ) {
					lang = "N-TRIPLE";
				}
				else if ( resultFormat.equals(ResultsFormat.FMT_RDF_TURTLE) ) {
					lang = "TURTLE";
				}
				else if ( resultFormat.equals(ResultsFormat.FMT_RDF_N3) ) {
					lang = "N3";
				}
				else {
					throw new IllegalArgumentException( "Unsupported result format specified. For CONSTRUCT or DESCRIBE queries use RDF/XML, NTRIPLE, TURTLE, or N3." );
				}
				result.write( out, lang );
			}
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
					SemWebTriple triple = iter.next();
					System.out.println( triple.toString() +
					                    " (result#: " + String.valueOf(++i) +
					                    " succ.URIs: " + String.valueOf(client.successfullyDereferencedURIs().size()) +
					                    " unsucc.URIs: " + String.valueOf(client.unsuccessfullyDereferencedURIs().size()) + ")" );
				}
			}
			else  {
				while (iter.hasNext()) {
					SemWebTriple triple = iter.next();
					System.out.println(triple.toString());
				}
			}
			System.out.println("--------------------------------");

		}
	}

	private void executeOutput() {
		if (this.outputRetrievedURIs) {
			System.out.println("Successfully dereferenced URIs: \n");
			Iterator<String> it = this.client.successfullyDereferencedURIs().iterator();
			while (it.hasNext()) {
				String uri = it.next();
				System.out.println(uri);
			}
			if ( verbose ) {
				System.out.println( " Count: " + String.valueOf(client.successfullyDereferencedURIs().size()) );
			}
			System.out.println("--------------------------------");
		}
		if (this.outputRedirectedURIs) {
			System.out.println("Redirected URIs: \n");
			Iterator<String> it = this.client.redirectedURIs().iterator();
			while (it.hasNext()) {
				String uri = it.next();
				String redirect = this.client.getRedirectURI(uri);
				System.out.println( uri + " -> " + redirect );
			}
			if ( verbose ) {
				System.out.println( " Count: " + String.valueOf(client.redirectedURIs().size()) );
			}
			System.out.println("--------------------------------");
		}
		if (this.outputFailedURIs) {
			System.out.println("Unsuccessfully dereferenced URIs: \n");

			Map<Class<? extends Exception>,Integer> reasons = new HashMap<Class<? extends Exception>,Integer> ();
			int count = 0;

			Iterator<String> it = this.client.unsuccessfullyDereferencedURIs().iterator();
			while (it.hasNext()) {
				String uri = it.next();
				if ( verbose ) {
					++count;
					Exception reason = client.getReasonForFailedDereferencing( uri );
					Class<? extends Exception> reasonType = reason.getClass();
					if ( ! reasons.containsKey(reasonType) ) {
						reasons.put( reasonType, Integer.valueOf(1) );
					}
					else {
						int i = (reasons.get(reasonType)).intValue() + 1;
						reasons.put( reasonType, Integer.valueOf(i) );
					}

					System.out.println( uri + " (" + Utils.classShortName(reasonType) + ": " + reason.getMessage() + ")" );
				}
				else {
					System.out.println(uri);
				}
			}

			if ( verbose && (count > 0) ) {
				System.out.println( " Count: " + String.valueOf(count) );
				System.out.println(" Reason statistics: " + String.valueOf(count) + " unsuccessfully dereferenced URIs");
				Iterator<Map.Entry<Class<? extends Exception>,Integer>> itR = reasons.entrySet().iterator();
				while ( itR.hasNext() ) {
					Map.Entry<Class<? extends Exception>,Integer> r = itR.next();
					int percent = ( r.getValue().intValue() * 100 ) / count;
					System.out.println(" - " + String.valueOf(percent) + "% " + Utils.classShortName(r.getKey()) );
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
			this.client.getConfig().setValue( SemanticWebClientConfig.MAXSTEPS, Integer.toString(this.maxsteps));
		if (this.timeout != -1)
			this.client.getConfig().setValue( SemanticWebClientConfig.TIMEOUT, Long.toString(this.timeout));
		if (this.maxthreads != -1)
			this.client.getConfig().setValue( SemanticWebClientConfig.MAXTHREADS, Long.toString(this.maxthreads));
		if (this.maxfilesize != -1)
			this.client.getConfig().setValue( SemanticWebClientConfig.MAXFILESIZE, Integer.toString(this.maxfilesize));

		client.getConfig().setValue( SemanticWebClientConfig.ENABLE_RDFA, Boolean.toString(enableRDFa) );
		client.getConfig().setValue( SemanticWebClientConfig.ENABLE_SINDICE, Boolean.toString(enableSindiceSearch) );
	}

	private void executeWriteIntro() {
		System.out.println("----------------------------------");
		System.out.println("Semantic Web Client Library V0.4.2");
		System.out.println("----------------------------------");
	}
}
