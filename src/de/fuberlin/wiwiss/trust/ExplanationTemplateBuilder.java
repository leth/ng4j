package de.fuberlin.wiwiss.trust;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;

/**
 * @version $Id: ExplanationTemplateBuilder.java,v 1.2 2005/03/15 08:59:08 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationTemplateBuilder {
    private List patterns;
    private Node rootTemplateNode;
    private Map patternsToTemplateNodes;
    
    public ExplanationTemplateBuilder(List graphPatterns, Node rootTextExplanation,
            Map patternTextExplanations) {
        this.patterns = graphPatterns;
        this.rootTemplateNode = rootTextExplanation;
        this.patternsToTemplateNodes = patternTextExplanations;
    }

    public ExplanationTemplate explanationTemplate() {
        if (this.rootTemplateNode == null && this.patternsToTemplateNodes.isEmpty()) {
            return null;	// no explanations at all defined
        }
        return createExplanationTemplate(
                this.rootTemplateNode,
                new GraphPatternTreeBuilder(this.patterns).getRootNode());
    }

    private ExplanationTemplate createExplanationTemplate(Node rdfExplanationTemplate, 
            GraphPatternTreeNode currentPattern) {
        ExplanationTemplate result;
        if (rdfExplanationTemplate == null) {
            result = new ExplanationTemplate();
        } else {
            result = new ExplanationTemplate(
		            rdfExplanationTemplate.getLiteral().getLexicalForm(),
		            rdfExplanationTemplate.getLiteral().language());
        }
        addChildTemplates(result, currentPattern);
        return result;
    }

    private void addChildTemplates(ExplanationTemplate parent,
            GraphPatternTreeNode currentPattern) {
        Iterator it = currentPattern.getChildren().iterator();
        while (it.hasNext()) {
            GraphPatternTreeNode child = (GraphPatternTreeNode) it.next();
            Node rdfTemplate = getRDFExplanationTemplateNode(child);
            if (rdfTemplate == null) {
                addChildTemplates(parent, child);
            } else {
                parent.addChild(createExplanationTemplate(rdfTemplate, child));
            }
        }
    }
    
    private Node getRDFExplanationTemplateNode(GraphPatternTreeNode forPattern) {
        return (Node) this.patternsToTemplateNodes.get(
                forPattern.getGraphPattern());        
    }
}
