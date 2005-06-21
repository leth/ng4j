package de.fuberlin.wiwiss.trust;

import java.io.StringReader;
import java.util.ArrayList;
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
 * {@link ExpressionConstraint} or {@link CountConstraint} instance.
 * The input is a string like this:</p>
 *
 * <pre>
 * ?date >= '2005-01-01' AND ?date &lt; '2005-12-31'
 * </pre>
 * 
 * @version $Id: ConstraintParser.java,v 1.3 2005/06/21 15:01:46 maresch Exp $
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
     * @param constraint The string representation of the constraint
     * @param prefixes Namespace prefixes that may be used in the constraint
     * @param metricInstances A collection of {@link Metric}s that can be used
     * 		within the constraint
     */
    public ConstraintParser(String constraint, PrefixMapping prefixes,
            Collection metricInstances, Collection rankBasedMetricInstances) {
        this.constraint = constraint;
        this.prefixes = prefixes;
        this.metricInstances = metricInstances;
        this.rankBasedMetricInstances = rankBasedMetricInstances;
    }
    
    public boolean isCountConstraint() {
        ensureParsed();
        return this.resultNode instanceof Q_CountExpression;
    }
    
    public boolean isRankBasedConstraint() {
        ensureParsed();
        return this.rankBasedMetric != null;
    }
    
    public RankBasedConstraint parseRankBasedConstraint(){
        ensureParsed();
        return new RankBasedConstraint((Q_MetricExpression) this.resultNode, this.rankBasedMetric);
    }
    
    /**
     * @return The parsed constraint
     * @throws TPLException on parse error
     */
    public ExpressionConstraint parseExpressionConstraint() {
        ensureParsed();
        return new ExpressionConstraint((Expr) this.resultNode);
    }

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
     * Return the metric instance identified by the URI Node or null otherwise.
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
     * Return the rank-based metric instance identified by the URI Node or null otherwise.
     */
    private RankBasedMetric findRankBasedConstraint(com.hp.hpl.jena.graph.Node metric){
        String uri = metric.getURI();
        Iterator it = this.rankBasedMetricInstances.iterator();
        while(it.hasNext()){
            RankBasedMetric rankBasedMetric = (RankBasedMetric) it.next();
            if(rankBasedMetric.getURI().equals(uri)){
                return rankBasedMetric;
            }
        }
        return null;
    }
    
}