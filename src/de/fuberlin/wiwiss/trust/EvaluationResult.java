package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.graph.Graph;

/**
 * The result of a call to {@link Metric#calculateMetric}. Encapsulates
 * the two parts of the result -- the boolean result, and the explanation,
 * which might be an RDF graph or textual.
 *
 * @version $Id: EvaluationResult.java,v 1.1 2005/03/22 01:01:48 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface EvaluationResult {
    
    /**
     * @return The result
     */
    public boolean getResult();
    
    /**
     * @return The text explanation of the result, or null if the
     * 		metric does not provide text explanations
     */
    public ExplanationPart getTextExplanation();

    /**
     * @return The graph explanation of the result, or null if the
     * 		metric does not provide graph explanations
     */
    public Graph getGraphExplanation();
}
