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
import de.fuberlin.wiwiss.trust.metric.vocab.MindswapTrust;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class SemanticPageRankTest extends TestCase {
    
    private SemanticPageRank metric = null;
    
    private List propertySet1;
    private List propertySet2;
    private List propertySet3;
    
    public SemanticPageRankTest(java.lang.String testName) {
        super(testName);
        
        propertySet1 = new ArrayList();
        propertySet2 = new ArrayList();
        propertySet3 = new ArrayList();
        
        propertySet1.add(MindswapTrust.trust0);
        propertySet1.add(MindswapTrust.trust1);
        propertySet1.add(MindswapTrust.trust2);
        propertySet1.add(MindswapTrust.trust3);
        propertySet1.add(MindswapTrust.trust4);
        propertySet1.add(MindswapTrust.trust5);
        propertySet1.add(MindswapTrust.trust6);
        propertySet1.add(MindswapTrust.trust7);
        propertySet1.add(MindswapTrust.trust8);
        propertySet1.add(MindswapTrust.trust9);
        propertySet1.add(MindswapTrust.trust10);

        propertySet2.add(MindswapTrust.trust0);
        propertySet2.add(MindswapTrust.trust1);
        propertySet2.add(MindswapTrust.trust2);
        propertySet2.add(MindswapTrust.trust3);
        propertySet2.add(MindswapTrust.trust4);
        propertySet2.add(MindswapTrust.trust5);
        
        propertySet3.add(MindswapTrust.trust6);
        propertySet3.add(MindswapTrust.trust7);
        propertySet3.add(MindswapTrust.trust8);
        propertySet3.add(MindswapTrust.trust9);
        propertySet3.add(MindswapTrust.trust10);
    }
    
    protected void setUp(){
        metric = new SemanticPageRank();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("JUnit Test Suite for the Semantic PageRank");
        suite.addTest(new SemanticPageRankTest("testcase1_emptyGraph"));
        suite.addTest(new SemanticPageRankTest("testcase2_TwoPagesOneLink"));
        suite.addTest(new SemanticPageRankTest("testcase3_TwoConnectedPagesOneWithNoLink"));
        suite.addTest(new SemanticPageRankTest("testcase4_TwoConnectedPagesOneWithInLinkButNoOutLink"));
        suite.addTest(new SemanticPageRankTest("testcase5_TwoConnectedPagesOneWithOutLinkButNoInLink"));
        suite.addTest(new SemanticPageRankTest("testcase6_OnePageLinkedByALotOfNonImportantPages"));
        suite.addTest(new SemanticPageRankTest("testcase7_OnePageLinkedByOneVeryImportantPage"));
        return suite;
    }
    
    private List createArgumentList(String sink, int top, List properties) {
        java.util.List arguments = new java.util.LinkedList();
        Node sinkNode = com.hp.hpl.jena.graph.Node.createURI(sink);
        arguments.add(0,sinkNode);
        Node topNode = Node.createLiteral(Integer.toString(top), null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(1,topNode);

        arguments.addAll(properties);
        
        return arguments;        
    }
    
    protected NamedGraphSet loadTestCaseData(int testCaseNumber) {
        NamedGraphSet data = new NamedGraphSetImpl();
        data.read(AllTests.TESTSPATH + "de/fuberlin/wiwiss/trust/metric/testSemPageRankTC" + testCaseNumber + ".trig", "TRIG");  
        return data;
    }
    
    public void testcase1_emptyGraph(){
        List bindings = new LinkedList();
        List binding = createArgumentList("mailto:testuser1@foo.com", 5, propertySet1);
        bindings.add(binding);
        
        try{
            metric.init(loadTestCaseData(1), bindings);
        
            assertFalse(metric.isAccepted(0));
            assertNotNull(metric.explain(0));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase2_TwoPagesOneLink(){
        List bindings = new LinkedList();
        List binding = createArgumentList("mailto:testuser1@foo.com", 5, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser2@foo.com", 5, propertySet1);
        bindings.add(binding);
        
        try{
            metric.init(loadTestCaseData(2), bindings);
        
            assertTrue(metric.isAccepted(0));
            assertTrue(metric.isAccepted(1));
            assertNotNull(metric.explain(0));
            assertNotNull(metric.explain(1));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase3_TwoConnectedPagesOneWithNoLink(){
        List bindings = new LinkedList();
        List binding = createArgumentList("mailto:testuser1@foo.com", 2, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser2@foo.com", 2, propertySet1);
        bindings.add(binding);
        // the page with no links
        binding = createArgumentList("mailto:testuser3@foo.com", 2, propertySet1);
        bindings.add(binding);
        
        try{
            metric.init(loadTestCaseData(3), bindings);
        
            assertTrue(metric.isAccepted(0));
            assertTrue(metric.isAccepted(1));
            assertFalse(metric.isAccepted(2));
            assertNotNull(metric.explain(0));
            assertNotNull(metric.explain(1));
            assertNotNull(metric.explain(2));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase4_TwoConnectedPagesOneWithInLinkButNoOutLink(){
        List bindings = new LinkedList();
        List binding = createArgumentList("mailto:testuser1@foo.com", 2, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser2@foo.com", 2, propertySet1);
        bindings.add(binding);
        // the page with no out link
        binding = createArgumentList("mailto:testuser3@foo.com", 2, propertySet1);
        bindings.add(binding);
        
        try{
            metric.init(loadTestCaseData(4), bindings);
        
            assertTrue(metric.isAccepted(0));
            assertTrue(metric.isAccepted(1));
            assertFalse(metric.isAccepted(2));
            assertNotNull(metric.explain(0));
            assertNotNull(metric.explain(1));
            assertNotNull(metric.explain(2));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase5_TwoConnectedPagesOneWithOutLinkButNoInLink(){
        List bindings = new LinkedList();
        
        List binding = createArgumentList("mailto:testuser1@foo.com", 2, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser2@foo.com", 2, propertySet1);
        bindings.add(binding);
        // the page with no in Link
        binding = createArgumentList("mailto:testuser3@foo.com", 2, propertySet1);
        bindings.add(binding);
        
        try{
            metric.init(loadTestCaseData(5), bindings);
        
            assertTrue(metric.isAccepted(0));
            assertTrue(metric.isAccepted(1));
            assertFalse(metric.isAccepted(2));
            assertNotNull(metric.explain(0));
            assertNotNull(metric.explain(1));
            assertNotNull(metric.explain(2));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase6_OnePageLinkedByALotOfNonImportantPages(){
        List bindings = new LinkedList();
        
        // Page with a lot of non important in links
        List binding = createArgumentList("mailto:testuser1@foo.com", 1, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser2@foo.com", 1, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser3@foo.com", 1, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser4@foo.com", 1, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser5@foo.com", 1, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser6@foo.com", 1, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser7@foo.com", 1, propertySet1);
        bindings.add(binding);
        
        try{
            metric.init(loadTestCaseData(6), bindings);
        
            assertTrue(metric.isAccepted(0));
            assertFalse(metric.isAccepted(1));
            assertFalse(metric.isAccepted(2));
            assertFalse(metric.isAccepted(3));
            assertFalse(metric.isAccepted(4));
            assertFalse(metric.isAccepted(5));
            assertFalse(metric.isAccepted(6));
            assertNotNull(metric.explain(0));
            assertNotNull(metric.explain(1));
            assertNotNull(metric.explain(2));
            assertNotNull(metric.explain(3));
            assertNotNull(metric.explain(4));
            assertNotNull(metric.explain(5));
            assertNotNull(metric.explain(6));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public void testcase7_OnePageLinkedByOneVeryImportantPage(){
        List bindings = new LinkedList();
        
        // Page with a lot of non important in links
        List binding = createArgumentList("mailto:testuser1@foo.com", 1, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser2@foo.com", 2, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser3@foo.com", 4, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser4@foo.com", 4, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser5@foo.com", 4, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser6@foo.com", 4, propertySet1);
        bindings.add(binding);
        binding = createArgumentList("mailto:testuser7@foo.com", 4, propertySet1);
        bindings.add(binding);
        
        try{
            metric.init(loadTestCaseData(7), bindings);
        
            assertTrue(metric.isAccepted(0));
            assertTrue(metric.isAccepted(1));
            assertTrue(metric.isAccepted(2));
            assertFalse(metric.isAccepted(3));
            assertFalse(metric.isAccepted(4));
            assertFalse(metric.isAccepted(5));
            assertTrue(metric.isAccepted(6));
            assertNotNull(metric.explain(0));
            assertNotNull(metric.explain(1));
            assertNotNull(metric.explain(2));
            assertNotNull(metric.explain(3));
            assertNotNull(metric.explain(4));
            assertNotNull(metric.explain(5));
            assertNotNull(metric.explain(6));
        }catch(Exception e){
            e.printStackTrace(System.err);
            assertTrue(false);
        }        
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(SemanticPageRankTest.suite());
    }
}
