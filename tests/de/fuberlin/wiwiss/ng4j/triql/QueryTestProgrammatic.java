/*
 * (c) Copyright 2001, 2002, 2003, Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package de.fuberlin.wiwiss.ng4j.triql;

// Unsubtle JUnit-ization of some test code.
// This will be replaced by a more general testing of queries.

import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/** Bunch of programmatic uses of query to complrment the script tests.
 * @author Andy Seaborne
 * @version $Id: QueryTestProgrammatic.java,v 1.2 2004/12/17 01:44:30 cyganiak Exp $
 */

public class QueryTestProgrammatic extends TestSuite
{
	static final String testSetName = "RDQL - Query - Programmatic" ;
	
    public static boolean dumpModel = false ;
    public static boolean verbose = false ;

    public static TestSuite suite()
    {
    	return new QueryTestProgrammatic(testSetName) ;
    }
    	
    	
    private QueryTestProgrammatic(String name)
    {
    	super(name) ;
    	
        try {
            Model model1 = makeModel1() ;
            Model model2 = makeModel2() ;
            //suite.addTest(new TestQuery("RDQL Query "));
            
            // Test templates
            // expect to get one result with ?z = 1+1 = 2
            
            ResultBinding rb = new ResultBinding() ;
            rb.add("x", model1.createResource("http://never/r-1")) ;
            rb.add("y", model1.createResource("http://never/p-1")) ;
            // No bindings
            addTest(new TestQueryTemplate("RDQL-Test-Template-1", model1, null, "SELECT * WHERE (?x, ?y, ?z)", model1.size())) ;
            // With bindings
//            addTest(new TestQueryTemplate("RDQL-Test-Template-2", model1, rb, "SELECT * WHERE (?x, ?y, ?z)", 1)) ;
        } catch (Exception ex)
        {
            System.err.println("Problems making RDQL test") ;
            ex.printStackTrace(System.err) ;
            return ;
        }
        // Tests of templates
    }

    static class TestQueryTemplate extends TestCase
    {
        Model model ;
        String queryString ;
        ResultBinding binding ;
        long numResults ;
        
        TestQueryTemplate(String testName, Model m, ResultBinding b, String q, long num)
        {
            super(testName) ;
            model = m ;
            queryString = q ;
            binding = b ;
            numResults = num ;
        }
        
        protected void runTest() throws Throwable
        {
            if ( verbose )
            {
                System.out.println() ;
                System.out.println("Query:") ;
                System.out.println(queryString) ;
            }
            // Currently "success" is executing the query at all!
            TriQLQuery query = new TriQLQuery(queryString) ;
            NamedGraphSet ngs = new NamedGraphSetImpl();
            ngs.addGraph(new NamedGraphImpl("http://example.com/graph1", model.getGraph()));
            query.setSource(ngs);
            Iterator results = query.getResults();
            long count = 0 ;
            for ( ; results.hasNext() ; )
            {
                Map rb = (Map)results.next() ;
                if ( rb == null )
                    throw new Exception("TestQueryTemplate: found null result binding") ;
                // Check all the variables are there.
                for ( Iterator iter = query.getResultVars().iterator() ;
                      iter.hasNext() ; )
                {
                    String varName = (String)iter.next() ; 
                    Object obj = rb.get(varName) ;
                    assertNotNull("Variable: "+varName, obj) ;
                    if ( binding != null )
                    {
                        Object original = binding.get(varName) ;
                        if ( original != null )
                            assertTrue("Variable: "+varName+" = "+original+" / "+obj, original.equals(obj)) ;
                    }
                }
                          
                count++ ;
            }
            
            if ( count != numResults )
            {
                throw new Exception("TestQueryTemplate: mismatch in counts.  Expected "+numResults+".  Got "+count+"  Query: "+queryString) ;
            }
            
        }
    }
    
    static public Model makeModel1() throws Exception
    {
        Model model = ModelFactory.createDefaultModel() ;

        // Resource loop
        for ( int i = 0 ; i < 2 ; i++ )
        {
            // Property loop
            for ( int j = 0 ; j < 2 ; j++ )
            {
                model.add(model.createResource("http://never/r-"+i) ,
                          model.createProperty("http://never/p-"+j) ,
                          //"val-r-"+i+"-p-"+j) ;
                          i+j) ;
            }
        }

        // Bag statements are added to the model.
        Bag bag = model.createBag("http://never/bag") ;
        bag.add("11") ;
        bag.add("22") ;

        // Path.
        model.add(model.createResource("http://never/path"),
                  model.createProperty("http://never/path") ,
                  model.createResource("http://never/r-0")) ;
        return model ;
    }
    
    
    static public Model makeModel2() throws Exception
    {
        Model model = makeModel1() ;

        // Add some stuff with anon. resources.
        Resource anon1 = model.createResource() ;
        Resource anon2 = model.createResource() ;

        model.add(anon1,
                  model.createProperty("http://never/p-anon-1") ,
                  "p-anon-1") ;

        model.add(anon2,
                  model.createProperty("http://never/p-anon-2") ,
                  "p-anon-2") ;

        // Path from anon1 to anon2
        model.add(anon1,
                  model.createProperty("http://never/p-anon-1-2") ,
                  anon2) ;

        return model ;
    }
}

/*
 *  (c) Copyright 2001, 2002, 2003 Hewlett-Packard Development Company, LP
 *  All rights reserved.
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
 */

