/*
 * AssertedGraphsTest.java
 * JUnit based test
 *
 * Created on 4. März 2005, 12:38
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.swp.vocabulary.SWP;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import junit.framework.*;

/**
 *
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class AssertedGraphsTest extends TestCase {
    
    public AssertedGraphsTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for the class AssertedGraphs");
        suite.addTest(new AssertedGraphsTest("testcase1_findAllAssertedGraphsOfAuthority"));
        return suite;
    }
    
    /**
     * Tests, that the AssertedGraphs class finds exactly those graphs, 
     * which are asserted by the given authority.
     */
    public void testcase1_findAllAssertedGraphsOfAuthority(){
        Node authority1 = Node.createURI("example1@example.com");
        Node warrant1 = Node.createURI("http://example.com/warrant1");
        Node graph1 = Node.createURI("http://example.com/graph1");
        Node authority2 = Node.createURI("example2@example.com");
        Node warrant2 = Node.createURI("http://example.com/warrant2");
        Node graph2 = Node.createURI("http://example.com/graph2");
        
        // setup test graphs
        NamedGraphSet set = new NamedGraphSetImpl();
        NamedGraph temp;
        
        temp = new NamedGraphImpl(warrant1, Factory.createDefaultGraph());
        set.addGraph(temp);
        temp = new NamedGraphImpl(graph1, Factory.createDefaultGraph());
        set.addGraph(temp);
        set.addQuad(new Quad(warrant1, warrant1, SWP.assertedBy, warrant1));
        set.addQuad(new Quad(warrant1, graph1, SWP.assertedBy, warrant1));
        set.addQuad(new Quad(warrant1, warrant1, SWP.authority, authority1));
        
        temp = new NamedGraphImpl(warrant2, Factory.createDefaultGraph());
        set.addGraph(temp);
        temp = new NamedGraphImpl(graph2, Factory.createDefaultGraph());
        set.addGraph(temp);
        set.addQuad(new Quad(warrant2, warrant2, SWP.assertedBy, warrant2));
        set.addQuad(new Quad(warrant2, graph2, SWP.assertedBy, warrant2));
        set.addQuad(new Quad(warrant2, warrant2, SWP.authority, authority2));
        
        // run AssertedGraphs
        AssertedGraphs asserted = new AssertedGraphs(authority1, set);
        NamedGraphSet graphs = asserted.getGraphs();
        Map warrantMap = asserted.getWarrantMap();
        
        // check
        assertNotNull(graphs);
        assertNotNull(warrantMap);
        assertTrue(graphs.containsGraph(warrant1));
        assertTrue(graphs.containsGraph(graph1));
        assertFalse(graphs.containsGraph(warrant2));
        assertFalse(graphs.containsGraph(graph2));
        assertEquals(warrant1, warrantMap.get(warrant1));
        assertEquals(warrant1, warrantMap.get(graph1));
    }
    
    public static void main(String[] args){
        junit.textui.TestRunner.run(AssertedGraphsTest.suite());
    }
}
