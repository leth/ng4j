/*
 * SemanticPageRank.java
 *
 * Created on 2. Juni 2005, 16:25
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import de.fuberlin.wiwiss.trust.EXPL;
import de.fuberlin.wiwiss.trust.ExplanationPart;
import de.fuberlin.wiwiss.trust.MetricException;
import de.fuberlin.wiwiss.trust.ExplanationToHTMLRenderer;

import de.fuberlin.wiwiss.trust.metric.vocab.MindswapTrust;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 *
 * Parameter:
 * sink
 * top
 * property list
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class SemanticPageRank extends RankBasedMetric implements de.fuberlin.wiwiss.trust.RankBasedMetric {
    
    public static final String URI = "http://www.wiwiss.fu-berlin.de/suhl/bizer/TPL/SemanticPageRank";

    private List inputTable;
    
    private NamedGraphSet sourceData;
    
    private SemanticPageRankCache ranking;
    
    /** Creates a new instance of SemanticPageRank */
    public SemanticPageRank() {
        super(URI);
        inputTable = null;
        sourceData = null;
        ranking = null;
    }
    
    public void init(NamedGraphSet source, List inputTable) throws MetricException{
        this.sourceData = source;
        this.inputTable = inputTable;
        
        Iterator bindings = this.inputTable.iterator();
        while(bindings.hasNext()){
            this.ranking = readArguments((List) bindings.next());
            Node entity = this.ranking.entity;
            if(hasCachedRanking(ranking)){
                this.ranking = (SemanticPageRankCache) getCachedRanking(ranking);
            } else {
                rank();
            }
            cache(this.ranking, entity);
        }
    }
     
   
    private SemanticPageRankCache readArguments( java.util.List arguments)
    throws MetricException{
        SemanticPageRankCache ranking = new SemanticPageRank.SemanticPageRankCache();
        
        if(arguments == null || arguments.size() < 3){
            throw new MetricException("The SemanticPageRank metric needs at least the node of the sink, the number of trusted nodes on top of the ranking and one URI of a property, which should be interpretated as a link in the PageRank algorithm.");
        }
        
        {
            ranking.entity = (Node) arguments.get(0);
        }
        
        // get the number of trusted nodes on top of the ranking
        try{
            com.hp.hpl.jena.graph.Node_Literal tmp = (com.hp.hpl.jena.graph.Node_Literal) arguments.get(1);
            ranking.top = Integer.parseInt(tmp.getLiteral().getLexicalForm());
            if(ranking.top < 1) throw new MetricException("The second argument of SemanticPageRank metric contains the number of trusted nodes. Its value have to be a positive integer (excluding zero) value.");
        }catch(Exception e){
            throw new MetricException("The second argument of SemanticPageRank metric contains the number of trusted nodes. Its value have to be a positive integer (excluding zero) value.");
        }
        
        // get the properties
        for(int i = 2; i < arguments.size(); i++){
            ranking.networkProperties.add((Node) arguments.get(i));
        }
        
        return ranking;
    }
    
    private void rank(){
        
        // get the pages with in and out links and the dangling pages
        Set rankPages = new HashSet();
        Set danglingPages = new HashSet();
        
        {
            Set in = new HashSet();
            Set out = new HashSet();

            // for all relevant properties of the graph, ...
            Iterator props = this.ranking.networkProperties.iterator();
            while(props.hasNext()){
                Node prop = (Node) props.next();
                // find all quads of the those properties ...
                Iterator stmts = this.sourceData.findQuads(new Quad(Node.ANY, Node.ANY, prop, Node.ANY));
                while(stmts.hasNext()){
                    // and add the subjects to the set of nodes with outedges and the 
                    // objects to the set of inedges. Also collect the graph names
                    // for source explanation
                    Quad q =  (Quad) stmts.next();
                    ranking.sourceGraphs.add(q.getGraphName());
                    in.add(q.getObject());
                    out.add(q.getSubject());
                }
            }

            // find those nodes with in and outedges
            Iterator inIt = in.iterator();
            while(inIt.hasNext()){
                Node inPage = (Node) inIt.next();
                if(out.contains(inPage)){
                    // if the node with the inedge is also in the 
                    // set of nodes with outedges add him to the set of nodes to rank
                    // and remove the node form the set of nodes with outedges.
                    rankPages.add(new Page(inPage));
                    out.remove(inPage);
                } else {
                    // if the node with inedge was not found in the set of nodes with 
                    // out edges, add the node to the set of dangling nodes (They wouldn't
                    // be ranked).
                    danglingPages.add(new Page(inPage));
                }
            }
            // all left nodes with outedge, which have no inedges are added to
            // the set of dangling nodes.
            Iterator outIt = out.iterator();
            while(outIt.hasNext()){
                danglingPages.add(new Page((Node)outIt.next()));
            }
        }
        
        // find backward links
        {
            // for all pages to rank ...
            Iterator pages = rankPages.iterator();
            while(pages.hasNext()){
                Page page = (Page) pages.next();
                
                // and all relevant properties ...
                Iterator props = this.ranking.networkProperties.iterator();
                while(props.hasNext()){
                    Node prop = (Node) props.next();
                    
                    // find all inedges ..
                    Iterator stmts = this.sourceData.findQuads(new Quad(Node.ANY, Node.ANY, prop, page.getNode()));
                    while(stmts.hasNext()){
                        Quad q = (Quad) stmts.next();
                        Iterator ps = rankPages.iterator();
                        boolean found = false;
                        // like the page.
                        while(ps.hasNext() && !found){
                            Page p = (Page) ps.next();
                            if(p.equals(q.getSubject())){
                                found = true;
                                page.addBackwardPage(p);
                            }
                        }
                    }
                    
                    // count the outedges
                    stmts = this.sourceData.findQuads(new Quad(Node.ANY, page.getNode(), prop, Node.ANY));
                    while(stmts.hasNext()){
                        stmts.next();
                        page.addNumForWardLinks(1);
                    }
                    
                }
            }
        }
        
        // calc PageRank
        List pages = new ArrayList(rankPages);
        float diff = Float.MAX_VALUE;
        float epsilon = 0.05f;
        float damping = 0.85f;
                
        while(diff > epsilon){
            diff = 0f;
            
            for(int i = 0; i < pages.size(); i++){
                ((Page) pages.get(i)).setupNextIteration();
            }
            
            
            for(int i = 0; i < pages.size(); i++){
                Page page = (Page) pages.get(i);
                
                Iterator backs = page.getBackwardPages();
                float sum = 0f;
                while(backs.hasNext()){
                    Page b = (Page) backs.next();
                    sum += b.getOldRank() / (float) b.getNumForwardLinks();
                 }
                
                page.setRank((1f-damping) + damping*sum);
                diff += Math.abs(page.getRank() - page.getOldRank());
            }
        }
        
        Collections.sort(pages);
        Collections.reverse(pages);
        
        Iterator dangling = danglingPages.iterator();
        while(dangling.hasNext()){
            Page p = (Page) dangling.next();
            p.setRank(0f);
            pages.add(p);
        }
        
        this.ranking.nodes = pages;
    }
    
    private class Page implements Comparable {
        
        private Node node;
        
        private List backwardPages;
        
        private int numForwardLinks;

        private float rank;
        
        private float oldRank;
        
        public Page(Node node){
            this.node = node;
            this.backwardPages = new LinkedList();
            this.numForwardLinks = 0;
            this.rank = 1f;
            this.oldRank = 1f;
        }
        
        public float getRank(){
            return this.rank;
        }
        
        public void setRank(float rank){
            this.rank = rank;
        }
        
        public float getOldRank(){
            return this.oldRank;
        }
        
        public void setupNextIteration(){
            this.oldRank = this.rank;
        }
        
        public int getNumForwardLinks(){
            return this.numForwardLinks;
        }
        
        public void addNumForWardLinks(int add){
            this.numForwardLinks += add;
        }
        
        public void addBackwardPage(Page p){
            this.backwardPages.add(p);
        }
        
        public Iterator getBackwardPages(){
            return this.backwardPages.iterator();
        }
        
        public boolean equals(Object obj){
            if( obj instanceof Page){
                return node.equals(((Page) obj).node);
            }
            return obj.equals(this.node);
        }
        
        public Node getNode(){
            return this.node;
        }
        
        public int compareTo(Object obj) {
            if(obj instanceof Page){
                Page page = (Page) obj;
                if(this.rank > page.rank){
                    return 1;
                }else if(this.rank < page.rank){
                    return -1;
                }else {
                    return 0;
                }
            }
            return -1;
        }
        
    }
    
    private class SemanticPageRankCache implements RankingCache {
        
        /** 
         * Jena Nodes with the URIS of all porperties, which should be interpretated
         * as Link in the PageRank algorithm.
         */
        public  Set networkProperties;
        
        /**
         * The URI Node of the entity, whose trustworthiness should be evaluated.
         */
        public Node entity;
        
        /**
         * Determines the number of trusted resources at the top of the ranking.
         */
        public int top;
        
        /**
         * The list of ranked nodes. After the ranking process finished, this list contains 
         * the result nodes in the order of the ranking.
         */
        public List nodes;
        
        /**
         * Collects the graph names of the graphs which contain the original data
         */
        public Set sourceGraphs;
        
        public SemanticPageRankCache(){
            networkProperties = new HashSet();
            nodes = new ArrayList();
            sourceGraphs = new HashSet();
        }
        
        public boolean equals(Object obj){
            if(obj instanceof SemanticPageRankCache){
                SemanticPageRankCache c = (SemanticPageRankCache) obj;
                return c.top == this.top
                    && c.networkProperties.equals(this.networkProperties);
            }
            return false;
        }
        
        
// Explanation Generator Section Begin ------------------------------------------
        public de.fuberlin.wiwiss.trust.ExplanationPart explain(com.hp.hpl.jena.graph.Node sink) {
            List text = new ArrayList();
            ExplanationPart expl = new ExplanationPart(text);
            expl.addPart(summary(sink));
            expl.addPart(generateRankingSummary());
            expl.addPart(generateSourceExpl());
            return expl;
        }
        
        private ExplanationPart summary(Node sink){
            List text = new ArrayList();

            Integer rank = getRankOf(sink);
 
            if(rank == null){
                text.add(cl("The "));
                text.add(com.hp.hpl.jena.graph.Node.createURI(SemanticPageRank.URI));
                text.add(cl(" could not find the sink "));
                text.add(sink);
                text.add(cl(" in the analysed property network."));
            }else { 
                int r = rank.intValue() + 1;
                if(r > top){
                    text.add(cl("The "));
                    text.add(com.hp.hpl.jena.graph.Node.createURI(SemanticPageRank.URI));
                    text.add(cl(" inferred, that the sink "));
                    text.add(sink);
                    text.add(cl(" is not trustworthy. The sink got the rank number " + r + ", which is out of the top " + top + "."));
                }else{
                    text.add(cl("The "));
                    text.add(com.hp.hpl.jena.graph.Node.createURI(SemanticPageRank.URI));
                    text.add(cl(" inferred, that the sink "));
                    text.add(sink);
                    text.add(cl(" is trustworhty. The sink got the rank number " + r + ", which is in the top " + top + "."));
                }
            }
            
            List details = new ArrayList();
            details.add(cl("The "));
            details.add(com.hp.hpl.jena.graph.Node.createURI(SemanticPageRank.URI));
            details.add(cl(" infers the reputation of a Node in a RDF graph using the PageRank algorithem. The links of the RDF graph are selected by a user-specified list of properties. All specified properties are handled unlabled links with the same weight. The interpretation of the ranking depends on the property selection. For example, if the list of properties contains only foaf:knows, the result can be interpretated as the most known person with in a foaf network."));
            
            ExplanationPart summary = new ExplanationPart(text);
            ExplanationPart detailExpl = new ExplanationPart(details);
            summary.setDetails(detailExpl);
            
            return summary;
        }
        
        private ExplanationPart generateRankingSummary(){
            List text = new ArrayList();
            
            text.add(cl("The "));
            text.add(Node.createURI(SemanticPageRank.URI));
            text.add(cl(" metric ranked " + nodes.size() + " entities. The ranking:"));
            
            ExplanationPart expl = new ExplanationPart(text);
            
            for(int i = 0; i < nodes.size(); i++){
                Page p = (Page) nodes.get(i);
                List t =  new ArrayList();
                t.add(cl("" + (i + 1) + ". Entity "));
                t.add(p.getNode());
                t.add(cl(" has a pagerank of " + p.getRank() + " points."));
                ExplanationPart e = new ExplanationPart(t);
                expl.addPart(e);
            }
            
            return expl;
        }
        
        private ExplanationPart generateSourceExpl(){
            List text = new ArrayList();

            text.add(cl("The "));
            text.add(com.hp.hpl.jena.graph.Node.createURI(SemanticPageRank.URI));
            text.add(cl(" metric found the relevant properties in " + sourceGraphs.size() + " graphs."));
            
            ExplanationPart expl = new ExplanationPart(text);
            
            List graphExpl = new ArrayList();
            graphExpl.add(cl("The sources are: "));
            
            Iterator sources = sourceGraphs.iterator();
            if(sources.hasNext()){
                graphExpl.add((Node) sources.next());
            }
            while(sources.hasNext()){
                graphExpl.add(cl(", "));
                graphExpl.add((Node) sources.next());
            }
            
            expl.setDetails(new ExplanationPart(graphExpl));
            
            return expl;
        }
        
        public com.hp.hpl.jena.graph.Graph explainRDF(com.hp.hpl.jena.graph.Node sink) {
            return null;
        }
// Explanation Generator Section End ------------------------------------------
        
        /**
         * Ranks begin with zero! Returns null if the node was not ranked.
         */
        public Integer getRankOf(com.hp.hpl.jena.graph.Node node){
            for(int i = 0; i < nodes.size(); i++){
                if(((Page) nodes.get(i)).getNode().equals(node)){
                    return new Integer(i);
                }
            }
            return null;
        }
        
        public boolean isAccepted(com.hp.hpl.jena.graph.Node sink) {
            Integer rank = getRankOf(sink);
            return rank != null && rank.intValue() < top;
        }        
    }
    
    
    public static void main(String[] args) throws de.fuberlin.wiwiss.trust.MetricException {

        // get data source
        NamedGraphSet data = new NamedGraphSetImpl();
        data.read("file:/home/voodoo/Java/project/trustlayer/ng4j/doc/trustlayer/finTrustData.trig", "TRIG");
        
        // run metric
        java.util.List arguments = new java.util.LinkedList();
        Node sink = Node.createURI("http://www.reuters.com");
        arguments.add(0,sink);
        Node top = Node.createLiteral("30", null, com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDinteger);
        arguments.add(1,top);
        Node link = MindswapTrust.trust0;
        arguments.add(2,link);
        link = MindswapTrust.trust1;
        arguments.add(3,link);
        link = MindswapTrust.trust2;
        arguments.add(4,link);
        link = MindswapTrust.trust3;
        arguments.add(5,link);
        link = MindswapTrust.trust4;
        arguments.add(6,link);
        link = MindswapTrust.trust5;
        arguments.add(7,link);
        link = MindswapTrust.trust6;
        arguments.add(8,link);
        link = MindswapTrust.trust7;
        arguments.add(9,link);
        link = MindswapTrust.trust8;
        arguments.add(10,link);
        link = MindswapTrust.trust9;
        arguments.add(11,link);
        link = MindswapTrust.trust10;
        arguments.add(12,link);
        
        java.util.List bindings = new java.util.LinkedList();
        bindings.add(arguments);

        SemanticPageRank metric = new SemanticPageRank();
        metric.init(data, bindings);
        

        
        // print explanations of all triples
        ExplanationPart part = metric.explain(0);

        System.out.println("Explanation:");
        
        Model m = ModelFactory.createDefaultModel();
        Graph g = m.getGraph();
        part.writeAsRDF(g);
        m.setNsPrefixes(PrefixMapping.Standard);
        m.setNsPrefix("expl", EXPL.getURI());
        m.write(System.out, "N3");
        // get data source
        
        System.out.println(ExplanationToHTMLRenderer.renderExplanationPart(part, data));
        
        System.out.println("Question: Is the sink <" + sink.toString() + "> trustworthy?\nAnswer: " + (metric.isAccepted(0)?"Yes.\n":"No.\n"));

    }
}
