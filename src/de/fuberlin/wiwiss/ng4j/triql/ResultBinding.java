// $Id: ResultBinding.java,v 1.2 2004/12/17 10:23:08 cyganiak Exp $
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

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
 *   All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */