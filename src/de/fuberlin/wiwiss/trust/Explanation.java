package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @version $Id: Explanation.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Explanation {
    private Collection parts = new ArrayList();
    private Triple triple;
    private TrustPolicy policy;
    
    public Explanation(Triple triple, TrustPolicy policy) {
        this.triple = triple;
        this.policy = policy;
    }
    
    public void addPart(ExplanationPart part) {
        this.parts.add(part);
    }
    
    public Collection parts() {
        return this.parts;
    }
    
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Explanation)) {
            return false;
        }
        Explanation otherExplanation = (Explanation) other;
        if (otherExplanation.parts.size() != this.parts.size()) {
            return false;
        }
        if (!otherExplanation.parts.containsAll(this.parts)) {
            return false;
        }
        if (!otherExplanation.triple.equals(this.triple)) {
            return false;
        }
        if (!otherExplanation.policy.getURI().equals(this.policy.getURI())) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        // don't use this.parts.hashCode() because hashCode must stay the
        // same irrespective of order of children
        return this.parts.size() + this.triple.hashCode()
        			+ this.policy.getURI().hashCode();
    }

    public Graph toRDF() {
        Graph result = ModelFactory.createDefaultModel().getGraph();
        Node explanation = Node.createAnon(new AnonId("explanation"));
        Node statement = Node.createAnon(new AnonId("explainedStatement"));
        result.add(new Triple(explanation, RDF.Nodes.type, EXPL.Explanation));
        result.add(new Triple(explanation, EXPL.policy, Node.createURI(
                this.policy.getURI())));
        result.add(new Triple(explanation, EXPL.statement, statement));
        result.add(new Triple(statement, RDF.Nodes.type, RDF.Nodes.Statement));
        result.add(new Triple(statement, RDF.Nodes.subject, this.triple.getSubject()));
        result.add(new Triple(statement, RDF.Nodes.predicate, this.triple.getPredicate()));
        result.add(new Triple(statement, RDF.Nodes.object, this.triple.getObject()));
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            ExplanationPart part = (ExplanationPart) it.next();
            part.writeAsRDF(explanation, result);
        }
        return result;
    }
}
