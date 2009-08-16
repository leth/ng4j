package de.fuberlin.wiwiss.jenaext.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.query.BindingQueryPlan;
import com.hp.hpl.jena.graph.query.ExpressionSet;
import com.hp.hpl.jena.graph.query.Mapping;
import com.hp.hpl.jena.graph.query.SimpleQueryHandler;
import com.hp.hpl.jena.graph.query.Stage;
import com.hp.hpl.jena.graph.query.Query;

import de.fuberlin.wiwiss.jenaext.IdBasedGraph;


/**
 * A query handler for RDF graph implementations that use identifiers for RDF
 * nodes (ie for {@link IdBasedGraph} implementations).
 * This query handler creates an {@link IdBasedQueryPlan}.
 *
 * @author Olaf Hartig
 */
public class IdBasedQueryHandler extends SimpleQueryHandler
{
	// initialization

	public IdBasedQueryHandler( IdBasedGraph graph )
	{
		super( graph );
	}


	// overridden base class methods

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.query.SimpleQueryHandler#patternStage(com.hp.hpl.jena.graph.query.Mapping, com.hp.hpl.jena.graph.query.ExpressionSet, com.hp.hpl.jena.graph.Triple[])
	 */
	@Override
	public Stage patternStage ( Mapping map, ExpressionSet constraints, Triple [] t )
	{
		// This QueryHandler implementation does not need this method because the
		// query plan does not use it.
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.query.SimpleQueryHandler#prepareBindings(com.hp.hpl.jena.graph.query.Query, com.hp.hpl.jena.graph.Node[])
	 */
	@Override
	public BindingQueryPlan prepareBindings ( Query q, Node [] variables )
	{
		return new IdBasedQueryPlan( (IdBasedGraph) graph, q, variables );
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.graph.query.SimpleQueryHandler#containsNode(com.hp.hpl.jena.graph.Node)
	 */
	@Override
	public boolean containsNode ( Node n )
	{
		IdBasedGraph g = (IdBasedGraph) graph;

		int id = g.getId( n );
		if ( id < 0 ) {
			return false;
		}

		return    g.contains( id, -1, -1 )
		       || g.contains( -1, id, -1 )
		       || g.contains( -1, -1, id );
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