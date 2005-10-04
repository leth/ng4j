package de.fuberlin.wiwiss.trust;

import java.io.StringReader;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdql.QueryException;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.triql.GraphPattern;
import de.fuberlin.wiwiss.ng4j.triql.parser.ParseException;
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_GraphPattern;
import de.fuberlin.wiwiss.ng4j.triql.parser.Q_TriplePattern;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;

/**
 * <p>Service for parsing a TriQL Graph Pattern into a
 * {@link GraphPattern} instance. Works by invoking the
 * right part of the TriQL parser. The input is a string
 * like this:</p>
 *
 * <pre>
 * ?warrant (?GRAPH swp:assertedBy ?warrant .
 *           ?warrant swp:authority ?USER)
 * </pre>
 * 
 * @version $Id: GraphPatternParser.java,v 1.3 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class GraphPatternParser {
    private String pattern;
    private PrefixMapping prefixes;
    
    /**
     * Sets up a new graph pattern parser.
     * @param pattern The string representation of the graph pattern
     * @param prefixes Namespace prefixes that may be used in the pattern 
     */
    public GraphPatternParser(String pattern, PrefixMapping prefixes) {
        this.pattern = pattern;
        this.prefixes = prefixes;
    }
    
    /**
     * @return The parsed graph pattern
     * @throws TPLException on parse error
     */
    public GraphPattern parse() {
        TriQLParser parser = new TriQLParser(new StringReader(this.pattern));
        try {
            parser.GraphPattern();
            Q_GraphPattern parseTree = (Q_GraphPattern)parser.top();
            parseTree.fixup(this.prefixes);
            return buildGraphPatternFromParseTree(parseTree);
        } catch (ParseException e) {
            throw new TPLException("Error in graph pattern:\n" + this.pattern + "\n" + e.getMessage());
        } catch (QueryException e) {
            throw new TPLException(e.getMessage());
        }
    }
    
    private GraphPattern buildGraphPatternFromParseTree(Q_GraphPattern parseTree) {
        int n = parseTree.jjtGetNumChildren() ;
        if (n == 0) {
        		throw new TPLException("Error in Graph Pattern syntax: no children in Q_GraphPattern parse tree");
        }
        int i = 0;
        Node graphName;
        if (parseTree.jjtGetChild(0) instanceof Q_TriplePattern) {
        		graphName = Node.ANY;
        } else {
        		i++;
        		graphName = TriQLHelper.toRDFNode(parseTree.jjtGetChild(0));
        }
        GraphPattern graphPattern = new GraphPattern(graphName);
        for ( ; i < n ; i++ )
        {
            Q_TriplePattern tp = (Q_TriplePattern)parseTree.jjtGetChild(i) ;
            if ( tp.jjtGetNumChildren() != 3 )
                throw new QueryException("Triple pattern has "+tp.jjtGetNumChildren()+" children") ;

            Node s = TriQLHelper.toRDFNode(tp.jjtGetChild(0)) ;
            Node p = TriQLHelper.toRDFNode(tp.jjtGetChild(1)) ;
            Node o = TriQLHelper.toRDFNode(tp.jjtGetChild(2)) ;
            graphPattern.addTriplePattern(new Triple(s, p, o)) ;
        }
        return graphPattern;
    }
}