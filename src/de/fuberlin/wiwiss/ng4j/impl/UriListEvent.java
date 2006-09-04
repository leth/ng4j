package de.fuberlin.wiwiss.ng4j.impl;

import java.util.EventObject;

public class UriListEvent extends EventObject{
	private String uri;
	
	public UriListEvent (Object src,String uri){
		super(src);
		this.uri = uri;
	}
	
	public String getUri(){
		return this.uri;
	}
	

}
