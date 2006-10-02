package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * The UriConnector tries to Retrieve a given URI. It builds a
 * HttpURLConnection, creates an InputStream and tries to parse it. If the
 * Thread is finished it contains a NamedGraphSet which cosists of the parsed
 * data.
 * 
 * @author Tobias Gauﬂ
 */
public class UriConnector extends Thread {
	private final static int STATUS_OK = 1;
	private final static int STATUS_PARSING_FAILED = -1;
	private final static int STATUS_MALFORMED_URL = -2;
	private final static int STATUS_UNABLE_TO_CONNECT = -3;
	
	/**
	 * The connection.
	 */
	private HttpURLConnection connection;

	/**
	 * Is true when the URI is retrieved.
	 */
	private volatile boolean isReady;

	/**
	 * The http response code.
	 */
	private int responseCode = 200;

	/**
	 * The corresponding URIRetriever.
	 */
	protected URIRetriever retriever;

	/**
	 * The retrieval step. If no step is passed it is set to 0.
	 */
	protected int step = 0;

	/**
	 * Is true when the Connector is stopped false otherwise.
	 */
	public boolean stopped = false;

	/**
	 * The NamedGraphSet which contains the retrieved data.
	 */
	private NamedGraphSet tempNgs;

	/**
	 * The URI String.
	 */
	protected String uri;

	/**
	 * One of the STATUS_XXX constants
	 */
	private int uriRetrieved;

	/**
	 * The URL object.
	 */
	protected URL url;

	private Log log = LogFactory.getLog(UriConnector.class);
	
	/**
	 * Generates the UriConnector.
	 * 
	 * @param retriever
	 *            The corresponding UriRetriever.
	 * @param uri
	 *            The URI to retrieve.
	 * @param step
	 *            The retrieval step.
	 */
	public UriConnector(URIRetriever retriever, String uri, int step) {
		this.uri = uri;
		this.setName(uri);
		this.step = step + 1;
		this.retriever = retriever;
		this.tempNgs = new NamedGraphSetImpl();
		try {
			this.url = new URL(uri);
		} catch (MalformedURLException e) {
			this.url = null;
			this.uriRetrieved = STATUS_MALFORMED_URL;
			this.stopped = true;
		}
	}

	/**
	 * Returns the NamedGraphSet.
	 * 
	 * @return NamedGraphSet
	 */
	public NamedGraphSet getNgs() {
		return this.tempNgs;
	}

	/**
	 * Returns the retrieval step.
	 * 
	 * @return int
	 */
	public int getStep() {
		return this.step;
	}

	/**
	 * Returns the URI String.
	 * 
	 * @return String
	 */
	public String getUriString() {
		return this.uri;
	}

	/**
	 * Returns true if the Thread has finished retrieving the URI false if not.
	 * 
	 * @return boolean
	 */
	public boolean isReady() {
		return this.isReady;
	}

	/**
	 * Returns true if the Thread has stopped false if not.
	 * 
	 * @return
	 */
	public boolean isStopped() {
		return this.stopped;
	}

	/**
	 * Parses an RDF String and adds the collected data to the NamedGraphSet.
	 */
	private void parseRdf(String lang) {
		try {
			if (lang.equals("default"))
				lang = null;
			RDFDefaultErrorHandler.silent = true;

			this.tempNgs.read(this.connection.getInputStream(), lang, this.url
					.toString());
			this.uriRetrieved = STATUS_OK;
		} catch (Exception e) {
			this.log.debug(e.getMessage());
			this.uriRetrieved = STATUS_PARSING_FAILED;
		}
	}

	/**
	 * Returns the http response code.
	 * 
	 * @return int
	 */
	public int responseCode() {
		return this.responseCode;
	}

	public void run() {
		this.isReady = false;
		if (!this.stopped) {
			try {
				// application/rdf+xml;q=1.0, */*;q=0.5
				this.connection = (HttpURLConnection) this.url.openConnection();
				this.connection.addRequestProperty("accept",
						"application/rdf+xml");
				this.connection.addRequestProperty("accept",
						"application/octet-stream");
				this.connection.addRequestProperty("accept", "text/plain");
				this.connection.addRequestProperty("accept", "application/xml");
				this.connection.addRequestProperty("accept", "text/rdf+n3");
				//this.connection.addRequestProperty("accept", "text/html");

				this.log.debug(this.connection.getResponseCode() + " " + this.url + " (" + this.connection.getContentType() + ")");
				
				String type = this.connection.getContentType();
				
				if (type != null) {
					// ToDo Http 303
					// this.retriever.getClient().addRemoteGraph();
					// Map keymap = this.connection.getHeaderFields();
					// String test = this.connection.getHeaderFieldKey(9);
					String lang = null;

					// /////////dbug
					
				//	if (type.equals("text/html")){
				//		this.parseHTML();
				//	}

					
					// ////////////////////
					if (this.connection.getContentType().startsWith(
							"application/rdf+xml")) {
						lang = "RDF/XML";
					} else {
						lang = "default";
					}
					if (lang != null) {
						this.parseRdf(lang);
					}
				} else {
					this.uriRetrieved = STATUS_UNABLE_TO_CONNECT;
					this.responseCode = this.connection.getResponseCode();
				}
			} catch (Exception e) {
				this.log.debug(e.getMessage());
				this.uriRetrieved = STATUS_PARSING_FAILED;
			}
		}
		this.isReady = true;
		this.log.debug("Ready & sleeping");
		synchronized (this) {
			try {
				this.wait();
			} catch (Exception e) {
				
			}
		}
		this.log.debug("Done");
	}

	/**
	 * Stops the UriConnector from retrieving the URI.
	 */
	public void stopConnector() {
		if (this.connection != null)
			try {
				this.connection.disconnect();
				this.stopped = true;
			} catch (Exception e) {

			}
		this.uriRetrieved = 0;
	}

	/**
	 * Returns true if the URI is retrieved false if not.
	 * 
	 * @return boolean
	 */
	public int uriRetrieved() {
		return this.uriRetrieved;
	}

	/**
	 * Notifies the UriConnector.
	 */
	synchronized public void wakeUp() {
		this.notify();
	}
	/*
	public void parseHTML(){
		 String site = null;
		 try{
		 InputStream stream = this.connection.getInputStream();
		 BufferedReader br = new BufferedReader(new
		 InputStreamReader(stream));
		 StringBuffer sb = new StringBuffer();
		 String line = null;

		 while ((line = br.readLine()) != null) {
		 sb.append(line + "\n");
		 }
		 site = sb.toString();
		 Pattern p = Pattern.compile("<link\\s*rel");
		 Matcher match = p.matcher(site);
		 int start = match.start(1);
		 int end   = match.end(1);
		
	}catch(Exception e){
		System.out.println(e.getLocalizedMessage());
	}
	int i = 0;
	i++;
	}
	*/

}
