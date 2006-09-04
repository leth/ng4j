package de.fuberlin.wiwiss.ng4j.impl;

import java.util.ArrayList;

import de.fuberlin.wiwiss.ng4j.ListListener;

public class ThreadList extends ArrayList{
private ListListener listener;
	
	public void addListListener(ListListener listener){
		this.listener = listener;
	}
	
	public void removeListListener(ListListener listener){
		this.listener = null;
	}
	synchronized public boolean add(Thread thread){
		this.listener.startRetrieve(new ThreadListEvent(this,thread));
		return super.add(thread);
	}
	synchronized public boolean remove(Thread thread){
		this.listener.finishRetrieve(new ThreadListEvent(this,thread));
		if(this.size() == 1){
			this.listener.retrievalFinished(new ThreadListEvent(this,thread));
		}
		return super.remove(thread);
	}

}
