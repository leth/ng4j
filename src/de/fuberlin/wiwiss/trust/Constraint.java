package de.fuberlin.wiwiss.trust;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.rdql.Query;

import de.fuberlin.wiwiss.ng4j.triql.ResultBinding;
import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;

/**
 * A constraint in a trust policy that must hold for a result
 * binding to be trusted. This is a boolean expression that can
 * use all variables occuring in the graph patterns. 
 *
 * @version $Id: Constraint.java,v 1.1 2005/03/21 00:23:28 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Constraint implements de.fuberlin.wiwiss.ng4j.triql.legacy.Constraint {
    private Expr expression;

    public Constraint(Expr expression) {
        this.expression = expression;
    }
    
    /**
     * For use within TriQL Trust Layer
     * @param binding A variable binding
     * @return does it satisfy the condition?
     */
    public boolean isSatisfiedBy(VariableBinding binding) {
        return isSatisfied(null, createResultBindingFrom(binding));
    }

    /**
     * For use within TriQL parser (RDQL compatibility)
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
}