package de.fuberlin.wiwiss.ng4j.swp.c14n;

/**
 * 
 * Current implementation works on RDF triples only. Jeremy Carroll thinks that
 * this is still the case for Named Graphs. This implementation is based on the
 * DBin code by Giovanni Tummarello.
 * 
 * @since 05-Oct-2004
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.test.NodeCreateUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;



public class RDFC14NImpl  
{
		static final Log log = LogFactory.getLog( RDFC14NImpl.class );
        public static final Node TILDE = NodeCreateUtils.create( "~" );
        public static final Node C14N_TRUE = NodeCreateUtils.create( "http://www-uk.hpl.hp.com/people/jjc/rdf/c14n#true" );
        public static final String C14N = "http://www-uk.hpl.hp.com/people/jjc/rdf/c14n#";
        public static final Node X = NodeCreateUtils.create( "x" );        
        
        private Model model = ModelFactory.createDefaultModel();
        private ArrayList<String> canonical_string;
        
        /**
         * This function will return an array list with a sequence of strings forming 
         * the canonical reppresentation of the RDF that was fed
         * each string reppresent a triple and by joining them in the given order 
         * the result can be used as needed for example in digital signatures.
         * @return the canonical string list.
         */
        public ArrayList<String> getCanonicalStringsArray() 
        {
        	return canonical_string;
        }
        
        /**
         * Will create AND immediately canonize the graph
         * @param rdf the RDF file in RDF/XML
         * @param base see Jena model.read() documentation
         * @throws FileNotFoundException
         */
        public RDFC14NImpl( String rdf,String base ) throws FileNotFoundException 
		{ 
        	model.read( new FileInputStream( rdf ), base );
        	doit( model );
        }

        /**
         * Will create AND immediately canonize the graph
         * @param rdfStream the RDF inputstream in RDF/XML
         * @param base see Jena model.read() documentation
         */
        
        public RDFC14NImpl( InputStream rdfStream, String base ) 
        {
        	model.read( rdfStream, base );
        	doit( model );
        }
        
        /**
         * Will create AND immediately canonize the graph
         * @param rdffile the RDF File (must be RDF/XML)
         * @param base see Jena model.read() documentation
         */

        public RDFC14NImpl( File rdffile, String base ) 
        		throws FileNotFoundException 
				
		{
        	model.read( new FileInputStream( rdffile ), base );
        	doit( model );
        }
        
        public RDFC14NImpl( Model model )
        {
        	doit( model );
        }
        
        private void doit( Model modelIn ) 
        {
        	this.model = modelIn;
        	StmtIterator st = modelIn.listStatements();
        	ArrayList<Triple> a = new ArrayList<Triple>();
        	while( st.hasNext() ) 
        	{
        		a.add( st.nextStatement().asTriple() );//create an ArrayList of Triples                
        	}
        	canonical_string=pre_canonicalization( a, modelIn );
        }
                
        /**
         * Inserisce TILDE in luogo dei BlankNodeId in posizione soggetto ed oggetto. I valori
         * degli Id vengono copiati negli opportuni campi degli oggetti C14NTtriple
         * @param a ArrayList di Triple
         * @return am ArrayList di C14NTtriple
         */
        private ArrayList<C14NTriple> putTilde( ArrayList<Triple> a )
        {
        	ArrayList<C14NTriple> am = new ArrayList<C14NTriple>();
        	for( int i=0;i<a.size();i++ )
        	{
        		Triple tmp = a.get( i );                        
        		am.add( i, new C14NTriple( tmp.getSubject(), tmp.getPredicate(), tmp.getObject() ) );
        	}
        	return am;                
        }
        /**
         * Label nodes with TILDE using an ID of the form: _gNNNNNN. Not all nodes are necessarily 
         * labeled(hard to label node)
         * @param a ArrayList of C14NTtriples
         */
                         
        private void labelledNode( ArrayList<C14NTriple> a )
        {
        	//create a hashtable and replace contents with a Tilde
        	Hashtable<Node,Node> ht = new Hashtable<Node,Node>();
        	int symCount = 1;
        	ArrayList<C14NTriple> af = new ArrayList<C14NTriple>();

        	for( int i=0;i<a.size();i++ )
        	{
        		C14NTriple t = ( C14NTriple ) a.get( i );
        		//Condition if arraylist has only one triple
        		if ( a.size() == 1 )
        		{ 
        			C14NTriple tmp = new C14NTriple( t, ht, symCount );
        			af.add( tmp );
        			break;
        		}                
        		//If the first element does not have a predecessor, compare with the next element
        		if (i==0)
        		{ 
        			//Compare only the triple that exclude the ID
        			if( ( t.compareTo( a.get( i+1 ) ) ) == 0 )
        			{ 
        				af.add( t );
        				continue;    //if the line is equal to the next one
        			}
        			else 
        			{
        				C14NTriple tmp = new C14NTriple( t, ht, symCount );
        				if ( tmp.flag ) symCount++;                                        
        				af.add( tmp );
        				continue;
        			}
        		}
        		//Test t to see if equal to the prec or the successive element
        		if( ( i>0 )&&( i<a.size()-1 ) )
        		{
        			
        			if( ( ( t.compareTo( a.get( i - 1 ) ) ) == 0 ) || ( ( t.compareTo( a.get( i + 1 ) ) ) == 0 ) )
        			{
        				af.add( t );
        				continue;
        			}
        			else 
        			{
        				C14NTriple tmp = new C14NTriple( t, ht, symCount );
        				if (tmp.flag ) symCount++; 
        				af.add( tmp );
        				continue;
        			}
        		}
        		//Test if t is equal to previous element
        		if ( i == a.size() - 1 )
        		{  
        			if( ( t.compareTo( a.get( i-1 ) ) ) == 0 )
        			{
        				af.add( t );
        				continue;
        			}
        			else 
        			{
        				C14NTriple tmp = new C14NTriple( t, ht, symCount );
        				if ( tmp.flag ) symCount++;
        				af.add( tmp );
        				continue;
        			}
        		}                        
        	}
        	for( int i=0;i<af.size();i++ )
        	{
        		C14NTriple t = ( C14NTriple ) a.get( i );
        		t.setTildeObject( ht );
        		t.setTildeSubject( ht );                        
        	}
        	a.clear();
        	a.addAll( af );
        }
                
        /**
         * The One-step Deterministic Labelling Algorithm described by Jeremy Carroll
         * @param a ArrayList of Triples
         * @return al ArrayList of C14NTtriples
         */
        private ArrayList<C14NTriple> one_step_algorithm( ArrayList<Triple> a )
        {//algorithm to determine if nodes exist that are hard to label
                        
        	ArrayList<C14NTriple> al = new ArrayList<C14NTriple>(); //ArrayList of StructuredString objects                 
        	al = putTilde( a );
        	Collections.sort( al );
        	labelledNode( al );
        	Collections.sort( al );                
        	return al;                
        }
        
        /**
         * 
         * @param a ArrayList C14NTtriple objects
         * @return true if no hard-to-label nodes exist
         */
        private boolean isAllLabelled( ArrayList<C14NTriple> a )
        {
        	boolean test = true;
        	for( int i=0;i<a.size();i++ )
        	{
        		C14NTriple t = a.get( i );
        		if ( ( t.subject.equals( TILDE ) ) || ( t.objectID.equals( TILDE ) ) )
        		{
        			test = false;
        		}
        	}
        	return test;
        }
        
        /**
         * 
         * Remove from model the triple with the predicate c14n:true and return an
         * ArrayList of the remaining triples.
         * 
         * @param a ArrayList of C14NTtriples 
         * @param modelIn 
         * @return ArrayList of Triples
         */
        private ArrayList<Triple> removeTripleWithC14N( ArrayList<C14NTriple> a, Model modelIn )
        {
                        
        	ArrayList<Statement> statementList = new ArrayList<Statement>();// ArrayList of statements to be removed
        	ArrayList<Triple> tripleList = new ArrayList<Triple>(); // New arraylist of Triple objects
        	//Create an arraylist of C14N statements to remove
        	for( int i=0;i<a.size();i++ )
        	{   
        		C14NTriple t = ( C14NTriple ) a.get( i );
        		if( t.predicate.equals( C14N_TRUE ) )
        		{
        			Statement st = modelIn.createStatement( modelIn.createResource( t.subjectID.getBlankNodeId() ), 
        												modelIn.createProperty( C14N, "true" ),
														modelIn.createLiteral( t.object.toString() ) );
        			statementList.add(st);
        		}
        	}                
        	modelIn.remove( statementList );
        	StmtIterator st=modelIn.listStatements();
                        
        	while( st.hasNext() ) 
        	{
        		tripleList.add( st.nextStatement().asTriple() );//create a new ArrayList of Triples to return
        	}                        
        	return tripleList;                
        }
        
        /**
         * 
         * Add to model a c14n:true triple and return the resulting ArrayList of the model
         * 
         * @param a ArrayList of C14NTtriples
         * @param modelIn
         * @return a ArrayList of Triples
         */
        private ArrayList<Triple> addTripleWithC14N( ArrayList<C14NTriple> a, Model modelIn )
        {               
        	ArrayList<Statement> statementList = new ArrayList<Statement>();// Arraylist of statements to be created              
        	ArrayList<Triple> tripleList = new ArrayList<Triple>();// New arraylist of Triple objects
        	Hashtable<Node,Node> ht = new Hashtable<Node,Node>();
        	int symCount = 1;
        	//Create a new set of statements and place them in arraylist
        	for( int i=0;i<a.size();i++ )
        	{   
        		C14NTriple t = ( C14NTriple ) a.get( i );
        		if( t.object.equals( TILDE ) )
        		{
        			//if ID object already is assigned in ht we'll ignore
        			if( !ht.containsKey( t.objectID ) )
        			{ 
        				ht.put( t.objectID, X );// if  not assigned create a new triple                                                                        
        				Statement st = modelIn.createStatement( modelIn.createResource( t.objectID.getBlankNodeId() ),
        													modelIn.createProperty( C14N, "true" ),
															modelIn.createLiteral( Integer.toString(symCount) ) );                                        
        				statementList.add( st );
        				t.objectID = Node.NULL;
        				symCount++;                                        
        			}                                
        		}
        		if( t.subject.equals( TILDE ) )
        		{	//if ID object already is assigned in ht we replace it
        			if( !ht.containsKey( t.subjectID ) )
        			{
        				ht.put( t.subjectID, X );                                        
        				Statement st = modelIn.createStatement( modelIn.createResource( t.subjectID.getBlankNodeId() ),
        													modelIn.createProperty( C14N, "true" ),
															modelIn.createLiteral( Integer.toString(symCount) ));                                        
        				statementList.add( st );                                        
        				t.subjectID = Node.NULL;
        				symCount++;                                        
        			}                                
        		}
        	}
        	modelIn.setNsPrefix( "c14n", C14N ); 
        	modelIn.add( statementList );
        	StmtIterator st = modelIn.listStatements();
        	while( st.hasNext() ) 
        	{
        		tripleList.add( st.nextStatement().asTriple() );//create an ArrayList of Triples
         	}                        
        	return tripleList;                
         }
         
        /**
         * 
         * Algorithm labels all the nodes in C14NTtriple arraylist and returns the canonical list. 
         * Incidentally, this also means the model is canonical.
         * 
         * @param a ArrayList of Triples
         * @param modelIn
         * @return canonicString ArrayList of strings representing the canonical triple list.
         */
         private ArrayList<String> pre_canonicalization( ArrayList<Triple> a, Model modelIn )
         { //return the canonical list as a string
                        
         	ArrayList<String> canonicString = new ArrayList<String>(); //ArrayList of string
         	ArrayList<C14NTriple> pre_canonic = one_step_algorithm( a ); // 1) alg one_step
         	//if it is in canonical form, add to canonicString           
         	if ( isAllLabelled( pre_canonic ) )
         	{  
         		for( int i=0;i<pre_canonic.size();i++ )
         		{
         			C14NTriple t = pre_canonic.get( i );
         			canonicString.add( i, t.createTripleString( t ) );
         		}
         	}
         	else
         	{   // if it is not canonical
         		a = removeTripleWithC14N( pre_canonic,modelIn ); // 2) remove triple with C14N:true
         		pre_canonic = one_step_algorithm( a );// 3) repeat? alg one_step
         		a = addTripleWithC14N( pre_canonic, modelIn ); // 4) create table to see which triples to add
         		pre_canonic = one_step_algorithm( a ); // 5) repeat alg: this time label all triples
         		for( int i=0;i<pre_canonic.size();i++ )
         		{
         			C14NTriple t = pre_canonic.get( i );
         			canonicString.add( i, t.createTripleString( t ) );
         		}                        
         	}
         	return canonicString;                
         }
}

/*
 *  (c)   Copyright 2004 - 2010 Rowland Watkins (rowland@grid.cx) & University of 
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
