package de.fuberlin.wiwiss.trust.example;

import java.util.Iterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

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
import de.fuberlin.wiwiss.trust.metric.AppleseedMetric;
import de.fuberlin.wiwiss.trust.metric.EbayMetric;
import de.fuberlin.wiwiss.trust.metric.SemanticPageRank;
import de.fuberlin.wiwiss.trust.metric.TidalTrustMetric;

/**
 * @version $Id: FindToHTMLMetric.java,v 1.4 2005/06/21 15:01:46 maresch Exp $
 * @author Ricard Cyganiak (richard@cyganiak.de)
 */
public class FindToHTMLMetric {

    public static void main(String[] args) {

        String trigFile = "file:/home/voodoo/Java/project/trustlayer/ng4j/doc/trustlayer/finTrustData.trig";
        String policiesFile = "file:/home/voodoo/Java/project/trustlayer/ng4j/doc/trustlayer/finTrustPolicies.n3";

        String policyURI = "http://www.fu-berlin/suhl/bizer/financialscenario/policies/trust/Policy3";
        File htmlFile = new File("/home/voodoo/tmp/find2html.html");
        
        try{
            // remove html file of the last run
            if(htmlFile.exists()){
                htmlFile.delete();
            }
            
            // create new html file
            htmlFile.createNewFile();
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(htmlFile)));

            // triple pattern
            Triple findMe = new Triple(
                    Node.ANY,
                    Node.createURI("http://www.fu-berlin/suhl/bizer/financialscenario/vocabulary/text"),
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
            // register Metrics
            tlg.registerMetricImplementation(TidalTrustMetric.class);
            tlg.registerMetricImplementation(EbayMetric.class);
            tlg.registerMetricImplementation(AppleseedMetric.class);
            tlg.registerMetricImplementation(SemanticPageRank.class);
            // select policy
            tlg.selectTrustPolicy(policyURI);
            
            tlg.setSystemVariable("USER", Node.createURI("mailto:dadean7@lycos.de"));

            out.println("<h1>FIND results and explanations</h1>");
            out.println("<p>Finding " + findMe + " ...</p>");

            // find triples and dump explanations as HTML
            Iterator it = tlg.find(findMe);
            int i = 1;
            while (it.hasNext()) {
                Triple found = (Triple) it.next();
                out.println("<h2>Result #" + i + "</h2>");
                i++;
                Explanation expl = tlg.explain(found);
                ExplanationToHTMLRenderer renderer = new ExplanationToHTMLRenderer(expl, source);
                renderer.setPrefixes(tplModel);
                out.println(renderer.getExplanationAsHTML());
                out.flush();
            }
            out.close();
            System.out.println("Finished!");
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
}
        