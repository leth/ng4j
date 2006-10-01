package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.graph.impl.SimpleEventManager;
import com.hp.hpl.jena.util.CollectionFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NullIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

public class SemWebMultiUnion extends GraphBase{
	private SemanticWebClientImpl client;
	
	public SemWebMultiUnion(){
		super();
	}
	public SemWebMultiUnion(SemanticWebClientImpl client){
		super();
		this.client = client;
		
	}

	public ExtendedIterator graphBaseFind(TripleMatch m){
		/*
		TestListener l = new TestListener();
		this.client.find(m,l);
		while(!l.finished){
		}
		
		Triple t = m.asTriple();
		ExtendedIterator it = WrappedIterator.create(this.client.findQuads(Node.ANY,t.getSubject(),t.getPredicate(),t.getObject()));
		return it;
		*/
		return WrappedIterator.create(this.client.find(m));
		
	}
	

}
