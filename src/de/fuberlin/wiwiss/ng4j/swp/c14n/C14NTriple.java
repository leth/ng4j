/*
 * Created on 05-Oct-2004
 * 
 */
package de.fuberlin.wiwiss.ng4j.swp.c14n;

import java.text.DecimalFormat;
import java.util.Hashtable;

import org.apache.log4j.Category;

import com.hp.hpl.jena.graph.Node;


/**
 * 
 * Canonical triple representation.
 * 
 * This implementation is based on the DBin code by Giovanni Tummarello.
 * 
 */

public class C14NTriple extends Object implements Comparable
{
	static final Category log = Category.getInstance( C14NTriple.class );
	public Node subject;
	public Node predicate;
	public Node object;
	public Node subjectID;
	public Node objectID;
                
	public static final Node TILDE = Node.create( "~" );
	public final DecimalFormat sixDigitsFormat = new DecimalFormat( "000000" );
	public boolean flag;
        
	public C14NTriple( Node s, Node p, Node o )
	{
		this.subject = s;
		this.predicate = p;
		this.object = o;
		if( subject.isBlank() )
		{
			this.subjectID = this.subject;
			this.subject = RDFC14NImpl.TILDE;
        } 
		else 
		{
        this.subjectID = Node.NULL;
		}
        if( object.isBlank() )
        {
        	this.objectID = this.object;
        	this.object = RDFC14NImpl.TILDE;
        } 
        else 
        {
        	this.objectID=Node.NULL;
        }
    }
	
	public C14NTriple( C14NTriple st, Hashtable ht, int symCount )
	{
		if( st.object.equals( TILDE ) )
		{
			if( st.setTildeObject( ht ) )
			{
				this.object = st.object;
				this.objectID = st.objectID;
			} 
			else 
			{
				ht.put( st.objectID,
						Node.create( "_:g"+sixDigitsFormat.format( symCount ) ) );
				this.object = ( Node ) ht.get( st.objectID );
				this.objectID = st.objectID;
				this.flag = true;
			}
		} 
		else 
		{
			this.object = st.object;
			this.objectID = Node.NULL;
        }
                
        this.predicate = st.predicate;
                
        if(st.subject.equals( TILDE ) )
        {
        	if( st.setTildeSubject( ht ) )
        	{
        		this.subject = st.subject;
        		this.subjectID = st.subjectID;
        	} 
        	else 
        	{//create and assign a new ID 
        		ht.put( st.subjectID,
        				Node.create( "_:g"+sixDigitsFormat.format( symCount ) ) );
        		this.subject = ( Node ) ht.get( st.subjectID );
        		this.subjectID = st.subjectID;
        		this.flag = true;
            }
        } 
        else 
        {
        	this.subject = st.subject;
        this.subjectID = Node.NULL;
        }
	}
	
    /**
     * Replace the values of object with value found in hashtable
     * @param ht hashtable of Node object
     * @return true if the value is set with the value in the hashtable
     */
	public boolean setTildeObject( Hashtable ht )
	{
		boolean test = false;                
		if( ht.containsKey( objectID ) )
		{
			object = ( Node ) ht.get( objectID );
			test = true;
		}
		return test;
	}
	
    /**
     * Replace the values of object with value found in hashtable
     * @param ht hashtable of Node object
     * @return true if the value is set with the value in the hashtable
    */
	public boolean setTildeSubject( Hashtable ht )
	{
		boolean test = false;
		if( ht.containsKey( subjectID ) )
		{
			subject = ( Node ) ht.get( subjectID );
			test = true;
		}
		return test;        
	}

	public int compareTo( Object arg ) 
			throws ClassCastException 
	{
		String current = subject.toString() 
						+ predicate.toString() 
						+ object.toString();                                        
		C14NTriple cs = ( C14NTriple )arg;
		String ext = cs.subject.toString() 
					+ cs.predicate.toString() 
					+ cs.object.toString();                 
		return current.compareTo( ext );
	}
	
    /**
     * Create a string of triples in the form: subject, predicate object.
     * @param t C14NTtriple object
     * @return triple string
     */
	public String createTripleString( C14NTriple t )
	{
		return t.subject.toString() + " " 
			+ t.predicate.toString() + " " 
			+ t.object.toString();                                                
	}
}

/*
 *  (c)   Copyright 2004, 2005, 2006, 2007, 2008 Rowland Watkins (rowland@grid.cx) & University of 
 * 		  Southampton, Declarative Systems and Software Engineering Research 
 *        Group, University of Southampton, Highfield, SO17 1BJ
 *   	  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

