package de.fuberlin.wiwiss.jenaext;

import com.hp.hpl.jena.graph.Triple;


/**
 * An ID-based triple represents a triple with identifiers for the three
 * components (subject, predicate, and object).
 *
 * @author Olaf Hartig
 */
public class IdBasedTriple
{
	// members

	/** The identifier for the subject node. */
	final public int s;

	/** The identifier for the predicate node. */
	final public int p;

	/** The identifier for the object node. */
	final public int o;

	/** The represented triple. */
	final public Triple triple;


	// initialization

	public IdBasedTriple ( Triple triple, int s, int p, int o )
	{
		assert triple != null;
		assert triple.isConcrete();
		assert s >= 0;
		assert p >= 0;
		assert o >= 0;

		this.triple = triple;
		this.s = s;
		this.p = p;
		this.o = o;
	}


	// redefinition of Object methods

	/** ID-based triples are equal if they have the same three identifiers. */
	@Override
	public boolean equals ( Object obj )
	{
		if ( obj instanceof IdBasedTriple )
		{
			IdBasedTriple et = (IdBasedTriple) obj;
			return ( s == et.s && p == et.p && o == et.o );
		}

		return false;
	}

	@Override
	public String toString ()
	{
		return "IdBasedTriple(" + String.valueOf(s) + "," + String.valueOf(p) + "," + String.valueOf(o) + ")";
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