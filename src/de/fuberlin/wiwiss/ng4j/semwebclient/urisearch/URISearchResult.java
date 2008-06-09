package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import java.util.Set;


/**
 * The result of a {@link URISearchTask}.
 *
 * @author Olaf Hartig
 */
public class URISearchResult {

	// members

	protected URISearchTask task;
	protected Set mentioningDocs;
	protected QueryProcessingException exception;


	// initialization

	/**
	 * Creates the result of a successful URI search.
	 *
	 * @param task the corresponding URI search task
	 * @param mentioningDocs a set of URLs (strings) for RDF documents that
	 *                       mention the URI from the search task
	 */
	public URISearchResult ( URISearchTask task, Set mentioningDocs ) {
		this.task = task;
		this.mentioningDocs = mentioningDocs;
		this.exception = null;
	}

	/**
	 * Creates the result of a URI search that failed.
	 *
	 * @param task the corresponding URI search task
	 * @param exception an exception that describes the failure
	 */
	public URISearchResult ( URISearchTask task, QueryProcessingException exception ) {
		this.task = task;
		this.mentioningDocs = null;
		this.exception = exception;
	}


	// accessor methods

	/**
	 * Returns the corresponding URI search task.
	 */
	public URISearchTask getTask () {
		return task;
	}

	/**
	 * Return true if the URI search was successful.
	 */
	public boolean isSuccess () {
		return ( exception == null );
	}

	/**
	 * Returns a set of URLs (strings) for RDF documents that mention the URI
	 * from the search task.
	 */
	public Set getMentioningDocs () {
		return mentioningDocs;
	}

	/**
	 * Returns an exception in case the URI search failed.
	 */
	public QueryProcessingException getException () {
		return exception;
	}

}
