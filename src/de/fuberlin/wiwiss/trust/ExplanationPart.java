package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @version $Id: ExplanationPart.java,v 1.2 2005/03/22 01:01:48 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationPart {
    private Collection parts = new ArrayList();
    private List explanation;

    public ExplanationPart() {
        this.explanation = Collections.EMPTY_LIST;
    }
    
    public ExplanationPart(List explanation) {
        this.explanation = explanation;
    }
    
    public void addPart(ExplanationPart part) {
        this.parts.add(part);
    }
    
    public Collection parts() {
        return this.parts;
    }
    
    public List explanationNodes() {
        return this.explanation;
    }
    
    public void writeAsRDF(Node parent, Graph target) {
        Node thisPart = Node.createAnon();
        target.add(new Triple(parent, EXPL.parts, thisPart));
        target.add(new Triple(thisPart, RDF.Nodes.type, EXPL.ExplanationPart));
        
        int counter = 1;
        Iterator it = this.explanation.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            Node seqPredicate = Node.createURI(RDF.getURI() + "_"
                    + Integer.toString(counter));
            target.add(new Triple(thisPart, seqPredicate, node));
            counter++;
        }
        
        it = this.parts.iterator();
        while (it.hasNext()) {
            ExplanationPart part = (ExplanationPart) it.next();
            part.writeAsRDF(thisPart, target);
        }
    }
    
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ExplanationPart)) {
            return false;
        }
        ExplanationPart otherExplanationPart = (ExplanationPart) other;
        if (!otherExplanationPart.explanation.equals(this.explanation)) {
            return false;
        }
        if (otherExplanationPart.parts.size() != this.parts.size()) {
            return false;
        }
        if (!otherExplanationPart.parts.containsAll(this.parts)) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        // don't use this.parts.hashCode() because hashCode must stay the
        // same irrespective of order of children
        return this.explanation.hashCode() + this.parts.size();
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer("Part" + this.explanation);
        if (this.parts.isEmpty()) {
            return result.toString();
        }
        result.append(" <");
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            ExplanationPart child = (ExplanationPart) it.next();
            result.append(child);
            if (it.hasNext()) {
                result.append(", ");
            }
        }
        result.append(">");
        return result.toString();
    }
}
