/*
 * TidalTrustMetric.java
 *
 * Created on 23. Februar 2005, 11:32
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;
import de.fuberlin.wiwiss.trust.EvaluationResult;
import de.fuberlin.wiwiss.trust.EXPL;
import de.fuberlin.wiwiss.trust.ExplanationPart;
import de.fuberlin.wiwiss.trust.MetricException;
import de.fuberlin.wiwiss.trust.metric.vocab.MindswapTrust;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Set;
import java.util.Vector;

/**
 * This trust metric computes the trust of a trust source in a trust sink. The trust value
 * consists of a float in the range of 0 (not trusted) and 1 (totaly trusted).
 *
 * METRIC(tpl:TidalTrustMetric, ?source, ?sink)
 * example: METRIC(tpl:TidalTrustMetric, ?USER, <oliver-maresch@gmx.de>)
 *
 * metric vocabulary: <a href="http://trust.mindswap.org/ont/trust.owl">http://trust.mindswap.org/ont/trust.owl</a>
 * docs
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public final class TidalTrustMetric extends Metric implements de.fuberlin.wiwiss.trust.Metric {
    
    /**
     * Contains all Nodes from the Trust Vocabulary 
     *  <a href="http://trust.mindswap.org/ont/trust.owl">http://trust.mindswap.org/ont/trust.owl</a>,
     * which represent the simple trust properties (not the trustsRegarding property and the 
     * prperties of the TopicalTrust class). The properties are ordered 
     * from low trust to high trust.
     */
    private Vector trustProperties;
            
    /**
     * Cached {@link NamedGraphSet} for the data source of the last call of the findDirectRating methode.
     * This attribute is used to indicate whether or not the current getDirectRating,
     * hasDirectRating or getDirectRatingGraph methode call is for the same
     * source, sink and data source.
     */
    private NamedGraphSet cachedSources = null;
    /**
     * Cached source {@link Node} of the last call of findDirectRating methode.
     * This attribute is used to indicate whether or not the current getDirectRating,
     * hasDirectRating or getDirectRatingGraph methode call is for the same
     * source, sink and data source.
     */
    private Node cachedDirectRatingSourceNode = null;
    /**
     * Cached sink {@link Node} of the last call of findDirectRating methode.
     * This attribute is used to indicate whether or not the current getDirectRating,
     * hasDirectRating or getDirectRatingGraph methode call is for the same
     * source, sink and data source.
     */
    private Node cachedDirectRatingSinkNode = null;
    /**
     * Cached result of the last call of the findDirectRating methode,
     * which indicates whether or not a direct rating was found.
     */
    private boolean cachedFoundDirectRating = false;
    /** 
     * Cached result of the last call of the findDirectRating methode,
     * which caches the found rating or 0, if no rating was found.
     */
    private float cachedDirectRating = 0;
    /**
     * Cached result of the last call of the findDirectRating methode,
     * which caches the {@link Node} of the graph, where the rating was found or
     * null, if no rating was found.
     */
    private Node cachedSourceGraph = null;
 
    /**
     * Contains after the run of the TidalTrust algorithm, whether or not the 
     * sink was found through the trust network.
     */
    private boolean foundSink = false;
    
    /**
     * Contains after the run of the TidalTrust algorithm, whether or not the
     * source has a own rating of the sink.
     */
    private boolean sourceHasOwnRating = true;
    
    /**
     * Contains the result of the metric;
     */
    private boolean sourceTrustsSink = false;
    
    /** 
     * The trust value for the inferred trust between the source and the sink.
     */
    private float trustValue = 0f;
    
    /**
     * The minimal path length from the source to the sink.
     */
    private int minPathLength = Integer.MAX_VALUE;
    
    /**
     * The maximal path flow form the source to the sink using the pathes with
     * the minimal path length.
     */
    private float maxPathFlow = 0;
    
    /**
     * Maps Nodes to vectors of Path objects. The Path objects contain pathes 
     * to the sink.
     */
    private Map pathesToSink = null;

        /**
     * The node, which trusts the sink or not.
     */
    private Node source = null;
    
    /**
     * The node, which's trust should be evaluated.
     */
    private Node sink = null;
    
    /**
     * The threshold value for trusting the sink.
     */
    private float threshold = 1f;

    
    /**
     * Collects all source information and generates the source summary.
     */
    private DataSourcesSummary sourceSummary = null;
    
    /**
     * Creates a new instance of TrustMailMetrik 
     */
    public TidalTrustMetric() {
        super("http://www.wiwiss.fu-berlin.de/suhl/bizer/TPL/TidalTrustMetric");
       this.sourceSummary = null;
        
        trustProperties = MindswapTrust.getTrustProperties();
    }
    
    
    /**
     * Calculates the trust value of a trust source in a trust sink. The trust value
     * consists of a float in the range of 0 (not trusted) and 1 (totaly trusted).
     * @param arguments Input arguments to the metric; a list of two {@link Node}s, 
     * <ol>
     *   <li>the {@link Node} of the trust source</li>
     *   <li>the {@link Node} of the trust sink.</li>
     * </ol>
     * @return The result, consisting of an RDF node, which contains a float value in the 
     * range of [0,1] and an explanation
     * @throws MetricException if the number or type of input arguments
     * 		are incorrect
     */
    public de.fuberlin.wiwiss.trust.EvaluationResult calculateMetric(java.util.List arguments) 
    throws de.fuberlin.wiwiss.trust.MetricException {
        
        // Contains the result rating
        float rating = 0;
        
        /* The metric evaluates the trust between a trust source and a trust sink. Both are
         * represented by a Node.
         */ 
        source = null;   
        sink = null;
        
        threshold = 1f;
        
        // Check and read the first argument, which should be the Node of the trust source.
        try{
            source = (Node) arguments.get(0);
        }catch(Exception e){
            throw new MetricException("The metric " + getURI() + " expects a Node, which identifies the source of trust as the first argument.");
        }
        if(!source.isURI()) throw new MetricException("The trust source (first argument) of the metric " + getURI() + " have to be a URI.");
        
        // Check and read the second argument, which should be the Node of the trust sink.
        try{
            sink = (Node) arguments.get(1);
        }catch(Exception e){
            throw new MetricException("The metric " + getURI() + " expects a Node, which identifies the sink of trust as the second argument.");
        }
        if(!sink.isURI()) throw new MetricException("The trust sink (second argument) of the metric " + getURI() + " have to be a URI.");
       
        // Check and read the third argument, which should be the Node with the threshold value inbetween 0 and 1.
        try{
            threshold = Float.parseFloat(((Node) arguments.get(2)).getLiteral().getLexicalForm());
        }catch(Exception e){
            throw new MetricException("The metric " + getURI() + " expects a Node, which contains the threshold as float value in the range from 0 to 1 as third argument.");
        }
       
        
        return calcTidalTrust();
    }
    
    /**
     * This methode implements the TidalTrust metric algorithm
     * specified by Jennifer Golbeck.
     * @return result and explanation
     */ 
    private de.fuberlin.wiwiss.trust.EvaluationResult calcTidalTrust()
    throws de.fuberlin.wiwiss.trust.MetricException { 
        
        // Explanation generation section begin -------------------------------------
        
        // contains after the run of the TidalTrust algorithm, whether or not the
        // source has a own rating of the sink.
        sourceHasOwnRating = true;

        // contains after the run of the TidalTrust algorithm, whether of not the 
        // sink was found through the trust network
        foundSink = false;
        // maps Nodes to vectors of Path objects, if pathes to the sink were found
        pathesToSink = new java.util.HashMap();
        
        // setup a new source summary
        sourceSummary = new DataSourcesSummary();
        
        // Explanation generation section end ---------------------------------------        
        
        // all nodes in the vector are colored gray, all
        // other nodes have the color white.
        Vector grayNodes =  new Vector();
        grayNodes.add(source);
        
        // holds the nodes of the current depth
        Stack q = new Stack();
        // start with trust source
        q.push(source);
        
        // collects the nodes of the depth level
        Stack temp_q = new Stack();
        
        // the current depth
        int depth = 1;
        
        // if the reference is null, the value is interpretated as infinity
        Integer maxdepth = null;
        
        // the stack contains the vectors of nodes of a certain depth. 
        // NOTE: The indexing of the depth vectors starts at  0. Therefore
        // vector of the depth 1 has the index 0 and so on.
        Vector d = new Vector();
        // add vector for the depth 1
        d.add(depth - 1, new Vector());
        
        // contains the cached ratings (Float) of nodes to the sink
        Map cachedRatingOfSink = new java.util.HashMap();

        // contains the vectors, which contain the children of the nodes
        // (A child is a node, for which the current node has a rating and
        // the child was not seen by the algorithm.)
        Map children = new java.util.HashMap();

        // contains the path flows of the nodes
        Map pathFlow = new java.util.HashMap();
        pathFlow.put(source, new Float(1));
        
        // 
        while(q.size() > 0 && (maxdepth == null || depth <= maxdepth.intValue())){
            Node n = (Node) q.pop();
            
            // add Node to the current depth vectorn if not allready added
            Vector currentDepth = (Vector) d.get(depth-1);
            if(!currentDepth.contains(n)) currentDepth.add(n);
            
            // create children vector for current node
            Vector childrenOfN = new Vector();

            
            // get the asserted graphs of n, the graphs are treated as trusted
            // form n points of view
            AssertedGraphs assertedGraphs = new AssertedGraphs(n, getSourceData());
            NamedGraphSet trustedGraphs = assertedGraphs.getGraphs();
            Map warrantMap = assertedGraphs.getWarrantMap();
            
            // Explanation generation section begin -------------------------------------
            
            // add the trustedGraphs to the used graphs
            Iterator graphIt = trustedGraphs.listGraphs();
            while(graphIt.hasNext()){
                Node graphNode = ((NamedGraph) graphIt.next()).getGraphName();
                sourceSummary.addDataSource(graphNode, (Node) warrantMap.get(graphNode), n);
            }
            
            // Explanation generation section end ---------------------------------------        

            // check, if sink adjacent to current node n
            if(hasDirectRating(n, sink, trustedGraphs)){
                float directRating = getDirectRating(n, sink, trustedGraphs);
                
                // cache the rating
                cachedRatingOfSink.put(n, new Float(directRating));
                // set the max depth to the current
                maxdepth = new Integer(depth);
                // calculate the path flow to sink
                Float flowN = (Float) pathFlow.get(n);
                float flow = Math.min(flowN.floatValue(), directRating);
                Float flowSink = (Float) pathFlow.get(sink);
                pathFlow.put(sink, new Float(Math.max((flowSink == null? 0f: flowSink.floatValue()), flow)));

                // Explanation generation section begin -------------------------------------
                foundSink = true;
                Vector pathesNToSink = new Vector(); 
                Path pathNToSink = new Path();
                pathNToSink.addEdge(new Edge(directRating,sink));
                pathesNToSink.add(pathNToSink);
                pathesToSink.put(n,pathesNToSink);
                // Explanation generation section end ---------------------------------------        
                
                // add sink to children of n
                childrenOfN.add(sink);
            }else{
                // if the sink is not adjacent to n, then:

                // Explanation generation section begin -------------------------------------
                sourceHasOwnRating = false;        
                // Explanation generation section end ---------------------------------------        
                
                // get all adjacent nodes
                Iterator neighbors = getNeighbors(n,trustedGraphs).iterator();
                
                while(neighbors.hasNext()){
                    Node n2 = (Node) neighbors.next();
                    // if the current neighbor n2 was not seen before, ...
                    if(!grayNodes.contains(n2)){
                        // add to collection of nodes in the next depth level
                        if(!temp_q.contains(n2)) temp_q.push(n2);
                        
                        // calculate the path flow for neighbor n2
                        Float flowN = (Float) pathFlow.get(n);
                        float flow = Math.min(flowN.floatValue(), getDirectRating(n, n2, trustedGraphs));
                        Float flowN2 = (Float) pathFlow.get(n2);
                        pathFlow.put(n2, new Float(Math.max((flowN2 == null? 0f: flowN2.floatValue()), flow)));

                        // add neighbor to children of n
                        childrenOfN.add(n2);
                    }
                }  
            }
            // add the children vector of the current node the other children vectors
            children.put(n, childrenOfN);
            
            // if all nodes of this depth level are seen, ...
            if(q.empty()){
                // make the next depth level to the current
                q = temp_q;
                depth++;
                d.add(depth -1, new Vector());
                
                // create a new stack for the next next level
                temp_q = new Stack();
                
                // color all nodes in the new q gray
                Iterator new_gray = q.iterator();
                while(new_gray.hasNext()){
                    grayNodes.add(new_gray.next());
                }
            }
        }
        
        
        Float flowSink = (Float) pathFlow.get(sink);
        maxPathFlow = (flowSink == null? 0f: flowSink.floatValue());
        depth--;
        minPathLength = depth;
        depth--;
        
        
        // start backtracking through the depth levels
        while(depth > 0) {
            Vector d_vec = (Vector) d.get(depth - 1);
            
            // for all nodes of the current depth level
            for(int i = 0; i < d_vec.size();i++){                
                Node n = (Node) d_vec.get(i);
                
                float numerator = 0;
                float dominator = 0;    
                
                // get the found child nodes
                Iterator childrenOfN = ((Vector) children.get(n)).iterator();
        
                // get the asserted graphs of n, the graphs are treated as trusted
                // form n points of view
                AssertedGraphs assertedGraphs = new AssertedGraphs(n, getSourceData());
                NamedGraphSet trustedGraphs = assertedGraphs.getGraphs();
                
                // for all child nodes of n 
                while(childrenOfN.hasNext()){
                    Node n2 = (Node) childrenOfN.next();
                    
                    // if the rating from n to its child n2 is greater or equal to the maximum path flow to the sink
                    // and there exists a rating form n2 to the sink
                    Float ratingN2ToSink = (Float) cachedRatingOfSink.get(n2);
                    float ratingNToN2 = getDirectRating(n,n2,trustedGraphs);

                    // Explanation generation section begin -------------------------------------
                    if(ratingN2ToSink != null){
                        Vector pathesNToSink;
                        if(!pathesToSink.containsKey(n)){
                            pathesNToSink = new Vector();
                            pathesToSink.put(n, pathesNToSink);
                        }else{
                            pathesNToSink = (Vector) pathesToSink.get(n);
                        }
                        Edge nToN2 = new Edge(ratingNToN2, n2);
                        Iterator pathesN2ToSink = ((Vector) pathesToSink.get(n2)).iterator();
                        while(pathesN2ToSink.hasNext()){
                            Path nToSink = new Path((Path) pathesN2ToSink.next());
                            nToSink.addEdge(nToN2);
                            pathesNToSink.add(nToSink);
                        }                        
                    }
                    // Explanation generation section end ---------------------------------------        

                    if(ratingNToN2 >= maxPathFlow &&  ratingN2ToSink != null){
                        // use the path from n over its child n2 to the sink for the calulation of the trust value
                        numerator += ratingNToN2 * ratingN2ToSink.floatValue();
                        dominator += ratingNToN2;
                    }
                }

                if(dominator > 0)
                    // if there is a trusted path from n over its childs to the sink
                    // cache the the trust value, ...chis@bizer.de
                    cachedRatingOfSink.put(n, new Float(numerator/dominator));
                else
                    // ... otherwise cache, that there is not trusted path from n to the sink.
                    cachedRatingOfSink.put(n, null);   
            }
            depth--;
        }
         
        
        Float result = (Float) cachedRatingOfSink.get(source);
        // default trust 
        trustValue = 0;
        // if a infered trust value exit, update trust 
        if(result != null) trustValue = result.floatValue();
       
        sourceTrustsSink = trustValue >= threshold;
        
        de.fuberlin.wiwiss.trust.metric.MetricResult metricResult = new de.fuberlin.wiwiss.trust.metric.MetricResult(sourceTrustsSink, explain(), explainRDF());
        metricResult.setTrustValue(trustValue);
        return metricResult;
    }
   
    
    private ExplanationPart generateMinPathLengthExplanation(){
        List explanation = new java.util.ArrayList();
        explanation.add(cl(
            "The minimum path length between the source and the sink was infered to be " + minPathLength + "."));
        ExplanationPart part = new ExplanationPart(explanation);
        
        List exampleExpl = new java.util.ArrayList();
        exampleExpl.add(cl("An example path is: The source "));
        exampleExpl.add(source);
        Vector edges = (Vector) ((Path) ((Vector) pathesToSink.get(source)).get(0)).getEdges();
        Edge edge = (Edge) edges.get(0);
        exampleExpl.add(cl(" has the rating " + edge.getTrustRating() + " of the entity "));
        exampleExpl.add(edge.getTrustedNode());
        for(int i = 1; i < edges.size() - 1;i++){
            edge = (Edge) edges.get(i);
            exampleExpl.add(cl(", which has the rating " + edge.getTrustRating() + " of the entity "));
            exampleExpl.add(edge.getTrustedNode());
        }
        edge = (Edge) edges.get(edges.size()-1);
        exampleExpl.add(cl(", which has the rating " + edge.getTrustRating() + " of the sink "));
        exampleExpl.add(edge.getTrustedNode());
        exampleExpl.add(cl("."));

        ExplanationPart examplePart = new ExplanationPart(exampleExpl);
        part.setDetails(examplePart);

        return part;
    }
    
    private ExplanationPart generateMaxPathFlowExplanation(){
        Vector pathesFromSourceToSink = (Vector) pathesToSink.get(source);
        
        List explanation = new java.util.ArrayList();
        explanation.add(cl(
            "The metric found " + pathesFromSourceToSink.size() + " trust path(es) with the minimal path length of " + 
            minPathLength + " stations."));
        explanation.add(cl(
            "The maximum pathflow over the trust ratings of those/this path(es) was " + 
            maxPathFlow + "."));
        ExplanationPart part = new ExplanationPart(explanation);
        
        List pathesExpl = new java.util.ArrayList();
        pathesExpl.add(cl("The path(es) from the source to the sink are/is: "));
        ExplanationPart pathesPart = new ExplanationPart(pathesExpl);
        part.setDetails(pathesPart);
        
        for(int i = 0; i < pathesFromSourceToSink.size(); i++){
            List pathExpl = new java.util.ArrayList();
            Path p = (Path) pathesFromSourceToSink.get(i);
            Vector edges = p.getEdges();
            pathExpl.add(cl("" + (i + 1) + ". source "));
            pathExpl.add(source);
            Edge e;
            for(int j = 0; j < edges.size()-1; j++){
                e = (Edge) edges.get(j);
                pathExpl.add(cl(" -" + e.getTrustRating() + "-> "));
                pathExpl.add(e.getTrustedNode());
            }
            e = (Edge) edges.get(edges.size()-1);
            pathExpl.add(cl(" -" + e.getTrustRating() + "-> sink "));
            pathExpl.add(e.getTrustedNode());
            pathExpl.add(cl(" (max pathflow of the path: " + p.getMinTrustRating() + ")"));
            pathesPart.addPart(new ExplanationPart(pathExpl));
        }
        
        return part;
    }
    
    private ExplanationPart generateSelectedPathesExplanation(Vector selectedPathes){
        List explanation = new java.util.ArrayList();
        explanation.add(cl(
            "The metric selects only pathes for the calculation of the weighted average trust value, " +
            "which contain only trust ratings along the path, which are greater or equal to the maximum pathflow."));
        explanation.add(cl(
            "" + selectedPathes.size() + " pathes satify this criteria."));
        ExplanationPart part = new ExplanationPart(explanation);
        
        List pathesExpl = new java.util.ArrayList();
        pathesExpl.add(cl("The selected path(es) are/is : "));
        ExplanationPart pathesPart = new ExplanationPart(pathesExpl);
        part.setDetails(pathesPart);
        
        for(int i = 0; i < selectedPathes.size(); i++){
            List pathExpl = new java.util.ArrayList();
            Path p = (Path) selectedPathes.get(i);
            Vector edges = p.getEdges();
            pathExpl.add(cl("" + (i + 1) + ". source "));
            pathExpl.add(source);
            Edge e;
            for(int j = 0; j < edges.size()-1; j++){
                e = (Edge) edges.get(j);
                pathExpl.add(cl(" -" + e.getTrustRating() + "-> "));
                pathExpl.add(e.getTrustedNode());
            }
            e = (Edge) edges.get(edges.size()-1);
            pathExpl.add(cl(" -" + e.getTrustRating() + "-> sink "));
            pathExpl.add(e.getTrustedNode());
            pathExpl.add(cl(" (max pathflow of the path: " + p.getMinTrustRating() + ")"));
            pathesPart.addPart(new ExplanationPart(pathExpl));
        }        
        
        return part;
    }
    
    private ExplanationPart generateWeightedAverageExplanation(int selectedPathesCount){
        List explanation = new java.util.ArrayList();
        explanation.add(cl(
            "The weighted average trust value of the " + selectedPathesCount + 
            " selected path(es) to the sink is " + trustValue + "."));
       
        ExplanationPart part = new ExplanationPart(explanation);
        return part;
    }
    
    private ExplanationPart generateCalculationExplanation(){
        List calculationSummary = new java.util.ArrayList();
        
        calculationSummary.add(cl("The infered trust value arises from the following calculation."));
        ExplanationPart explCalculation = new ExplanationPart(calculationSummary);
        
        // expl min path length 
        explCalculation.addPart(generateMinPathLengthExplanation());
        
        // max pathflow to sink
        explCalculation.addPart(generateMaxPathFlowExplanation());
        
        Vector selectedPathes = new Vector();
        Iterator pathes = ((Vector) pathesToSink.get(source)).iterator();
        while(pathes.hasNext()){
            Path p = (Path) pathes.next();
            if(p.getMinTrustRating() >= maxPathFlow){
                selectedPathes.add(p);
            }
        }
        
        // remaining pathes due to max path flow criteria
        explCalculation.addPart(generateSelectedPathesExplanation(selectedPathes));
        
        // weighted average calculation
        explCalculation.addPart(generateWeightedAverageExplanation(selectedPathes.size()));
        
        // add the summary of the used sources
        explCalculation.addPart(sourceSummary.summarize());        
        
        return explCalculation;
    }
    

    
    /**
     * Checks, whether or not the trusted graphs contain trust statements between the 
     * trust source and the trust sink for this metric.
     * @param trustSource
     * @param trustSink
     * @param graphs
     * @return true, if the graphs contain a relevant trust statment from the source to the sink, false otherwise
     */
    private boolean hasDirectRating(Node trustSource, Node trustSink, NamedGraphSet graphs){
        if(!(this.cachedDirectRatingSinkNode != null 
          && this.cachedDirectRatingSinkNode.equals(trustSink)
          && this.cachedDirectRatingSourceNode != null
          && this.cachedDirectRatingSourceNode.equals(trustSource)
          && this.cachedSourceGraph != null
          && this.cachedSourceGraph.equals(graphs))){
           findDirectRating(trustSource, trustSink, graphs);
        }
        return this.cachedFoundDirectRating;
    }
    
    /**
     * Gets the direct rating from the source to the sink. If no direct rating exist, the returned 
     * rating is 0 (no trustworthiness). If more than one trust rating exist, the lowest value will
     * be returned.
     * @param trustSource
     * @param trustSink
     * @param graphs - the GraphSet, which is the datasource for this analysis
     * @return the trust value
     */
    private float getDirectRating(Node trustSource, Node trustSink, NamedGraphSet graphs){
        if(!(this.cachedDirectRatingSinkNode != null 
          && this.cachedDirectRatingSinkNode.equals(trustSink)
          && this.cachedDirectRatingSourceNode != null
          && this.cachedDirectRatingSourceNode.equals(trustSource)
          && this.cachedSourceGraph != null
          && this.cachedSourceGraph.equals(graphs))){
           findDirectRating(trustSource, trustSink, graphs);
        }        
        return this.cachedDirectRating;
    }
    
    
    /**
     * Gets the source graph {@link Node}, if the source graphs contain a direct
     * rating.
     * @param trustSource
     * @param trustSink
     * @param sourecGraphs - the GraphSet, which is the datasource for this analysis
     * @return the source graph of a found rating or null, if no rating was found
     */
    private Node getDirectRatingSourceGraph(Node trustSource, Node trustSink, NamedGraphSet sourceGraphs){
        if(!(this.cachedDirectRatingSinkNode != null 
          && this.cachedDirectRatingSinkNode.equals(trustSink)
          && this.cachedDirectRatingSourceNode != null
          && this.cachedDirectRatingSourceNode.equals(trustSource)
          && this.cachedSourceGraph != null
          && this.cachedSourceGraph.equals(sourceGraphs))){
           findDirectRating(trustSource, trustSink, sourceGraphs);
        }        
        return this.cachedSourceGraph;
    }
        

    /**
     * 
     */
    private void findDirectRating(Node trustSource, Node trustSink, NamedGraphSet graphs){
        // Update cache
        this.cachedDirectRatingSinkNode = trustSink;
        this.cachedDirectRatingSourceNode = trustSource;
        this.cachedSources = graphs;
        
        // initialize cached rating
        this.cachedDirectRating = 0;
        this.cachedSourceGraph = null;
        this.cachedFoundDirectRating = false;
        
        // for all trustProperties from low to high trust , search for the lowest
        // trust rating.
        int i = 0;
        while(i < trustProperties.size() && !this.cachedFoundDirectRating){
            Quad pattern = new Quad(Node.ANY, trustSource, (Node) trustProperties.get(i), trustSink);

            //if the lowes trust rating is found, update the cached values 
            if(graphs.containsQuad(pattern)) {
                this.cachedDirectRating = ((float) i)/10f;
                this.cachedFoundDirectRating = true;
                Iterator quads = graphs.findQuads(pattern);
                this.cachedSourceGraph = ((Quad) quads.next()).getGraphName();
            }
            i++;
        }
    }
    
    
    /**
     * Returns all Nodes, which are neighbors of the trust source.
     * A {@link Node} is a neighbor of the source, if the source
     * has a direct rating of that node.
     * (All those nodes, which are adjacent to the source, in the trust graph)
     * @param source - the node, which's neighbors should be found
     * @param trustedSources - the graphs, which are trusted By the source
     * @return the set of neighbors of the source
     */
    private java.util.Set getNeighbors(Node trustSource, NamedGraphSet trustedSources){
        Set neighbors = new java.util.HashSet();
        
        // for all trust properties ....
        for(int i = 0; i < trustProperties.size(); i++){
            // find the quads with the source as subjet and the trust property as predicate ...
            Iterator n = trustedSources.findQuads(Node.ANY, trustSource, (Node) trustProperties.get(i), Node.ANY);
            while(n.hasNext()){
                // and add the object nodes to the neighbors of the source
                neighbors.add(((Quad) n.next()).getObject());
            }
        }
        return neighbors;
    }
    
    public void setup(de.fuberlin.wiwiss.ng4j.NamedGraphSet sourceData) {
        super.setup(sourceData);
        source = null;
        sink = null;
        threshold = 1f;

        foundSink = false;
        sourceHasOwnRating = true;
        sourceTrustsSink = false;
        trustValue = 0f;
        minPathLength = Integer.MAX_VALUE;
        maxPathFlow = 0f;
        pathesToSink = null;

        cachedFoundDirectRating = false;
        cachedDirectRating = 0f;
        cachedDirectRatingSinkNode = null;
        cachedDirectRatingSourceNode = null;
        cachedSourceGraph = null;
        cachedSources = null;
    }

    private class Path{
        private Vector edges;
        private float minTrustRating;

        public Path(){
            edges = new Vector();
            minTrustRating = 1;
        }
        
        public Path(Path p){
            edges = new Vector(p.getEdges());
            minTrustRating = p.getMinTrustRating();
        }
        
        public void addEdge(Edge edge){
            minTrustRating = Math.min(minTrustRating, edge.getTrustRating());
            edges.add(0,edge);
        }
        
        public Vector getEdges(){
            return edges;
        }
        
        public float getMinTrustRating(){
            return minTrustRating;
        }
    }
    
    private class Edge{
        private Node trustedNode;
        private float trustRating;
        
        public Edge(float trustRating, Node trustedNode){
            this.trustedNode = trustedNode;
            this.trustRating = trustRating;
        }
        
        public Node getTrustedNode(){
            return trustedNode;
        }
        
        public float getTrustRating(){
            return trustRating;
        }
    }
    
