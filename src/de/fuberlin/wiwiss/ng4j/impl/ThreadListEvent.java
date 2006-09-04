package de.fuberlin.wiwiss.ng4j.impl;

import java.util.EventObject;

public class ThreadListEvent extends EventObject{
	private Thread thread;

	public ThreadListEvent(Object source,Thread thread){
		super(source);
		this.thread = thread;
	}
	
	public Thread getThread(){
		return this.thread;
	}
	
}
