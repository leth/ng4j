package de.fuberlin.wiwiss.ng4j.semwebclient.urisearch;

import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.Task;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskExecutorBase;


/**
 * A thread that executes a {@link URISearchTask}.
 *
 * @author Olaf Hartig
 */
public class URISearchThread extends TaskExecutorBase {

	// members

	protected QueryProcessor proc = new QueryProcessorSindice();


	// implementation of the TaskExecutorBase interface

	public Class getTaskType () {
		return URISearchTask.class;
	}


	protected void executeTask ( Task task ) {
		URISearchResult result = executeTask( (URISearchTask) task );

		// deliver the result of the task to the listener
		synchronized ( this ) {
			if ( isStopped() )
				return;

			( (URISearchTask) task ).getListener().uriSearchFinished( result );
		}
	}


	// helper methods

	/**
	 * Executes the given task and returns the result.
	 */
	protected URISearchResult executeTask ( URISearchTask task ) {
		try {
			return new URISearchResult( task, proc.process(task.getURI()) );
		} catch ( QueryProcessingException e ) {
			return new URISearchResult( task, e );
		} catch ( RuntimeException e ) {
			return new URISearchResult( task,
			                            new QueryProcessingException(proc, task.getURI(), "Unexpected " + e.getClass().getName() + " caught (" + e.getMessage() + ").", e) );
		}
	}

}

/*
 * (c) Copyright 2006 - 2009 Christian Bizer (chris@bizer.de) All rights reserved.
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
