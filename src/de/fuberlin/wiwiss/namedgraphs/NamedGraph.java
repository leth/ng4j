
// $Id: NamedGraph.java,v 1.2 2004/09/15 08:21:59 bizer Exp $

package de.fuberlin.wiwiss.namedgraphs;



import com.hp.hpl.jena.graph.Graph;

import com.hp.hpl.jena.graph.Node;



/**

 * A collection of RDF triples which is named by an URI. 

 * For details about Named Graphs see the

 * <a href="http://www.w3.org/2004/03/trix/">Named Graphs homepage</a>.

 * <p>

 * The core interface is small (add, delete, find, contains) and

 * is augmented by additional classes to handle more complicated matters

 * such as reification, query handling, bulk update, event management,

 * and transaction handling.

 *

 * @author Chris Bizer

 */

public interface NamedGraph extends Graph {



	/**

	 * Returns the URI of the named graph. The returned Node

	 * instance is always an URI and cannot be a blank node

	 * or literal.

	 */

	public Node getGraphName();

}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
 *   All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */