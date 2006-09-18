package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * The UriConnector tries to Retrieve a given URI.
 * It builds a HttpURLConnection, creates an InputStream and
 * tries to parse it. If the Thread is finished it contains a
 * NamedGraphSet which cosists of the parsed data.
 * 
 * @author Tobias Gauß
 *
 */
public class UriConnector extends Thread {
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
	 * Is true if the URI is succsessfully retrieved fals otherwise.
	 */
	private boolean uriRetrieved;

	/**
	 * The URL object.
	 */
	protected URL url;

	/**
	 * Generates the UriConnector.
	 * 
	 * @param retriever The corresponding UriRetriever.
	 * @param uri The URI to retrieve.
	 * @param step The retrieval step.
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
			this.uriRetrieved = false;
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
	 * Returns true if the Thread has finished retrieving the URI
	 * false if not.
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
			if(lang.equals("default"))
				lang = null;
				this.tempNgs.read(this.connection.getInputStream(), lang, this.url
					.toString());
			this.uriRetrieved = true;
		} catch (Exception e) {
			this.uriRetrieved = false;
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
		this.uriRetrieved = false;
		if (!this.stopped && !(this.step >= this.retriever.getMaxsteps())) {
			try {
				this.connection = (HttpURLConnection) this.url.openConnection();
				if (this.connection.getContentType() != null) {
					String lang = null;
					if (this.connection.getContentType().startsWith("application/rdf+xml")){
							lang = "RDF/XML";
					}else{
						lang = "default";
					}
						if(lang!=null)
							this.parseRdf(lang);
					}
			} catch (Exception e) {
				this.uriRetrieved = false;
			}
		}
		if(this.step >= this.retriever.getMaxsteps())
			this.responseCode = 666;
		
		this.isReady = true;
		synchronized (this) {
			try {
				this.wait();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Stops the UriConnector from retrieving the URI.
	 */
	public void stopConnector() {
		if (this.connection != null)
			try{
				this.connection.disconnect();
				this.stopped = true;
			}catch(Exception e){
				
			}
		
	}

	/**
	 * Returns true if the URI is retrieved false if not.
	 * 
	 * @return boolean
	 */
	public boolean uriRetrieved() {
		return this.uriRetrieved;
	}

	/**
	 * Notifies the UriConnector.
	 */
	synchronized public void wakeUp() {
		this.notify();
	}

}
