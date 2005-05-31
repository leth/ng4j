package de.fuberlin.wiwiss.trust.example;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.trust.Explanation;
import de.fuberlin.wiwiss.trust.ExplanationToHTMLRenderer;
import de.fuberlin.wiwiss.trust.TrustLayerGraph;

/**
 * @version $Id: FindToHTML.java,v 1.4 2005/05/31 09:53:56 maresch Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class FindToHTML {

    public static void main(String[] args) {
        String trigFile = "file:doc/trustlayer/finData.trig";
        String policiesFile = "file:doc/trustlayer/finPolicies.n3";
        String policyURI = "http://www.wiwiss.fu-berlin.de/suhl/bizer/TPL/TrustEverything";

        Triple findMe = new Triple(
                Node.ANY,
                Node.createURI("http://xmlns.com/foaf/0.1/name"),
                Node.ANY);
        
        // Read the source data from a TriG file into an NG4J NamedGraphSet
        NamedGraphSet source = new NamedGraphSetImpl();
        source.read(trigFile, "TRIG");

        // Read the policy suite from an N3 file into a Jena Graph
        Model tplModel = ModelFactory.createDefaultModel();
        tplModel.read(policiesFile, "N3");
        Graph tplGraph = tplModel.getGraph();
        
        // Set up the TrustLayerGraph
        TrustLayerGraph tlg = new TrustLayerGraph(source, tplGraph);
        tlg.selectTrustPolicy(policyURI);
//        tlg.setSystemVariable("USER", Node.createURI("http://example.org/user"));
        
        System.out.println("<h1>FIND results and explanations</h1>");
        System.out.println("<p>Finding " + findMe + " ...</p>");
        
        // find triples and dump explanations as HTML
        Iterator it = tlg.find(findMe);
        int i = 1;
        while (it.hasNext()) {
            Triple found = (Triple) it.next();
            System.out.println("<h2>Result #" + i + "</h2>");
            i++;
            Explanation expl = tlg.explain(found);
            ExplanationToHTMLRenderer renderer = new ExplanationToHTMLRenderer(expl, source);
            renderer.setPrefixes(tplModel);
            System.out.println(renderer.getExplanationAsHTML());
        }
    }
}
