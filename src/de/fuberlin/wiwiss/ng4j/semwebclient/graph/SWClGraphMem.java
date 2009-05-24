package de.fuberlin.wiwiss.ng4j.semwebclient.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;


/**
 * An RDF graph implemented using on six main memory-based indexes (S, P, O,
 * SP, PO, SO).
 * This implementation is optimized for read-only access.
 *
 * @author Olaf Hartig
 */
public class SWClGraphMem extends GraphBase
                          implements IdBasedGraph
{
	// members

	final protected Index indexS = new Index ();
	final protected Index indexP = new Index ();
	final protected Index indexO = new Index ();
	final protected Index2 indexSP = new Index2 ();
	final protected Index2 indexSO = new Index2 ();
	final protected Index2 indexPO = new Index2 ();

	/** the node dictionary */
	final protected NodeDictionary nodeDict;

	/**
	 * A set that holds the identifiers of all nodes that occur in the triples
	 * of this graph.
	 * This set allows to answer triple pattern queries (i.e. find queries) that
	 * contain unknown nodes (unknown to this graph) very fast because for this
	 * graph cannot contain triples that match the pattern.
	 */
	final protected Set<Integer> containedIds = new HashSet<Integer> ();


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
		super();

		assert nodeDict != null;
		this.nodeDict = nodeDict;
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
		super( style );

		assert nodeDict != null;
		this.nodeDict = nodeDict;
	}


	// implementation of the GraphBase abstract methods

	/**
	 * The standard method that implements the execution of a triple pattern
	 * query.
	 * This method obtains the identifiers for the concrete components of the
	 * given triple pattern and facilitates the identifier-based find method
	 * (i.e. {@link #find}).
	 */
	protected ExtendedIterator graphBaseFind ( TripleMatch m )
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

	/**
	 * Adds the given triple to this graph.
	 * Uses the node dictionary to obtain or (if necessary) create identifiers
	 * for the RDF nodes in the given triple.
	 */
	public void performAdd ( Triple t )
	{
		assert ( t.isConcrete() );

		EncodedTriple tEnc = new EncodedTriple( t,
		                                        nodeDict.createId(t.getSubject()),
		                                        nodeDict.createId(t.getPredicate()),
		                                        nodeDict.createId(t.getObject()) );

		indexS.put( tEnc.s, tEnc );
		indexP.put( tEnc.p, tEnc );
		indexO.put( tEnc.o, tEnc );
		indexSP.put( tEnc.s, tEnc.p, tEnc );
		indexSO.put( tEnc.s, tEnc.o, tEnc );
		indexPO.put( tEnc.p, tEnc.o, tEnc );

		containedIds.add( tEnc.s );
		containedIds.add( tEnc.p );
		containedIds.add( tEnc.o );
	}

	/**
	 * Deleting triples from this graph is not supported.
	 */
	public void performDelete ( Triple t )
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a query handler (see {@link IdBasedQueryHandler} that is based on
	 * the identifiers used to represent RDF nodes in this RDF graph
	 * implementation.
	 */
	@Override
	public QueryHandler queryHandler ()
	{
		if ( queryHandler == null ) {
			queryHandler = new IdBasedQueryHandler( this );
		}
		return queryHandler;
	}

	@Override
	protected int graphBaseSize ()
	{
		return indexS.size();
	}


	// implementation of the IdBasedGraph interface

	public Node getNode ( int id )
	{
		return nodeDict.getNode( id );
	}

	public int getId ( Node n )
	{
		return nodeDict.getId( n );
	}

	public boolean contains ( int sId, int pId, int oId )
	{
		return ( find(sId,pId,oId).hasNext() );
	}

	public Iterator<EncodedTriple> find ( int sId, int pId, int oId )
	{
		if ( sId != -1 && ! containedIds.contains(sId) ) {
			return EmptyIterator.emptyEncodedTripleIterator;
		}
		if ( pId != -1 && ! containedIds.contains(pId) ) {
			return EmptyIterator.emptyEncodedTripleIterator;
		}
		if ( oId != -1 && ! containedIds.contains(oId) ) {
			return EmptyIterator.emptyEncodedTripleIterator;
		}

		if ( sId < 0 )          // PO, P, O, or none
		{
			if ( pId < 0 )       // O or none
			{
				if ( oId < 0 ) {  // none !
					return indexS.getAll();
				} else {            // O !
					return new IteratorO( indexO, oId );
				}
			}
			else                   // PO or P
			{
				if ( oId < 0 ) {  // P !
					return new IteratorP( indexP, pId );
				} else {            // PO !
					return new IteratorPO( indexPO, pId, oId );
				}
			}
		}
		else                      // SPO, SP, SO, or S
		{
			if ( pId < 0 )       // SO or S
			{
				if ( oId < 0 ) {  // S !
					return new IteratorS( indexS, sId );
				} else {            // SO !
					return new IteratorSO( indexSO, sId, oId );
				}
			}
			else                   // SPO or SP
			{
				if ( oId < 0 ) {  // SP !
					return new IteratorSP( indexSP, sId, pId );
				} else {                      // SPO !
					return findOne( sId, pId, oId );
				}
			}
		}
	}


	// helpers

	/**
	 * Executes a triple pattern query without wildcards.
	 * None of the given identifiers must be -1.
	 */
	protected Iterator<EncodedTriple> findOne ( int sId, int pId, int oId )
	{
		assert sId >= 0;
		assert pId >= 0;
		assert oId >= 0;

		return new IteratorSPO( indexSP, sId, pId, oId );
	}


	/**
	 * An iterator that provides only a single element.
	 */
	static class SingleElementIterator<E> implements Iterator<E>
	{
		private E e;
		public SingleElementIterator ( E e ) { this.e = e; }
		public boolean hasNext () { return ( e != null ); }
		public E next () { E e = this.e; this.e = null; return e; }
		public void remove() { throw new UnsupportedOperationException(); }
	}

	/**
	 * Base class for all iterators over one of the indexes.
	 */
	static abstract class IteratorIndex1 implements Iterator<EncodedTriple>
	{
		final private Iterator<EncodedTriple> base;
		final protected int reqId;
		private EncodedTriple nextTriple;

		public IteratorIndex1 ( Index index, int reqId )
		{
			this.base = index.get( reqId );
			this.reqId = reqId;
		}

		protected IteratorIndex1 ( Iterator<EncodedTriple> base, int reqId )
		{
			this.base = base;
			this.reqId = reqId;
		}

		final public boolean hasNext ()
		{
			if ( nextTriple != null ) {
				return true;
			}

			EncodedTriple e;
			while ( base.hasNext() )
			{
				e = base.next();
				if ( matches(e) )
				{
					nextTriple = e;
					break;
				}
			}

			return ( nextTriple != null );
		}

		final public EncodedTriple next ()
		{
			if ( ! hasNext() ) {
				throw new NoSuchElementException();
			}

			EncodedTriple t = this.nextTriple;
			this.nextTriple = null;
			return t;
		}

		final public void remove () { throw new UnsupportedOperationException(); }

		abstract protected boolean matches ( EncodedTriple e );
	}

	static class IteratorS extends IteratorIndex1
	{
		public IteratorS ( Index index, int reqId ) { super(index,reqId); }
		final protected boolean matches ( EncodedTriple e ) { return e.s == reqId; }
	}

	static class IteratorP extends IteratorIndex1
	{
		public IteratorP ( Index index, int reqId ) { super(index,reqId); }
		final protected boolean matches ( EncodedTriple e ) { return e.p == reqId; }
	}

	static class IteratorO extends IteratorIndex1
	{
		public IteratorO ( Index index, int reqId ) { super(index,reqId); }
		final protected boolean matches ( EncodedTriple e ) { return e.o == reqId; }
	}

	static abstract class IteratorIndex2 extends IteratorIndex1
	{
		final protected int reqId2;

		protected IteratorIndex2 ( Index2 index, int reqId1, int reqId2 )
		{
			super( index.get(reqId1,reqId2), reqId1 );
			this.reqId2 = reqId2;
		}
	}

	static class IteratorSP extends IteratorIndex2
	{
		public IteratorSP ( Index2 index, int reqId1, int reqId2 ) { super(index,reqId1,reqId2); }
		final protected boolean matches ( EncodedTriple e ) { return (e.s == reqId) && (e.p == reqId2); }
	}

	static class IteratorSO extends IteratorIndex2
	{
		public IteratorSO ( Index2 index, int reqId1, int reqId2 ) { super(index,reqId1,reqId2); }
		final protected boolean matches ( EncodedTriple e ) { return (e.s == reqId) && (e.o == reqId2); }
	}

	static class IteratorPO extends IteratorIndex2
	{
		public IteratorPO ( Index2 index, int reqId1, int reqId2 ) { super(index,reqId1,reqId2); }
		final protected boolean matches ( EncodedTriple e ) { return (e.p == reqId) && (e.o == reqId2); }
	}

	static class IteratorSPO extends IteratorIndex2
	{
		final protected int reqId3;

		public IteratorSPO ( Index2 index, int sId, int pId, int oId )
		{
			super( index, sId, pId );
			this.reqId3 = oId;
		}

		final protected boolean matches ( EncodedTriple e )
		{
			return (e.o == reqId3) && (e.s == reqId) && (e.p == reqId2);
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