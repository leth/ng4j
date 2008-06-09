package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;


/**
 * An exception that occured during praparation or execution of a URI search
 * query.
 *
 * @author Olaf Hartig
 */
public class QueryProcessingException extends Exception {

	// members

	/** the query processor that threw this exception */
	final protected QueryProcessor qp;

	/** the URI for which the search failed */
	final protected String uri;


	// initialization

	/**
	 * @param qp the query processor that threw this exception
	 * @param uri the URI for which the search failed
	 * @param msg a short description of this exception
	 * @param cause the cause
	 */
	public QueryProcessingException ( QueryProcessor qp, String uri, String msg, Throwable cause ) {
		super( msg, cause );
		this.qp = qp;
		this.uri = uri;
	}

	/**
	 * @param qp the query processor that threw this exception
	 * @param uri the URI for which the search failed
	 * @param msg a short description of this exception
	 */
	public QueryProcessingException ( QueryProcessor qp, String uri, String msg ) {
		super( msg );
		this.qp = qp;
		this.uri = uri;
	}


	// accessor methods

	/**
	 * Returns the query processor that threw this exception.
	 */
	public QueryProcessor getQueryProcessor () {
		return qp;
	}

	/**
	 * Returns the URI for which the search failed.
	 */
	public String getURI () {
		return uri;
	}

}
