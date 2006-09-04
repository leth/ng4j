package de.fuberlin.wiwiss.ng4j.impl;

import java.util.Iterator;
import java.util.LinkedList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.ListListener;
import de.fuberlin.wiwiss.ng4j.NamedGraph;


public class URIRetriever implements ListListener{
	private SemanticWebClientImpl client;
	private ThreadList threadlist = new ThreadList();
	public LinkedList waitingThreads = new LinkedList();
	
	public URIRetriever(SemanticWebClientImpl client){
		this.client = client;
		this.threadlist.addListListener(this);
	}
	
	
	
	///////////// Listener Functions ////////////
	
	/**
	 * Generate Thread and write it to the Threadlist
	 * or the list for waiting threads if the threadlist is full
	 * 
	 */
	public void retrieveUri(UriListEvent e){
		String uri = e.getUri();
		this.seeAlsoUri(uri);
		this.derefUri(uri);
	}
	

	/**
	 * if added to the threadlist start the thread
	 * 
	 */
	public void startRetrieve(ThreadListEvent e){
		Thread t = e.getThread();
		t.start();
	}
	
	public void retrievalFinished(ThreadListEvent e){
		//dbug
		System.out.println("FINISH!!!. NGS Size: "+this.getClient().getNamedGraphSet().countQuads());
		System.out.println("Graphlist: ");
		Iterator it = this.getClient().getNamedGraphSet().listGraphs();
		while(it.hasNext()){
			NamedGraph g = (NamedGraph)it.next();
			System.out.println("Graph: "+g.getGraphName().getURI()+" contains: "+g.size()+" Triples.");
		}
		NamedGraph gr = this.getClient().getNamedGraphSet().getGraph("http://localhost/provenanceInformation");
		Iterator iter = gr.find(Node.ANY,Node.ANY,Node.ANY);
		while(iter.hasNext()){
			Triple t = (Triple)iter.next();
			System.out.println(t.toString());
		}
	}
	
	/**
	 * if remove is performed. Check if the waiting list is empty. if not
	 * add the first in thread to the threadlist.
	 */
	synchronized public void finishRetrieve(ThreadListEvent e){
		if(!this.waitingThreads.isEmpty()){
			Thread t = (Thread) this.waitingThreads.getFirst();
			int i = this.waitingThreads.indexOf(t);
			
			this.waitingThreads.remove(i);
			this.threadlist.add(t);
		}
	}
	
	
	
	
	
	
	
	///////////////////////////////////////////////
	
	synchronized public void derefUri(String uri){
		Thread t = new UriConnectorDeref(this,uri);
		if(this.threadlist.size()<20){
			this.threadlist.add(t);
		}else{
			this.waitingThreads.add(t);
		}
	}
	synchronized public void seeAlsoUri(String uri){
		Thread t = new UriConnectorSeeAlso(this,uri);
		if(this.threadlist.size()<20){
			this.threadlist.add(t);
		}else{
			this.waitingThreads.add(t);
		}
	}
	
	public SemanticWebClientImpl getClient(){
		return this.client;
	}
	
	public ThreadList getThreadList(){
		return this.threadlist;
	}

}
