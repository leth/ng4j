package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;

/**
 * @version $Id: ExplanationTemplate.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ExplanationTemplate {
    private Node[] templateNodes;
    private Set usedVariableNames = new HashSet();
    private Set usedVariables = new HashSet();
    private Collection childTemplates = new ArrayList();
    
    public ExplanationTemplate(String pattern, String lang) {
        compilePattern(pattern, lang);
    }
    
    public ExplanationTemplate(String pattern) {
        this(pattern, null);
    }
    
    public ExplanationTemplate() {
        this("", null);
    }
    
    public void addChild(ExplanationTemplate childTemplate) {
        this.childTemplates.add(childTemplate);
        this.usedVariables.addAll(childTemplate.usedVariables());
    }

    public Set usedVariables() {
        return this.usedVariables;
    }
    
    private void compilePattern(String pattern, String lang) {
        Matcher matcher = Pattern.compile("@@\\?([^@]+)@@").matcher(pattern);
        int currentStart = 0;
        List nodes = new ArrayList();
        while (matcher.find()) {
	        if (matcher.start() > currentStart) {
	            nodes.add(createTextNode(pattern.substring(
	                    currentStart, matcher.start()), lang));
	        }
	        Node variable = Node.createVariable(matcher.group(1));
	        nodes.add(variable);
	        this.usedVariables.add(variable);
	        this.usedVariableNames.add(matcher.group(1));
	        currentStart = matcher.end();
        }
        if (currentStart < pattern.length()) {
            nodes.add(createTextNode(pattern.substring(currentStart), lang));
        }
        this.templateNodes = (Node[]) nodes.toArray(new Node[nodes.size()]);
    }
    
    public ExplanationPart instantiate(VariableBinding binding) {
        List instanceNode = new ArrayList();
        for (int i = 0; i < this.templateNodes.length; i++) {
            if (!this.templateNodes[i].isVariable()) {
                instanceNode.add(this.templateNodes[i]);
                continue;
            }
            if (!binding.containsName(this.templateNodes[i].getName())) {
                throw new IllegalArgumentException(
                        "Unbound variable: " + this.templateNodes[i]);
            }
            instanceNode.add(binding.value(this.templateNodes[i].getName()));
        }
        return new ExplanationPart(instanceNode);
    }
    
    public Collection instantiateTree(ResultTable results) {
        List resultParts = new ArrayList();
        Iterator it = results.selectDistinct(this.usedVariableNames).bindingIterator();
        while (it.hasNext()) {
            VariableBinding binding = (VariableBinding) it.next();
            ExplanationPart explanationPart = instantiate(binding);
            ResultTable matchingResults = results.selectMatching(binding);
            Iterator childTemplateIt = this.childTemplates.iterator();
            while (childTemplateIt.hasNext()) {
                ExplanationTemplate childTemplate = (ExplanationTemplate) childTemplateIt.next();
                childTemplate.addInstantiations(explanationPart, matchingResults);
            }
            resultParts.add(explanationPart);
        }
        return resultParts;
    }
    
    private Node createTextNode(String text, String lang) {
        return Node.createLiteral(text, lang, (RDFDatatype) null);
    }

    private void addInstantiations(ExplanationPart parent, ResultTable results) {
        Iterator it2 = instantiateTree(results).iterator();
        while (it2.hasNext()) {
            ExplanationPart part = (ExplanationPart) it2.next();
            parent.addPart(part);
        }
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer("Explanation:");
        if (this.templateNodes.length > 0) {
            result.append(" '");
            for (int i = 0; i < this.templateNodes.length; i++) {
                if (this.templateNodes[i].isVariable()) {
                    result.append("@@?" + this.templateNodes[i].getName() + "@@");
                } else {
                    result.append(this.templateNodes[i].getLiteral().getLexicalForm());
                }
            }
	        result.append("'");
        }
        if (!this.childTemplates.isEmpty()) {
            result.append(" {");
            Iterator it = this.childTemplates.iterator();
            while (it.hasNext()) {
                ExplanationTemplate template = (ExplanationTemplate) it.next();
                result.append(template);
                if (it.hasNext()) {
                    result.append(", ");
                }
            }
            result.append("}");
        }
        return result.toString();
    }
}