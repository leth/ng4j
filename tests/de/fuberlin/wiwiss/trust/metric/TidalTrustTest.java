package de.fuberlin.wiwiss.trust.metric;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.trust.EXPL;
import de.fuberlin.wiwiss.trust.Metric;
import de.fuberlin.wiwiss.trust.MetricResult;
import de.fuberlin.wiwiss.trust.ExplanationPart;


/**
 * .
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Oliver Maresch (oliver-maresch@gmx.de)
 */
public class TidalTrustTest {

    public static void main(String[] args) throws de.fuberlin.wiwiss.trust.MetricException {

        // get data source
        NamedGraphSet data = new NamedGraphSetImpl();
        data.read("file:/home/voodoo/Java/project/trustlayer/ng4j/tests/de/fuberlin/wiwiss/trust/metric/testDataTidalTrust.trig", "TRIG");
        
        // run metric
        java.util.List arguments = new java.util.LinkedList();
        Node source = Node.createURI("mailto:tc12.testuser1@foo.com");
        arguments.add(0,source);
        Node sink = Node.createURI("mailto:tc12.testuser6@foo.com");
        arguments.add(1,sink);
        Node threshold = Node.createLiteral("0.5", null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDfloat);
        arguments.add(2,threshold);
        
        Metric metric = new TidalTrustMetric();
        metric.setup(data);
        MetricResult result = metric.calculateMetric(arguments);

        
        // print explanations of all triples
        ExplanationPart part = result.getTextExplanation();
        System.out.println("Question: Should the source <" + source.toString() + "> trust the sink <" + sink.toString() + ">?\nAnswer: " + (result.getResult()?"Yes.\n":"No.\n"));
        System.out.println("Explanation:");
        
        Model m = ModelFactory.createDefaultModel();
        Graph g = m.getGraph();
        part.writeAsRDF(Node.createAnon(), g);
        m.setNsPrefixes(PrefixMapping.Standard);
        m.setNsPrefix("expl", EXPL.getURI());
        m.write(System.out, "N3");
    }
}
