// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/semwebclient/graph/SWClGraphMem.java,v 1.8 2010/02/25 14:28:21 hartig Exp $
package de.fuberlin.wiwiss.ng4j.semwebclient.graph;

import com.hp.hpl.jena.shared.ReificationStyle;

import de.fuberlin.wiwiss.jenaext.NodeDictionary;
import de.fuberlin.wiwiss.jenaext.impl.IdBasedGraphMem;


/**
 * An RDF graph implemented using on six main memory-based indexes (S, P, O,
 * SP, PO, SO).
 * This implementation is optimized for read-only access.
 *
 * @author Olaf Hartig
 */
public class SWClGraphMem extends IdBasedGraphMem
{
	// initialization

	/**
	 * Creates a graph with reification style Minimal.
	 *
	 * @param nodeDict the node dictionary used to get and create identifiers
	 *                 for RDF nodes that occur in triple pattern queries issued
	 *                 to this graph ({@link #graphBaseFind}) and for RDF nodes
	 *                 that occur in triples added to this graph
	 */
	public SWClGraphMem ( NodeDictionary nodeDict )
	{
		super( nodeDict );
	}

	/**
	 * Creates a graph with the given reification style.
	 *
	 * @param nodeDict the node dictionary used to get and create identifiers
	 *                 for RDF nodes that occur in triple pattern queries issued
	 *                 to this graph ({@link #graphBaseFind}) and for RDF nodes
	 *                 that occur in triples added to this graph
	 * @param style the reification style to be used for this graph
	 */
	public SWClGraphMem ( NodeDictionary nodeDict, ReificationStyle style )
	{
		super( nodeDict, style );
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