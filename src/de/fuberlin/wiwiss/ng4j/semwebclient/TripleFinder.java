package de.fuberlin.wiwiss.ng4j.semwebclient;

import com.hp.hpl.jena.graph.Node;

/**
 * A Thread which starts a retrieval process and reports every found triple
 * to a listener.
 * 
 * @author Tobias Gauﬂ
 */
public class TripleFinder extends Thread{
	private Node sub;
	private Node pred;
	private Node obj;
	private SemanticWebClientImpl client;
	private TripleListener listener;
	
	/**
	 * Constructs a TripleFinder.
	 * 
	 * @param sub The subject Node.
	 * @param pred The predicate Node.
	 * @param obj The object Node.
	 * @param client The corresponding SemanticWebClient.
	 * @param listener The TripleListener.
	 */
	public TripleFinder(Node sub, Node pred, Node obj, SemanticWebClientImpl client, TripleListener listener){
		this.sub = sub;
		this.pred = pred;
		this.obj = obj;
		this.client = client;
		this.listener = listener;
	}
	
	public void run(){
		SemWebIterator iter2 = new SemWebIterator(this.client,this.sub,this.pred,this.obj);
		while(iter2.hasNext()){
			SemWebTriple triple = (SemWebTriple) iter2.next();
			listener.tripleFound(new TripleFoundEvent(this,triple));
		}
		this.listener.findFinished(new TripleFoundEvent(this,null));
	}

}
