package de.fuberlin.wiwiss.trust;

import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.rdql.QueryException;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;
import de.fuberlin.wiwiss.ng4j.triql.parser.Node;
import de.fuberlin.wiwiss.ng4j.triql.parser.ParseException;
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_CountExpression;
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_MetricExpression;
import de.fuberlin.wiwiss.ng4j.triql.parser.SimpleNode;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;

/**
 * <p>Service for parsing a TriQL constraint into a
 * {@link ExpressionConstraint}, {@link RankBasedMetricConstraint} or
 * {@link CountConstraint} instance. The input is a string like this:</p>
 *
 * <pre>
 * ?date >= '2005-01-01' AND ?date &lt; '2005-12-31'
 * </pre>
 * 
 * <p>Or:</p>
 * 
 * <pre>
 * COUNT(?authority) >= 2
 * </pre>
 * 
 * <p>The various <tt>isXXX()</tt> methods can be used to distinguish
 * between the different kinds of constraints. Then, the appropriate
 * <tt>parseXXX()</tt> method can be called to retrieve the parsed
 * constraint.</p>
 * 
 * @version $Id: ConstraintParser.java,v 1.5 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConstraintParser {
    private String constraint;
    private PrefixMapping prefixes;
    private Collection metricInstances;
    private Collection rankBasedMetricInstances;
    private SimpleNode resultNode = null;
    private RankBasedMetric rankBasedMetric = null;
    
    /**
     * Creates a new parser.
     * 
     * @param constraint The string representation of the constraint
     * @param prefixes Namespace prefixes that may be used in the constraint
     * @param metricInstances A collection of {@link Metric}s that can be used
     * 		within the constraint
     * @param rankBasedMetricInstances A collection of {@link RankBasedMetric}s
     * 		that can be used within the constraint
     */
    public ConstraintParser(String constraint, PrefixMapping prefixes,
            Collection metricInstances, Collection rankBasedMetricInstances) {
        this.constraint = constraint;
        this.prefixes = prefixes;
        this.metricInstances = metricInstances;
        this.rankBasedMetricInstances = rankBasedMetricInstances;
    }
    
    /**
     * @return true if the constraint string is an expression constraint.
     */
    public boolean isExpressionConstraint() {
        ensureParsed();
        return !(this.resultNode instanceof Q_CountExpression);
    }
    
    /**
     * @return true if the constraint string is a count constraint.
     */
    public boolean isCountConstraint() {
        ensureParsed();
        return this.resultNode instanceof Q_CountExpression;
    }
    
    /**
     * @return true if the constraint string is a rank based metric
     * constraint.
     */
    public boolean isRankBasedConstraint() {
        ensureParsed();
        return this.rankBasedMetric != null;
    }
    
    /**
     * Parses the constraint string into a rank based metric constraint.
     * Must not be called unless {@link #isRankBasedConstraint()} is
     * true.
     * @return The parsed rank based metric constraint
     * @throws TPLException on parse error
     */
    public RankBasedMetricConstraint parseRankBasedConstraint(){
        ensureParsed();
        return new RankBasedMetricConstraint((Q_MetricExpression) this.resultNode, this.rankBasedMetric);
    }
    
    /**
     * Parses the constraint string into an expression constraint.
     * Must not be called unless {@link #isExpressionConstraint()} is
     * true.
     * @return The parsed expression constraint
     * @throws TPLException on parse error
     */
    public ExpressionConstraint parseExpressionConstraint() {
        ensureParsed();
        return new ExpressionConstraint((Expr) this.resultNode);
    }

    /**
     * Parses the constraint string into a count constraint.
     * Must not be called unless {@link #isCountConstraint()} is
     * true.
     * @return The parsed count constraint
     * @throws TPLException on parse error
     */
    public CountConstraint parseCountConstraint() {
        ensureParsed();
        Q_CountExpression count = (Q_CountExpression) this.resultNode;
        return new CountConstraint(
                count.variableName(), count.operator(), (int) count.value());
    }
    
    private void ensureParsed() {
        if (this.resultNode != null) {
            return;
        }
        TriQLParser parser = new TriQLParser(new StringReader(this.constraint));
        try {
            parser.CountOrExpression();
            this.resultNode = parser.top();
            this.resultNode.fixup(this.prefixes);
            setMetricInstances(this.resultNode);
        } catch (ParseException e) {
            throw new TPLException("Error in constraint '" + this.constraint +
                    "': " + e.getMessage());
        } catch (QueryException e) {
            throw new TPLException(e);
        }
    }
    
    private void setMetricInstances(Node node) {
        if (node instanceof Q_MetricExpression) {
            Q_MetricExpression metricExpression = (Q_MetricExpression) node;
            com.hp.hpl.jena.graph.Node metricNode = metricExpression.getURI();
            
            Metric metric = findMetricInstance(metricNode);
            if(metric != null){
                // if non-rank-based metric
                metricExpression.setMetricInstance(metric);
                this.rankBasedMetric = null;
            } else {
                // if rank-based metric
                this.rankBasedMetric = findRankBasedConstraint(metricExpression.getURI());
                if(this.rankBasedMetric == null){
                    // if no metric instance could be found
                    throw new TPLException("No Metric instance found for <" + metricNode.getURI() + ">");
                }
            }
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            setMetricInstances(node.jjtGetChild(i));
        }
    }
    
    /**
     * @param uri A URI identifying a metric 
     * @return The metric instance identified by the URI Node or null otherwise.
     */
    private Metric findMetricInstance(com.hp.hpl.jena.graph.Node uri) {
        Iterator it = this.metricInstances.iterator();
        while (it.hasNext()) {
            Metric metric = (Metric) it.next();
            if (metric.getURI().equals(uri.getURI())) {
                return metric;
            }
        }
        return null;
    }

    /**
     * @param metric A URI identifying a rank-based metric 
     * @return The rank-based metric instance identified by the URI Node or null otherwise.
     */
    private RankBasedMetric findRankBasedConstraint(com.hp.hpl.jena.graph.Node metric){
        String uri = metric.getURI();
        Iterator it = this.rankBasedMetricInstances.iterator();
        while(it.hasNext()){
            RankBasedMetric registeredMetric = (RankBasedMetric) it.next();
            if(registeredMetric.getURI().equals(uri)){
                return registeredMetric;
            }
        }
        return null;
    }
    
}