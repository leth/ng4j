/*
 * MetricResult.java
 *
 * Created on 2. März 2005, 14:49
 */

package de.fuberlin.wiwiss.trust.metric;


/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class MetricResult implements de.fuberlin.wiwiss.trust.EvaluationResult {
    
    
    private boolean result;
    private de.fuberlin.wiwiss.trust.ExplanationPart textExplanation;
    private com.hp.hpl.jena.graph.Graph graphExplanation;
    private float trustValue;
    
    /** Creates a new instance of MetricResult */
    public MetricResult(boolean result, de.fuberlin.wiwiss.trust.ExplanationPart textExplanation, com.hp.hpl.jena.graph.Graph graphExplanation) {
        this.result = result;
        this.textExplanation = textExplanation;
        this.graphExplanation = graphExplanation;
        this.trustValue = 0f;
    }
    
    public com.hp.hpl.jena.graph.Graph getGraphExplanation() {
        return graphExplanation;
    }
    
    public boolean getResult() {
        return this.result;
    }
    
    public de.fuberlin.wiwiss.trust.ExplanationPart getTextExplanation() {
        return this.textExplanation;
    }
    
    public void setTrustValue(float trustValue){
        this.trustValue = trustValue;
    }
    
    public float getTrustValue(){
        return this.trustValue;
    }
}
