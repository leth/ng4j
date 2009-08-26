package de.fuberlin.wiwiss.jenaext;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;


/**
 * An RDF graph implementation that uses identifiers for RDF nodes.
 *
 * @author Olaf Hartig
 */
public interface IdBasedGraph extends Graph
{
	/**
	 * Returns the node dictionary used by this graph.
	 */
	public NodeDictionary getNodeDictionary ();

	/**
	 * Answer true iff the graph contains a triple matching the triple pattern
	 * specified by the given identifiers.
	 * An identifier of -1 represents a wildcard.
	 *
	 * @param sId the identifier for the subject of the triple pattern
	 * @param pId the identifier for the predicate of the triple pattern
	 * @param oId the identifier for the object of the triple pattern
	 */
	public boolean contains ( int sId, int pId, int oId );

	/**
	 * Executes a triple pattern query specified by the given identifiers.
	 * An identifier of -1 represents a wildcard.
	 *
	 * @param sId the identifier for the subject of the triple pattern
	 * @param pId the identifier for the predicate of the triple pattern
	 * @param oId the identifier for the object of the triple pattern
	 */
	public Iterator<IdBasedTriple> find ( int sId, int pId, int oId );
}

/*
 * (c) Copyright 2009 Christian Bizer (chris@bizer.de)
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