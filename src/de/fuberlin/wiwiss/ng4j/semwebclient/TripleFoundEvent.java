package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.EventObject;

/**
 * Event which is generated when a Triple is matched.
 * 
 * @author Tobias Gauﬂ
 *
 */
public class TripleFoundEvent extends EventObject{
	
	/**
	 * The SemWebTriople.
	 */
	private SemWebTriple triple;
	
	/**
	 * Constructs a TripleFoundEvent.
	 * 
	 * @param source The source which generates the event.
	 * @param triple The triple.
	 */
	public TripleFoundEvent(Object source, SemWebTriple triple){
		super(source);
		this.triple = triple;
	}
	
	/**
	 * @return The SemWebTriple.
	 */
	public SemWebTriple getTriple(){
		return this.triple;
	}

}
