// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/DbCompatibility.java,v 1.12 2010/09/27 19:13:57 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db.specific;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.sql.PreparedStatement;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.JenaException;

/** Superclass for compatibility with different types of databases.
 *
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public abstract class DbCompatibility {
	
	// TODO Change code (and tests) to use PreparedStatement's exclusively
	// Then methods execute(String) and executeNoErrorHandling(String) can be eliminated
	// Well, that would be the ideal.  In reality, there's a problem because for certain
	// databases, to create a PreparedStatement, the tables referenced in it need to exist,
	// so first need to create the tables, so can't do that using PreparedStatement's.

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
	
	// For each PreparedStatement added here, it should be
	// * initialized in initializePreparedStatements
	// * added to shouldBeSaved()
	// * have a new getter method added for it
	PreparedStatement containsAnyGraphStmt;
	PreparedStatement containsAnyQuadStmt;
	PreparedStatement containsGraphNameStmt;
	PreparedStatement deleteAllGraphsStmt;
	PreparedStatement deleteGraphStmt;
	PreparedStatement dropGraphNamesTableStmt;
	PreparedStatement dropQuadsTableStmt;
	PreparedStatement insertGraphNameStmt;
	PreparedStatement insertQuadsTableStmt;
	PreparedStatement listGraphNamesStmt;
	
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
	 * @param stmt The SQL command to execute.
	 */
	public void execute(PreparedStatement stmt) {
		try {
			// TODO Consider using sql.executeUpdate for INSERT, DELETE, and UPDATE and executeQuery for SELECT statements.
			// These offer more detailed results, rather than the boolean returned by execute.
			// And also consider taking a look at the results and/or returning them.
			// QuadDB contains private method executeQuery, so if that is needed by anything here
			// then could move to here (and make public static)
			stmt.execute();
		} catch (SQLException ex) {
			if ( ( stmt != null ) && shouldBeSaved(stmt) ) {
				try {
					stmt.close();
				} catch (SQLException ex2) {
					throw new JenaException(ex);
				}
			}
			throw new JenaException(ex);
		}
	}
	
	/** Executes the given SELECT SQL command.
	 * 
	 * @param stmt The SQL SELECT command to execute.
	 * 
	 * @return the ResultSet object generated by the query. 
	 */
	public ResultSet executeQuery(PreparedStatement stmt) {
		try {
			return stmt.executeQuery();
		} catch (SQLException ex) {
			if ( ( stmt != null ) && shouldBeSaved(stmt) ) {
				try {
					stmt.close();
				} catch (SQLException ex2) {
					throw new JenaException(ex);
				}
			}
			throw new JenaException(ex);
		}
	}

