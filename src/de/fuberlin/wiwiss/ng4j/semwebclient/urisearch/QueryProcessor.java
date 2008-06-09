package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import java.util.Set;


/**
 * Prepares and executes the query of a URI search.
 * Implementations of this interface will usually be engine-specific (i.e. each
 * implementation is customized for a specific search engine).
 *
 * @author Olaf Hartig
 */
public interface QueryProcessor
{
	/**
	 * Processes a query to search for the given URI.
	 * This method can be expected to run in a task-specific thread.
	 *
	 * @param uri the URI to search for
	 * @return the query result which is a set of URLs (strings) that refer to
	 *         RDF documents which mention the given URI
	 */
	public Set process ( String uri ) throws QueryProcessingException;

}
