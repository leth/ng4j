package de.fuberlin.wiwiss.jenaext.impl;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.SimpleBulkUpdateHandler;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;
import de.fuberlin.wiwiss.jenaext.IdBasedTriple;


/**
 * A bulk update handler for RDF graph implementations that use identifiers
 *  for RDF nodes (ie for {@link IdBasedGraph} implementations).
 *
 * @author Olaf Hartig
 */
public class IdBasedBulkUpdateHandler extends SimpleBulkUpdateHandler
{
	// initialization

	public IdBasedBulkUpdateHandler( IdBasedGraphMem graph )
	{
		super( graph );
	}


	// overridden base class methods

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.impl.SimpleBulkUpdateHandler#remove(com.hp.hpl.jena.graph.Node,com.hp.hpl.jena.graph.Node,com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public void remove ( Node s, Node p, Node o )
	{
		IdBasedGraphMem g = (IdBasedGraphMem) graph;
		Set<IdBasedTriple> tmp = new HashSet<IdBasedTriple> ();
		Iterator<IdBasedTriple> it = g.findIdBased( s, p, o );
		while ( it.hasNext() ) {
			tmp.add( it.next() );
		}

		for ( IdBasedTriple t : tmp ) {
			g.delete( t );
		}

		manager.notifyEvent( graph, GraphEvents.remove(s,p,o) );
	}

}

/*
 * (c) Copyright 2009 - 2010 Christian Bizer (chris@bizer.de)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */