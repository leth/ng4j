package de.fuberlin.wiwiss.jenaext.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.fuberlin.wiwiss.jenaext.EmptyIterator;
import de.fuberlin.wiwiss.jenaext.IdBasedTriple;


/**
 * Indexes ID-based triples ({@link IdBasedTriple} objects) by node identifiers.
 * This class can be used to create S, P, and O indexes of triples.
 *
 * @author Olaf Hartig
 */
public class Index
{
	// members

	/** Bitmask that selects the bits of identifiers used for hash keys. */
	static final private int KEYMASKSIZE = 4;
	static final private int INDEXKEYMASK = ( 1 << KEYMASKSIZE ) - 1;

	/** the hash table */
	final private List<IdBasedTriple> [] index = new List [INDEXKEYMASK+1];


	// accessors

	/**
	 * Indexes the given triple using the given key.
	 */
	public void put ( int key, IdBasedTriple t )
	{
		int indexKey = getIndexKey( key );
		if ( index[indexKey] == null ) {
			index[indexKey] = new ArrayList<IdBasedTriple> ();
		}

		index[indexKey].add( t );
	}

	/**
	 * Returns all triples indexed with a key from the class of the given key.
	 * Attention: the given iterator may provide more triples as requested.
	 */
	public Iterator<IdBasedTriple> get ( int key )
	{
		int indexKey = getIndexKey( key );
		return ( index[indexKey] == null ) ? EmptyIterator.emptyIdBasedTripleIterator : index[indexKey].iterator();
	}

	/**
	 * Returns all triples in this index.
	 */
	public Iterator<IdBasedTriple> getAll ()
	{
		return new AllEntriesIterator();
	}

	/**
	 * Returns the number of entries in this index.
	 */
	public int size ()
	{
		int result = 0;
		int buckets = index.length;
		for ( int i = 0; i< buckets; ++i )
		{
			if ( index[i] != null ) {
				result += index[i].size();
			}
		}
		return result;
	}


	// helpers

	/**
	 * Calculates the hash key from the given key.
	 */
	static protected int getIndexKey ( int key )
	{
		return key & INDEXKEYMASK;
	}


	/**
	 * This iterator provides all triples in this index.
	 */
	protected class AllEntriesIterator implements Iterator<IdBasedTriple>
	{
		private int curBucketIdx = -1;
		private Iterator<IdBasedTriple> itCurEntry = null;
		private IdBasedTriple curEntry = null;

		public boolean hasNext ()
		{
			if ( curEntry != null ) {
				return true;
			}

			if ( itCurEntry == null || ! itCurEntry.hasNext() )
			{
				if ( curBucketIdx == INDEXKEYMASK ) {
					return false;
				}

				do {
					curBucketIdx++;
				} while ( curBucketIdx <= INDEXKEYMASK && index[curBucketIdx] == null );

				if ( curBucketIdx > INDEXKEYMASK ) {
					return false;
				}

				itCurEntry = index[curBucketIdx].iterator();
			}

			if ( itCurEntry.hasNext() ) {
				curEntry = itCurEntry.next();
			}

			return ( curEntry != null );
		}

		public IdBasedTriple next ()
		{
			if ( ! hasNext() ) {
				throw new NoSuchElementException();
			}

			IdBasedTriple t = curEntry;
			curEntry = null;
			return t;
		}

		public void remove () { throw new UnsupportedOperationException(); }
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