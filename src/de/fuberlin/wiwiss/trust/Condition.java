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
import de.fuberlin.wiwiss.ng4j.triql.legacy.Constraint;
import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;

/**
 * A condition in a trust policy that must hold for a result
 * binding to be trusted. This is a boolean expression that can
 * use all variables occuring in the graph patterns. 
 *
 * @version $Id: Condition.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Condition implements Constraint {
    private Expr expression;

    public Condition(Expr expression) {
        this.expression = expression;
    }
    
    /**
     * For use within TriQL Trust Layer
     * @param varNamesToNodes A variable binding
     * @return does it satisfy the condition?
     */
    public boolean isSatisfiedBy(Map varNamesToNodes) {
        return isSatisfied(null, createResultBindingFrom(varNamesToNodes));
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

    private ResultBinding createResultBindingFrom(Map varNamesToNodes) {
        Map varNamesToRDFNodes = new HashMap();
        Iterator it = varNamesToNodes.entrySet().iterator();
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