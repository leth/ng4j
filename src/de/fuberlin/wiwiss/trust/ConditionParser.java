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
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_MetricExpression;
import de.fuberlin.wiwiss.ng4j.triql.parser.SimpleNode;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;

/**
 * <p>Service for parsing a TriQL condition into a
 * {@link Condition} instance. The input is a string
 * like this:</p>
 *
 * <pre>
 * ?date >= '2005-01-01' AND ?date &lt; '2005-12-31'
 * </pre>
 * 
 * @version $Id: ConditionParser.java,v 1.2 2005/03/15 08:59:08 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConditionParser {
    private String condition;
    private PrefixMapping prefixes;
    private Collection metricInstances = new ArrayList();
    
    /**
     * @param condition The string representation of the condition
     * @param prefixes Namespace prefixes that may be used in the condition
     * @param metricInstances A collection of {@link Metric}s that can be used
     * 		within the condition
     */
    public ConditionParser(String condition, PrefixMapping prefixes,
            Collection metricInstances) {
        this.condition = condition;
        this.prefixes = prefixes;
        this.metricInstances = metricInstances;
    }
    
    /**
     * @return The parsed constraint
     * @throws TPLException on parse error
     */
    public Condition parse() {
        TriQLParser parser = new TriQLParser(new StringReader(this.condition));
        try {
            parser.CountOrExpression();
            SimpleNode parseTree = parser.top();
            parseTree.fixup(this.prefixes);
            setMetricInstances(parseTree);
            return new Condition((Expr) parseTree);
        } catch (ParseException e) {
            throw new TPLException("Error in condition '" + this.condition +
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