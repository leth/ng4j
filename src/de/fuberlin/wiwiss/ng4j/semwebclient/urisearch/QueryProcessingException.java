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

/*
 * (c) Copyright 2006, 2007, 2008 Christian Bizer (chris@bizer.de) All rights reserved.
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
