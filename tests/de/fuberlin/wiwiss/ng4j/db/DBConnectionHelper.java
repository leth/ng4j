// $Id: DBConnectionHelper.java,v 1.3 2007/03/06 14:09:05 zedlitz Exp $
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
	private static String URL = "jdbc:hsqldb:mem:ng4j";
	private static String USER = "sa";
	private static String PW = "";
	private static String DRIVER = "org.hsqldb.jdbcDriver";
	
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
