/*
 * Metric.java
 *
 * Created on 8. April 2005, 12:05
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Graph;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.trust.ExplanationPart;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public abstract class Metric implements de.fuberlin.wiwiss.trust.Metric {
    
    /**
     * URI, which identifies the Metric in the TriQL Trust-Architecture.
     */
    protected String uri =  null;
    
    /** 
     * Contains the data which are available for the evaluating of trust.
     */  
    private NamedGraphSet sourceData = null;
    
    
    public Metric(String uri){
        this.uri = uri;
        this.sourceData = null;
    }
    
    public void setup(NamedGraphSet sourceData) {
        this.sourceData = sourceData;
    }
    
    public String getURI() {
        return uri;
    }
    
    protected NamedGraphSet getSourceData(){
        return sourceData;
    }
    
    protected abstract ExplanationPart explain();
    
    protected abstract Graph explainRDF();
}