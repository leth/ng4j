package de.fuberlin.wiwiss.trust.example;

import java.util.Collections;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.trust.EXPL;
import de.fuberlin.wiwiss.trust.Explanation;
import de.fuberlin.wiwiss.trust.PolicySuite;
import de.fuberlin.wiwiss.trust.PolicySuiteFromRDFBuilder;
import de.fuberlin.wiwiss.trust.QueryResult;
import de.fuberlin.wiwiss.trust.TrustEngine;
import de.fuberlin.wiwiss.trust.TrustPolicy;

/**
 * Runs a few tests.
 *
 * @version $Id: TrustLayerTest.java,v 1.2 2005/03/15 08:59:08 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustLayerTest {

    public static void main(String[] args) {

        // get trust policy
        Model tplFile = ModelFactory.createDefaultModel();
        tplFile.read("file:doc/trustlayer/finPolicies.n3", "N3");
        PolicySuite suite = new PolicySuiteFromRDFBuilder(
                tplFile.getGraph(), Collections.EMPTY_LIST).buildPolicySuite();
        TrustPolicy policy = suite.getTrustPolicy("http://www.fu-berlin/suhl/bizer/financialscenario/policies/Policy2");
//        TrustPolicy policy = TrustPolicy.TRUST_EVERYTHING;
//        TrustPolicy policy = FixtureWithLotsOfNodes.getPolicyTrustOnlySelfAssertedInformation();
        
        // get data source
        NamedGraphSet source = new NamedGraphSetImpl();
        source.read("file:doc/trustlayer/finData.trig", "TRIG");
        
        // run query
        TrustEngine trusted = new TrustEngine(source);
        QueryResult result = trusted.find(
                new Triple(Node.ANY, RDF.Nodes.type, Node.ANY),
                policy);

        // print triples
        Iterator it = result.tripleIterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        
        // print explanations of all triples
        it = result.tripleIterator();
        while (it.hasNext()) {
            System.out.println("# ===============================================");
            Triple triple = (Triple) it.next();
            Explanation explanation = result.explain(triple);
            Model m = ModelFactory.createDefaultModel();
            m.getGraph().getBulkUpdateHandler().add(explanation.toRDF());
            m.setNsPrefixes(PrefixMapping.Standard);
            m.setNsPrefix("expl", EXPL.getURI());
            m.write(System.out, "N3");
        }
    }
}
