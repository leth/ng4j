// $Id: DBConnectionHelper.java,v 1.4 2008/09/03 16:37:28 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides database connections for the tests. Database access data must be
 * provided in this file.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class DBConnectionHelper {
	// For HSQLDB
	private static String URL = "jdbc:hsqldb:mem:ng4j";
	private static String DRIVER = "org.hsqldb.jdbcDriver";

	// For Postgres
	//private static String URL = "jdbc:postgresql:postgres:ng4j";
	//private static String DRIVER = "org.postgresql.Driver";

	// For MySQL
	//private static String URL = "jdbc:mysql://localhost/ng4j";
	//private static String DRIVER = "com.mysql.jdbc.Driver";

	private static String USER = "sa";
	private static String PW = "";

	static NamedGraphSetDB createNamedGraphSetDB() {
		return new NamedGraphSetDB(getConnection());
	}
	
	static void deleteNamedGraphSetTables() {
		try {
			Class.forName(DRIVER);
			NamedGraphSetDB.delete(getConnection());
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	static Connection getConnection() {
		try {
			Class.forName(DRIVER);
			return DriverManager.getConnection(URL, USER, PW);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);			
		}
	}
}
