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
