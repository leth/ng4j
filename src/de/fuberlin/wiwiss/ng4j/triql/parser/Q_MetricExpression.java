package de.fuberlin.wiwiss.ng4j.triql.parser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.triql.ResultBinding;
import de.fuberlin.wiwiss.ng4j.triql.legacy.Value;
import de.fuberlin.wiwiss.trust.Metric;
import de.fuberlin.wiwiss.trust.MetricException;
import de.fuberlin.wiwiss.trust.EvaluationResult;
import de.fuberlin.wiwiss.trust.TriQLHelper;
import de.fuberlin.wiwiss.trust.Constraint.MetricResultCollector;

/**
 * A METRIC expression in TriQL.P. This is not used for vanilla TriQL.
 * The Metric instance is set from the outside after parsing has finished.
 * 
 * @version $Id: Q_MetricExpression.java,v 1.3 2005/03/22 01:01:48 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Q_MetricExpression extends SimpleNode implements Expr, ExprBoolean {
    private List argumentExpressions = new ArrayList();	// [Expr]
    private com.hp.hpl.jena.graph.Node uri;
    private Metric metric;
    
    Q_MetricExpression(int id) {
	    super(id);
	}

    Q_MetricExpression(TriQLParser p, int id) {
        super(p, id);
    }

    public void fixup(PrefixMapping prefixes) {
        int exprCount = 0;
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            ((SimpleNode) jjtGetChild(i)).fixup(prefixes);
            if (jjtGetChild(i) instanceof Expr) {
                exprCount++;
                if (exprCount == 1) {
                    this.uri = TriQLHelper.toRDFNode(jjtGetChild(i));
                } else {
                    this.argumentExpressions.add(jjtGetChild(i));
                }
            }
        }
    }
    
    public com.hp.hpl.jena.graph.Node getURI() {
        return this.uri;
    }
    
    public void setMetricInstance(Metric instance) {
        this.metric = instance;
    }
    
    public Value eval(Query q, ResultBinding env) {
        List args = new ArrayList();
        Iterator it = this.argumentExpressions.iterator();
        while (it.hasNext()) {
            Expr expression = (Expr) it.next();
            args.add(TriQLHelper.toRDFNode(expression.eval(q, env)));
        }
        try {
            EvaluationResult metricResult = this.metric.calculateMetric(args);
            if (q != null) {  
                ((MetricResultCollector) q).collectMetricResult(metricResult);
            }
            ParsedLiteral result = new ParsedLiteral();
            result.setBoolean(metricResult.getResult());
            return result;
        } catch (MetricException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public String asInfixString() {
        return toString();
    }

    public String asPrefixString() {
        return toString();
    }

    public void print(PrintWriter pw, int level) {
        pw.print(toString());
    }

    public String toString() {
        StringBuffer result = new StringBuffer("METRIC(<");
        result.append(this.uri);
        result.append(">");
        Iterator it = this.argumentExpressions.iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (!first) {
                result.append(", ");
            }
            Expr expr = (Expr) it.next();
            result.append(expr);
        }
        result.append(")");
        return result.toString();
    }
}