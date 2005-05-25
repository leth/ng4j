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
        return suite;
    }
    
    private MetricResult calcMetric(String source, String sink, int top, float in, float d, float T, int M, int l, float e)
        throws MetricException {
     
        metric.setup(data);
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
        
        return (MetricResult) metric.calculateMetric(arguments);
        
    }
    

    
    public void testcase1_emptyGraph(){
        MetricResult result = null;
        try{
            result = calcMetric("tc1.testuser1@foo.com", "tc1.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 200, 6, 1f);
            assertFalse(result.getResult());
            assertEquals(1, metric.getIterations());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }        
    }
    
    public void testcase2_onlyOneBlindTrustEdge(){
        MetricResult result = null;
        try{
            result = calcMetric("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 200, 6, 1f);
            assertTrue(result.getResult());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }
    }
    
    public void testcase3_findRankinOfTheTrustedNode(){
        MetricResult result = null;
        try{
            result = calcMetric("tc3.testuser1@foo.com", "tc3.testuser2@foo.com", 1, 1, 0.85f, 0.05f, 200, 6, 1f);
            assertTrue(result.getResult());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }
    }
    
    public void testcase4_findRankingOfTheUntrustworhyNode(){
        MetricResult result = null;
        try{
            result = calcMetric("tc3.testuser1@foo.com", "tc3.testuser3@foo.com", 1, 1, 0.85f, 0.05f, 200, 6, 1f);
            assertFalse(result.getResult());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }
    }
    
    public void testcase5_testPathLengthParamSinkOutOfRange(){
        MetricResult result = null;
        try{
            result = calcMetric("tc5.testuser1@foo.com", "tc5.testuser4@foo.com", 5, 20, 0.85f, 0.05f, 200, 2, 1);
            assertFalse(result.getResult());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }
    }
    
    public void testcase6_testPathLengthParamSinkWithinRange(){
        MetricResult result = null;
        try{
            result = calcMetric("tc5.testuser1@foo.com", "tc5.testuser3@foo.com", 5, 20, 0.85f, 0.05f, 200, 2, 1);
            assertTrue(result.getResult());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }
    }
    
    public void testcase7_testMaxNodesParamMoreNodesThanMaximum(){
        MetricResult result = null;
        try{
            result = calcMetric("tc5.testuser1@foo.com", "tc5.testuser4@foo.com", 5, 20, 0.85f, 0.05f, 3, 6, 1);
            assertFalse(result.getResult());
            assertEquals(3, metric.getNumberOfRankedNodes());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }
    }
    
    public void testcase8_testMaxNodesParamLessNodesThanMaximum(){
        MetricResult result = null;
        try{
            result = calcMetric("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 20, 0.85f, 0.05f, 3, 6, 1);
            assertTrue(result.getResult());
            assertEquals(metric.getNumberOfRankedNodes(), 2);
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        } 
    }
    
    public void testcase9_testTerminationByThreshold1(){
        MetricResult result = null;
        try{
            result = calcMetric("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.5f, 0.5f, 200, 6, 1);
            assertTrue(result.getResult());
            assertEquals(2, metric.getIterations());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        } 
    }

    public void testcase10_testTerminationByThreshold2(){
        MetricResult result = null;
        try{
            result = calcMetric("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.4f, 0.5f, 200, 6, 1);
            assertTrue(result.getResult());
            assertEquals(3, metric.getIterations());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        }     }
    
    public void testcase11_testTerminationByThreshold3(){
        MetricResult result = null;
        try{
            result = calcMetric("tc2.testuser1@foo.com", "tc2.testuser2@foo.com", 5, 1, 0.6f, 0.5f, 200, 6, 1);
            assertTrue(result.getResult());
            assertEquals(2,metric.getIterations());
        }catch(Exception e){
            System.err.print(e.toString());
            assertTrue(false);
        } 
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AppleseedMetricTest.suite());
    }
}
