/*
 * DataSourcesSummarizer.java
 *
 * Created on 5. April 2005, 09:57
 */

package de.fuberlin.wiwiss.trust.metric;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.trust.ExplanationPart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class DataSourcesSummary {
    
    private Vector sources;
    
    private ExplanationUtilities util = new ExplanationUtilities();
   
    
    /** Creates a new instance of DataSourcesSummarizer */
    public DataSourcesSummary() {
        sources = new Vector();
    }
    
    private Source getSource(Node source){
        Iterator it = sources.iterator();
        while(it.hasNext()){
            Source s = (Source) it.next();
            if(source.equals(s.getSourceNode())){
                return s;
            }
        }
        return null;
    }
    
    public void addDataSource(Node source, Node warrant, Node authority){
        Source s = getSource(source);
        if(s == null){
            s = new Source(source);
            sources.add(s);
        }
        s.addWarrantAuthority(warrant, authority);
    }
    
    public ExplanationPart summarize(){
        List summary = new ArrayList();
        summary.add(util.cl("" + sources.size() + " sources were used to calculate the metric."));
        ExplanationPart sourceSummary = new ExplanationPart(summary);
        
        List details = new ArrayList();
        details.add(util.cl("The Sources are: "));
        ExplanationPart sourceDetails = new ExplanationPart(details);
        Iterator ss = sources.iterator();
        while(ss.hasNext()){
            sourceDetails.addPart(((Source) ss.next()).summarize());
        }
        sourceSummary.setDetails(sourceDetails);
        
        return sourceSummary;
    }
    
    
    private class Authority{
        private Node authority;
     
        public Authority(Node authority){
            this.authority = authority;
        }
        
        public ExplanationPart summarize(){
            List expl = new ArrayList();
            expl.add(authority);
            
            ExplanationPart authoritySummary = new ExplanationPart(expl);
            
            return authoritySummary;
        }
        
        public Node getAuthorityNode() {
            return this.authority;
        }
    }
    
    private class Warrant{
        private Node warrant;
        private Vector authorities;
        
        public Warrant(Node warrant){
            this.warrant = warrant;
            authorities = new Vector();
        }
        
        public ExplanationPart summarize(){
            List expl = new ArrayList();
            expl.add(util.cl("Warrant graph "));
            expl.add(warrant);
            expl.add(util.cl(" auhtorized by the authorities: "));
            
            ExplanationPart warrantSummary = new ExplanationPart(expl);
            Iterator as = authorities.iterator();
            while(as.hasNext()){
                warrantSummary.addPart(((Authority) as.next()).summarize());
                
            }
            return warrantSummary;
        }
        
        public Node getWarrantNode(){
            return warrant;
        }
        
        private Authority getAuthority(Node authority){
            Iterator it = authorities.iterator();
            while(it.hasNext()){
                Authority a = (Authority) it.next();
                if(authority.equals(a)){
                    return a;
                }
            }
            return null;
        }

        public void addAuthority(Node authority){
            Authority a = getAuthority(authority);
            if(a == null){
                a = new Authority(authority);
                authorities.add(a);
            }
        }
    }
    
    private class Source{
        private Node source;
        private Vector warrants;
        
        public Source(Node source){
            this.source = source;
            this.warrants = new Vector();
        }
        
        public ExplanationPart summarize(){
            List expl = new ArrayList();
            expl.add(util.cl("Source graph "));
            expl.add(source);
            expl.add(util.cl(" asserted by the warrants: "));
            
            ExplanationPart sourceSummary = new ExplanationPart(expl);
            Iterator it = warrants.iterator();
            while(it.hasNext()){
                sourceSummary.addPart(((Warrant)it.next()).summarize());
            }
            return sourceSummary;
        }
        
        public Node getSourceNode(){
            return source;
        }
        
        private Warrant getWarrant(Node warrant){
            Iterator it = warrants.iterator();
            while(it.hasNext()){
                Warrant w = (Warrant) it.next();
                if(warrant.equals(w.getWarrantNode())){
                    return w;
                }
            }
            return null;
        }
        
        public void addWarrantAuthority(Node warrant, Node authority){
            Warrant w = getWarrant(warrant);
            if(w == null){
                w = new Warrant(warrant);
                warrants.add(w);
            }
            w.addAuthority(authority);
        }
    }
}
