/*
 * RankBasedConstraint.java
 *
 * Created on 16. Juni 2005, 16:25
 */

package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.rdql.Query;

import de.fuberlin.wiwiss.ng4j.triql.ResultBinding;
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_MetricExpression;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class RankBasedConstraint {
    
    private Q_MetricExpression expression;
    
    private RankBasedMetric metric;
    
    /** Creates a new instance of RankBasedConstraint */
    public RankBasedConstraint(Q_MetricExpression expression, RankBasedMetric metric) {
        this.expression = expression;
        this.metric = metric;
    }
    
    public List getArgumentBindings(ResultTable t){
        List args = new ArrayList();
        Iterator it = t.bindingIterator();
        while(it.hasNext()){
            VariableBinding binding = (VariableBinding) it.next();
            args.add(expression.evalArgumentExpressions(new Query(),createResultBindingFrom(binding))); 
        }
        return args;
    }
    
    public RankBasedMetric getRankBasedMetric(){
        return this.metric;
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
    
}
