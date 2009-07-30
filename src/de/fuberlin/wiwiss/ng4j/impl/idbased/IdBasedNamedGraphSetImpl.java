package de.fuberlin.wiwiss.ng4j.impl.idbased;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
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
public class IdBasedNamedGraphSetImpl extends NamedGraphSetImpl
{
	// members

	final protected NodeDictionary nodeDict = new NodeDictionary ();


	// redefinitions of NamedGraphSetImpl methods

	/**
	 * Uses the RDF graph implementation and the named graph implementation for
	 * the Semantic Web client ({@link IdBasedGraphMem} and {@link IdBasedNamedGraphImpl}).
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl#createNamedGraphInstance(com.hp.hpl.jena.graph.Node)
	 */
	@Override
	protected NamedGraph createNamedGraphInstance ( Node graphName )
	{
		if ( ! graphName.isURI() ) {
			throw new IllegalArgumentException( "Graph names must be URIs" );
		}

		return new IdBasedNamedGraphImpl( graphName, new IdBasedGraphMem(nodeDict,ReificationStyle.Standard) );
	}

	/**
	 * Uses the union graph defined below.
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl#asJenaGraph(com.hp.hpl.jena.graph.Node)
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
	                 implements IdBasedGraph
	{
		public UnionGraph ( List<NamedGraph> members )
		{
			super( members );
		}

		/**
		 * Returns a query handler (see {@link IdBasedQueryHandler} that is based
		 * on the identifiers used to represent RDF nodes in the RDF graphs that
		 * make up this union graph.
		 * 
		 * @see com.hp.hpl.jena.graph.compose.MultiUnion#queryHandler()
		 */
		@Override
		public QueryHandler queryHandler ()
		{
			if ( queryHandler == null ) {
				queryHandler = new IdBasedQueryHandler( this );
			}
			return queryHandler;
		}

		/* (non-Javadoc)
		 * @see com.hp.hpl.jena.graph.compose.MultiUnion#graphBaseFind(com.hp.hpl.jena.graph.TripleMatch)
		 */
		@Override
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

			return new ConvertingIterator( find(sId,pId,oId) );
		}


		// implementation of the IdBasedGraph interface

		/* (non-Javadoc)
		 * @see de.fuberlin.wiwiss.ng4j.impl.idbased.IdBasedGraph#getNode(int)
		 */
		public Node getNode ( int id )
		{
			return nodeDict.getNode( id );
		}

		/* (non-Javadoc)
		 * @see de.fuberlin.wiwiss.ng4j.impl.idbased.IdBasedGraph#getId(com.hp.hpl.jena.graph.Node)
		 */
		public int getId ( Node n )
		{
			return nodeDict.getId( n );
		}

		/* (non-Javadoc)
		 * @see de.fuberlin.wiwiss.ng4j.impl.idbased.IdBasedGraph#contains(int, int, int)
		 */
		public boolean contains ( int sId, int pId, int oId )
		{
			Iterator itGraph = m_subGraphs.iterator();
			while ( itGraph.hasNext() )
			{
				IdBasedNamedGraphImpl ng = (IdBasedNamedGraphImpl) itGraph.next();
				if ( ng.contains(sId,pId,oId) ) {
					return true;
				}
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see de.fuberlin.wiwiss.ng4j.impl.idbased.IdBasedGraph#find(int, int, int)
		 */
		public Iterator<EncodedTriple> find ( int sId, int pId, int oId )
		{
			return new UnionFindIterator( m_subGraphs, sId, pId, oId );
		}
	}

	/**
	 * Evaluates a find query (triple pattern query) over a set of RDF graphs
	 * where the result is a union of the triples found in the graphs.
	 * Hence, a matching triple that occurs in multiple graphs is returned only
	 * once.
	 */
	static class UnionFindIterator implements Iterator<EncodedTriple>
	{
		final protected List<Graph> graphs;
		final protected int sId;
		final protected int pId;
		final protected int oId;

		/**
		 * This index stores the matching triples that have already been returned
		 * in order to skip later occurences of them.
		 */
		final protected Index seen = new Index ();

		/**
		 * Represents the element of the seen triples that is used as index key
		 * in {@link #seen}.
		 * 1 for subject, 2 for predicate, 3 for object
		 */
		final protected byte seenIndexKey;

		protected Iterator<Graph> itCurrentGraph;
		protected Iterator<EncodedTriple> itCurrentMatch;
		protected EncodedTriple currentMatch;

		/**
		 * @param graphs the graphs in this list must be instances of the class
		 *               {@link IdBasedNamedGraphImpl}
		 * @param sId identifier representing the subject of the triple pattern
		 * @param pId identifier representing the predicate of the triple pattern
		 * @param oId identifier representing the object of the triple pattern
		 */
		public UnionFindIterator ( List<Graph> graphs, int sId, int pId, int oId )
		{
			this.graphs = graphs;
			this.sId = sId;
			this.pId = pId;
			this.oId = oId;

			if ( sId == -1 ) {
				seenIndexKey = 1;
			} else if ( pId == -1 ) {
				seenIndexKey = 2;
			} else {
				seenIndexKey = 3;
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext ()
		{
			if ( currentMatch != null ) {
				return true;
			}

			while ( currentMatch == null || hasSeen(currentMatch) )
			{
				while ( itCurrentMatch == null || ! itCurrentMatch.hasNext() )
				{
					if ( itCurrentGraph == null ) {
						itCurrentGraph = graphs.iterator();
					}

					if ( ! itCurrentGraph.hasNext() )
					{
						currentMatch = null;
						return false;
					}

					itCurrentMatch = ( (IdBasedNamedGraphImpl) itCurrentGraph.next() ).find( sId, pId, oId );
				}

				currentMatch = itCurrentMatch.next();
			}

			return true;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public EncodedTriple next ()
		{
			if ( ! hasNext() ) {
				throw new NoSuchElementException();
			}

			recordAsSeen( currentMatch );
			EncodedTriple result = currentMatch;
			currentMatch = null;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove () { throw new UnsupportedOperationException(); }

		protected boolean hasSeen ( EncodedTriple et )
		{
			Iterator<EncodedTriple> itBucket;
			if ( seenIndexKey == 1 ) {
				itBucket = seen.get( et.s );
			} else if ( seenIndexKey == 2 ) {
				itBucket = seen.get( et.p );
			} else {
				itBucket = seen.get( et.o );
			}

			while ( itBucket.hasNext() )
			{
				if ( itBucket.next().equals(et) ) {
					return true;
				}
			}

			return false;
		}

		protected void recordAsSeen ( EncodedTriple et )
		{
			if ( seenIndexKey == 1 ) {
				seen.put( et.s, et );
			} else if ( seenIndexKey == 2 ) {
				seen.put( et.p, et );
			} else {
				seen.put( et.o, et );
			}
		}
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