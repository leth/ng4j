package de.fuberlin.wiwiss.trust;

import de.fuberlin.wiwiss.trust.PolicySuite;
import de.fuberlin.wiwiss.trust.TrustPolicy;
import junit.framework.TestCase;

/**
 * Tests for {@link PolicySuite}
 *
 * @version $Id: PolicySuiteTest.java,v 1.2 2005/06/22 21:21:23 maresch Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class PolicySuiteTest extends TestCase {

    public void testCreation() {
        String nonexistingURI = "http://example.com/nonexsisting_policy";
        PolicySuite suite = new PolicySuite("Test suite");

        assertEquals("Test suite", suite.getSuiteName());
        assertNull(suite.getTrustPolicy(nonexistingURI));
        assertTrue(suite.getAllPolicyURIs().size() == 1);
        assertEquals(TrustPolicy.TRUST_EVERYTHING.getURI(),
            suite.getAllPolicyURIs().iterator().next());
        assertNull(suite.getPolicyName(nonexistingURI));
        assertNull(suite.getPolicyDescription(nonexistingURI));
    }
    
    public void testSuiteWithoutName() {
        PolicySuite suite = new PolicySuite(null);
        assertNull(suite.getSuiteName());
    }

    public void testOnePolicy() {
        String policy1URI = "http://example.org/policies#policy1";
        PolicySuite suite = new PolicySuite("Test suite");
        TrustPolicy policy1 = new TrustPolicy(policy1URI);
        suite.addPolicy(policy1URI, "Policy 1", "Description", policy1);

        assertEquals(policy1, suite.getTrustPolicy(policy1URI));
        assertEquals(2, suite.getAllPolicyURIs().size()); // + TrustEverything
        assertTrue(suite.getAllPolicyURIs().contains(policy1URI));
        assertEquals("Policy 1", suite.getPolicyName(policy1URI));
        assertEquals("Description", suite.getPolicyDescription(policy1URI));
    }

    public void testTwoPolicies() {
        String policy1URI = "http://example.org/policies#policy1";
        String policy2URI = "http://example.org/policies#policy2";
        PolicySuite suite = new PolicySuite("Test suite");
        TrustPolicy policy1 = new TrustPolicy(policy1URI);
        TrustPolicy policy2 = new TrustPolicy(policy2URI);
        suite.addPolicy(policy1URI, "Policy 1", "Description", policy1);
        suite.addPolicy(policy2URI, "Policy 2", "Description", policy2);

        assertEquals(policy1, suite.getTrustPolicy(policy1URI));
        assertEquals(policy2, suite.getTrustPolicy(policy2URI));
        assertEquals(3, suite.getAllPolicyURIs().size()); // + TrustEveryThing
        assertTrue(suite.getAllPolicyURIs().contains(policy1URI));
        assertTrue(suite.getAllPolicyURIs().contains(policy2URI));
        assertEquals("Policy 1", suite.getPolicyName(policy1URI));
        assertEquals("Policy 2", suite.getPolicyName(policy2URI));
    }
    
    public void testPolicyWithoutNameAndDescription() {
        String policy1URI = "http://example.org/policies#policy1";
        PolicySuite suite = new PolicySuite("Test suite");
        TrustPolicy policy1 = new TrustPolicy(policy1URI);
        suite.addPolicy(policy1URI, null, null, policy1);        

        assertEquals(policy1, suite.getTrustPolicy(policy1URI));
        assertNull(suite.getPolicyName(policy1URI));
        assertNull(suite.getPolicyDescription(policy1URI));
    }
}
