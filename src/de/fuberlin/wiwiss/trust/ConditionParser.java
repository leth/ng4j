package de.fuberlin.wiwiss.trust;

import java.io.StringReader;

import com.hp.hpl.jena.rdql.QueryException;
import com.hp.hpl.jena.shared.PrefixMapping;

import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;
import de.fuberlin.wiwiss.ng4j.triql.parser.ParseException;
import de.fuberlin.wiwiss.ng4j.triql.parser.SimpleNode;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;

/**
 * <p>Service for parsing a TriQL condition into a
 * {@link Condition} instance. The input is a string
 * like this:</p>
 *
 * <pre>
 * ?date >= '2005-01-01' AND ?date &lt; '2005-12-31'
 * </pre>
 * 
 * @version $Id: ConditionParser.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConditionParser {
    private String condition;
    private PrefixMapping prefixes;
    
    /**
     * @param condition The string representation of the condition
     * @param prefixes Namespace prefixes that may be used in the condition
     */
    public ConditionParser(String condition, PrefixMapping prefixes) {
        this.condition = condition;
        this.prefixes = prefixes;
    }
    
    /**
     * @return The parsed constraint
     * @throws TPLException on parse error
     */
    public Condition parse() {
        TriQLParser parser = new TriQLParser(new StringReader(this.condition));
        try {
            parser.Expression();
            SimpleNode parseTree = parser.top();
            parseTree.fixup(this.prefixes);
            return new Condition((Expr) parseTree);
        } catch (ParseException e) {
            throw new TPLException("Error in condition '" + this.condition +
                    "': " + e.getMessage());
        } catch (QueryException e) {
            throw new TPLException(e.getMessage());
        }
    }
}