//	/** Executes the given SELECT SQL command.
//	 * 
//	 * @param sql The SQL command to execute.
//	 * 
//	 * @return the ResultSet object generated by the query. 
//	 */
//	public int executUpdate(PreparedStatement sql) {
//		try {
//			return sql.executeUpdate();
//		} catch (SQLException ex) {
//			throw new JenaException(ex);
//		}
//	}

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
		try {
			getDropGraphNamesTableStmt().execute();
			getDropQuadsTableStmt().execute();
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}

	/** Subclasses should override if the particular database
	 * requires that any items mentioned in a PreparedStatement
	 * exist before the statement is created.
	 * 
	 * @return whether the creation of a PreparedStatement requires items, such as tables, mentioned in the statement to already exist.
	 */
	public boolean preparedStatementsRequireTablesToExist() {
		return false;
	}

	/** Initializes the SQL statements to be used repeatedly with this database.
	 * 
	 */
	public void initializePreparedStatements() {
		
		// When preparing a PreparedStatement, certain databases, 
		// particularly when used in in-memory mode
		// appear to try to check the statement when it is created.
		// If a table mentioned in the statement does not yet exist, 
		// then an error occurs.
		
		// This work-around creates the tables if they don't already exist,
		// and then after creating the prepared statements, deletes the tables if they didn't exist previously.
		
		boolean tablesAlreadyExist = true;
		if ( preparedStatementsRequireTablesToExist() && ( ! tablesExist() ) ) {
			// Tables don't already exist; create them so certain databases won't 
			// complain when creating statements referring to these tables.
			tablesAlreadyExist = false;
			createTables();
		}
		
		try {
			containsAnyGraphStmt = connection.prepareStatement(
					"SELECT COUNT(*) FROM " + graphNamesTableName);
			
			containsAnyQuadStmt = connection.prepareStatement(
					"SELECT COUNT(*) FROM " + quadsTableName);
			
			containsGraphNameStmt = connection.prepareStatement(
					"SELECT COUNT(*) FROM " + graphNamesTableName + " WHERE name=?");
			
			deleteAllGraphsStmt = connection.prepareStatement(
					"DELETE FROM " + graphNamesTableName);
			
			deleteGraphStmt = connection.prepareStatement(
					"DELETE FROM " + graphNamesTableName + " WHERE name=?");
			
			dropGraphNamesTableStmt = connection.prepareStatement(
					"DROP TABLE " + graphNamesTableName);
			
			dropQuadsTableStmt = connection.prepareStatement(
					"DROP TABLE " + quadsTableName);
			
			insertGraphNameStmt = connection.prepareStatement(
					"INSERT INTO " + graphNamesTableName + " VALUES (?)");
			
			insertQuadsTableStmt = connection.prepareStatement(
					"INSERT INTO " + quadsTableName +
					" (graph, subject, predicate, object, literal, lang, datatype) VALUES (" +
					"  ?,     ?,       ?,         ?,      ?,       ?,    ?)"
					);
			
			listGraphNamesStmt = connection.prepareStatement(
					"SELECT name FROM " + graphNamesTableName);
			
		} catch (SQLException ex) {
			throw new RuntimeException("Unable to initialize prepared statements for database " 
					+ getClass().getName() + ".  Error code = " + ex.getErrorCode() + ": " + ex.getLocalizedMessage() );
		} finally {
			
			if ( ! tablesAlreadyExist ) {
				// Now that initialization is complete,
				// since the tables did not exist previously, delete them.
//				try {
					deleteTables();
//				} catch (SQLException ex) {
//					throw new JenaException(ex);
//				}
			}
		}
	}

	public boolean shouldBeSaved(PreparedStatement preparedStatement) {
		// PreparedStatements which are initialized at the beginning
		// should not be closed after use because then can't be re-used.
		// Provide a way to check the status of a PreparedStatement.
		// Later if have a better way to track what should be done when
		// cleaning-up, this may not be necessary.
		if ( preparedStatement.equals(containsAnyGraphStmt) ) {
			return true;
		} else if ( preparedStatement.equals(containsAnyQuadStmt) ) {
			return true;
		} else if ( preparedStatement.equals(containsGraphNameStmt) ) {
			return true;
		} else if ( preparedStatement.equals(deleteAllGraphsStmt) ) {
			return true;
		} else if ( preparedStatement.equals(deleteGraphStmt) ) {
			return true;
		} else if ( preparedStatement.equals(dropGraphNamesTableStmt) ) {
			return true;
		} else if ( preparedStatement.equals(dropQuadsTableStmt) ) {
			return true;
		} else if ( preparedStatement.equals(insertGraphNameStmt) ) {
			return true;
		} else if ( preparedStatement.equals(insertQuadsTableStmt) ) {
			return true;
		} else if ( preparedStatement.equals(listGraphNamesStmt) ) {
			return true;
		}
		
		return false;
	}

	public PreparedStatement getContainsAnyGraphStmt() {
		return containsAnyGraphStmt;
	}

	public PreparedStatement getContainsAnyQuadStmt() {
		return containsAnyQuadStmt;
	}

	public PreparedStatement getContainsGraphNameStmt(Node graphName) throws SQLException {
		containsGraphNameStmt.setString(1, graphName.getURI());
		return containsGraphNameStmt;
	}

	public PreparedStatement getDeleteAllGraphsStmt() {
		return deleteAllGraphsStmt;
	}

	public PreparedStatement getDeleteGraphStmt(Node graphName) throws SQLException {
		deleteGraphStmt.setString(1, graphName.getURI());
		return deleteGraphStmt;
	}

	public PreparedStatement getDropGraphNamesTableStmt() {
		return dropGraphNamesTableStmt;
	}

	public PreparedStatement getDropQuadsTableStmt() {
		return dropQuadsTableStmt;
	}

	public PreparedStatement getInsertGraphNameStmt(Node graphName) throws SQLException {
		insertGraphNameStmt.setString(1, graphName.getURI());
		return insertGraphNameStmt;
	}

	public PreparedStatement getInsertQuadsTableStmt() {
		return insertQuadsTableStmt;
	}

	public PreparedStatement getListGraphNamesStmt() {
		return listGraphNamesStmt;
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
