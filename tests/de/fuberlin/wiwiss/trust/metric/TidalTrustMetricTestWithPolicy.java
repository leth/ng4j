/*
 * Created on 3. März 2005, 18:30
 */

package de.fuberlin.wiwiss.trust.metric;

import junit.framework.*;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.trust.TrustedGraph;


/**
 *
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class TidalTrustMetricTestWithPolicy extends TestCase {
    
    private TrustedGraph tlg = null;
    
    public TidalTrustMetricTestWithPolicy(java.lang.String testName) {
        super(testName);
     }
    
    protected void setUp(){
        // get trust policy
        Model tplFile = ModelFactory.createDefaultModel();
        tplFile.read(AllTests.TESTSPATH + "de/fuberlin/wiwiss/trust/metric/testMetricPolicies.n3", "N3");
        Graph policies = tplFile.getGraph();
        
        NamedGraphSet source = new NamedGraphSetImpl();
        source.read(AllTests.TESTSPATH + "de/fuberlin/wiwiss/trust/metric/testDataTidalTrust.trig", "TRIG");

        this.tlg = new TrustedGraph(source, policies);
        this.tlg.registerMetricImplementation(TidalTrustMetric.class);
        this.tlg.selectTrustPolicy("http://www.fu-berlin/suhl/bizer/financialscenario/policies/test/Policy1");
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("JUnit test of the TidalTrust metric");
        suite.addTest(new TidalTrustMetricTest("testcase1_SourceHasDirectRating1"));
        suite.addTest(new TidalTrustMetricTest("testcase2_SourceHasDirectRating2"));
        suite.addTest(new TidalTrustMetricTest("testcase3_NoPathToSink1"));
        suite.addTest(new TidalTrustMetricTest("testcase4_OnePathOfLengthTwo1"));
        suite.addTest(new TidalTrustMetricTest("testcase5_OnePathOfLengthTwo2"));
        suite.addTest(new TidalTrustMetricTest("testcase6_OnePathOfLengthTwo3"));
        suite.addTest(new TidalTrustMetricTest("testcase7_TwoPathesOfLengthTwo1"));
        suite.addTest(new TidalTrustMetricTest("testcase8_TwoPathesOfLengthTwo2"));
        suite.addTest(new TidalTrustMetricTest("testcase9_TwoPathesOfLengthTwo3"));
        suite.addTest(new TidalTrustMetricTest("testcase10_SelectShortestPath"));
        suite.addTest(new TidalTrustMetricTest("testcase11_NoPathToSink2"));
        suite.addTest(new TidalTrustMetricTest("testcase12_complexGraph"));
        return suite;
    }
    
    private ExtendedIterator calcMetric(Node source){
        tlg.setSystemVariable("USER", source);
        
        return tlg.find(new Triple(Node.ANY, RDFS.Nodes.label, Node.ANY));
    }
    
    private void assertOneTriple(ExtendedIterator it){
        assertTrue(it.hasNext());
        it.next();
        assertFalse(it.hasNext());
    }
    
    private void assertNoTriple(ExtendedIterator it){
        assertFalse(it.hasNext());
    }
    
    private void assertTrustsSink(Node sink, ExtendedIterator it){
        assertTrue(it.hasNext());
        if(it.hasNext()){
            Triple t = (Triple) it.next();
            assertTrue(t.getSubject().equals(sink));
        }
    }
    
    private void assertFoundTrustedTriple(Node source, Node sink){
        ExtendedIterator result = calcMetric(source); 
        assertOneTriple(result);
        result = calcMetric(source);
        assertTrustsSink(sink, result);        
    }    
    
    private void assertFoundNoTrustedTriple(Node source, Node sink){
        ExtendedIterator result = calcMetric(source); 
        assertNoTriple(result);
    }    
    
    /**
     * TestCase 1: The trust graphs contain only one direct trust edge between the source
     * and the sink with the value 0.7 (trust:trust7). The Test verifies that this
     * edge is found and its value is evaluated to be greater than the threshold.
     */
    public void testcase1_SourceHasDirectRating1(){
        Node source = Node.createURI("mailto:tc1.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc1.testuser2@foo.com");
        assertFoundTrustedTriple(source, sink);        
    }
    
    /**
     * TestCase 2: The trust graphs contain only ont direct trust edge between the source
     * and the sink with the value 0.4 (trust:trust4). The test verifies that this
     * edge is found and its value is evaluated to be less than the threshold.
     */
    public void testcase2_SourceHasDirectRating2(){
        Node source = Node.createURI("mailto:tc2.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc2.testuser2@foo.com");
        assertFoundNoTrustedTriple(source, sink);        
    }    

    /**
     * TestCase 3: This trust graphs contain no trust edges. The metric should 
     * return, that the sink is not trustworthy.
     */
    public void testcase3_NoPathToSink1(){
        Node source = Node.createURI("mailto:tc3.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc3.testuser2@foo.com");
        assertFoundNoTrustedTriple(source, sink);        
    }    

    /**
     * TestCase 4: The trust graphs contain two trust edges, which build a path
     * of the legth 2 from the source to the sink. The test case should verify 
     * that the metric can find the path and that the evaluation of the trust
     * value returns the correct value. In this case the trust value should be
     * greater than the threshold.
     */
    public void testcase4_OnePathOfLengthTwo1(){
        Node source = Node.createURI("mailto:tc4.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc4.testuser3@foo.com");
        assertFoundTrustedTriple(source, sink);        
    }    

    /**
     * TestCase 5: The trust graphs contain two trust edges, which build a path
     * of the legth 2 from the source to the sink. The test case should verify 
     * that the metric can find the path and that the evaluation of the trust
     * value returns the correct value. In this case the trust value should be
     * less than the threshold.
     */
    public void testcase5_OnePathOfLengthTwo2(){
        Node source = Node.createURI("mailto:tc5.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc5.testuser3@foo.com");
        assertFoundNoTrustedTriple(source, sink);        
    }    

    /**
     * TestCase5: The trust graphs contain two trust edges, which build a path
     * of the legth 2 from the source to the sink. The test case should verify 
     * that the metric can find the path and that the evaluation of the trust
     * value returns the correct value. In this case the trust value should be
     * exactly the threshold. The metric should return that the sink is trustwothy.
     */
    public void testcase6_OnePathOfLengthTwo3(){
        Node source = Node.createURI("mailto:tc6.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc6.testuser3@foo.com");
        assertFoundTrustedTriple(source, sink);        
    }    

    /**
     * TestCase 7: The trust graphs contain exact to pathes of the length 2 from
     * the source to the sink. This 
     * test case verifies the calculation of the weighted average and checks the path
     * selection by the maxflow criteria. Both pathes should be used for the 
     * evaluation. The evaluated trust value should be greater than the threshold.
     */
    public void testcase7_TwoPathesOfLengthTwo1(){
        Node source = Node.createURI("mailto:tc7.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc7.testuser3@foo.com");
        assertFoundTrustedTriple(source, sink);        
    }    

    /**
     * TestCase 8: The trust graphs contain exact to pathes of the length 2 from
     * the source to the sink. This 
     * test case verifies the calculation of the weighted average and checks the path
     * selection by the maxflow criteria. Both pathes should be used for the 
     * evaluation. The evaluated trust value should be less than the threshold.
     */
    public void testcase8_TwoPathesOfLengthTwo2(){
        Node source = Node.createURI("mailto:tc8.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc8.testuser3@foo.com");
        assertFoundNoTrustedTriple(source, sink);        
    }    

    /**
     * TestCase 9: The trust graphs contain exact to pathes of the length 2 from
     * the source to the sink. This 
     * test case verifies the calculation of the weighted average and checks the path
     * selection by the maxflow criteria. Due to the maxflow criteria only one 
     * path should be used for the evaluation. The infered trust value should be
     * greater than the threshold.
     */
    public void testcase9_TwoPathesOfLengthTwo3(){
        Node source = Node.createURI("mailto:tc9.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc9.testuser3@foo.com");
        assertFoundTrustedTriple(source, sink);        
    }    

    /** 
     * TestCase 10: The trust graphs contain two pathes from the source to the
     * sink. The first has the length 1 and the second has the length 2. This
     * Testcase should verify, that the metric always select the shortest path.
     * The evaluated trust value for the shortest path should be greater than 
     * the threshold.
     */
    public void testcase10_SelectShortestPath(){
        Node source = Node.createURI("mailto:tc10.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc10.testuser2@foo.com");
        assertFoundTrustedTriple(source, sink);        
    }    
    
    /**
     * TestCase 11: The trust graphs contain an outgoing edge for the source,
     * but contain no path to the sink. The test case should verify that the 
     * metric evaluate the trust value of the sink to be 0. 
     */
    public void testcase11_NoPathToSink2(){
        Node source = Node.createURI("mailto:tc11.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc11.testuser3@foo.com");
        assertFoundNoTrustedTriple(source, sink);        
    }
 
    /**
     * TestCase 12: This test case uses a more complex trust graph, which is 
     * (a little bit) closer to the real world. The result should be, that 
     * the source trusts the sink. 
     */
    public void testcase12_complexGraph(){
        Node source = Node.createURI("mailto:tc12.testuser1@foo.com");
        Node sink = Node.createURI("mailto:tc12.testuser6@foo.com");
        assertFoundTrustedTriple(source, sink);        
   }
 
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TidalTrustMetricTest.suite());
    }
    
    
}
