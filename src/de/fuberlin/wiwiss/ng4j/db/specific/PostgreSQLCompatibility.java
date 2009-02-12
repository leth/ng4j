// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/PostgreSQLCompatibility.java,v 1.2 2009/02/12 20:56:39 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db.specific;

import java.sql.Connection;
import java.sql.SQLException;

import com.hp.hpl.jena.shared.JenaException;

/** Provides compatibility methods for PostgreSQL databases.
 *
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class PostgreSQLCompatibility extends DbCompatibility {

	protected static final String VARCHAR_NAME = "text";

	public PostgreSQLCompatibility(Connection connection) {
		super(connection);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#createTables()
	 */
	public void createTables() {
		execute("CREATE TABLE " + graphNamesTableName + " (" +
		"name text PRIMARY KEY default '')");
		try {
			executeNoErrorHandling(
					"CREATE TABLE " + quadsTableName + " (" +
					"graph text NOT NULL default ''," +
					"subject text NOT NULL default ''," +
					"predicate text NOT NULL default ''," +
					"object text default NULL," +
					"literal text," +
					"lang text default NULL," +
					"datatype text default NULL)");
			} catch (SQLException ex) {
				execute("DROP TABLE " + graphNamesTableName);
				throw new JenaException(ex);
		}
		execute("CREATE INDEX " + tablePrefix + "_graph_index ON " +
				quadsTableName + " (graph)");
		execute("CREATE INDEX " + tablePrefix + "_subject_index ON " +
				quadsTableName + " (subject)");
		execute("CREATE INDEX " + tablePrefix + "_predicate_index ON " +
				quadsTableName + " (predicate)");
		execute("CREATE INDEX " + tablePrefix + "_object_index ON " +
				quadsTableName + " (object)");
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#getVarcharName()
	 */
	@Override
	public String getVarcharName() {
		return VARCHAR_NAME;
	}

	/*
	 *  (c)   Copyright 2008 Christian Bizer (chris@bizer.de)
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
}
