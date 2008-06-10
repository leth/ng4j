package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskExecutorBase;
import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.TaskQueueBase;


/**
 * The DereferencingTaskQueue is a thread which observes the
 * DereferencerThreads. It starts all DereferencerThreads tries to 
 * assign new tasks to free DereferencerThreads and interrupts them
 * if the timeout is reached.
 * 
 * @author Tobias Gauﬂ
 * @author Olaf Hartig
 */
public class DereferencingTaskQueue extends TaskQueueBase {
	private Log log = LogFactory.getLog(DereferencingTaskQueue.class);
	private int maxfilesize;
        private boolean enablegrddl;

        public DereferencingTaskQueue(int maxThreads,int maxfilesize, boolean enablegrddl) {
		super( maxThreads );
		this.maxfilesize = maxfilesize;
		this.enablegrddl = enablegrddl;
		setName("DereferencingTaskQueue");
		start();
	}


	// implementation of the TaskQueueBase interface

	protected TaskExecutorBase createThread () {
		DereferencerThread thread = new DereferencerThread();
		thread.setMaxfilesize(this.maxfilesize);
		thread.setEnableGrddl(this.enablegrddl);
		return  thread;
	}

}
