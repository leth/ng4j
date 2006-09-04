package de.fuberlin.wiwiss.ng4j.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

public class UriConnectorSeeAlso extends Thread {
	private String uri;
	private URL url;
	private URIRetriever retriever;
	
	
	public UriConnectorSeeAlso(URIRetriever retriever, String uri){
		this.retriever = retriever;
		this.uri       = uri;		
		try{
			this.url = new URL(uri);
			this.uri = uri;
		}catch(MalformedURLException e){
			System.out.println("malformed URL");
			this.url = null;
		}
	//dbug
		//	System.out.println(this.retriever.getClient().threadcounter++);
	}
	
	public void run(){
//		Debug
	//	System.out.println("Thread "+this.getName()+" startet (lookup)");
		
		Node node = Node.createURI(this.uri);
		this.lookupNgs(node);
		
		this.retriever.getClient().getUrisToRetrieve().remove(this.uri);
		this.retriever.getClient().getRetrievedUris().add(this.uri);
		
		
//		 dbug
//		System.out.println("Function: lookupNgs. Retrieval finished. NgsSize:"+this.retriever.getClient().getNamedGraphSet().countQuads());
//		Debug
//		System.out.println("Thread "+this.getName()+" finished (lookup)");
		
		this.retriever.getThreadList().remove(this);
		// dbug
//		System.out.println("Waiting Threads: "+this.retriever.waitingThreads.size());
	}
	
	
	
	//------------------------------------------------//
	
	
	synchronized public void lookupNgs(Node node){
		Iterator iter = this.retriever.getClient().getNamedGraphSet().findQuads(Node.ANY,node,Node.createURI("http://www.w3.org/2000/01/rdf-schema#seeAlso"),Node.ANY);
		while(iter.hasNext()){
			Quad quad = (Quad)iter.next();
			Node obj = quad.getObject();
			if(obj.isURI()){
				this.retriever.derefUri(obj.getURI());
			}
		}
	}
	
}
