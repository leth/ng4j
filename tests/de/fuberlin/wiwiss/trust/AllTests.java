package de.fuberlin.wiwiss.trust;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test suite for entire project
 *
 * @version $Id: AllTests.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("JUnit test suite for trust");
        //$JUnit-BEGIN$
        suite.addTestSuite(ExplanationTemplateTest.class);
        suite.addTestSuite(ConditionParserTest.class);
        suite.addTestSuite(ExplanationTest.class);
        suite.addTestSuite(PolicySuiteTest.class);
        suite.addTestSuite(TrustPolicyTest.class);
        suite.addTestSuite(GraphPatternTreeBuilderTest.class);
        suite.addTestSuite(VariableBindingTest.class);
        suite.addTestSuite(GraphPatternParserTest.class);
        suite.addTestSuite(ExplanationPartTest.class);
        suite.addTestSuite(TrustEngineTest.class);
        suite.addTestSuite(PolicySuiteFromRDFBuilderTest.class);
        suite.addTestSuite(QueryFactoryTest.class);
        suite.addTestSuite(ResultTableTest.class);
        suite.addTestSuite(ExplanationTemplateBuilderTest.class);
        //$JUnit-END$
        return suite;
    }
}