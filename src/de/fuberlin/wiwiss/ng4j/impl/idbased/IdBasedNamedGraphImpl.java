package de.fuberlin.wiwiss.ng4j.impl.idbased;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;
import de.fuberlin.wiwiss.jenaext.IdBasedTriple;
import de.fuberlin.wiwiss.jenaext.NodeDictionary;

/**
 * A named graph that is based on a main memory implementation of RDF graphs
 * and that is particularily well suited for the Semantic Web client.
 *
 * @author Olaf Hartig
 */
public class IdBasedNamedGraphImpl extends NamedGraphImpl
                                   implements IdBasedGraph
{
	// members

	final protected IdBasedGraph idGraph;


	// initialization

	public IdBasedNamedGraphImpl ( Node graphName, IdBasedGraph graph )
	{
		super( graphName, graph );
		idGraph = graph;
	}

	public IdBasedNamedGraphImpl ( String graphNameURI, IdBasedGraph graph )
	{
		super( graphNameURI, graph );
		idGraph = graph;
	}


	// implementation of the IdBasedGraph interface

	/**
	 * Calls the corresponding method of the embedded graph.
	 * 
	 * @see de.fuberlin.wiwiss.jenaext.IdBasedGraph#getNodeDictionary()
	 */
	final public NodeDictionary getNodeDictionary ()
	{
		return idGraph.getNodeDictionary();
	}

	/**
	 * Calls the corresponding method of the embedded graph ({@link IdBasedGraph#contains}).
	 * 
	 * @see de.fuberlin.wiwiss.jenaext.IdBasedGraph#contains(int, int, int)
	 */
	final public boolean contains ( int sId, int pId, int oId )
	{
		return idGraph.contains( sId, pId, oId );
	}

	/**
	 * Calls the corresponding method of the embedded graph ({@link IdBasedGraph#find}).
	 * 
	 * @see de.fuberlin.wiwiss.jenaext.IdBasedGraph#find(int, int, int)
	 */
	final public Iterator<IdBasedTriple> find ( int sId, int pId, int oId )
	{
		return idGraph.find( sId, pId, oId );
	}

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