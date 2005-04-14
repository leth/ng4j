/*
 * MindswapTrust.java
 *
 * Created on 24. Februar 2005, 12:34
 */

package de.fuberlin.wiwiss.trust.metric.vocab;

import com.hp.hpl.jena.graph.Node;

import java.util.Vector;

/**
 * This class includes the vocabulary used for the TrustMail trust metrics. 
 * The vocabulary definition is available at 
 * <a href="http://trust.mindswap.org/ont/trust.owl">http://trust.mindswap.org/ont/trust.owl</a>.
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class MindswapTrust {
    
    public static final String NS = "http://trust.mindswap.org/ont/trust.owl";
    
    public static String getURI() {return NS;}
    
    public static final Node NAMESPACE = Node.createURI( NS );
    
    public static final Node trust0 = Node.createURI( NS + "#trust0" );

    public static final Node trust1 = Node.createURI( NS + "#trust1" );    

    public static final Node trust2 = Node.createURI( NS + "#trust2" );    

    public static final Node trust3 = Node.createURI( NS + "#trust3" );    

    public static final Node trust4 = Node.createURI( NS + "#trust4" );    

    public static final Node trust5 = Node.createURI( NS + "#trust5" );    

    public static final Node trust6 = Node.createURI( NS + "#trust6" );    

    public static final Node trust7 = Node.createURI( NS + "#trust7" );    

    public static final Node trust8 = Node.createURI( NS + "#trust8" );    

    public static final Node trust9 = Node.createURI( NS + "#trust9" );    

    public static final Node trust10 = Node.createURI( NS + "#trust10" );    


    
    public static final Node trustsRegarding = Node.createURI(  NS + "#trustsRegarding" );
    
    public static final Node TopicalTrust = Node.createURI( NS + "#TopicalTrust" );    

    public static final Node trustValue = Node.createURI( NS + "#trustValue" );    

    public static final Node trustSubject = Node.createURI( NS + "#trustSubject" );    

    public static final Node trustedAgent = Node.createURI( NS + "#trustedAgent" );    

    public static final Node trustedPerson = Node.createURI( NS + "#trustedPerson" );    
    
    
    public static Vector getTrustProperties(){
        Vector trustProperties = new Vector();
        trustProperties.add(MindswapTrust.trust0);
        trustProperties.add(MindswapTrust.trust1);
        trustProperties.add(MindswapTrust.trust2);
        trustProperties.add(MindswapTrust.trust3);
        trustProperties.add(MindswapTrust.trust4);
        trustProperties.add(MindswapTrust.trust5);
        trustProperties.add(MindswapTrust.trust6);
        trustProperties.add(MindswapTrust.trust7);
        trustProperties.add(MindswapTrust.trust8);
        trustProperties.add(MindswapTrust.trust9);
        trustProperties.add(MindswapTrust.trust10);  
        return trustProperties;
    }

}
