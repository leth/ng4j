/*
 * AllTests.java
 * JUnit based test
 *
 * Created on 4. März 2005, 11:51
 */

package de.fuberlin.wiwiss.trust.metric;

import junit.framework.*;

/**
 *
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class AllTests extends TestCase {
    
    public static final String TESTSPATH= "file:/home/voodoo/Java/project/trustlayer/ng4j/tests/";
    
    public AllTests(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for all Metric Tests");
        suite.addTest(TidalTrustMetricTest.suite());
        suite.addTest(TidalTrustMetricTestWithPolicy.suite());
        suite.addTest(AppleseedMetricTest.suite());
        suite.addTest(EbayMetricTest.suite());
        suite.addTest(SemanticPageRankTest.suite());
        suite.addTest(AssertedGraphsTest.suite());
        return suite;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }  
}
