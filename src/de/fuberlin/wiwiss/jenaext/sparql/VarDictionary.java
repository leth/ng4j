package de.fuberlin.wiwiss.jenaext.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.sparql.core.Var;


/**
 * A dictionary that assigns identifiers to query variables.
 *
 * @author Olaf Hartig
 */
public class VarDictionary
{
	// members

	final protected ArrayList<Var> dictId2Var = new ArrayList<Var> ();
	final protected Map<String,Integer> dictVarName2Id = new HashMap<String,Integer> ();


	// accessors

	/**
	 * Returns the query variable identified by the given identifier.
	 *
	 * @throws IllegalArgumentException if the given identifier is unknown to
	 *                                  this dictionary
	 */
	final public Var getVar ( int id ) throws IllegalArgumentException
	{
		Var v = dictId2Var.get( id );

		if ( v == null ) {
			throw new IllegalArgumentException( "The given identifier (" + String.valueOf(id) + ") is unknown." );
		}

		return v;
	}

	/**
	 * Returns the identifier that identifies the given query variable.
	 *
	 * @throws IllegalArgumentException if the given variable is unknown to
	 *                                  this dictionary
	 */
	final public int getId ( Var v ) throws IllegalArgumentException
	{
		Integer i = dictVarName2Id.get( v.getVarName() );

		if ( i == null ) {
			throw new IllegalArgumentException( "The given variable (" + v.getVarName() + ") is unknown." );
		}

		return  i.intValue();
	}

	/**
	 * Returns the number of query variables known by this dictionary.
	 */
	final public int size ()
	{
		return dictId2Var.size();
	}


	// operations

	/**
	 * Returns an identifier that identifies the given query variable.
	 * If there is no identifier for the given query variable yet this method
	 * creates a new identifier and adds it to the dictionary.
	 */
	final public int createId ( Var v )
	{
		int result;
		Integer i = dictVarName2Id.get( v.getVarName() );

		if ( i == null )
		{
			result = dictId2Var.size();
			dictId2Var.add( v );

			assert result < Integer.MAX_VALUE;

			dictVarName2Id.put( v.getVarName(), Integer.valueOf(result) );
		}
		else {
			result = i.intValue();
		}

		return result;
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