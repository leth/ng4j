package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * TODO: Write documentation and tests!
 * 
 * @version $Id: TrustLayerGraph.java,v 1.2 2005/03/22 01:01:47 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustLayerGraph extends GraphBase {
    private TrustEngine engine;
    private Graph policySuiteGraph;
    private Collection metricInstances = new ArrayList();
    private PolicySuite suite;
    private TrustPolicy currentPolicy = TrustPolicy.TRUST_EVERYTHING;
    private boolean policiesParsed = false;
    private Map explanationCache = new HashMap();
    private VariableBinding systemVariables = new VariableBinding();
    
    public TrustLayerGraph(NamedGraphSet untrustedDatasource, Graph policySuite) {
        this.engine = new TrustEngine(untrustedDatasource, this.systemVariables);
        this.policySuiteGraph = policySuite;
    }
    
    public List getAllTrustPolicyURIs() {
        ensurePoliciesParsed();
        return this.suite.getAllPolicyURIs();
    }
    
    public String getTrustPolicyName(String uri) {
        ensurePoliciesParsed();
        return this.suite.getPolicyName(uri);
    }
    
    public String getTrustPolicyDescription(String uri) {
        ensurePoliciesParsed();
        return this.suite.getPolicyName(uri);
    }

    public void selectTrustPolicy(String uri) {
        ensurePoliciesParsed();
        clearExplanationCache();
        TrustPolicy newPolicy = this.suite.getTrustPolicy(uri);
        if (newPolicy == null) {
            throw new IllegalArgumentException("Unknown trust policy: <" + uri + ">");
        }
        this.currentPolicy = newPolicy;
    }
    
    public String getSelectedTrustPolicyURI() {
        ensurePoliciesParsed();
        return this.currentPolicy.getURI();
    }
    
    public Explanation explain(Triple t) {
        QueryResult queryResult = (QueryResult) this.explanationCache.get(t);
        if (queryResult == null) {
            throw new IllegalArgumentException("No explanation cached for triple " + t);
        }
        return queryResult.explain(t);
    }
    
    public Graph explainAsGraph(Triple t) {
        // TODO GraphExplanations
        return null;
    }
    
    public void clearExplanationCache() {
        this.explanationCache.clear();
    }
    
    public void setSystemVariable(String varName, Node varValue) {
        this.systemVariables.setValue(varName, varValue);
    }
    
    public void registerMetricImplementation(Class metricImplementationClass) {
        if (this.policiesParsed) {
            throw new IllegalStateException(
                    "Must register metrics before using the TrustLayerGraph");
        }
        if (!isImplementorOfMetric(metricImplementationClass)) {
            throw new IllegalArgumentException(metricImplementationClass.getName()
                    + " must implement " + Metric.class.getName());
        }
        try {
            this.metricInstances.add(metricImplementationClass.newInstance());
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected ExtendedIterator graphBaseFind(TripleMatch t) {
        ensurePoliciesParsed();
        QueryResult result = this.engine.find(t.asTriple(), this.currentPolicy);
        return new ExplanationCachingIterator(result, this.explanationCache);
    }
    
    private void ensurePoliciesParsed() {
        if (this.policiesParsed) {
            return;
        }
        this.suite = new PolicySuiteFromRDFBuilder(
                this.policySuiteGraph,
                this.metricInstances).buildPolicySuite();
        this.policiesParsed = true;
    }
    
    private boolean isImplementorOfMetric(Class aClass) {
        for (int i = 0; i < aClass.getInterfaces().length; i++) {
            if (aClass.getInterfaces()[i] == Metric.class) {
                return true;
            }
        }
        return false;
    }
    
    private class ExplanationCachingIterator extends NiceIterator {
        private Iterator it;
        private QueryResult queryResult;
        private Map cache;
        public ExplanationCachingIterator(QueryResult result, Map explanationCache) {
            this.queryResult = result;
            this.it = result.tripleIterator();
            this.cache = explanationCache;
        }
        public boolean hasNext() {
            return this.it.hasNext();
        }
        public Object next() {
            Triple next = (Triple) this.it.next();
            this.cache.put(next, this.queryResult);
            return next;
        }
    }
}
