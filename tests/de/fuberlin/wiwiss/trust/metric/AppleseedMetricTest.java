/*
 * AppleseedMetricTest.java
 * JUnit based test
 *
 * Created on 7. April 2005, 11:29
 */

package de.fuberlin.wiwiss.trust.metric;

import junit.framework.*;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.trust.Metric;
import de.fuberlin.wiwiss.trust.MetricException;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class AppleseedMetricTest extends TestCase {
    
    NamedGraphSet data = null;
    AppleseedMetric metric = null;
    
    public AppleseedMetricTest(java.lang.String testName) {
        super(testName);
    }
    
    protected void setUp(){
        data = new NamedGraphSetImpl();
        data.read(AllTests.TESTSPATH + "de/fuberlin/wiwiss/trust/metric/testDataAppleseed.trig", "TRIG");  
        metric = new AppleseedMetric();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("JUnit Test Suite for the Appleseed metric");
        suite.addTest(new AppleseedMetricTest("testcase1_emptyGraph"));
        suite.addTest(new AppleseedMetricTest("testcase2_onlyOneBlindTrustEdge"));
        suite.addTest(new AppleseedMetricTest("testcase3_findRankinOfTheTrustedNode"));
        suite.addTest(new AppleseedMetricTest("testcase4_findRankingOfTheUntrustworhyNode"));
        suite.addTest(new AppleseedMetricTest("testcase5_testPathLengthParamSinkOutOfRange"));
        suite.addTest(new AppleseedMetricTest("testcase6_testPathLengthParamSinkWithinRange"));
        suite.addTest(new AppleseedMetricTest("testcase7_testMaxNodesParamMoreNodesThanMaximum"));
        suite.addTest(new AppleseedMetricTest("testcase8_testMaxNodesParamLessNodesThanMaximum"));
        suite.addTest(new AppleseedMetricTest("testcase9_testTerminationByThreshold1"));
        suite.addTest(new AppleseedMetricTest("testcase10_testTerminationByThreshold2"));
        suite.addTest(new AppleseedMetricTest("testcase11_testTerminationByThreshold3"));
        suite.addTest(new AppleseedMetricTest("testcase12_testCache"));
        return suite;
    }
    
    private List createArgumentList(String source, String sink, int top, float in, float d, float T, int M, int l, float e) {
        java.util.List arguments = new java.util.LinkedList();
        com.hp.hpl.jena.graph.Node sourceNode = com.hp.hpl.jena.graph.Node.createURI(source);
        arguments.add(0,sourceNode);
        com.hp.hpl.jena.graph.Node sinkNode = com.hp.hpl.jena.graph.Node.createURI(sink);
        arguments.add(1,sinkNode);
        com.hp.hpl.jena.graph.Node topNode = com.hp.hpl.jena.graph.Node.createLiteral(Integer.toString(top), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(2,topNode);
        com.hp.hpl.jena.graph.Node inNode = com.hp.hpl.jena.graph.Node.createLiteral(Float.toString(in), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(3,inNode);
        com.hp.hpl.jena.graph.Node dNode = com.hp.hpl.jena.graph.Node.createLiteral(Float.toString(d), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(4,dNode);
        com.hp.hpl.jena.graph.Node TNode = com.hp.hpl.jena.graph.Node.createLiteral(Float.toString(T), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(5,TNode);
        com.hp.hpl.jena.graph.Node MNode = com.hp.hpl.jena.graph.Node.createLiteral(Integer.toString(M), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(6,MNode);
        com.hp.hpl.jena.graph.Node lNode = com.hp.hpl.jena.graph.Node.createLiteral(Integer.toString(l), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(7,lNode);
        com.hp.hpl.jena.graph.Node eNode = com.hp.hpl.jena.graph.Node.createLiteral(Float.toString(e), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(8,eNode);
        
        return arguments;        
    }
    

    
    public void testcase1_emptyGraph(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc1.testuser1@foo.com", "tc1.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);
        
            assertFalse(metric.isAccepted(0));
            assertEquals(1, metric.getIterations(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase2_onlyOneBlindTrustEdge(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);
        
            assertTrue(metric.isAccepted(0));
            assertEquals(2, metric.getIterations(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase3_findRankinOfTheTrustedNode(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc3.testuser1@foo.com", "tc3.testuser2@foo.com", 1, 1, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertTrue(metric.isAccepted(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }
    }
    
    public void testcase4_findRankingOfTheUntrustworhyNode(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc3.testuser1@foo.com", "tc3.testuser3@foo.com", 1, 1, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertFalse(metric.isAccepted(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }
    }
    
    public void testcase5_testPathLengthParamSinkOutOfRange(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc5.testuser1@foo.com", "tc5.testuser4@foo.com", 5, 20, 0.85f, 0.05f, 200, 2, 1);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertFalse(metric.isAccepted(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }
    }
    
    public void testcase6_testPathLengthParamSinkWithinRange(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc5.testuser1@foo.com", "tc5.testuser3@foo.com", 5, 20, 0.85f, 0.05f, 200, 2, 1);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertTrue(metric.isAccepted(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }
    }
    
    public void testcase7_testMaxNodesParamMoreNodesThanMaximum(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc5.testuser1@foo.com", "tc5.testuser4@foo.com", 5, 20, 0.85f, 0.05f, 3, 6, 1);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertFalse(metric.isAccepted(0));
            assertEquals(3, metric.getNumberOfRankedNodes(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }
    }
    
    public void testcase8_testMaxNodesParamLessNodesThanMaximum(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 3, 6, 1);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertTrue(metric.isAccepted(0));
            assertEquals(2, metric.getNumberOfRankedNodes(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        } 
    }
    
    public void testcase9_testTerminationByThreshold1(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.5f, 0.5f, 200, 6, 1);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertTrue(metric.isAccepted(0));
            assertEquals(2, metric.getIterations(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        } 
    }

    public void testcase10_testTerminationByThreshold2(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.4f, 0.5f, 200, 6, 1);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertTrue(metric.isAccepted(0));
            assertEquals(2, metric.getIterations(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }
    }
    
    public void testcase11_testTerminationByThreshold3(){
        List bindings = new LinkedList();
        List binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.6f, 0.5f, 200, 6, 1);
        bindings.add(binding);
        
        try{
            metric.init(data, bindings);

            assertTrue(metric.isAccepted(0));
            assertEquals(2,metric.getIterations(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        } 
    }
    
    public void testcase12_testCache(){
        
        // runs all testcases from 1 to 11 at once and tests the results by using the cache
        List bindings = new LinkedList();
        // TC1
        List binding = createArgumentList("tc1.testuser1@foo.com", "tc1.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        // TC2
        binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        // TC3
        binding = createArgumentList("tc3.testuser1@foo.com", "tc3.testuser2@foo.com", 1, 1, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        // TC4
        binding = createArgumentList("tc3.testuser1@foo.com", "tc3.testuser3@foo.com", 1, 1, 0.85f, 0.05f, 200, 6, 1f);
        bindings.add(binding);
        // TC5
        binding = createArgumentList("tc5.testuser1@foo.com", "tc5.testuser4@foo.com", 5, 20, 0.85f, 0.05f, 200, 2, 1);
        bindings.add(binding);
        // TC6
        binding = createArgumentList("tc5.testuser1@foo.com", "tc5.testuser3@foo.com", 5, 20, 0.85f, 0.05f, 200, 2, 1);
        bindings.add(binding);
        // TC7
        binding = createArgumentList("tc5.testuser1@foo.com", "tc5.testuser4@foo.com", 5, 20, 0.85f, 0.05f, 3, 6, 1);
        bindings.add(binding);
        // TC8
        binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 3, 6, 1);
        bindings.add(binding);
        // TC9
        binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.5f, 0.5f, 200, 6, 1);
        bindings.add(binding);
        // T10
        binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.4f, 0.5f, 200, 6, 1);
        bindings.add(binding);
        // 11 
        binding = createArgumentList("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.6f, 0.5f, 200, 6, 1);
        bindings.add(binding);
        
        
        try{
            metric.init(data, bindings);

            // TC1
            assertFalse(metric.isAccepted(0));
            assertEquals(1, metric.getIterations(0));
            assertNotNull(metric.explain(0));
            // TC2
            assertTrue(metric.isAccepted(1));
            assertEquals(2, metric.getIterations(1));
            assertNotNull(metric.explain(1));
            // TC3
            assertTrue(metric.isAccepted(2));
            assertNotNull(metric.explain(2));
            // TC4
            assertFalse(metric.isAccepted(3));
            assertNotNull(metric.explain(3));
            // TC5
            assertFalse(metric.isAccepted(4));
            assertNotNull(metric.explain(4));
            // TC6
            assertTrue(metric.isAccepted(5));
            assertNotNull(metric.explain(5));
            // TC7
            assertFalse(metric.isAccepted(6));
            assertEquals(3, metric.getNumberOfRankedNodes(6));
            assertNotNull(metric.explain(6));
            // TC8
            assertTrue(metric.isAccepted(7));
            assertEquals(2, metric.getNumberOfRankedNodes(7));
            assertNotNull(metric.explain(7));
            // TC9
            assertTrue(metric.isAccepted(8));
            assertEquals(2, metric.getIterations(8));
            assertNotNull(metric.explain(8));
            // TC10
            assertTrue(metric.isAccepted(9));
            assertEquals(2, metric.getIterations(9));
            assertNotNull(metric.explain(9));
            // TC11
            assertTrue(metric.isAccepted(10));
            assertEquals(2,metric.getIterations(10));
            assertNotNull(metric.explain(10));
            
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        } 
        
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AppleseedMetricTest.suite());
    }
}
