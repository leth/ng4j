package de.fuberlin.wiwiss.trust;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;

/**
 * @version $Id: Explainer.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Explainer {
    private ResultTable results;
    private Triple triple;
    private TrustPolicy policy;
    
    public Explainer(ResultTable results, Triple tripleToExplain, TrustPolicy policyInUse) {
        this.policy = policyInUse;
        this.triple = tripleToExplain;
        this.results = results.selectMatching(tripleToExplain);
        if (this.results.countBindings() == 0) {
            throw new IllegalArgumentException("Triple " + tripleToExplain
                    + " is not in result tabe");
        }
    }
    
    public Explanation explain() {
        Explanation result = new Explanation(this.triple, this.policy);
        if (this.policy.getExplanationTemplate() == null) {
            return result;
        }
        Iterator it = this.policy.getExplanationTemplate().instantiateTree(this.results).iterator();
        while (it.hasNext()) {
            ExplanationPart part = (ExplanationPart) it.next();
            result.addPart(part);
        }
        return result;
    }
}
