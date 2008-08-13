package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskExecutorBase;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskQueueBase;


/**
 * A queue of URI search tasks ({@link URISearchTask} objects) that manages
 * threads ({@link URISearchThread} objects) which execute the queued tasks.
 * This queue is implemented as a thread itself. It observes the search
 * threads: it tries to assign new tasks to free search threads and interrupts
 * search threads if the timeout is reached.
 *
 * @author Olaf Hartig
 */
public class URISearchTaskQueue extends TaskQueueBase {

	// members

	static final public int MAXTHREADS_DEFAULT = 10;


	// initialization

	public URISearchTaskQueue () {
		this( MAXTHREADS_DEFAULT );
	}

	public URISearchTaskQueue ( int maxThreads ) {
		super( maxThreads );
		setName( "URISearchTaskQueue" );
	}


	// implementation of the TaskQueueBase interface

	protected TaskExecutorBase createThread () {
		return new URISearchThread();
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
