package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.rdql.Query;

import de.fuberlin.wiwiss.ng4j.triql.ResultBinding;
import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_MetricExpression;

/**
 * A constraint in a trust policy that must hold for a result
 * binding to be trusted. This is a boolean expression that can
 * use all variables occuring in the graph patterns. 
 *
 * @version $Id: Constraint.java,v 1.2 2005/03/21 21:51:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Constraint {
    private Expr expression;

    public Constraint(Expr expression) {
        this.expression = expression;
    }
    
    /**
     * For use within TriQL Trust Layer
     * @param binding A variable binding
     * @return A boolean result and explanations from metrics
     */
    public MetricResult evaluate(VariableBinding binding) {
        final MetricResultCollector metricResultCollector = new MetricResultCollector();
        final boolean result = isSatisfied(metricResultCollector,
                createResultBindingFrom(binding));
        return new MetricResult() {
            public boolean getResult() {
                return result;
            }
            public ExplanationPart getTextExplanation() {
                return metricResultCollector.getTextExplanation();
            }
            public Graph getGraphExplanation() {
                return null;
            }
        };
    }
    
    /**
     * For use within TriQL parser (RDQL compatibility). Must be
     * public because declared in 
     * @param q The query we're in 
     * @param env A variable binding
     * @return does it satisfy the condition?
     */
    public boolean isSatisfied(Query q, ResultBinding env) {
        return this.expression.eval(q, env).getBoolean();
    }

    private ResultBinding createResultBindingFrom(VariableBinding vb) {
        Map varNamesToRDFNodes = new HashMap();
        Iterator it = vb.asMap().entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            Node varValue = (Node) entry.getValue();
            varNamesToRDFNodes.put(entry.getKey(), convertToRDFNode(varValue));
        }
        return new ResultBinding(varNamesToRDFNodes);
    }
    
    private RDFNode convertToRDFNode(Node node) {
        return StatementImpl.createObject(node, null);
    }
    
    /**
     * This is an ugly hack. The TriQL parser is designed to
     * return only a boolean from a query evaluation, but we
     * also need to return an {@link ExplanationPart}. We don't
     * want to change the node interface just for this single
     * use.
     * 
     * When evaluating a constraint, a {@link Query} instance
     * is passed around as a parameter, but we don't actually
     * need this for TriQL.P. So we abuse this parameter to
     * pass around a MetricResultCollector instance. The
     * {@link Q_MetricExpression} implementation then casts
     * the Query parameter to MetricResultCollector and passes
     * the {@link MetricResult} to the collector. Added benefit
     * of passing MetricResult instead of the explanation itself:
     * We keep the ability to generate the explanation on demand.
     * 
     * @version $Id: Constraint.java,v 1.2 2005/03/21 21:51:59 cyganiak Exp $
     * @author Richard Cyganiak (richard@cyganiak.de)
     */
    public class MetricResultCollector extends Query {
        private Collection metricResults = new ArrayList();
        
        public void collectMetricResult(MetricResult result) {
            this.metricResults.add(result);
        }
        
        public ExplanationPart getTextExplanation() {
            if (this.metricResults.size() == 1) {
                MetricResult result = (MetricResult) this.metricResults.iterator().next();
                return result.getTextExplanation();
            }
            ExplanationPart result = new ExplanationPart();
            Iterator it = this.metricResults.iterator();
            while (it.hasNext()) {
                MetricResult metricResult = (MetricResult) it.next();
                result.addPart(metricResult.getTextExplanation());
            }
            return result;
        }
    }
}