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
 * <p>A Jena {@link Graph} implementation that filters a
 * {@link NamedGraphSet}, exposing only the triples that match
 * a {@link TrustPolicy}. Top layer of the TriQL.P trust engine.</p>
 * 
 * TODO: Rename to AcceptedGraph
 * 
 * @version $Id: TrustedGraph.java,v 1.2 2005/10/12 12:35:05 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TrustedGraph extends GraphBase {
    private NamedGraphSet sourceData;
    private TrustEngine engine;
    private Graph policySuiteGraph;
    private Collection metricInstances = new ArrayList();
    private Collection rankeBasedMetricInstances = new ArrayList();
    private PolicySuite suite;
    private TrustPolicy currentPolicy = TrustPolicy.TRUST_EVERYTHING;
    private boolean policiesParsed = false;
    private Map explanationCache = new HashMap();
    private VariableBinding systemVariables = new VariableBinding();

    /**
     * Sets up a new trusted graph.
     * @param untrustedDatasource The untrusted repository
     * @param policySuite A suite of trust policies that are available
     * 		to filter the repository
     */
    public TrustedGraph(NamedGraphSet untrustedDatasource, Graph policySuite) {
        this.sourceData = untrustedDatasource;
        this.engine = new TrustEngine(untrustedDatasource, this.systemVariables);
        this.policySuiteGraph = policySuite;
    }

    /**
     * @return A list of all available trust policies as URI strings
     */
    public List getAllTrustPolicyURIs() {
        ensurePoliciesParsed();
        return this.suite.getAllPolicyURIs();
    }
    
    /**
     * @param uri The URI of a trust policy
     * @return The policy's name
     */
    public String getTrustPolicyName(String uri) {
        ensurePoliciesParsed();
        return this.suite.getPolicyName(uri);
    }
    
    /**
     * @param uri The URI of a trust policy
     * @return The policy's description
     */
    public String getTrustPolicyDescription(String uri) {
        ensurePoliciesParsed();
        return this.suite.getPolicyDescription(uri);
    }

    /**
     * Switches the trusted graph to a different trust policy.
     * Subsequent calls to find and friends will use the new
     * policy.
     * @param uri The URI of a trust policy from the suite
     */
    public void selectTrustPolicy(String uri) {
        ensurePoliciesParsed();
        clearExplanationCache();
        TrustPolicy newPolicy = this.suite.getTrustPolicy(uri);
        if (newPolicy == null) {
            throw new IllegalArgumentException("Unknown trust policy: <" + uri + ">");
        }
        this.currentPolicy = newPolicy;
    }
    
    /**
     * @return The URI of the currently selected trust policy
     */
    public String getSelectedTrustPolicyURI() {
        ensurePoliciesParsed();
        return this.currentPolicy.getURI();
    }
    
    /**
     * @param t A triple that was the result of an earlier find call 
     * @return The explanation why the triple fulfils the current policy
     */
    public Explanation explain(Triple t) {
        QueryResult queryResult = (QueryResult) this.explanationCache.get(t);
        if (queryResult == null) {
            throw new IllegalArgumentException("No explanation cached for triple " + t);
        }
        return queryResult.explain(t);
    }
    
    /**
     * @param t A triple that was the result of an earlier find call 
     * @return The explanation why the triple fulfils the current policy
     */
    public Graph explainAsGraph(Triple t) {
        QueryResult queryResult = (QueryResult) this.explanationCache.get(t);
        if (queryResult == null) {
            throw new IllegalArgumentException("No explanation cached for triple " + t);
        }
        // TODO Support GraphExplanations
        return null;
    }

    /**
     * Forgets the explanations of all prior query results. This
     * frees memory.
     */
    public void clearExplanationCache() {
        this.explanationCache.clear();
    }
    
    /**
     * Adds a system variable like ?NOW or ?USER that will be available
     * in policies.
     * @param varName The variable's name
     * @param varValue The variable's value
     */
    public void setSystemVariable(String varName, Node varValue) {
        this.systemVariables.setValue(varName, varValue);
    }

    /**
     * Registers a metric plugin that can then be used in the
     * trust policy suite.
     * @param metricImplementationClass A class implementing
     * 		{@link Metric} or {@link RankBasedMetric}
     */
    public void registerMetricImplementation(Class metricImplementationClass) {
        if (this.policiesParsed) {
            throw new IllegalStateException(
                    "Must register metrics before using the TrustLayerGraph");
        }
        boolean isMetric = isImplementorOfMetric(metricImplementationClass);
        boolean isRankBasedMetric = isImplementorOfRankBasedMetric(metricImplementationClass);
        if (!(isMetric || isRankBasedMetric)) {
            throw new IllegalArgumentException(metricImplementationClass.getName()
                    + " must implement " + Metric.class.getName() 
                    + " or " + RankBasedMetric.class.getName());
        }
        try {
            if(isMetric){
                Metric metricInstance = (Metric) metricImplementationClass.newInstance();
                metricInstance.setup(this.sourceData);
                this.metricInstances.add(metricInstance);
            } else if(isRankBasedMetric){
                RankBasedMetric metricInstance = (RankBasedMetric) metricImplementationClass.newInstance();
                this.rankeBasedMetricInstances.add(metricInstance);
            }
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
                this.metricInstances,
                this.rankeBasedMetricInstances).buildPolicySuite();
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
    
    private boolean isImplementorOfRankBasedMetric(Class aClass) {
        for (int i = 0; i < aClass.getInterfaces().length; i++) {
            if (aClass.getInterfaces()[i] == RankBasedMetric.class) {
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
