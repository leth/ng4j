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
 * A node in the tree of graph patterns. Represents one
 * {@link GraphPattern}.
 * 
 * @version $Id: GraphPatternTreeNode.java,v 1.3 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPatternTreeNode {
    private GraphPattern pattern;
    private GraphPatternTreeNode parent;
    private Node labelOnArcToParent;
    private List children = new ArrayList();

    /**
     * Creates a new graph pattern tree node.
     * @param pattern The graph pattern represented by the node
     */
    public GraphPatternTreeNode(GraphPattern pattern) {
        this.pattern = pattern;
    }
    
    /**
     * @return The graph pattern represented by the node
     */
    public GraphPattern getGraphPattern() {
        return this.pattern;
    }
    
    /**
     * @return A list of GraphPatternTreeNodes, the children of this node
     */
    public List getChildren() {
        return this.children;
    }

    /**
     * Adds children to the node. Those children from the argument
     * list that can be children of this node will be added.
     * @param candidateGraphPatterns A list of candidate
     * 		{@link GraphPattern}s
     */
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
