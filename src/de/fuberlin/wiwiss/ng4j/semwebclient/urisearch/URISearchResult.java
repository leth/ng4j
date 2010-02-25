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
	protected Set<String> mentioningDocs;
	protected QueryProcessingException exception;


	// initialization

	/**
	 * Creates the result of a successful URI search.
	 *
	 * @param task the corresponding URI search task
	 * @param mentioningDocs a set of URLs (strings) for RDF documents that
	 *                       mention the URI from the search task
	 */
	public URISearchResult ( URISearchTask task, Set<String> mentioningDocs ) {
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
	public Set<String> getMentioningDocs () {
		return mentioningDocs;
	}

	/**
	 * Returns an exception in case the URI search failed.
	 */
	public QueryProcessingException getException () {
		return exception;
	}

}

/*
 * (c) Copyright 2006 - 2010 Christian Bizer (chris@bizer.de) All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
