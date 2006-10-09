package de.fuberlin.wiwiss.ng4j.semwebclient;

import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

public class SemWebMultiUnion extends GraphBase{
	private SemanticWebClient client;
	
	public SemWebMultiUnion(){
		super();
	}
	public SemWebMultiUnion(SemanticWebClient client){
		super();
		this.client = client;
		
	}

	public ExtendedIterator graphBaseFind(TripleMatch m){
		return WrappedIterator.create(this.client.find(m));
		
	}
	

}
