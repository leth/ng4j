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
public class DereferencerThread extends Thread {
	private final static int STATUS_PARSING_FAILED = -1;
	private final static int STATUS_MALFORMED_URL = -2;
	private final static int STATUS_UNABLE_TO_CONNECT = -3;
	
	private DereferencingTask task = null;
	private HttpURLConnection connection;
	private boolean stopped = false;
	private boolean available = true;

	/**
	 * The NamedGraphSet which contains the retrieved data.
	 */
	private NamedGraphSet tempNgs = null;
	private URL url;

	private Log log = LogFactory.getLog(DereferencerThread.class);
	
	public void run() {
		while (!this.stopped) {
			if (hasTask()) {
				dereferenceAndDeliver();
				clearTask();
			}
			if (this.stopped) {
				break;
			}
			try {
				synchronized(this) {
					wait();
				}
			} catch (InterruptedException ex) {
				// Don't know when this happens
				throw new RuntimeException(ex);
			}
		}
		this.log.debug("Thread stopped.");
	}
	
	public synchronized boolean isAvailable() {
		return this.available;
	}
	
	public synchronized boolean startDereferencingIfAvailable(DereferencingTask task) {
		if (!isAvailable()) {
			return false;
		}
		this.task = task;
		this.url = toURL(task.getURI());
		if (this.url == null) {
			deliverError(STATUS_MALFORMED_URL);
		}
		this.available = false;
		this.notify();
		return true;
	}
	
	private boolean hasTask() {
		return this.task != null;
	}
	
	private void clearTask() {
		this.task = null;
		this.url = null;
		this.connection = null;
		this.tempNgs = null;
		this.available = true;
	}
	
	private void deliverError(int errorCode) {
		this.task.getListener().dereferencingFailed(this.task, errorCode);
	}
	
	private void dereferenceAndDeliver() {
		this.tempNgs = new NamedGraphSetImpl();
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
			
			if (type == null) {
				deliverError(STATUS_UNABLE_TO_CONNECT);
				return;
			}
			// TODO Http 303
			String lang = null;
//			if (type.equals("text/html")){
//				this.parseHTML();
//			}
			if (this.connection.getContentType().startsWith(
					"application/rdf+xml")) {
				lang = "RDF/XML";
			} else {
				lang = "default";
			}
			if (lang != null) {
				if (!this.parseRdf(lang)) {
					// There was a parse error
					return;
				}
			}
		} catch (Exception e) {
			this.log.debug(e.getMessage());
			deliverError(STATUS_PARSING_FAILED);
		}
		this.task.getListener().dereferencingSuccessful(this.task, this.tempNgs);
	}
	
	/**
	 * Parses an RDF String and adds the collected data to the NamedGraphSet.
	 */
	private boolean parseRdf(String lang) {
		try {
			if (lang.equals("default"))
				lang = null;
			RDFDefaultErrorHandler.silent = true;
	
			this.tempNgs.read(this.connection.getInputStream(), lang, this.url
					.toString());
			return true;
		} catch (Exception e) {
			this.log.debug(e.getMessage());
			deliverError(STATUS_PARSING_FAILED);
			return false;
		}
	}

	private URL toURL(String uri){
		try {
			return new URL(uri);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Stops the UriConnector from retrieving the URI.
	 */
	public synchronized void stopThread() {
		// TODO: How to close hanging HTTP connections here?
		this.stopped = true;
		this.available = false;
		this.notify();
		this.log.debug("Received stop message");
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
