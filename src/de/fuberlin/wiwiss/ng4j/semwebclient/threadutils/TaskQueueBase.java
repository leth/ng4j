package de.fuberlin.wiwiss.ng4j.semwebclient.threadutils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A base class for task queues that manage a pool of threads which execute the
 * queued tasks.
 * This queue is implemented as a thread itself. New tasks are assigned to free
 * threads. If all threads are busy a new task is queued and its execution is
 * postponed until a thread becomes free.
 *
 * @author Olaf Hartig
 */
abstract public class TaskQueueBase extends Thread {

	// members

	static final public int MAXTHREADS_DEFAULT = 10;
	final protected int maxThreads;
	final protected Queue freeThreads = new LinkedList ();
	final protected Set busyThreads = new HashSet ();
	protected Queue tasks = new LinkedList ();
	private boolean closed = false;

	static private Log log = LogFactory.getLog( TaskQueueBase.class );


	// initialization

	/**
	 * Creates a task queue with the default ({@link #MAXTHREADS_DEFAULT})
	 * number of available threads.
	 */
	public TaskQueueBase () {
		this( MAXTHREADS_DEFAULT );
	}

	/**
	 * Creates a task queue with the given number of available threads.
	 *
	 * @param maxThreads size of the thread pool
	 */
	public TaskQueueBase ( int maxThreads ) {
		this.maxThreads = maxThreads;
	}


	// interface

	/**
	 * Creates a task-specific thread.
	 */
	abstract protected TaskExecutorBase createThread ();


	// accessor methods

	/**
	 * Adds the given task to the queue.
	 */
	public synchronized void addTask ( Task task ) {
		if ( closed )
			throw new IllegalStateException( "This queue '" + getName() + "' (type: " + getClass().getName() + ") has been closed." );

		tasks.offer ( task );
		log.trace( "Enqueued task '" + task.getIdentifier() + "' in queue '" + getName() + "' (type: " + getClass().getName() + ") - " + tasks.size() + " tasks in queue." );
		notify();
	}

	/**
	 * Returns true if the queue is empty and none of the threads is busy.
	 */
	final public boolean isIdle () {
		return ( tasks.isEmpty() && busyThreads.isEmpty() );
	}

	/**
	 * Returns true if this queue has been closed already.
	 */
	final public boolean isClosed () {
		return closed;
	}


	// operations

	public void run () {
		log.trace( "Task queue '" + getName() + "' (type: " + getClass().getName() + ") started." );

		initThreadPool();

		Set tmp = new HashSet ();
		while ( ! this.closed ) {
			while ( ! this.tasks.isEmpty() && ! this.freeThreads.isEmpty() ) {
				TaskExecutorBase thread = (TaskExecutorBase) freeThreads.remove();
				Task task = (Task) this.tasks.remove();
				thread.startTask( task );
				busyThreads.add( thread );

				log.trace( "Dequeued task '" + task.getIdentifier() + "' in queue '" + getName() + "' (type: " + getClass().getName() + ") - still " + tasks.size() + " tasks in queue." );
			}

			// move threads that finished their task to the pool of free threads
			tmp.clear();
			Iterator it = busyThreads.iterator();
			while ( it.hasNext() ) {
				TaskExecutorBase t = (TaskExecutorBase) it.next();
				if ( ! t.hasTask() )
					tmp.add( t );
			}
			busyThreads.removeAll( tmp );
			freeThreads.addAll( tmp );

			try {
				synchronized ( this ) {
					wait(100);
				}
			} catch ( InterruptedException e ) {
				throw new RuntimeException( "Task queue '" + getName() + "' (type: " + getClass().getName() + ") interrupted (" + e.getMessage() + ").", e );
			}
		}
	}

	/**
	 * Stops this queue and all threads in the pool.
	 */
	public synchronized void close () {
		log.trace( "Closing task queue '" + getName() + "' (type: " + getClass().getName() + ")." );

		closed = true;

		while ( ! freeThreads.isEmpty() ) {
			TaskExecutorBase t = (TaskExecutorBase) freeThreads.remove();
			t.stopThread();
			t.interrupt();
		}

		Iterator it = busyThreads.iterator();
		while ( it.hasNext() ) {
			TaskExecutorBase t = (TaskExecutorBase) it.next();
			t.stopThread();
			t.interrupt();
		}

		freeThreads.addAll( busyThreads );
		busyThreads.clear();
		tasks.clear();

		notify();

		log.trace( "Task queue '" + getName() + "' (type: " + getClass().getName() + ") closed." );
	}


	// helper methods

	private void initThreadPool () {
		for ( int i = 0; i < maxThreads; i++ ) {
			TaskExecutorBase t = createThread();
			t.setPriority( getPriority() - 1 );
			t.start();
			freeThreads.offer( t );
		}
	}

}
