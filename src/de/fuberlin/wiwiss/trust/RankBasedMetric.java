/*
 * RankBasedMetric.java
 *
 * Created on 26. Mai 2005, 12:55
 */

package de.fuberlin.wiwiss.trust;

import java.util.List;

import com.hp.hpl.jena.graph.Graph;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public interface RankBasedMetric {
    
    public void init(NamedGraphSet source, List inputTable) throws MetricException;
    
    public String getURI();
    
    public boolean isAccepted(int row);
    
    public int rows();
    
    public ExplanationPart explain(int row);
    
    public Graph explainRDF(int row); 
}