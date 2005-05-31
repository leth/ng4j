package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A suite of {@link TrustPolicy}s.
 *
 * @version $Id: PolicySuite.java,v 1.2 2005/05/31 09:53:56 maresch Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class PolicySuite {
    private String suiteName;
    private Map policies = new HashMap();
    private Map policyNames = new HashMap();
    private Map policyDescriptions = new HashMap();

    // List of all policy URIs. This is already stored in the key sets of
    // all the maps, but we want ther order, so we store it again as a list.
    private List policyURIs = new ArrayList();

    /**
     * Constructs a new suite with a given suite name.
     * @param suiteName
     */
    public PolicySuite(String suiteName) {
        this.suiteName = suiteName;
        this.addPolicy(TrustPolicy.TRUST_EVERYTHING.getURI(),"trust everything","",TrustPolicy.TRUST_EVERYTHING);
    }

    /**
     * @return The policy suite's name
     */
    public String getSuiteName() {
        return this.suiteName;
    }
    
    /**
     * Gets a {@link TrustPolicy} for a given URI. If no trust
     * policy with that URI is known, null will be returned.
     * @param uri The trust policy's URI
     * @return The trust policy
     */
    public TrustPolicy getTrustPolicy(String uri) {
        return (TrustPolicy) this.policies.get(uri);
    }

    /**
     * Gets the name of a trust policy that is part of the suite.
     * @param uri The policy's URI
     * @return The policy's name
     */
    public String getPolicyName(String uri) {
        return (String) this.policyNames.get(uri);
    }

    /**
     * Gets the description of a trust policy that is part of the suite.
     * @param uri The policy's URI
     * @return The policy's description
     */
    public String getPolicyDescription(String uri) {
        return (String) this.policyDescriptions.get(uri);
    }

    /**
     * Gets the URIs of all trust policies in the suite. The list
     * is in the order the policies were added to the suite.
     * @return A list of Strings
     */
    public List getAllPolicyURIs() {
        return this.policyURIs;
    }
    
    /**
     * Adds a {@link TrustPolicy} to the suite.
     * @param uri The policy's URI
     * @param name The policy's name
     * @param description The policy's description
     * @param policy The policy itself
     */
    public void addPolicy(String uri, String name, String description,
            TrustPolicy policy) {
        this.policyNames.put(uri, name);
        this.policyDescriptions.put(uri, description);
        this.policies.put(uri, policy);
        this.policyURIs.add(uri);
    }
}
