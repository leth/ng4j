package de.fuberlin.wiwiss.trust;

import java.util.List;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * <p>A trust metric to be used with the METRIC() syntax in Trust Policies.
 * An implementation of this interface implements one metric algorithm.</p>
 * 
 * <p>Trust metrics are used as part of trust policies. A metric can calculate
 * a boolean value for a list of input RDF resource. Trust decisions can
 * be made based on this boolean value.</p>
 * 
 * <p>Classes implementing this
 * interface will be instantiated once at startup time of the TriQL Trust Layer.
 * The engine will call the {@link #setup} method of each metric and pass
 * the untrusted dataset on which the engine operates.</p>
 * 
 * <p>When the engine processes a query, it will pass RDF nodes to the 
 * {@link #calculateMetric} method for evaluation.</p>
 * 
 * <p>The implementation will then use information from the untrusted dataset to
 * calculate its value. The boolean value is returned. Implementations are free
 * to apply their own filtering to the utrusted dataset.</p>
 * 
 * <p>Optionally, an explanation is returned. It presents a justification
 * for the trust decision. Explanations can be represented by
 * {@link ExplanationPart} instances, or as generic application-specifc
 * RDF graphs.</p>
 * 
 * @version $Id: Metric.java,v 1.2 2005/03/22 01:01:48 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface Metric {

    /**
     * Initializes the metric implementation and provides the dataset on
     * which the metric will operate. Is called once at startup time and
     * then never again.
     * 
     * @param sourceData The untrusted dataset
     */
    public void setup(NamedGraphSet sourceData);
    
    /**
     * Provides the metric's URI which uniquely identifies the algorithm.
     * Must return a constant value.
     * @return The URI identifying the algorithm
     */
    public String getURI();
    
    /**
     * Calculates the value of the metric for a given list of
     * inputs, which are RDF resources.
     * @param arguments Input arguments to the metric; a list of {@link Node}s
     * @return The result, consisting of an RDF node and an explanation
     * @throws MetricException if the number or type of input arguments
     * 		are incorrect
     */
    public EvaluationResult calculateMetric(List arguments) throws MetricException;
}
