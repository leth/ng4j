package de.fuberlin.wiwiss.trust;

import java.io.StringReader;

import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;
import de.fuberlin.wiwiss.ng4j.triql.parser.ParseException;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;
import de.fuberlin.wiwiss.trust.Condition;

/**
 * Some {@link Condition} instances for use in test cases.
 *
 * @version $Id: ConditionFixture.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConditionFixture {
	
	public static Condition getCondition(String expression) {
	    TriQLParser p = new TriQLParser(new StringReader(expression));
	    try {
	        p.Expression();
	    } catch (ParseException ex) {
	        throw new RuntimeException(ex);
	    }
	    p.top();
	    return new Condition((Expr) p.top());	    	    
	}
}