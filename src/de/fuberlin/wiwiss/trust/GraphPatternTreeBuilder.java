package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;

/**
 * Builds a tree of {@link GraphPatternTreeNode}s from a set
 * of {@link GraphPattern}s. The algorithm is specified in
 * a separate document.
 *
 * @version $Id: GraphPatternTreeBuilder.java,v 1.2 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @see GraphPatternTreeNode
 */
public class GraphPatternTreeBuilder {
    private List patterns;
    private List unconnectedPatterns;
    
    /**
     * Sets up a new graph pattern tree builder.
     * @param graphPatterns The {@link GraphPattern}s that should be
     * 		arranged in a tree
     */
    public GraphPatternTreeBuilder(List graphPatterns) {
        this.patterns = graphPatterns;
        this.unconnectedPatterns = new ArrayList(graphPatterns);
    }
    
    /**
     * @return The root node of the resulting graph pattern tree
     */
    public GraphPatternTreeNode getRootNode() {
        GraphPatternTreeNode root = new GraphPatternTreeNode(null);
        chooseChildren(root);
        markConnectedRecursive(root);
        checkForUnconnectedPatterns();
        return root;
    }
    
    private void chooseChildren(GraphPatternTreeNode node) {
        node.chooseChildren(this.patterns);
        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            GraphPatternTreeNode child = (GraphPatternTreeNode) it.next();
            chooseChildren(child);
        }
    }
    
    private void markConnectedRecursive(GraphPatternTreeNode node) {
        this.unconnectedPatterns.remove(node.getGraphPattern());
        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            GraphPatternTreeNode child = (GraphPatternTreeNode) it.next();
            markConnectedRecursive(child);
        }
    }
    
    private void checkForUnconnectedPatterns() {
        if (!this.unconnectedPatterns.isEmpty()) {
            throw new IllegalArgumentException(
                    "A graph pattern is not connected to ?GRAPH by variables");
        }
    }
}
