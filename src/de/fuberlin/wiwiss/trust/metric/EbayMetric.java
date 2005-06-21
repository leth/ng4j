/*
 * EbayMetric.java
 *
 * Created on 22. Mai 2005, 12:38
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import de.fuberlin.wiwiss.ng4j.Quad;

import de.fuberlin.wiwiss.trust.ExplanationPart;
import de.fuberlin.wiwiss.trust.MetricException;
import de.fuberlin.wiwiss.trust.metric.vocab.EbayTrust;

/**
 * The Ebay metric counts the positive, neutral and negative ratings of 
 * a sink. The sink is trustworthy if there are more positive than negative
 * ratings. The only argument of the metric is the node of the sink.
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class EbayMetric extends Metric implements de.fuberlin.wiwiss.trust.Metric {
    
    public static final String URI = "http://www.wiwiss.fu-berlin.de/suhl/bizer/TPL/EbayMetric";
    
    /** the sink of the current evaluation */
    private Node sink = null;
    
    /** The list of Quads with positive ratings of the sink */
    private List positiveRatings = Collections.EMPTY_LIST;
    
    /** The list of Quads with neutral ratings of the sink */
    private List neutralRatings = Collections.EMPTY_LIST;
    
    /** The list of Quads with negative ratings of the sink */
    private List negativeRatings = Collections.EMPTY_LIST;
    
    /** The result of the evaluation. Ture if the sink is trustworty, false otherwise. */
    private boolean result = false;
    
    /** Creates a new instance of the EbayMetric */
    public EbayMetric() {
        super(URI);
    }
    
    /** 
     * Starts the calculation of the metric. The argument list have to contain one Node with the URI of the sink. 
     */
    public de.fuberlin.wiwiss.trust.EvaluationResult calculateMetric(java.util.List arguments) throws de.fuberlin.wiwiss.trust.MetricException {
        
        if(arguments == null || arguments.size() < 1){
            throw new MetricException("The Ebay metric needs the node of the sink.");
        }
        
        // read required arguments
                
        // get sink node
        {
            sink = (Node) arguments.get(0);
        }
        
        positiveRatings = listRatings(EbayTrust.positiveRating);
        neutralRatings = listRatings(EbayTrust.neutralRating);
        negativeRatings = listRatings(EbayTrust.negativeRating);
        
        result = (positiveRatings.size() - negativeRatings.size()) > 0;
        
        return new MetricResult(result, explain(), explainRDF());
    }
    
    /**
     * Searchs all ratings of a specified kind of rating (pos, neu, neg).
     * Returns the result of the search in a list of Quads.
     */
    private List listRatings(Node trustProperty){
        List result = new LinkedList();
        
        Quad pattern = new Quad(Node.ANY, Node.ANY, trustProperty, sink);
        Iterator it = getSourceData().findQuads(pattern);
        while(it.hasNext()){
            result.add(it.next());
        }
        return result;
    }
    
