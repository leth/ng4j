package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.Task;


/**
 * A task to search for RDF documents that mention a specific URI.
 *
 * @author Olaf Hartig
 */
public class URISearchTask implements Task {

	// members

	final protected String uri;
	final protected URISearchListener listener;


	// initialization

	public URISearchTask ( String uri, URISearchListener listener ) {
		if ( uri == null )
			throw new IllegalArgumentException( "The given URI is undefined." );
		if ( listener == null )
			throw new IllegalArgumentException( "The given listener is undefined." );

		this.uri = uri;
		this.listener = listener;
	}


	// implementation of the Task interface

	public String getIdentifier () {
		return uri;
	}


	// accessor methods

	public String getURI () {
		return this.uri;
	}

	public URISearchListener getListener () {
		return this.listener;
	}

}
