package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * <p>A node in the graph pattern tree of a policy. Each node, except for the
 * root, <em>stands for</em> a graph pattern. Each node <em>exposes</em>
 * one or more variables. The branches connecting the nodes are <em>labelled</em> with a
 * variable name.</p>
 * 
 * <p>Graph pattern trees are used for evaluating COUNT constraints and for
 * generating explanation trees.</p>
 * 
 * <p>The root exposes the variable <tt>?GRAPH</tt>. All other nodes expose
 * all variables that occur in the graph pattern it stands for, minus those
 * that are labels on the path back to the root.</p>
 * 
 * <p>The following algorithm builds a tree from a set of graph patterns:</p>
 * 
 * <ul>
 *   <li>Begin with the root.</li>
 *   <li>Finding the children of a node <em>n</em>: For each of its exposed
 *     variables <em>v</em>, add as children all graph patterns that contain
 *     <em>v</em>, are not <em>n</em> itself, and are not an ancestor of
 *     <em>n</em>. Label the branch connecting <em>n</em> and the child
 *     with <em>v</em>.</li>
 *   <li>Recurse.</li>
 * </ul>
 * 
 * <p>Multiple nodes might stand for the same graph pattern.</p>
 *
 * <p>@@@ Evaluating COUNT: Start at the root. We need things I call
 * result parts. A result part is a number of result bindings and sits at a node.
 * and is labelled with a variable name (one of the exposed vars at the node).
 * The root has one result part, which is the table of all result bindings.
 * For other nodes, the result parts are generated by a process I call
 * <em>handing down</em> the result parts of the parent. When a result
 * part is handed down from a parent to a child, then it is plit into
 * multiple result parts, each containing those rows with identical l, where
 * l is the label of the arc connecting parent and child. For example, if
 * there are three different values g1, g2, g3 for GRAPH at the root, then a
 * child of root will have three result parts, one with all rows having GRAPH=g1,
 * one with GRAPH=g2 and one with GRAPH=g3. To eval COUNT, walk the
 * graph pattern tree from the leaves to the root. At each node, if there's
 * an exposed variable v with a COUNT constraint, then check the number of
 * distinct values of v within each result part, and eliminate those parts
 * where the count doesn't mach the constraint. When a part is eliminated,
 * then all of its rows are removed from all parts on the path towards the
 * root.</p>
 * 
 * <p>@@@ Generating explanations: Start at the leaves. Use a data structure
 * consisting of a set of (explanation tree fragment + IDs of all rows it explains).
 * Start with the empty set. When a node's pattern has no explanation template,
 * just hand up what you got handed. If there is an explanation template,
 * then group the result binding table by all variables used in the template, create
 * a new explanation exn for each distinct value, and append as children
 * to exn all explanations that were handed up to you and that apply to at least
 * one of the rows explained by exn. Hand up all the explanations to the parent.</p>
 * 
 * TODO: What about ?SUBJ, ?PRED, ?OBJ?
 *  
 * @version $Id: GraphPatternTreeNode.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPatternTreeNode {
    private GraphPattern pattern;
    private GraphPatternTreeNode parent;
    private Node labelOnArcToParent;
    private List children = new ArrayList();

    public GraphPatternTreeNode(GraphPattern pattern) {
        this.pattern = pattern;
    }
    
    public GraphPattern getGraphPattern() {
        return this.pattern;
    }
    
    public List getChildren() {
        return this.children;
    }
    
    public void chooseChildren(List candidateGraphPatterns) {
        Iterator it = candidateGraphPatterns.iterator();
        while (it.hasNext()) {
            GraphPattern candidatePattern = (GraphPattern) it.next();
            if (candidatePattern == this.pattern || isAncestorPattern(candidatePattern)) {
                continue;
            }
            Iterator exposedVars = getExposedVariables().iterator();
            while (exposedVars.hasNext()) {
                Node variable = (Node) exposedVars.next();
                if (candidatePattern.getAllVariables().contains(variable)) {
                    addChild(new GraphPatternTreeNode(candidatePattern), variable);
                }
            }
        }
    }
    
    private void addChild(GraphPatternTreeNode child, Node labelOnArc) {
        this.children.add(child);
        child.setParent(this, labelOnArc);
    }
    
    private void setParent(GraphPatternTreeNode parent, Node labelOnArc) {
        this.parent = parent;
        this.labelOnArcToParent = labelOnArc;
    }
    
    private Set getLabelsOnPathToRoot() {
        if (this.parent == null) {
            return new HashSet();
        }
        Set result = this.parent.getLabelsOnPathToRoot();
        result.add(this.labelOnArcToParent);
        return result;
    }
    
    private Set getExposedVariables() {
        if (this.pattern == null) {
            return Collections.singleton(TrustPolicy.GRAPH);
        }
        Set result = new HashSet(this.pattern.getAllVariables());
        result.removeAll(getLabelsOnPathToRoot());
        return result;
    }
    
    private boolean isAncestorPattern(GraphPattern candidatePattern) {
        if (this.parent == null) {
            return false;
        }
        return this.parent.getGraphPattern() == candidatePattern
        			|| this.parent.isAncestorPattern(candidatePattern);
    }
}