package de.fuberlin.wiwiss.trust;

import java.io.StringReader;

import de.fuberlin.wiwiss.ng4j.triql.parser.Expr;
import de.fuberlin.wiwiss.ng4j.triql.parser.ParseException;
import de.fuberlin.wiwiss.ng4j.triql.parser.TriQLParser;
import de.fuberlin.wiwiss.trust.ExpressionConstraint;

/**
 * Some {@link ExpressionConstraint} instances for use in test cases.
 *
 * TODO: Move to trust.helpers package
 * 
 * @version $Id: ConstraintFixture.java,v 1.3 2005/10/11 20:51:35 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConstraintFixture {
	
	public static ExpressionConstraint getConstraint(String expression) {
	    TriQLParser p = new TriQLParser(new StringReader(expression));
	    try {
	        p.Expression();
	    } catch (ParseException ex) {
	        throw new RuntimeException(ex);
	    }
	    p.top();
	    return new ExpressionConstraint((Expr) p.top());	    	    
	}
}