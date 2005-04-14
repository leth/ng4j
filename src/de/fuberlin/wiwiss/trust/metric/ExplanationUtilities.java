/*
 * ExplanationUtilities.java
 *
 * Created on 4. April 2005, 12:22
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Node;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class ExplanationUtilities {
    

    /**
     * Creates a String Literal Node
     * @param str
     * @return StringLiteral as a Node
     */
    public static Node cl(String str){
        return Node.createLiteral(str);
    }    
}
