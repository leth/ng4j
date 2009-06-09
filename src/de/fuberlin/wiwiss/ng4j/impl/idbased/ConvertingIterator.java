package de.fuberlin.wiwiss.ng4j.impl.idbased;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.NiceIterator;


/**
 * This iterator converts the elements of an iterator over encoded triples to
 * actual triples.
 *
 * @author Olaf Hartig
 */
public class ConvertingIterator extends NiceIterator
{
	// members

	/** the iterator that is being converted */
	final private Iterator<EncodedTriple> base;


	// initialization

	public ConvertingIterator ( Iterator<EncodedTriple> base )
	{
		assert base != null;
		this.base = base;
	}


	// implementation of the Iterator interface

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.util.iterator.NiceIterator#hasNext()
	 */
	@Override
	final public boolean hasNext ()
	{
		return base.hasNext();
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.util.iterator.NiceIterator#next()
	 */
	@Override
	final public Triple next ()
	{
		return base.next().triple;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.util.iterator.NiceIterator#remove()
	 */
	@Override
	final public void remove ()
	{
		throw new UnsupportedOperationException();
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