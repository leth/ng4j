/*
 * CachObject.java
 *
 * Created on 26. Mai 2005, 13:06
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.trust.ExplanationPart;

/**
 * 
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public interface RankingCache {
    
    public boolean isAccepted(Node sink);
    
    public ExplanationPart explain(Node sink);
    
    public Graph explainRDF(Node sink);     

    public boolean equals(Object obj);
}
