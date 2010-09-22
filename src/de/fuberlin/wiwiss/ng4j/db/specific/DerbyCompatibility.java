// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/DerbyCompatibility.java,v 1.6 2010/09/22 19:26:25 jenpc Exp $ 
package de.fuberlin.wiwiss.ng4j.db.specific;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.hp.hpl.jena.shared.JenaException;

/** Provides compatibility methods for Apache Derby databases. <p>
 *
 * Works with both the Embedded driver and Client driver for Derby.
 * 
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class DerbyCompatibility extends DbCompatibility {
	/* The default schema in Apache Derby databases is "app".
	 */

	protected static final String VARCHAR_NAME = "VARCHAR";

	public DerbyCompatibility(Connection connection) {
		super(connection);
		
		// If a schema for the username does not exist yet, then that user can't create prepared statements.
		// Specifically, when try to create a prepared statement, get error 
		//     Schema 'USERNAME' does not exist
		// (Where USERNAME is the name of the user.)
		// So this happens if even a single statement is prepared in DbCompatibilty 
		// method initializePreparedStatements and a schema does not yet exist for the username.
		
		// Per http://db.apache.org/derby/faq.html#schema_exist
		//
		// 5.3. Why do I get the error 'schema does not exist'?
		// The current schema for any connection defaults to a schema corresponding to the user name. 
		// If no user name is supplied then the user name (and hence current schema) defaults to APP.
		//
		// However even though the current schema is set to the user name, that schema may not exist. 
		// A schema is only created by CREATE SCHEMA or creating an object (table etc.) in that schema (this is implicit schema creation). 
		
		// Ideally we would create the schema for the username directly, as follows:
		// 	String username;
		// 	try {
		// 		username = connection.getClientInfo("ClientUser");
		// 	} catch (SQLException ex) {
		// 		throw new RuntimeException("Unable to get the username of the connection to Derby database: " + ex.getLocalizedMessage());
		// 	}
		// 	if ( username == null ) {
		//		throw new RuntimeException("Username for Derby database connection is null.");
		// 	}
		//	execute("CREATE SCHEMA " + username);
		
		// HOWEVER
		// Per http://db.apache.org/derby/javadoc/engine/org/apache/derby/client/am/LogicalConnection40.html#getClientInfo%28java.lang.String%29
		// getClientInfo forwards to physicalConnection_. Always returns a null String since Derby does not support ClientInfoProperties. 
		// This means that we can't get the username in the proper way (see commented code above).
		
		// Therefore (per the apache FAQ above) the alternative is to create an object.
		// Thus we create and delete an object just so the PreparedStatement creation will work.
		// This is not ideal.
		
		// TODO Find better way to create schema for user.  See above explaining why creating indirectly by creating another object.
		String dummyTablename = "tempgarbagegook";
		execute("CREATE TABLE " + dummyTablename + " (name " + URI_DATATYPE + " , PRIMARY KEY(name)) ");
		execute("DROP TABLE " + dummyTablename);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#createTables()
	 */
	@Override
	public void createTables() {
		execute("CREATE TABLE app." + graphNamesTableName + " (name " + URI_DATATYPE + " , PRIMARY KEY(name)) ");
		try {
			executeNoErrorHandling(
					"CREATE TABLE app." + quadsTableName + " (" +
					"graph " + URI_DATATYPE + " NOT NULL," +
					"subject " + URI_DATATYPE + " NOT NULL," +
					"predicate " + URI_DATATYPE + " NOT NULL," +
					"object " + URI_DATATYPE + "," +
					"literal " + LITERAL_DATATYPE + "," +
					"lang " + LANGUAGE_DATATYPE + "," +
					"datatype " + DATATYPE_DATATYPE + " )");
			execute("CREATE INDEX g_idx ON " + quadsTableName +
					" (graph, subject, predicate, object)");
		} catch (SQLException ex) {
			execute("DROP TABLE " + graphNamesTableName);
			execute("DROP INDEX g_idx");
			throw new JenaException(ex);
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#getGraphNamesTableNameForQueries()
	 */
	@Override
	public String getGraphNamesTableNameForQueries() {
		return graphNamesTableName.toUpperCase();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#getVarcharName()
	 */
	@Override
	public String getVarcharName() {
		return VARCHAR_NAME;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#setSchema(java.sql.Statement)
	 */
	@Override
	public void setSchema(Statement stmt) throws SQLException {
		stmt.execute("set schema app");
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#initializePreparedStatements()
	 */
	@Override
	public void initializePreparedStatements() throws SQLException {
		
		// When preparing a PreparedStatement, Apache Derby
		// appears to try to check the statement when it is created.
		// The error for creating dropGraphnamesTableStmt, for example, is:
		//   'DROP TABLE' cannot be performed on 'NG4J_TEST_GRAPHS' because it does not exist.
		
		// This work-around creates the tables if they don't already exist,
		// and then after creating the prepared statements, deletes the tables if they didn't exist previously.
		
		boolean tablesAlreadyExist = true;
		if ( ! tablesExist() ) {
			// Tables don't already exist; create them so Derby won't 
			// complain when creating statements referring to these tables.
			tablesAlreadyExist = false;
			createTables();
		}
		
		super.initializePreparedStatements();
		
		if ( ! tablesAlreadyExist ) {
			// Now that initialization is complete,
			// since the tables did not exist previously, delete them.
//			try {
				deleteTables();
//			} catch (SQLException ex) {
//				throw new JenaException(ex);
//			}
		}
	}
}

/*
 *  (c) Copyright 2009 - 2010 Christian Bizer (chris@bizer.de)
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
