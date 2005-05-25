/*
 * EbayMetricTest.java
 *
 * Created on 22. Mai 2005, 14:38
 */

package de.fuberlin.wiwiss.trust.metric;

import junit.framework.*;

import java.util.List;
import java.util.LinkedList;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import de.fuberlin.wiwiss.trust.MetricException;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class EbayMetricTest extends TestCase {
    
    NamedGraphSet data = null;
    EbayMetric metric = null;

    
    /** Creates a new instance of EbayMetricTest */
    public EbayMetricTest(java.lang.String testName) {
        super(testName);
    }
    
    protected void setUp(){
        metric = new EbayMetric();
    }
    
    public static Test suite(){
        TestSuite suite = new TestSuite("JUnit Test Suite for the Ebay metric");
        suite.addTest(new EbayMetricTest("testNoRatingTC1"));
        suite.addTest(new EbayMetricTest("testOnePosRatingTC2"));
        suite.addTest(new EbayMetricTest("testOneNegRatingTC3"));
        suite.addTest(new EbayMetricTest("testOneNeuRatingTC4"));
        suite.addTest(new EbayMetricTest("testOnePosAndNegRatingTC5"));
        suite.addTest(new EbayMetricTest("testOnePosNegAndNeuRatingTC6"));
        suite.addTest(new EbayMetricTest("testTwoPosOneNegAndNeuRatingTC7"));
        suite.addTest(new EbayMetricTest("testTwoNegOnePosAndNeuRatingTC8"));
        return suite;
    }
    
    protected void loadTestCaseData(int testCaseNumber) {
        data = new NamedGraphSetImpl();
        data.read(AllTests.TESTSPATH + "de/fuberlin/wiwiss/trust/metric/testEbayMetricTC" + testCaseNumber + ".trig", "TRIG");  
        metric.setup(data);
    }
    
    private MetricResult calcMetric(){
        Node sink = Node.createURI("mailto:testuser1@foo.com");
        List arguments = new LinkedList();
        arguments.add(sink);
        try {
            return (MetricResult) metric.calculateMetric(arguments);
        } catch(MetricException e){
            assertFalse(true);
        }
        return null;
    }
    
    public void testNoRatingTC1(){
        loadTestCaseData(1);
        MetricResult result = calcMetric();
        assertFalse(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(0, metric.getPositiveRatings());
        assertEquals(0, metric.getNeutralRatings());
        assertEquals(0, metric.getNegativeRatings());
    }
    
    public void testOnePosRatingTC2(){
        loadTestCaseData(2);
        MetricResult result = calcMetric();
        assertTrue(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(1, metric.getPositiveRatings());
        assertEquals(0, metric.getNeutralRatings());
        assertEquals(0, metric.getNegativeRatings());
    }
    
    public void testOneNegRatingTC3(){
        loadTestCaseData(3);
        MetricResult result = calcMetric();
        assertFalse(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(0, metric.getPositiveRatings());
        assertEquals(0, metric.getNeutralRatings());
        assertEquals(1, metric.getNegativeRatings());
    }
    
    public void testOneNeuRatingTC4(){
        loadTestCaseData(4);
        MetricResult result = calcMetric();
        assertFalse(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(0, metric.getPositiveRatings());
        assertEquals(1, metric.getNeutralRatings());
        assertEquals(0, metric.getNegativeRatings());
    }
    
    public void testOnePosAndNegRatingTC5(){
        loadTestCaseData(5);
        MetricResult result = calcMetric();
        assertFalse(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(1, metric.getPositiveRatings());
        assertEquals(0, metric.getNeutralRatings());
        assertEquals(1, metric.getNegativeRatings());
    }
    
    public void testOnePosNegAndNeuRatingTC6(){
        loadTestCaseData(6);
        MetricResult result = calcMetric();
        assertFalse(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(1, metric.getPositiveRatings());
        assertEquals(1, metric.getNeutralRatings());
        assertEquals(1, metric.getNegativeRatings());
    }
    
    public void testTwoPosOneNegAndNeuRatingTC7(){
        loadTestCaseData(7);
        MetricResult result = calcMetric();
        assertTrue(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(2, metric.getPositiveRatings());
        assertEquals(1, metric.getNeutralRatings());
        assertEquals(1, metric.getNegativeRatings());
    }
    
    public void testTwoNegOnePosAndNeuRatingTC8(){
        loadTestCaseData(8);
        MetricResult result = calcMetric();
        assertFalse(result.getResult());
        assertNotNull(result.getTextExplanation());
        assertEquals(1, metric.getPositiveRatings());
        assertEquals(1, metric.getNeutralRatings());
        assertEquals(2, metric.getNegativeRatings());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(EbayMetricTest.suite());
    }
}
