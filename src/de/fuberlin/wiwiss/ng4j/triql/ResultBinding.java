// $Id: ResultBinding.java,v 1.1 2004/12/17 01:44:29 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.triql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.ng4j.triql.legacy.Value;
import de.fuberlin.wiwiss.ng4j.triql.legacy.WorkingVar;

/**
 * A set of variable values. Used by the TriQL parser to
 * evaluate constraints. The TriQL parser is forked off
 * the RDQL parser from Jena 2.1. The ResultBinding class
 * was later changed massively in Jena. We introduce our
 * own minimal ResultBinding class to be independent of
 * these changes.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ResultBinding {
	private Map namesToValues = new HashMap();

	public void add(String varName, RDFNode varValue) {
		this.namesToValues.put(varName, varValue);
	}
	
	public RDFNode get(String varName) {
		return (RDFNode) this.namesToValues.get(varName);
	}

	public Value getValue(String varName) {
		RDFNode arg = get(varName);
		if (arg == null) {
			return null;
		}
		if (arg instanceof Resource) {
			WorkingVar w = new WorkingVar();
			w.setRDFResource((Resource) arg);
			return w;
		}
		if (arg instanceof Literal) {
			WorkingVar w = new WorkingVar();
			w.setRDFLiteral((Literal) arg);
			return w;
		}

		throw new JenaException(
				"ResultBinding: unexpected object class: " +
				arg.getClass().getName());
	}
	
	public Set getTriples() {
		return Collections.EMPTY_SET;
	}
}
