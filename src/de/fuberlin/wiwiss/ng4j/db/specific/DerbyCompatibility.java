// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/DerbyCompatibility.java,v 1.2 2009/02/12 20:56:39 jenpc Exp $ 
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
}
