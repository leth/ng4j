package de.fuberlin.wiwiss.ng4j.semwebclient.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;


/**
 * Named graph set that is particularily well suited for the Semantic Web
 * client.
 * Uses specific graph implementations that are optimized for read-only access.
 * Furthermore, during the execution of triple pattern queries these graph
 * implementations use identifiers for RDF nodes instead of the nodes themself.
 *
 * @author Olaf Hartig
 */
public class SWClNamedGraphSetImpl extends NamedGraphSetImpl
{
	// members

	final protected NodeDictionary nodeDict = new NodeDictionary ();


	// redefinitions of NamedGraphSetImpl methods

	/**
	 * Uses the RDF graph implementation and the named graph implementation for
	 * the Semantic Web client ({@link SWClGraphMem} and {@link SWClNamedGraphImpl}).
	 */
	@Override
	protected NamedGraph createNamedGraphInstance ( Node graphName )
	{
		if ( ! graphName.isURI() ) {
			throw new IllegalArgumentException( "Graph names must be URIs" );
		}

		return new SWClNamedGraphImpl( graphName, new SWClGraphMem(nodeDict,ReificationStyle.Standard) );
	}

	/**
	 * Uses the union graph defined below.
	 */
	@Override
	public Graph asJenaGraph ( Node defaultGraphForAdding )
	{
		if ( defaultGraphForAdding != null && ! containsGraph(defaultGraphForAdding) ) {
			createGraph( defaultGraphForAdding );
		}

		UnionGraph unionGraph = new UnionGraph( graphs );
		if ( defaultGraphForAdding != null ) {
			unionGraph.setBaseGraph( getGraph(defaultGraphForAdding) );
		}

		return unionGraph;
	}


	// helpers

	/**
	 * A graph implementation that wraps all named graphs of a named graph set
	 * and that uses the identifier-based query execution.
	 */
	class UnionGraph extends NamedGraphSetImpl.UnionGraph
	{
		public UnionGraph ( List<NamedGraph> members )
		{
			super( members );
		}

		public ExtendedIterator graphBaseFind ( TripleMatch m )
		{
			Node matchSubject = m.getMatchSubject();
			Node matchPredicate = m.getMatchPredicate();
			Node matchObject = m.getMatchObject();

			int sId = ( matchSubject == null ) ? -1 : nodeDict.getId( matchSubject );
			int pId = ( matchPredicate == null ) ? -1 : nodeDict.getId( matchPredicate );
			int oId = ( matchObject == null ) ? -1 : nodeDict.getId( matchObject );

			if (    ( matchSubject != null && sId < 0 )
			     || ( matchPredicate != null && pId < 0 )
			     || ( matchObject != null && oId < 0 ) ) {
				return WrappedIterator.create( EmptyIterator.emptyTripleIterator );
			}

			Set seen = new HashSet();
 			ExtendedIterator result = WrappedIterator.create( EmptyIterator.emptyTripleIterator );
			Iterator itGraph = m_subGraphs.iterator();
			while ( itGraph.hasNext() )
			{
				SWClNamedGraphImpl ng = (SWClNamedGraphImpl) itGraph.next();
				ExtendedIterator itFind = new ConvertingIterator( ng.find(sId,pId,oId) );
				ExtendedIterator newTriples = recording( rejecting( itFind, seen ), seen );
				result = result.andThen( newTriples );
			}
			return result;
		}
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