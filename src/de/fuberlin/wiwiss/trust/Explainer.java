package de.fuberlin.wiwiss.trust;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;

/**
 * @version $Id: Explainer.java,v 1.2 2005/05/24 13:50:27 maresch Exp $
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
            System.out.println(part);
            result.addPart(part);
        }
        // append constraint explanation parts
        Iterator bindings = results.bindingIterator();
        while(bindings.hasNext()){
            VariableBinding binding = (VariableBinding) bindings.next();
            
            // add text explanations
            Iterator textExpls = binding.getTextExplanations().iterator();
            while(textExpls.hasNext()){
                result.addPart((ExplanationPart) textExpls.next());
            }
            
            //TODO: add graphs with RDF explanations to explanations
        }
        
        return result;
    }
}
