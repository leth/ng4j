// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/DerbyCompatibility.java,v 1.1 2009/01/20 16:13:39 jenpc Exp $ 
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

	public DerbyCompatibility(Connection connection) {
		super(connection);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#createTables()
	 */
	@Override
	public void createTables() {
		execute("CREATE TABLE app." + graphNamesTableName + " (name VARCHAR(160) , PRIMARY KEY(name)) ");
		try {
			executeNoErrorHandling(
					"CREATE TABLE app." + quadsTableName + " (" +
					"graph VARCHAR(160) NOT NULL," +
					"subject VARCHAR(160) NOT NULL," +
					"predicate VARCHAR(160) NOT NULL," +
					"object VARCHAR(160)," +
					"literal VARCHAR(2000)," +
					"lang VARCHAR(10)," +
					"datatype VARCHAR(160) )");
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
	public String getGraphNamesTableNameForQueries() {
		return graphNamesTableName.toUpperCase();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#setSchema(java.sql.Statement)
	 */
	@Override
	public void setSchema(Statement stmt) throws SQLException {
		stmt.execute("set schema app");
	}
}
