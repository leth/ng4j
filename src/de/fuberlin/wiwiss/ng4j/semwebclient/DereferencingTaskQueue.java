package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.HashMap;
import java.util.Map;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.Task;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskExecutorBase;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskQueueBase;


/**
 * The DereferencingTaskQueue is a thread which observes the
 * DereferencerThreads. It starts all DereferencerThreads tries to 
 * assign new tasks to free DereferencerThreads and interrupts them
 * if the timeout is reached.
 * 
 * @author Tobias Gauß
 * @author Olaf Hartig
 */
public class DereferencingTaskQueue extends TaskQueueBase
                                    implements DereferencingListener
{
//	private Log log = LogFactory.getLog(DereferencingTaskQueue.class);
	private int maxfilesize;
        private boolean enablegrddl;
	private int connectTimeout = 0;
	private int readTimeout = 0;
	private Map<String,DereferencingTask> currentTasks = new HashMap<String,DereferencingTask> (); // maps task IDs to tasks


	/**
	 * Old constructor.
	 * @deprecated Please use the other constructor instead.
	 */
        public DereferencingTaskQueue(int maxThreads,int maxfilesize, boolean enablegrddl) {
		this( maxThreads, maxfilesize, enablegrddl, 0, 0 );
	}

	public DereferencingTaskQueue(int maxThreads,int maxfilesize, boolean enablegrddl, int connectTimeout, int readTimeout) {
		super( maxThreads );
		this.maxfilesize = maxfilesize;
		this.enablegrddl = enablegrddl;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		setName("DereferencingTaskQueue");
		start();
	}


	// implementation of the TaskQueueBase interface

	protected TaskExecutorBase createThread () {
		DereferencerThread thread = new DereferencerThread();
		thread.setMaxfilesize(this.maxfilesize);
		thread.setEnableGrddl(this.enablegrddl);
		thread.setConnectTimeout(this.connectTimeout);
		thread.setReadTimeout(this.readTimeout);
		return  thread;
	}

	@Override
	public synchronized void addTask ( Task task ) {
		currentTasks.put( task.getIdentifier(), (DereferencingTask) task );
		super.addTask( task );
	}


	// implementation of the DereferencingListener interface

	public synchronized void dereferenced ( DereferencingResult result ) {
		currentTasks.remove( result.getTask().getIdentifier() );
	}


	// accessors

	/**
	 * Returns the task identified by the given ID (if any).
	 */
	public synchronized DereferencingTask getTask ( String identifier ) {
		return currentTasks.get( identifier );
	}

}
