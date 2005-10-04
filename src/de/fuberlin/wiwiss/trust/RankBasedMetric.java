package de.fuberlin.wiwiss.trust;

import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * <p>A trust metric to be used with the METRIC() syntax in Trust Policies.
 * There are two kinds of metrics:</p>
 * 
 * <ul>
 * <li>Simple metrics are algorithms that implement {@link Metric}. They
 * operate on a single {@link VariableBinding}.</li>
 * <li>Rank-based metrics are algorithms that implement this interface.
 * They operate on an entire {@link ResultTable}.</li>
 * </ul>
 * 
 * <p>Trust metrics are used as part of trust policies. Rank-based trust
 * metrics calculate boolean values for each row of a {@link ResultTable}.
 * Trust decisions can be made based on these boolean values.</p>
 * 
 * <p>Classes implementing this
 * interface will be instantiated once at startup time of the TriQL 
 * Trust Layer.</p>
 * 
 * <p>When the engine processes a query, it will call the {@link #init}
 * method of the {@link RankBasedMetric} and pass the untrusted dataset
 * and the input table.</p>
 * 
 * <p>The implementation will then use information from the untrusted
 * dataset to determine the boolean value for each row. The engine
 * will query the boolean values using the {@link #isAccepted} method.
 * Implementations are free to apply their own filtering to the utrusted
 * dataset.</p>
 * 
 * <p>Optionally, the metric can generate explanations. They present
 * a justification for the trust decision. Explanations can be
 * represented by {@link ExplanationPart} instances, or as generic
 * application-specifc RDF graphs.</p>
 * 
 * @version $Id: RankBasedMetric.java,v 1.2 2005/10/04 00:03:44 cyganiak Exp $
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface RankBasedMetric {
    
	/**
	 * (Re-)initializes the metric with the input data for a new query.
	 * @param source The untrusted repository
	 * @param inputTable The input data; a list (rows) of lists of {@link Node}s
	 * @throws MetricException
	 */
    public void init(NamedGraphSet source, List inputTable) throws MetricException;
    
    /**
     * Provides the metric's URI which uniquely identifies the algorithm.
     * Must return a constant value.
     * @return The URI identifying the algorithm
     */
    public String getURI();
    
    /**
     * @param row A row number of the input table
     * @return True if that row is accepted by the metric
     */
    public boolean isAccepted(int row);
    
    /**
     * @return The number of rows in the input table
     */
    public int rows();
    
    /**
     * @param row A row number of the input table
     * @return The text explanation for that row
     */
    public ExplanationPart explain(int row);
    
    /**
     * @param row A row number of the input table
     * @return The graph explanation for that row
     */
    public Graph explainRDF(int row); 
}