package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;
import de.fuberlin.wiwiss.trust.GraphPatternTreeBuilder;
import de.fuberlin.wiwiss.trust.GraphPatternTreeNode;
import de.fuberlin.wiwiss.trust.TrustPolicy;

/**
 * Tests for {@link GraphPatternTreeBuilder}
 *
 * @version $Id: GraphPatternTreeBuilderTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPatternTreeBuilderTest extends FixtureWithLotsOfNodes {
    private GraphPatternTreeNode root;
    private List patterns;
    
    public void setUp() {
        this.root = null;
        this.patterns = new ArrayList();
    }
    
    public void testNoPatterns() {
        buildTree();
        assertEquals(0, this.root.getChildren().size());
        assertNull(this.root.getGraphPattern());
    }
    
    public void testUnconnectedPattern() {
        GraphPattern p1 = createPattern(Node.ANY);
        p1.addTriplePattern(new Triple(richard, mbox, richardsMbox));
        try {
            buildTree();
            fail("Expected failure because the pattern is not connected to GRAPH");
        } catch (IllegalArgumentException e) {
            // is expected
        }
    }

    public void testOnePattern() {
        GraphPattern p1 = createPattern(Node.ANY);
        p1.addTriplePattern(new Triple(TrustPolicy.GRAPH, assertedBy, varWarrant));
        buildTree();
        
        assertNull(this.root.getGraphPattern());
        assertEquals(1, this.root.getChildren().size());
        GraphPatternTreeNode n = (GraphPatternTreeNode) this.root.getChildren().iterator().next();
        assertEquals(0, n.getChildren().size());
        assertEquals(p1, n.getGraphPattern());
    }
    
    public void testTwoPatternsOnRoot() {
        GraphPattern p1 = createPattern(Node.ANY);
        p1.addTriplePattern(new Triple(TrustPolicy.GRAPH, assertedBy, varWarrant));
        GraphPattern p2 = createPattern(TrustPolicy.GRAPH);
        p2.addTriplePattern(new Triple(TrustPolicy.GRAPH, RDF.Nodes.type, PPD));
        buildTree();
        
        assertNull(this.root.getGraphPattern());
        assertEquals(2, this.root.getChildren().size());
        assertEquals(p1, ((GraphPatternTreeNode) this.root.getChildren().get(0)).getGraphPattern());
        assertEquals(p2, ((GraphPatternTreeNode) this.root.getChildren().get(1)).getGraphPattern());
    }
    
    public void testTwoChainedPatterns() {
        GraphPattern p1 = createPattern(varWarrant);
        p1.addTriplePattern(new Triple(TrustPolicy.GRAPH, assertedBy, varWarrant));
        p1.addTriplePattern(new Triple(varWarrant, authority, varKnownPerson));
        GraphPattern p2 = createPattern(Node.ANY);
        p2.addTriplePattern(new Triple(varKnownPerson, mbox, richardsMbox));
        buildTree();
        
        assertNull(this.root.getGraphPattern());
        assertEquals(1, this.root.getChildren().size());
        GraphPatternTreeNode n = (GraphPatternTreeNode) this.root.getChildren().get(0);
        assertEquals(1, n.getChildren().size());
        GraphPatternTreeNode n2 = (GraphPatternTreeNode) n.getChildren().get(0);
        assertEquals(0, n2.getChildren().size());
        assertEquals(p2, n2.getGraphPattern());
    }
    
    public void testChooseChildren() {
        GraphPatternTreeNode node = new GraphPatternTreeNode(null);
        List list = new ArrayList();
        list.add(new GraphPattern(TrustPolicy.GRAPH));
        list.add(new GraphPattern(TrustPolicy.GRAPH));
        list.add(new GraphPattern(graph1));
        list.add(new GraphPattern(Node.ANY));
        node.chooseChildren(list);
        
        assertEquals(2, node.getChildren().size());
    }
    
    public void testComplex() {
        GraphPattern Ga = createPattern(Node.ANY);
        Ga.addTriplePattern(new Triple(TrustPolicy.GRAPH, node1, Node.createVariable("a")));
        GraphPattern Gb = createPattern(Node.ANY);
        Gb.addTriplePattern(new Triple(TrustPolicy.GRAPH, node1, Node.createVariable("b")));
        GraphPattern Gc = createPattern(Node.ANY);
        Gc.addTriplePattern(new Triple(TrustPolicy.GRAPH, node1, Node.createVariable("c")));
        GraphPattern ab = createPattern(Node.ANY);
        ab.addTriplePattern(new Triple(Node.createVariable("a"), node1, Node.createVariable("b")));
        GraphPattern bc = createPattern(Node.ANY);
        bc.addTriplePattern(new Triple(Node.createVariable("b"), node1, Node.createVariable("c")));
        buildTree();
        
        assertEquals(3, this.root.getChildren().size());
        GraphPatternTreeNode l1, l2, l3, l4;
        l1 = (GraphPatternTreeNode) this.root.getChildren().get(0);
        assertEquals(Ga, l1.getGraphPattern());
        assertEquals(1, l1.getChildren().size());
        l2 = (GraphPatternTreeNode) l1.getChildren().get(0);
        assertEquals(ab, l2.getGraphPattern());
        assertEquals(2, l2.getChildren().size());
        l3 = (GraphPatternTreeNode) l2.getChildren().get(0);
        assertEquals(Gb, l3.getGraphPattern());
        assertEquals(0, l3.getChildren().size());
        l3 = (GraphPatternTreeNode) l2.getChildren().get(1);
        assertEquals(bc, l3.getGraphPattern());
        assertEquals(1, l3.getChildren().size());
        l4 = (GraphPatternTreeNode) l3.getChildren().get(0);
        assertEquals(Gc, l4.getGraphPattern());
        assertEquals(0, l4.getChildren().size());

        l1 = (GraphPatternTreeNode) this.root.getChildren().get(1);
        assertEquals(Gb, l1.getGraphPattern());
        assertEquals(2, l1.getChildren().size());
        l2 = (GraphPatternTreeNode) l1.getChildren().get(0);
        assertEquals(ab, l2.getGraphPattern());
        assertEquals(1, l2.getChildren().size());
        l3 = (GraphPatternTreeNode) l2.getChildren().get(0);
        assertEquals(Ga, l3.getGraphPattern());
        assertEquals(0, l3.getChildren().size());
        l2 = (GraphPatternTreeNode) l1.getChildren().get(1);
        assertEquals(bc, l2.getGraphPattern());
        assertEquals(1, l2.getChildren().size());
        l3 = (GraphPatternTreeNode) l2.getChildren().get(0);
        assertEquals(Gc, l3.getGraphPattern());
        assertEquals(0, l3.getChildren().size());

        l1 = (GraphPatternTreeNode) this.root.getChildren().get(2);
        assertEquals(Gc, l1.getGraphPattern());
        assertEquals(1, l1.getChildren().size());
        l2 = (GraphPatternTreeNode) l1.getChildren().get(0);
        assertEquals(bc, l2.getGraphPattern());
        assertEquals(2, l2.getChildren().size());
        l3 = (GraphPatternTreeNode) l2.getChildren().get(0);
        assertEquals(Gb, l3.getGraphPattern());
        assertEquals(0, l3.getChildren().size());
        l3 = (GraphPatternTreeNode) l2.getChildren().get(1);
        assertEquals(ab, l3.getGraphPattern());
        assertEquals(1, l3.getChildren().size());
        l4 = (GraphPatternTreeNode) l3.getChildren().get(0);
        assertEquals(Ga, l4.getGraphPattern());
        assertEquals(0, l4.getChildren().size());
    }
    
    private GraphPattern createPattern(Node graphName) {
        GraphPattern result = new GraphPattern(graphName);
        this.patterns.add(result);
        return result;
    }
    
    private void buildTree() {
        this.root = new GraphPatternTreeBuilder(this.patterns).getRootNode();
    }
}
