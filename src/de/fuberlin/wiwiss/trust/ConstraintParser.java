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
 * @version $Id: ConstraintParser.java,v 1.2 2005/03/28 22:31:51 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConstraintParser {
    private String constraint;
    private PrefixMapping prefixes;
    private Collection metricInstances = new ArrayList();
    private SimpleNode resultNode = null;
    
    /**
     * @param constraint The string representation of the constraint
     * @param prefixes Namespace prefixes that may be used in the constraint
     * @param metricInstances A collection of {@link Metric}s that can be used
     * 		within the constraint
     */
    public ConstraintParser(String constraint, PrefixMapping prefixes,
            Collection metricInstances) {
        this.constraint = constraint;
        this.prefixes = prefixes;
        this.metricInstances = metricInstances;
    }
    
    public boolean isCountConstraint() {
        ensureParsed();
        return this.resultNode instanceof Q_CountExpression;
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
            metricExpression.setMetricInstance(getMetricInstance(metricExpression.getURI()));
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            setMetricInstances(node.jjtGetChild(i));
        }
    }
    
    private Metric getMetricInstance(com.hp.hpl.jena.graph.Node uri) {
        Iterator it = this.metricInstances.iterator();
        while (it.hasNext()) {
            Metric metric = (Metric) it.next();
            if (metric.getURI().equals(uri.getURI())) {
                return metric;
            }
        }
        throw new TPLException("No Metric instance found for <" + uri + ">");
    }
}