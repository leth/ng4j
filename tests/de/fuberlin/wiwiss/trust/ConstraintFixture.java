package de.fuberlin.wiwiss.trust;

import java.io.StringReader;

import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;
import de.fuberlin.wiwiss.ng4j.triql.parser.ParseException;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;
import de.fuberlin.wiwiss.trust.Constraint;

/**
 * Some {@link Constraint} instances for use in test cases.
 *
 * @version $Id: ConstraintFixture.java,v 1.1 2005/03/21 00:23:24 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConstraintFixture {
	
	public static Constraint getConstraint(String expression) {
	    TriQLParser p = new TriQLParser(new StringReader(expression));
	    try {
	        p.Expression();
	    } catch (ParseException ex) {
	        throw new RuntimeException(ex);
	    }
	    p.top();
	    return new Constraint((Expr) p.top());	    	    
	}
}