/*
 * Created on 19-Feb-2005
 *
 */
package de.fuberlin.wiwiss.ng4j.swp.vocabulary;

import com.hp.hpl.jena.graph.Node;

/**
 * @author Rowland Watkins (rowland@grid.cx)
 *
 */
public class SWP_V 
{
	/** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2004/03/trix/swp-verification";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Node NAMESPACE = Node.createURI( NS );
    
    public static final Node default_graph = Node.createURI( "http://www.w3.org/2004/03/trix/swp-verification/verifiedSignatures" );
    
    /** <p>The object contains the status value of a signature for the subject graph.</p> */
    public static final Node successful = Node.createURI( "http://www.w3.org/2004/03/trix/swp-verification/successful" );
    
    public static final Node notSuccessful = Node.createURI( "http://www.w3.org/2004/03/trix/swp-verification/notSuccessful" );
}
