package de.fuberlin.wiwiss.ng4j.impl.idbased;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;


/**
 * A dictionary that assigns identifiers to RDF nodes.
 *
 * @author Olaf Hartig
 */
public class NodeDictionary
{
	// members

	final protected ArrayList<Node> dictId2Node = new ArrayList<Node> ();
	final protected Map<String,Integer> dictURINode2Id = new HashMap<String,Integer> ();
	final protected Map<String,Integer> dictBlankNode2Id = new HashMap<String,Integer> ();
	final protected Map<String,Integer> dictLitNode2Id = new HashMap<String,Integer> ();


	// accessors

	/** Returns the node identified by the given identifier (or null). */
	final public Node getNode ( int id )
	{
		return dictId2Node.get( id );
	}

	/** Returns the identifier that identifies the given node (or -1). */
	final public int getId ( Node n )
	{
		Integer i;
		if ( n.isURI() ) {
			i = dictURINode2Id.get( n.getURI() );
		} else if ( n.isBlank() ) {
			i = dictBlankNode2Id.get( n.getBlankNodeId().getLabelString() );
		} else if ( n.isLiteral() ) {
			i = dictLitNode2Id.get( n.getLiteral().toString(true) );
		} else {
			i = null;
		}

		return ( i == null ) ? -1 : i.intValue();
	}


	// operations

	/**
	 * Returns an identifier that identifies the given node.
	 * If there is no identifier for the given node yet this method creates a
	 * new identifier and adds it to the dictionary.
	 */
	final public int createId ( Node n )
	{
		int i = getId( n );

		if ( i < 0 )
		{
			i = dictId2Node.size();
			dictId2Node.add( n );

			assert i < Integer.MAX_VALUE;

			if ( n.isURI() ) {
				dictURINode2Id.put( n.getURI(), Integer.valueOf(i) );
			} else if ( n.isBlank() ) {
				dictBlankNode2Id.put( n.getBlankNodeId().getLabelString(), Integer.valueOf(i) );
			} else { // if ( n.isLiteral() ) {
				dictLitNode2Id.put( n.getLiteral().toString(true), Integer.valueOf(i) );
			}
		}

		return i;
	}

}

/*
 * (c) Copyright 2006 - 2009 Christian Bizer (chris@bizer.de)
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