// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/DbCompatibility.java,v 1.10 2010/09/22 19:22:34 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db.specific;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.sql.PreparedStatement;

import com.hp.hpl.jena.shared.JenaException;

/** Superclass for compatibility with different types of databases.
 *
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public abstract class DbCompatibility {
	
	// TODO Change code (and tests) to use PreparedStatement's exclusively
	// Then methods execute(String) and executeNoErrorHandling(String) can be eliminated

	public static final Pattern DEFAULT_ESCAPE_PATTERN = Pattern.compile("([\\\\'])");
	public static final String DEFAULT_ESCAPE_REPLACEMENT = "\\\\$1";
	
	protected static final String URI_DATATYPE_LENGTH = "255";
	protected static final String LITERAL_DATATYPE_LENGTH = "2000";
	protected static final String LANGUAGE_DATATYPE_LENGTH = "10";
	protected static final String DATATYPE_DATATYPE_LENGTH = "255";
	
	protected final String URI_DATATYPE;
	protected final String LITERAL_DATATYPE;
	protected final String LANGUAGE_DATATYPE;
	protected final String DATATYPE_DATATYPE;
	
	protected static final String INITIALIZATION_NOT_COMPLETED_ERROR_MSG = "";
	
	final Connection connection;
	String tablePrefix = null;
	String graphNamesTableName = null;
	String quadsTableName = null;
	
	public DbCompatibility( Connection connection ) {
		this.connection = connection;
		String varcharName = getVarcharName();
		URI_DATATYPE = getDatatype(varcharName, URI_DATATYPE_LENGTH);
		LITERAL_DATATYPE = getDatatype(varcharName, LITERAL_DATATYPE_LENGTH);
		LANGUAGE_DATATYPE = getDatatype(varcharName, LANGUAGE_DATATYPE_LENGTH);
		DATATYPE_DATATYPE = getDatatype(varcharName, DATATYPE_DATATYPE_LENGTH);
	}

	/** 
	 * Initializes the database compatibility mechanism. <br>
	 * 
	 * This method must be called after instantiating the class,
	 * before any methods relying on these variables are called.
	 * 
	 * @param tablePrefixIn
	 * @param graphNamesTableNameIn
	 * @param quadsTableNameIn
	 */
	public void initialize(String tablePrefixIn, 
			String graphNamesTableNameIn, String quadsTableNameIn) {
		this.tablePrefix = tablePrefixIn;
		this.graphNamesTableName = graphNamesTableNameIn;
		this.quadsTableName = quadsTableNameIn;
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

	/** This is only needed if the DbCompatibility implementation uses
	 * URI_DATATYPE, LITERAL_DATATYPE, LANGUAGE_DATATYPE, or DATATYPE_DATATYPE
	 * in a method such as createTables(). <p>
	 * 
	 * However a dummy implementation should not return <code>null</code>
	 * because the result is used to populate the aforementioned fields even
	 * if they are not used.
	 * 
	 * @return the correct term for a VARCHAR for the database, e.g. "VARCHAR", "varchar", "VARCHAR2"
	 */
	public abstract String getVarcharName();

	/** For variable-length varchars.  
	 * Given the term for the varchar and the desired length, 
	 * returns varcharName(length), e.g. VARCHAR(10).
	 * 
	 * @param varcharName the term for a varchar for the particular database, e.g. "VARCHAR", "varchar", "VARCHAR2"
	 * @param length the length of the datatype to create, e.g. 10, 100, 432
	 * @return varcharName with the length appended in parentheses.
	 */
	protected String getDatatype(String varcharName, String length) {
		return varcharName + "(" + length + ")";
	}

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
			throw new JenaException(ex);
		}
	}
	
	/** Executes the given SQL command.
	 * 
	 * @param sql The SQL command to execute.
	 */
	public void execute(PreparedStatement sql) {
		try {
			// TODO Consider using sql.executeUpdate for INSERT, DELETE, and UPDATE and executeQuery for SELECT statements.
			// These offer more detailed results, rather than the boolean returned by execute.
			// And also consider taking a look at the results and/or returning them.
			// QuadDB contains private method executeQuery, so if that is needed by anything here
			// then could move to here (and make public static)
			sql.execute();
		} catch (SQLException ex) {
			throw new JenaException(ex);
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

	/** Deletes the database tables created by method
	 * createTables().
	 */
	public void deleteTables() {
		execute("DROP TABLE " + graphNamesTableName);
		execute("DROP TABLE " + quadsTableName);
	}
	
	public void initializePreparedStatements() throws SQLException {
		// TODO Implement
		
	}

	/*
	 *  (c)   Copyright 2008 - 2010 Christian Bizer (chris@bizer.de)
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
