package de.fuberlin.wiwiss.trust.example;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.trust.IsFooMetric;
import de.fuberlin.wiwiss.trust.TrustLayerGraph;
import de.fuberlin.wiwiss.trust.metric.TidalTrustMetric;

/**
 * @version $Id: Filter.java,v 1.1 2005/03/15 08:59:08 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Filter {

    public static void main(String[] args) {
        String trigFile = "file:doc/trustlayer/finData.trig";
        String policiesFile = "file:doc/trustlayer/finPolicies.n3";
        String policyURI = "http://www.fu-berlin/suhl/bizer/financialscenario/policies/Policy2";
        
        // Read the source data from a TriG file into an NG4J NamedGraphSet
        NamedGraphSet source = new NamedGraphSetImpl();
        source.read(trigFile, "TRIG");

        // Read the policy suite from an N3 file into a Jena Graph
        Model tplModel = ModelFactory.createDefaultModel();
        tplModel.read(policiesFile, "N3");
        Graph tplGraph = tplModel.getGraph();
        
        // Set up the TrustLayerGraph
        TrustLayerGraph tlg = new TrustLayerGraph(source, tplGraph);

        // Make some Metric implementations available to be used in policies
        tlg.registerMetricImplementation(IsFooMetric.class);
        tlg.registerMetricImplementation(TidalTrustMetric.class);

        // Print available policies
        Iterator it = tlg.getAllTrustPolicyURIs().iterator();
        while (it.hasNext()) {
            String uri = (String) it.next();
            System.out.println("Policy URI: " + uri);
            System.out.println("Policy name: " + tlg.getTrustPolicyName(uri));
            System.out.println("Policy description: " + tlg.getTrustPolicyDescription(uri));
            System.out.println();
        }
        
        // Wrap graph into Jena Model 
        Model tlm = new ModelCom(tlg);
        
        // Select the policy for all subsequent operations on the graph
        tlg.selectTrustPolicy(policyURI);
        
        // Write all statements matching the policy to System.out
        tlm.write(System.out, "N3");
    }
}