// Explanation generation section begin -------------------------------------
    
    protected de.fuberlin.wiwiss.trust.ExplanationPart explain() {
        List text = new ArrayList();
        
        ExplanationPart expl = new ExplanationPart(text);
        expl.addPart(summary());
        expl.addPart(generateRatingExpl());
        return expl;
    }
    
    private ExplanationPart summary(){
        List text = new ArrayList();
        
        text.add(cl("The "));
        text.add(Node.createURI(getURI()));
        text.add(cl(" infered, that the sink "));
        text.add(sink);
        if(result){
            text.add(cl(" is trustworthy, because the metric found more positive than negative ratings of the sink. "));
        } else {
            text.add(cl(" is not trustworthy, because the metric found more negative than positive ratings of the sink. "));            
        }
        
        ExplanationPart summary = new ExplanationPart(text);
        
        List details = new ArrayList();
        details.add(cl("The "));
        details.add(com.hp.hpl.jena.graph.Node.createURI(EbayMetric.URI));
        details.add(cl(" looks up all positive, negative and neutral ratings of the sink. If the difference of the sums of  positive and negative ratings is positive, than the sink is trustworhy. Otherwise the sink is not trustworthy."));
        summary.setDetails(new ExplanationPart(details));
        
        return summary;
    }
    
    private ExplanationPart generateRatingExpl() {
        List text = new ArrayList();

        ExplanationPart expl = new ExplanationPart(text);
        expl.addPart(generatePosRatings());
        expl.addPart(generateNeuRatings());
        expl.addPart(generateNegRatings());
        
        return expl;
    }
    
    private ExplanationPart generatePosRatings() {
        List text = new ArrayList();
        
        text.add(cl("The metric found " + positiveRatings.size() + " positive ratings."));
        ExplanationPart expl = new ExplanationPart(text);
        expl.setDetails(generatePosRatingList());

        return expl;
    }
    
    private ExplanationPart generatePosRatingList() {
        List text = new ArrayList();
        
        ExplanationPart expl = new ExplanationPart(text);
        
        Iterator it = positiveRatings.iterator();
        while(it.hasNext()){
            List t = new ArrayList();
            Quad q = (Quad) it.next();
            
            t.add(q.getSubject());
            t.add(cl(" rated the sink positive in "));
            t.add(q.getGraphName());
            t.add(cl(". "));
            
            expl.addPart(new ExplanationPart(t));
        }

        if(positiveRatings.size() <=0){
            text.add(cl("No positive ratings found. "));
        }
        
        return expl;
    }    
    
    private ExplanationPart generateNeuRatings() {
        List text = new ArrayList();
        
        text.add(cl("The metric found " + neutralRatings.size() + " neutral ratings."));
        ExplanationPart expl = new ExplanationPart(text);
        expl.setDetails(generateNeuRatingList());

        return expl;
    }
    
    private ExplanationPart generateNeuRatingList() {
        List text = new ArrayList();
        
        ExplanationPart expl = new ExplanationPart(text);
        
        Iterator it = neutralRatings.iterator();
        while(it.hasNext()){
            List t = new ArrayList();
            Quad q = (Quad) it.next();
            
            t.add(q.getSubject());
            t.add(cl(" rated the sink neutral in "));
            t.add(q.getGraphName());
            t.add(cl(". "));
            
            expl.addPart(new ExplanationPart(t));
        }

        if(neutralRatings.size() <=0){
            text.add(cl("No neutral ratings found. "));
        }
        
        return expl;
    }

    
    private ExplanationPart generateNegRatings() {
        List text = new ArrayList();
        
        text.add(cl("The metric found " + negativeRatings.size() + " negative ratings."));
        ExplanationPart expl = new ExplanationPart(text);
        expl.setDetails(generateNegRatingList());

        return expl;
    }
    
    private ExplanationPart generateNegRatingList() {
        List text = new ArrayList();
        
        ExplanationPart expl = new ExplanationPart(text);
        
        Iterator it = negativeRatings.iterator();
        while(it.hasNext()){
            List t = new ArrayList();
            Quad q = (Quad) it.next();
            
            t.add(q.getSubject());
            t.add(cl(" rated the sink negative in "));
            t.add(q.getGraphName());
            t.add(cl(". "));
            
            expl.addPart(new ExplanationPart(t));
        }

        if(negativeRatings.size() <=0){
            text.add(cl("No negative ratings found. "));
        }
        
        return expl;
    }
    
    protected com.hp.hpl.jena.graph.Graph explainRDF() {
        return null;
    }
// Explanation generation section end ---------------------------------------
    
    public int getPositiveRatings() {
        return this.positiveRatings.size();
    }
    
    public int getNeutralRatings() {
        return this.neutralRatings.size();
    }
    
    public int getNegativeRatings() {
        return this.negativeRatings.size();
    }
}
