// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/OracleCompatibility.java,v 1.2 2009/02/12 20:56:39 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db.specific;

import java.sql.Connection;
import java.sql.SQLException;

import com.hp.hpl.jena.shared.JenaException;

/** Provides compatibility methods for Oracle databases.
 *
 * @author Daryl McCullough, Architecture Technology Corporation
 * @author Haim Bar, Architecture Technology Corporation
 */
public class OracleCompatibility extends DbCompatibility {
//	private static final String DEFAULT_STRING_ASSIGNMENT = "DEFAULT ''";
	private static final String DEFAULT_NULL_ASSIGNMENT = "DEFAULT NULL";
//	private static final String URI_DATATYPE = "VARCHAR2(160)";
//	private static final String LITERAL_DATATYPE = "VARCHAR2(2000)";
//	private static final String LANGUAGE_DATATYPE = "VARCHAR2(10)";
//	private static final String DATATYPE_DATATYPE = "VARCHAR2(160)";
	
	protected static final String VARCHAR_NAME = "VARCHAR2";

	public OracleCompatibility(Connection connection) {
		super(connection);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#getGraphNamesTableNameForQueries()
	 */
	@Override
	public String getGraphNamesTableNameForQueries() {
		return graphNamesTableName.toUpperCase();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#createTables()
	 */
	public void createTables() {
		execute("CREATE TABLE " + graphNamesTableName +
			"(name " + URI_DATATYPE + " NOT NULL" + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ " PRIMARY KEY)");
		try {
			executeNoErrorHandling(
					"CREATE TABLE " + quadsTableName + " (" +
					"graph " + URI_DATATYPE + " NOT NULL"  + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ "," +
					"subject " +  URI_DATATYPE + " NOT NULL"  + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ "," +
					"predicate " + URI_DATATYPE + " NOT NULL"  + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ "," +
					"object " +  URI_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT + "," +
					"literal " + LITERAL_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT + "," +
					"lang " + LANGUAGE_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT + "," +
					"datatype " + DATATYPE_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT +")");
			execute("CREATE INDEX g_idx ON " + quadsTableName +
					" (graph, subject, predicate, object)");

		} catch (SQLException ex) {
			execute("DROP TABLE " + graphNamesTableName);
			execute("DROP INDEX g_idx");
			throw new JenaException(ex);
		}
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
