package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * The DereferencerThread executes a given DereferencingTask. It opens a
 * HttpURLConnection, creates an InputStream and tries to parse it. If the
 * Thread is finished it delivers the retrieval result.
 * 
 * @author Tobias Gauß
 * @author Olaf Hartig
 */
public class DereferencerThread extends Thread {
	private DereferencingTask task = null;

	private HttpURLConnection connection;

	private boolean stopped = false;

	private NamedGraphSet tempNgs = null;
	
	private int maxfilesize = -1;

        private boolean enablegrddl = false;

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
	public boolean hasTask() {
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
	 * Creates a new DereferencingResult which contains information about the
	 * retrieval failure.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param exception
	 *            the thrown exception
	 * @return
	 */
	private DereferencingResult createNewUrisResult(int errorCode, ArrayList urilist) {
		return new DereferencingResult(this.task, errorCode, urilist);
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
		DereferencingResult result = null;
		this.tempNgs = new NamedGraphSetImpl();
		try {
			URLConnection con = this.url.openConnection();
// TODO This works only with Java 5,
// and Tobias said he's not even sure if it has any positive effect. [RC]
//			con.setReadTimeout(60000);
			this.connection = (HttpURLConnection) con;
			this.connection.setInstanceFollowRedirects(false);
					this.connection.addRequestProperty(
							"Accept",
							"application/rdf+xml;q=1,"
							+ "text/xml;q=0.6,text/rdf+n3;q=0.9,"
							+ "application/octet-stream;q=0.5,"
							+ "application/xml q=0.5,application/rss+xml;q=0.5,"
							+ "text/plain; q=0.5,application/x-turtle;q=0.5,"
							+ "application/x-trig;q=0.5,"
							+ "application/xhtml+xml;q=0.5, "
							+ "text/html;q=0.5"
							);


			this.log.debug(this.connection.getResponseCode() + " " + this.url
				       + " (" + this.connection.getContentType() + ")");

			String type = this.connection.getContentType();

			if (type == null) {
				return createErrorResult(
						DereferencingResult.STATUS_UNABLE_TO_CONNECT, null);
			}

			if ( this.connection.getResponseCode() == 303 ) {
				String redirectURI = this.connection.getHeaderField("Location");
				return new DereferencingResult(this.task, DereferencingResult.STATUS_REDIRECTED, redirectURI);
			}

			String lang = setLang();
			try {
				result = this.parseRdf(lang);
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
		//return new DereferencingResult(this.task,
		//		DereferencingResult.STATUS_OK, this.tempNgs, null);
		return result;
	}

	/**
	 * Parses an RDF String.
	 */
	private DereferencingResult parseRdf(String lang) throws Exception {
		if (lang.equals("default"))
			lang = null;
		if (lang.equals("html") || lang.equals("HTML")){
		        if (this.enablegrddl) {
			    com.hp.hpl.jena.grddl.GRDDLReader r = new com.hp.hpl.jena.grddl.GRDDLReader();
			    /*
			    Gleaner g = new Gleaner(this.connection.getURL().toString(),
						    this.connection.getInputStream());
			    g.glean(this.tempNgs);
			    */
			    Model m = ModelFactory.createDefaultModel();
			    r.read(m, this.connection.getInputStream(), this.url.toString());
			    this.tempNgs.addGraph( new NamedGraphImpl(this.url.toString(), 
								      m.getGraph()) );

			    if (this.tempNgs.countGraphs() > 0)
				return new DereferencingResult(this.task,
							       DereferencingResult.STATUS_OK, this.tempNgs, null);
			}
			ArrayList l = HtmlLinkFetcher.fetchLinks(this.connection.getInputStream());
			Iterator iter = l.iterator();
			ArrayList urilist = new ArrayList();
			while (iter.hasNext()) {
			    String link = (String) iter.next();
			    int pos = this.url.toString().lastIndexOf("/");
			    String newurl = this.url.toString().substring(0,pos+1);
			    newurl+=link;
			    urilist.add(newurl);
			}
			return createNewUrisResult(DereferencingResult.STATUS_NEW_URIS_FOUND, urilist);
			
		}
			
		RDFDefaultErrorHandler.silent = true;
		LimitedInputStream lis = new LimitedInputStream(this.connection.getInputStream(),this.maxfilesize);
		this.tempNgs.read(lis, lang, this.url
				.toString());
		return new DereferencingResult(this.task,
						DereferencingResult.STATUS_OK, this.tempNgs, null);
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
		if (type.startsWith("text/html"))
			return "html";

		return type;
	}

	/**
	 * Stops the UriConnector from retrieving the URI.
	 */
	public synchronized void stopThread() {
		this.stopped = true;
		this.interrupt();
	}
	
	public synchronized void setMaxfilesize(int size){
		this.maxfilesize = size;
	}
	public synchronized void setEnableGrddl(boolean g){
		this.enablegrddl = g;
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
