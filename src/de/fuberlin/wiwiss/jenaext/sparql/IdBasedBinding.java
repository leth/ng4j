package de.fuberlin.wiwiss.jenaext.sparql;

import java.util.Iterator;


/**
 * An identifier based binding is a mapping from query variables that
 * are represented by identifiers to values which are also represented
 * by identifiers.
 *
 * @author Olaf Hartig
 */
public class IdBasedBinding
{
	// members

	final static public int UNBOUND = -1;

	final protected int[] map;


	// initialization

	public IdBasedBinding ( int size )
	{
		map = new int[size];
		for ( int i = 0; i < size; ++i ) {
			map[i] = UNBOUND;
		}
	}

	/** Copy constructor. */
	public IdBasedBinding ( IdBasedBinding template )
	{
		int size = template.map.length;
		map = new int[size];
		for ( int i = 0; i < size; ++i ) {
			map[i] = template.map[i];
		}
	}


	// accessors

	/** Set the (variable, value) pair in the binding. */
	public void set ( int varId, int valueId )
	{
		map[varId] = valueId;
	}

	/**
	 * Return true if the variable specified by the given identifier is bound to
	 * some object.
	 */
	public boolean contains ( int varId )
	{
		return ( map[varId] != UNBOUND );
	}

	/**
	 * Return the identifier of the value bound to the specified variable, or -1.
	 */
	public int get ( int varId )
	{
		return map[varId];
	}

	/**
	 * Return the number of (variable, value) pairs in this binding.
	 * This method also counts pairs where the variable is unbound.
	 */
	public int size ()
	{
		return map.length;
	}


	// redefinition of Object methods

	@Override
	public String toString ()
	{
		String s = "IdBasedBinding(";

		int size = map.length;
		for ( int i = 0; i < size; ++i )
		{
			if ( map[i] != UNBOUND ) {
				s += String.valueOf(i) + "->" + String.valueOf(map[i]) + " ";
			}
		}

		s += ")";
		return s;
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