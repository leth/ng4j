package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;

/**
 * @version $Id: ExplanationTemplateBuilder.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
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
        ExplanationTemplate rootExplanation =
            (this.rootTemplateNode == null)
                    ? new ExplanationTemplate()
                    : createExplanationTemplate(this.rootTemplateNode);
        GraphPatternTreeNode root =
            new GraphPatternTreeBuilder(this.patterns).getRootNode();
        addChildTemplates(rootExplanation, getChildTemplates(root));
        return rootExplanation;
    }
    
    private ExplanationTemplate createExplanationTemplate(Node pattern) {
        return new ExplanationTemplate(
                pattern.getLiteral().getLexicalForm(),
                pattern.getLiteral().language());
    }
    
    private void addChildTemplates(ExplanationTemplate template, List children) {
        Iterator it = children.iterator();
        while (it.hasNext()) {
            ExplanationTemplate child = (ExplanationTemplate) it.next();
            template.addChild(child);
        }
    }
    
    private List getChildTemplates(GraphPatternTreeNode node) {
        Node patternForNode = (Node) this.patternsToTemplateNodes.get(
                node.getGraphPattern());
        if (patternForNode == null) {
            List result = new ArrayList();
            Iterator it = node.getChildren().iterator();
            while (it.hasNext()) {
                GraphPatternTreeNode child = (GraphPatternTreeNode) it.next();
                result.addAll(getChildTemplates(child));
            }
            return result;
        }
        ExplanationTemplate template = createExplanationTemplate(patternForNode);
        return Collections.singletonList(template);
    }
}
