/*
 * TrustedGraphs.java
 *
 * Created on 2. März 2005, 11:16
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Node;
import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.triql.TriQLQuery;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class AssertedGraphs{
    
    /**
     * All asserted graphs of the authority
     */
    private NamedGraphSet graphs;
    /**
     * Map from the graph Nodes to a warrant Node of the authority, which contains the 
     * tpl:assertedBy statement. Note: Only one warrant per graph.
     */
    private Map warrantMap;

    /**
     * Creates a new instance of the AssertedGraphs class.
     * Finds all graphs, which were asserted by the authority and all warrants used
     * to assert the graphs. 
     * @param authority
     * @return all asserted graphs and used warrants of the authority
     */
    public AssertedGraphs(Node authority, NamedGraphSet sourceData){
        graphs = new NamedGraphSetImpl();
        warrantMap = new java.util.HashMap();

        // Query: Find all graphs, its warrants and authorities
        String query = "SELECT ?graph, ?authority ?warrant\n" +
                       "WHERE ?warrant (?graph swp:assertedBy ?warrant .\n" +
                                       "?warrant swp:assertedBy ?warrant .\n" +
                                       "?warrant swp:authority  ?authority)\n" +
                       "USING swp FOR <http://www.w3.org/2004/03/trix/swp-2/>\n";
        // execute query
        Iterator results = TriQLQuery.exec(sourceData, query);

        while ( results.hasNext() )
        {
            Map binding = (Map) results.next();
            Node graphNode = (Node) binding.get("graph");
            Node authorityNode = (Node) binding.get("authority");
            Node warrantNode = (Node) binding.get("warrant");
            
            // select only those graphs asserted by the authority
            if(authority.equals(authorityNode)){
                NamedGraph graph = sourceData.getGraph(graphNode);
                graphs.addGraph(graph);
                graph = sourceData.getGraph(warrantNode);
                graphs.addGraph(graph);
                warrantMap.put(graphNode, warrantNode);
            }
        }
    }
    
    /**
     * Returns all asserted graphs of the authority.
     */
    public NamedGraphSet getGraphs(){
        return graphs;
    }
    
    /**
     * Returns the map, which maps form the graph Nodes to its warrant Node of the authority,
     * which contains the tpl:assertedBy statement. Note: Only one warrant per graph.
     */
    public Map getWarrantMap(){
        return warrantMap;
    }
}
