// $Id: NamedGraphStatement.java,v 1.2 2004/09/15 08:21:59 bizer Exp $

package de.fuberlin.wiwiss.namedgraphs;



import java.util.ArrayList;

import java.util.Iterator;

import java.util.List;



import com.hp.hpl.jena.graph.Node;

import com.hp.hpl.jena.rdf.model.Property;

import com.hp.hpl.jena.rdf.model.RDFNode;

import com.hp.hpl.jena.rdf.model.Resource;

import com.hp.hpl.jena.rdf.model.Statement;

import com.hp.hpl.jena.rdf.model.impl.StatementImpl;



/**

 * A Statement which can provide provenance information about the

 * {@link NamedGraph}s in which it is contained. NamedGraphStatements

 * are used in {@link NamedGraphModel}s. Those are backed by a

 * NamedGraphSet. The NamedGraphStatement knows in which graphs from

 * that set it is contained.

 * <p>

 * TODO: Write tests for NamedGraphStatement

 * @author Chris Bizer

 * @author Richard Cyganiak (richard@cyganiak.de)

 */

public class NamedGraphStatement extends StatementImpl {

	private NamedGraphSet namedGraphSet;



	public NamedGraphStatement(Resource s, Property p, RDFNode o, NamedGraphModel m) {

		super(s, p, o, m);

		this.namedGraphSet = m.getNamedGraphSet();

	}



	/**

	 * Returns a NamedGraph that contains the statement. If several

	 * graphs contain the statement, one of them will be returned

	 * arbitrarily. <tt>null</tt> will be returned if no graph contains

	 * the statement.

	 * 

	 * @return A NamedGraph containing the statement

	 */

	public NamedGraph getGraph() {

		Iterator it = this.namedGraphSet.findQuads(

				Node.ANY,

				getSubject().asNode(),

				getPredicate().asNode(),

				getObject().asNode());

		if (!it.hasNext()) {

			return null;

		}

		return this.namedGraphSet.getGraph(((Quad) it.next()).getGraphName());

	}



	/**

	 * Returns a NamedGraph that contains the statement. If several

	 * graphs contain the statement, one of them will be returned

	 * arbitrarily. <tt>null</tt> will be returned if no graph contains

	 * the statement.

	 * 

	 * @return The name of a NamedGraph containing the statement

	 */

	public Resource getGraphName() {

		return this.getModel().createResource(getGraph().getGraphName().getURI());

	}



	/**

	 * Finds the {@link NamedGraph}s which contain the statement.

	 * @return An iterator over NamedGraphs.

	 */

	public Iterator listGraphs() {

		// This is probably rather slow.

		List graphs = new ArrayList();

		Iterator it = this.namedGraphSet.findQuads(

				Node.ANY,

				getSubject().asNode(),

				getPredicate().asNode(),

				getObject().asNode());

		while (it.hasNext()) {

			Quad quad = (Quad) it.next();

			graphs.add(this.namedGraphSet.getGraph(quad.getGraphName()));

		}

		return graphs.iterator();	

	}



	/**

	 * Returns the names of NamedGraphs containing the

	 * statement as Jena {@link Resource}s. They can be handy for

	 * creating {@link Statement}s about the graphs.

	 * @return An iterator over Jena Resources.

	 */

	public Iterator listGraphNames() {

		// This is probably rather slow.

		List graphs = new ArrayList();

		Iterator it = this.namedGraphSet.findQuads(

				Node.ANY,

				getSubject().asNode(),

				getPredicate().asNode(),

				getObject().asNode());

		while (it.hasNext()) {

			Quad quad = (Quad) it.next();

			NamedGraph graph = this.namedGraphSet.getGraph(quad.getGraphName());

			graphs.add(getModel().createResource(graph.getGraphName().getURI()));

		}

		return graphs.iterator();	

	}



	/**

	 * Finds all {@link Quad}s which contain the subject, predicate and

	 * object of the statement.

	 * @return an iterator over Quads.

	 */

	public Iterator listQuads() {

		return this.namedGraphSet.findQuads(

				Node.ANY,

				getSubject().asNode(),

				getPredicate().asNode(),

				getObject().asNode());

	}

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

