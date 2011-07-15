// $Id: DBConnectionHelper.java,v 1.7 2011/07/15 23:04:35 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides database connections for the tests. Database access data must be
 * provided in this file.  Defaults to HSQLDB since in-memory - doesn't require installation.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class DBConnectionHelper {
	
	// Supported database types
	public static enum Ng4jDatabaseType {
		APACHE_DERBY,
		HSQLDB,
		MYSQL,
		ORACLE,
		POSTGRESQL;
	}
	
	// User should change the constant here to the desired database.  (Uncomment the appropriate line and comment out the rest.)
	// Alternately we could create and require a constructor that sets the database.
	// Or could make it a parameter for the methods.
	// Both of the latter have the advantage of being able to automate tests for both databases.
	// For example, could run through all database-related tests with HSQLDB in-memory
	// and then repeat them all for Apache Derby in-memory.  
	// And users with additional databases installed could easily modify the tests
	// to run through those as well.
	// TODO Modify so that it supports multiple databases at once as described above
	// TODO See about a more efficient solution right now switching over all possibilities each time call any of the methods
	private static final Ng4jDatabaseType ng4jDatabaseType = 
//		Ng4jDatabaseType.APACHE_DERBY;
		Ng4jDatabaseType.HSQLDB;
//		Ng4jDatabaseType.MYSQL;
//		Ng4jDatabaseType.ORACLE;
//		Ng4jDatabaseType.POSTGRESQL;
	
	
//	/* Database URL and Driver */
//	private static final String URL;
//	private static final String DRIVER;
//	
//	/* Database username and password */
//	private static final String USER;
//	private static final String PW;
	
	// Note that even though the user and password shown are the same for all database types,
	// this won't necessarily typically be the case.  So we list them separately so that
	// users/testers can change them appropriately for their setup.
	
	// Settings for Apache Derby
	private static final String URL_FOR_DERBY_LOCAL = "jdbc:derby://localhost/ng4j;create=true";
	private static final String URL_FOR_DERBY_MEMORY = "jdbc:derby:memory:ng4j;create=true";
	private static final String URL_FOR_DERBY = URL_FOR_DERBY_MEMORY;
	private static final String DRIVER_FOR_DERBY = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String USER_FOR_DERBY = "sa";
	private static final String PW_FOR_DERBY = "";
	
	// Settings for HSQLDB (in-memory)
	private static final String URL_FOR_HSQLDB_LOCAL = "jdbc:hsqldb:file:ng4j";
	private static final String URL_FOR_HSQLDB_MEMORY = "jdbc:hsqldb:mem:ng4j";
	private static final String URL_FOR_HSQLDB = URL_FOR_HSQLDB_MEMORY;
	private static final String DRIVER_FOR_HSQLDB = "org.hsqldb.jdbcDriver";
	private static final String USER_FOR_HSQLDB = "sa";
	private static final String PW_FOR_HSQLDB = "";
	// See also http://jena.sourceforge.net/DB/hsql-howto.html
	// which explains that unlike Derby, HSQLDB uses the same driver but different URLs for in-memory vs. file
	
	// Settings for MySQL
	private static final String URL_FOR_MYSQL = "jdbc:mysql://localhost/ng4j";
	private static final String DRIVER_FOR_MYSQL = "com.mysql.jdbc.Driver";
	private static final String USER_FOR_MYSQL = "sa";
	private static final String PW_FOR_MYSQL = "";
	
	// Settings for Oracle
	// Note that Oracle 10g Express is not supported - Express is buggy.  Errors when run tests against it.
	private static final String URL_FOR_ORACLE = "jdbc:oracle:thin:@localhost:1521:orcl";
	private static final String DRIVER_FOR_ORACLE = "oracle.jdbc.driver.OracleDriver";
	private static final String USER_FOR_ORACLE = "sa";
	private static final String PW_FOR_ORACLE = "";
	
	// Settings for PostgreSQL - tested with library postgresql-8.2-504.jdbc3.jar
	private static final String URL_FOR_POSTGRESQL = "jdbc:postgresql:postgres:ng4j";
	private static final String DRIVER_FOR_POSTGRESQL = "org.postgresql.Driver";
	private static final String USER_FOR_POSTGRESQL = "sa";
	private static final String PW_FOR_POSTGRESQL = "";


	static NamedGraphSetDB createNamedGraphSetDB() {
		return new NamedGraphSetDB(getConnection());
	}
	
	static void deleteNamedGraphSetTables() {
		try {
			Class.forName(getDatabaseDriverString());
			NamedGraphSetDB.delete(getConnection());
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	static Connection getConnection() {
		try {
			Class.forName(getDatabaseDriverString());
			return DriverManager.getConnection(
					getDatabaseUrlString(), 
					getDatabaseUsername(), 
					getDatabasePassword());
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);			
		}
	}

	static String getDatabaseDriverString() {
		switch ( ng4jDatabaseType ) {
		case APACHE_DERBY :
			return DRIVER_FOR_DERBY;
		case HSQLDB :
			return DRIVER_FOR_HSQLDB;
		case MYSQL :
			return DRIVER_FOR_MYSQL;
		case ORACLE :
			return DRIVER_FOR_ORACLE;
		case POSTGRESQL :
			return DRIVER_FOR_POSTGRESQL;
		// purposefully do not include a default - if a new database-type is added,
		// we want this to fail so the code can be fixed
		}
		return null; // never reach since switch returns or fails
	}

	static String getDatabaseUrlString() {
		switch ( ng4jDatabaseType ) {
		case APACHE_DERBY :
			return URL_FOR_DERBY;
		case HSQLDB :
			return URL_FOR_HSQLDB;
		case MYSQL :
			return URL_FOR_MYSQL;
		case ORACLE :
			return URL_FOR_ORACLE;
		case POSTGRESQL :
			return URL_FOR_POSTGRESQL;
		// purposefully do not include a default - if a new database-type is added,
		// we want this to fail so the code can be fixed
		}
		return null; // never reach since switch returns or fails
	}

	static String getDatabaseUsername() {
		switch ( ng4jDatabaseType ) {
		case APACHE_DERBY :
			return USER_FOR_DERBY;
		case HSQLDB :
			return USER_FOR_HSQLDB;
		case MYSQL :
			return USER_FOR_MYSQL;
		case ORACLE :
			return USER_FOR_ORACLE;
		case POSTGRESQL :
			return USER_FOR_POSTGRESQL;
		// purposefully do not include a default - if a new database-type is added,
		// we want this to fail so the code can be fixed
		}
		return null; // never reach since switch returns or fails
	}

	static String getDatabasePassword() {
		switch ( ng4jDatabaseType ) {
		case APACHE_DERBY :
			return PW_FOR_DERBY;
		case HSQLDB :
			return PW_FOR_HSQLDB;
		case MYSQL :
			return PW_FOR_MYSQL;
		case ORACLE :
			return PW_FOR_ORACLE;
		case POSTGRESQL :
			return PW_FOR_POSTGRESQL;
		// purposefully do not include a default - if a new database-type is added,
		// we want this to fail so the code can be fixed
		}
		return null; // never reach since switch returns or fails
	}

}
