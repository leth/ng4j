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
