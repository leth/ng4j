/*
 * Metric.java
 *
 * Created on 8. April 2005, 12:05
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.trust.ExplanationPart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public abstract class RankBasedMetric implements de.fuberlin.wiwiss.trust.RankBasedMetric {
    
    /**
     * URI, which identifies the Metric in the TriQL Trust-Architecture.
     */
    protected String uri =  null;
    
    
    /** 
     * Caches metric-specific cache objects in a map. 
     * The keys are Integers with the value of resultbinding number (begin with zero),
     * for which the cached values were calculated. 
     */
   private Map cache;
   
   /**
    * Maps the number of the resultbinding to its sink nodes.
    */
   private Map numberToSink;
    
    
    public RankBasedMetric(String uri){
        this.uri = uri;
        this.cache = new HashMap();
        this.numberToSink = new HashMap();
    }
    
    /**
     * Looks up the sink of the binding i.
     */
    protected Node getSink(int i){
       return (Node) numberToSink.get(new Integer(i)); 
    }
        
    /**
     * Looks up the RankingCache of the binding i.
     */
    protected RankingCache getRankingCache(int i){
        return (RankingCache) cache.get(new Integer(i));
    }
    
    /** 
     * Stores a metric-specific RankingCache into the cache.
     * The RankingCache is accessible with the specified key, which should be
     * the number of the resultbindg, which produces the data of the RankingCache.
     */
    protected void cache(RankingCache obj, Node sink){
        Integer i = new Integer(numberToSink.size());
        numberToSink.put(i, sink);
        cache.put(i, obj);
    }
    
    public int rows(){
        return numberToSink.size();
    }

        
    public String getURI() {
        return uri;
    }
    
    public boolean hasCachedRanking(RankingCache obj){
        return cache.containsValue(obj);
    }
    
    /** 
     * Checks for a ranking, whether or not a ranking with the same
     * parameters was allready calculated. If so, the cached rating 
     * will be returned, null otherwise.
     */
    public RankingCache getCachedRanking(RankingCache notCached){
        Iterator it = cache.values().iterator();
        while(it.hasNext()){
            RankingCache cached = (RankingCache) it.next();
            if(cached.equals(notCached)){
                return cached;
            }
        }
        return null;
    }
        
    /**
     * Creates a String Literal Node
     * @param str
     * @return StringLiteral as a Node
     */
    protected com.hp.hpl.jena.graph.Node cl(String str){
        return com.hp.hpl.jena.graph.Node.createLiteral(str);
    } 
    
    public ExplanationPart explain(int i){
        Node sink = getSink(i);
        RankingCache rc = getRankingCache(i);
        return rc.explain(sink);
    }
    
    public Graph explainRDF(int i){
        return null;
    }
    
    public boolean isAccepted(int i){
        Node sink = getSink(i);
        RankingCache rc = getRankingCache(i);
        return rc.isAccepted(sink);
    }
      
}