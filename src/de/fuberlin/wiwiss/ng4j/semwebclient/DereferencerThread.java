package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * The DereferencerThread executes a given DereferencingTask. It opens a
 * HttpURLConnection, creates an InputStream and tries to parse it. If the
 * Thread is finished it delivers the retrieval result.
 * 
 * @author Tobias Gau�
 */
public class DereferencerThread extends Thread {
	private DereferencingTask task = null;

	private HttpURLConnection connection;

	private boolean stopped = false;

	private NamedGraphSet tempNgs = null;

	private URL url;

	private Log log = LogFactory.getLog(DereferencerThread.class);

	public DereferencerThread() {
		// Lower priority a little bit
		setPriority(getPriority() - 1);
	}

	public void run() {
		this.log.debug("Thread started.");
		while (!this.stopped) {
			if (hasTask()) {
				DereferencingResult result = dereference();
				deliver(result);
				clearTask();
			}
			try {
				synchronized (this) {
					if (this.stopped) {
						break;
					}
					wait();
				}
			} catch (InterruptedException ex) {
				// Happens when the thread is stopped
			}
		}
		this.log.debug("Thread stopped.");
	}

	/**
	 * @return Returns true if the DereferencerThread is available for new
	 *         tasks.
	 */
	public synchronized boolean isAvailable() {
		return !hasTask() && !this.stopped;
	}

	/**
	 * Starts to execute the DereferencingTask task. Returns true if the
	 * retrieval process is started false if the thread is unable to execute the
	 * task.
	 * 
	 * @param task
	 *            The task to execute.
	 * @return
	 */
	public synchronized boolean startDereferencingIfAvailable(
			DereferencingTask task) {
		if (!isAvailable()) {
			return false;
		}
		try {
			this.url = new URL(task.getURI());
			this.task = task;
			this.notify();
			return true;
		} catch (MalformedURLException ex) {
			deliver(createErrorResult(DereferencingResult.STATUS_MALFORMED_URL,
					ex));
			return true;
		}
	}

	/**
	 * @return Returns true if the DereferencerThread is busy false if not.
	 */
	private boolean hasTask() {
		return this.task != null;
	}

	/**
	 * Clears the DereferencerThreads tasks.
	 */
	private void clearTask() {
		this.url = null;
		this.connection = null;
		this.tempNgs = null;
		this.task = null;
	}

	/**
	 * Creates a new DereferencingResult which contains information about the
	 * retrieval failure.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param exception
	 *            the thrown exception
	 * @return
	 */
	private DereferencingResult createErrorResult(int errorCode,
			Exception exception) {
		return new DereferencingResult(this.task, errorCode, null, exception);
	}

	/**
	 * Delivers the retrieval result.
	 * 
	 * @param result
	 */
	private synchronized void deliver(DereferencingResult result) {
		if (this.stopped) {
			return;
		}
		this.task.getListener().dereferenced(result);
	}

	private DereferencingResult dereference() {
		this.tempNgs = new NamedGraphSetImpl();
		try {
			this.connection = (HttpURLConnection) this.url.openConnection();

			this.connection
					.addRequestProperty(
							"accept",
							"application/rdf+xml ; q=1, "
									+ "text/xml ; q=0.6 , text/rdf+n3 ; q=0.9 , "
									+ "application/octet-stream ; q=0.5 , "
									+ "application/xml q=0.5, application/rss+xml ; q=0.5 , "
									+ "text/plain ; q=0.5, application/x-turtle ; q=0.5, "
									+ "application/x-trig ; q=0.5");

			// TODO html handling

			this.log.debug(this.connection.getResponseCode() + " " + this.url
					+ " (" + this.connection.getContentType() + ")");

			String type = this.connection.getContentType();

			if (type == null) {
				return createErrorResult(
						DereferencingResult.STATUS_UNABLE_TO_CONNECT, null);
			}
			// TODO Http 303

			String lang = setLang();
			try {
				this.parseRdf(lang);
			} catch (Exception ex) { // parse error
				this.log.debug(ex.getMessage());
				return createErrorResult(
						DereferencingResult.STATUS_PARSING_FAILED, ex);
			}
			// }
		} catch (IOException e) {
			this.log.debug(e.getMessage());
			return createErrorResult(DereferencingResult.STATUS_PARSING_FAILED,
					e);
		}
		return new DereferencingResult(this.task,
				DereferencingResult.STATUS_OK, this.tempNgs, null);
	}

	/**
	 * Parses an RDF String.
	 */
	private void parseRdf(String lang) throws Exception {
		if (lang.equals("default"))
			lang = null;
		RDFDefaultErrorHandler.silent = true;
		this.tempNgs.read(this.connection.getInputStream(), lang, this.url
				.toString());
	}

	/**
	 * Tries to guess a lang String from a connection.
	 * 
	 * @return
	 */
	private String setLang() {
		String type = this.connection.getContentType();
		if (type == null)
			return "default";

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

		return type;
	}

	/**
	 * Stops the UriConnector from retrieving the URI.
	 */
	public synchronized void stopThread() {
		this.stopped = true;
		this.interrupt();
	}

	/*
	 * public void parseHTML(){ String site = null; try{ InputStream stream =
	 * this.connection.getInputStream(); BufferedReader br = new
	 * BufferedReader(new InputStreamReader(stream)); StringBuffer sb = new
	 * StringBuffer(); String line = null;
	 * 
	 * while ((line = br.readLine()) != null) { sb.append(line + "\n"); } site =
	 * sb.toString(); Pattern p = Pattern.compile("<link\\s*rel"); Matcher
	 * match = p.matcher(site); int start = match.start(1); int end =
	 * match.end(1);
	 * 
	 * }catch(Exception e){ System.out.println(e.getLocalizedMessage()); }
	 *  }
	 */
}
