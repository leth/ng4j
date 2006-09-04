package de.fuberlin.wiwiss.ng4j.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;

public class UriConnectorDeref extends Thread {
	private URL url;
	private String uri;
	private HttpURLConnection connection;
	private URIRetriever retriever;
	
	
	public UriConnectorDeref(URIRetriever retriever, String uri){
		this.retriever = retriever;
		try{
			this.url = new URL(uri);
			this.uri = uri;
		}catch(MalformedURLException e){
			System.out.println("malformed URL");
			this.url = null;
		}
//		System.out.println(this.retriever.getClient().threadcounter++);
	}
	public String getUriString(){
		return this.url.toString();
	}
	synchronized public void run(){
		//		Debug
	//	System.out.println("Thread "+this.getName()+" startet (deref)");
		try{
			this.connection = (HttpURLConnection) this.url.openConnection();
		}catch(IOException e){
			System.out.println("unable to connect");
		}
		if(this.connection.getContentType() != null)
		if(this.connection.getContentType().equals("application/rdf+xml")){
			// Parse RDF File and add content to the graphset
			this.parseRdf();
			// remove uri from the "to retrieve" list and add it to the "retrieved" list
			this.retriever.getClient().getUrisToRetrieve().remove(this.uri);
			this.retriever.getClient().getRetrievedUris().add(this.uri);
			
			/// lookup for rdfs:seeAlso
			this.retriever.seeAlsoUri(this.uri);
//			 dbug
//			System.out.println("Function: derefUris. Retrieval finished. NgsSize:"+this.retriever.getClient().getNamedGraphSet().countQuads());
			
			//		Debug
//			System.out.println("Thread "+this.getName()+" finished (deref)");
			this.addProvenanceInformation();
		}
		this.retriever.getThreadList().remove(this);
//		System.out.println("Waiting Threads: "+this.retriever.waitingThreads.size());
	}
	
	
	
	//------------------------------------------------//

	
	synchronized private void parseRdf(){
		try{
			this.retriever.getClient().getNamedGraphSet().read(this.connection.getInputStream(),null,this.url.toString());
		}catch(Exception e){
			System.out.println("IoException");
		}
	}
	
	synchronized private void addProvenanceInformation(){
		String label = Long.toString(Calendar.getInstance().getTimeInMillis());
		NamedGraph provenanceGraph = this.retriever.getClient().getNamedGraphSet().getGraph("http://localhost/provenanceInformation");
		provenanceGraph.add(new Triple(Node.createURI(this.uri),Node.createURI("http://www.w3.org/2004/03/trix/swp-2/sourceURL"),Node.createURI(this.uri)));
		provenanceGraph.add(new Triple(Node.createURI(this.uri),Node.createURI("http://www.w3.org/2004/03/trix/swp-2/retrievalTimestamp"),Node.createLiteral(label)));
	}

}
