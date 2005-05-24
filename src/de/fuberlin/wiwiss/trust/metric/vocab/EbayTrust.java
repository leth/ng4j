/*
 * EbayTrust.java
 *
 * Created on 22. Mai 2005, 12:29
 */

package de.fuberlin.wiwiss.trust.metric.vocab;

import com.hp.hpl.jena.graph.Node;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class EbayTrust {
    
    public static final String NS = "http://www.oliver-maresch.de/ontologies/ebayTrust.n3";
    
    public static String getURI() {return NS;}
    
    public static final Node NAMESPACE = Node.createURI( NS );
    
    public static final Node positiveRating = Node.createURI( NS + "#positiveRating" );
    
    public static final Node neutralRating = Node.createURI( NS + "#neutralRating" );
    
    public static final Node negativeRating = Node.createURI( NS + "#negativeRating" );
    
}