// Explanation generation section begin -------------------------------------
        
    protected Graph explainRDF() {
        return null;
    }
    
    protected ExplanationPart explain() {
        ExplanationPart explComplete;
        List summary = new java.util.ArrayList();
        if(foundSink){
            // Summary
            if(sourceTrustsSink){
                summary.add(cl("The "));
                summary.add(com.hp.hpl.jena.graph.Node.createURI(this.getURI()));
                summary.add(cl(" infered, that the source "));
                summary.add(source);
                summary.add(cl(" trusts the sink "));
                summary.add(sink);
                if(sourceHasOwnRating){
                    summary.add(cl(", because the source has a own rating of the sink with the trust value " + trustValue + ", which holds the threshold of " + threshold + "."));
                }else{
                    summary.add(cl(", because the infered trust value is " + trustValue + " and holds the threshold of " + threshold + "."));
                }
            } else {
                summary.add(cl("The "));
                summary.add(com.hp.hpl.jena.graph.Node.createURI(this.getURI()));
                summary.add(cl(" infered, that the source "));
                summary.add(source);
                summary.add(cl(" doesn't trust the sink "));
                summary.add(sink);
                if(sourceHasOwnRating){
                    summary.add(cl(", because the source has a own rating of the sink with the trust value " + trustValue + ", which doesn't hold the threshold of " + threshold + "."));
                }else{
                    summary.add(cl(", because the infered trust value is " + trustValue + " and doesn't hold the threshold of " + threshold + "."));
                }
            }
        } else {
            summary.add(cl("The "));
            summary.add(com.hp.hpl.jena.graph.Node.createURI(this.getURI()));
            summary.add(cl(" couldn't find a path from the source  "));
            summary.add(source);
            summary.add(cl(" to the sink "));
            summary.add(sink);
            summary.add(cl(". Therefore the source doesn't trust the sink."));
        }
        explComplete = new ExplanationPart(summary);
        
        if(!sourceHasOwnRating && foundSink){
            // add calculation explanation, if the source have no own rating of the source
            // and a path to the sink was found.
            explComplete.setDetails(generateCalculationExplanation());
        }
        
        return explComplete;
    }
 // Explanation generation section end ---------------------------------------        
    
    
    /**
     * Simple test methode
     */
    public static void main(String[] args) throws de.fuberlin.wiwiss.trust.MetricException {

        NamedGraphSet data = new NamedGraphSetImpl();
        data.read("file:/home/voodoo/Java/project/trustlayer/ng4j/doc/trustlayer/finTrustData.trig", "TRIG");
        
        // run metric
        java.util.List arguments = new java.util.LinkedList();
        Node source = Node.createURI("dadean7@lycos.de");
        arguments.add(0,source);
        Node sink = Node.createURI("http://www.reuters.com");
        arguments.add(1,sink);
        Node threshold = Node.createLiteral("0.5", null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDfloat);
        arguments.add(2,threshold);
        
        Metric metric = new TidalTrustMetric();
        metric.setup(data);
        EvaluationResult result = metric.calculateMetric(arguments);

        
        // print explanations of all triples
        ExplanationPart part = result.getTextExplanation();

        System.out.println("Question: Should the source <" + source.toString() + "> trust the sink <" + sink.toString() + ">?\nAnswer: " + (result.getResult()?"Yes.\n":"No.\n"));
        System.out.println("Explanation:");
        
        Model m = ModelFactory.createDefaultModel();
        Graph g = m.getGraph();
        part.writeAsRDF(g);
        m.setNsPrefixes(PrefixMapping.Standard);
        m.setNsPrefix("expl", EXPL.getURI());
        m.write(System.out, "N3");
    }

}