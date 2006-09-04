package de.fuberlin.wiwiss.ng4j.impl;

import java.net.URL;
import java.util.ArrayList;

import de.fuberlin.wiwiss.ng4j.ListListener;

public class UriList extends ArrayList{
	private ListListener listener;
	
	public void addListListener(ListListener listener){
		this.listener = listener;
	}
	
	public void removeListListener(ListListener listener){
		this.listener = null;
	}
	synchronized public boolean add(String uri){
		this.listener.retrieveUri(new UriListEvent(this,uri));
		return super.add(uri);
	}

}
