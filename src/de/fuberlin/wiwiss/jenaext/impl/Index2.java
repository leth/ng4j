package de.fuberlin.wiwiss.jenaext.impl;

import java.util.Iterator;

import de.fuberlin.wiwiss.jenaext.IdBasedTriple;


/**
 * Indexes ID-based triples ({@link IdBasedTriple} objects) by two node
 * identifiers.
 * This class can be used to create SP, PO, and SO indexes of triples.
 *
 * @author Olaf Hartig
 */
public class Index2 extends Index
{
	// accessors

	/**
	 * Indexes the given triple using the two given keys.
	 */
	public void put ( int key1, int key2, IdBasedTriple t )
	{
		put( key1*key2, t );
	}

	/**
	 * Returns all triples indexed with keys from the class of the two given
	 * keys.
	 * Attention: the given iterator may provide more triples as requested.
	 */
	public Iterator<IdBasedTriple> get ( int key1, int key2 )
	{
		return get( key1*key2 );
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