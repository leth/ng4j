// $Id: DBConnectionHelper.java,v 1.1 2004/12/12 17:30:29 cyganiak Exp $
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
	private static String URL = "jdbc:mysql://localhost/ng4j";
	private static String USER = "root";
	private static String PW = "";
	private static String DRIVER = "com.mysql.jdbc.Driver";
	
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
