// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/DbCompatibility.java,v 1.2 2009/01/20 16:14:35 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db.specific;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import com.hp.hpl.jena.shared.JenaException;

/** Superclass for compatibility with different types of databases.
 *
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public abstract class DbCompatibility {

	public static final Pattern DEFAULT_ESCAPE_PATTERN = Pattern.compile("([\\\\'])");
	public static final String DEFAULT_ESCAPE_REPLACEMENT = "\\\\$1";
	
	protected static final String INITIALIZATION_NOT_COMPLETED_ERROR_MSG = "";
	
	final Connection connection;
	String tablePrefix = null;
	String graphNamesTableName = null;
	String quadsTableName = null;
	
	public DbCompatibility( Connection connection ) {
		this.connection = connection;
	}

	/** Initializes the database compatibility mechanism. <br>
	 * 
	 * This method must be called after instantiating the class,
	 * before any methods relying on these variables are called.
	 * 
	 * @param tablePrefix
	 * @param graphNamesTableName
	 * @param quadsTableName
	 */
	public void initialize(String tablePrefix, 
			String graphNamesTableName, String quadsTableName) {
		this.tablePrefix = tablePrefix;
		this.graphNamesTableName = graphNamesTableName;
		this.quadsTableName = quadsTableName;
	}

	/** Creates the following 7 required database tables: <br>
	 *    * graph <br>
	 *    * subject <br>
	 *    * predicate <br>
	 *    * object <br>
	 *    * literal <br>
	 *    * lang <br>
	 *    * datatype <br>
	 * and the tables named <code>quadsTableName</code>
	 * and <code>graphNamesTableName</code>. 
	 * 
	 * <p>The <code>initialize</code> method must be called
	 * before this method is called.
	 */
	public abstract void createTables();
	
	/** Returns the graphNamesTableName to be used for queries.
	 * 
	 * <p>The <code>initialize</code> method must be called
	 * before this method is called.
	 * 
	 * <p>NOTE: subclasses should override if appropriate. 
	 * In particular, some databases require that the name
	 * be entirely upper case.
	 * 
	 * @return the graphNamesTableName to be used for queries
	 */
	protected String getGraphNamesTableNameForQueries() {
		return graphNamesTableName;
	}

	/** Checks that table creation has already been done.
	 * 
	 * In order to do so this method checks for the existence
	 * of the table named <code>graphNamesTableName</code>.
	 * 
	 * <p>The <code>initialize</code> method must be called
	 * before this method is called.
	 * 
	 * <p>NOTE: subclasses should override if appropriate.
	 * 
	 * @return <code>true</code> if table creation has been completed, <code>false</code> otherwise.
	 */
	public boolean tablesExist() {
		try {
			ResultSet results = this.connection.getMetaData().getTables(
					null, null, getGraphNamesTableNameForQueries(), null);
			return results.next();
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}

	/** NOTE: subclasses should override if appropriate.
	 * 
	 * @return a pattern matching special characters that should be replaced in database literals in order to avoid SQL injection.
	 */
	public Pattern getEscapePattern() {
		return DEFAULT_ESCAPE_PATTERN;
	}

	/** NOTE: subclasses should override if appropriate.
	 * 
	 * @return the string used to escape special characters in database literals in order to avoid SQL injection.
	 */
	public String getEscapeReplacement() {
		return DEFAULT_ESCAPE_REPLACEMENT;
	}

	/** Executes the given SQL command without error handling.
	 * 
	 * @param sql The SQL command to execute.
	 * @throws SQLException 
	 */
	protected void executeNoErrorHandling(String sql) throws SQLException {
		Statement stmt = this.connection.createStatement();
		setSchema(stmt);
		stmt.execute(sql);
		stmt.close();
	}

	/** NOTE: subclasses should override if appropriate. <p>
	 * The default implementation does nothing.<p>
	 * 
	 * Set the schema, or do any other necessary processing
	 * prior to continued execution. <p>
	 * 
	 * If this method is used for a more general purpose
	 * than setting the schema
	 * then it can be renamed appropriately.
	 * 
	 * @param stmt The statement being used to execute SQL commands.
	 * @throws SQLException
	 */
	public void setSchema(Statement stmt) throws SQLException {
	}

	/** Executes the given SQL command.
	 * 
	 * @param sql The SQL command to execute.
	 */
	public void execute(String sql) {
		try {
			executeNoErrorHandling(sql);
		} catch (SQLException ex) {
			if (ex.getErrorCode() != 1062) {
				throw new JenaException(ex);
			}
		}
	}

	/** Closes the database connection.
	 */
	public void close() {
		try {
			this.connection.close();
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}

	/**
	 * @return the database connection
	 */
	public Connection getConnection() {
		return connection;
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
