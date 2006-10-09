package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The DereferencingTaskQueue is a thread which observes the
 * DereferencerThreads. It starts all DereferencerThreads tries to 
 * assign new tasks to free DereferencerThreads and interrupts them
 * if the timeout is reached.
 */
public class DereferencingTaskQueue extends Thread {
	private int maxthreads;
	private List threads = new ArrayList();
	private boolean stopped = false;
	private LinkedList tasks = new LinkedList();
	private Log log = LogFactory.getLog(DereferencingTaskQueue.class);
	
	public DereferencingTaskQueue(int maxThreads) {
		this.maxthreads = maxThreads;
		setName("Queue");
		start();
	}
	
	public synchronized void addTask(DereferencingTask task) {
		this.tasks.addLast(task);
		this.log.debug("Enqueue: <" + task.getURI() + ">@" + task.getStep() + 
				" (n = " + this.tasks.size() + ")");
		this.notify();
	}

	public void run() {
		initThreadPool(this.maxthreads);
		while (!this.stopped) {
			checkForTasksAndWait();
		}
	}

	public synchronized void close() {
		Iterator it = this.threads.iterator();
		while (it.hasNext()) {
			DereferencerThread thread = (DereferencerThread) it.next();
			thread.stopThread();
		}
		this.stopped = true;
		notify();
	}
	
	private void checkForTasksAndWait() {
		while (!this.tasks.isEmpty()) {
			DereferencingTask task = (DereferencingTask) this.tasks.getFirst();
			if (tryAssignTask(task)) {
				this.tasks.removeFirst();
				this.log.debug("Dequeue: <" + task.getURI() + ">@" + task.getStep() + 
						" (n = " + this.tasks.size() + ")");
			} else {
				break;
			}
		}
		try {
			// TODO Wake up when a worker thread is finished
			synchronized (this) {
				wait(100);
			}
		} catch (InterruptedException ex) {
			// Don't know when this happens
			throw new RuntimeException(ex);
		}
	}

	private boolean tryAssignTask(DereferencingTask task) {
		Iterator it = this.threads.iterator();
		while (it.hasNext()) {
			DereferencerThread thread = (DereferencerThread) it.next();
			if (thread.startDereferencingIfAvailable(task)) {
				return true;
			}
		}
		return false;
	}

	private void initThreadPool(int numThreads) {
		for (int i = 0; i < numThreads; i++) {
			DereferencerThread thread = new DereferencerThread();
			thread.setName("DerefThread"+i);
			thread.start();
			this.threads.add(thread);
		}
	}
}
