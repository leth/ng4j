package de.fuberlin.wiwiss.trust;

import com.hp.hpl.jena.graph.Node;

/**
 * Jena Nodes for the
 * <a href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriQLP/#blocks">Trust Policy
 * Language</a> vocabulary
 *
 * @version $Id: TPL.java,v 1.2 2005/03/22 22:09:11 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TPL {
    public static final String ns = "http://www.wiwiss.fu-berlin.de/suhl/bizer/TPL/";
    
    // Classes
    public static final Node TrustPolicySuite = Node.createURI(ns + "TrustPolicySuite");
    public static final Node TrustPolicy = Node.createURI(ns + "TrustPolicy");
    public static final Node GraphPattern = Node.createURI(ns + "GraphPattern");

    // Properties
    public static final Node suiteName = Node.createURI(ns + "suiteName");
    public static final Node includesPolicy = Node.createURI(ns + "includesPolicy");
    public static final Node policyName = Node.createURI(ns + "policyName");
    public static final Node policyDescription = Node.createURI(ns + "policyDescription");
    public static final Node graphPattern = Node.createURI(ns + "graphPattern");
    public static final Node pattern = Node.createURI(ns + "pattern");
    public static final Node constraint = Node.createURI(ns + "constraint");
    public static final Node textExplanation = Node.createURI(ns + "textExplanation");
    public static final Node graphExplanation = Node.createURI(ns + "graphExplanation");
    
    public static String getURI() {
        return ns;
    }
}
