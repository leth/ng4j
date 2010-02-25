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
	final protected int step;


	// initialization

	public URISearchTask ( String uri, URISearchListener listener, int step ) {
		if ( uri == null )
			throw new IllegalArgumentException( "The given URI is undefined." );
		if ( listener == null )
			throw new IllegalArgumentException( "The given listener is undefined." );

		this.uri = uri;
		this.listener = listener;
		this.step = step;
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
	
	public int getStep () {
		return step;
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
