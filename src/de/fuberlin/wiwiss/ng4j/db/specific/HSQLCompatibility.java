// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/db/specific/HSQLCompatibility.java,v 1.6 2011/07/15 23:40:53 jenpc Exp $
package de.fuberlin.wiwiss.ng4j.db.specific;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.hp.hpl.jena.shared.JenaException;

/** Provides compatibility methods for HSQL databases.
 *
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class HSQLCompatibility extends DbCompatibility {

	private static final String DEFAULT_NULL_ASSIGNMENT = "DEFAULT NULL";
	
	public static final Pattern HSQLDB_ESCAPE_PATTERN = Pattern.compile("([\\'])");
	public static final String HSQLDB_ESCAPE_REPLACEMENT = "$1$1";

	protected static final String VARCHAR_NAME = "VARCHAR";

	public HSQLCompatibility(Connection connection) {
		super(connection);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#createTables()
	 */
	@Override
	public void createTables() {
		execute("CREATE TABLE " + graphNamesTableName + 
				//" (name VARCHAR , PRIMARY KEY(name)) ");
				"(name " + URI_DATATYPE + " NOT NULL" + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ " PRIMARY KEY)");
		try {
			executeNoErrorHandling(
					"CREATE TABLE " + quadsTableName + " (" +
					//"graph VARCHAR NOT NULL," +
					"graph " + URI_DATATYPE + " NOT NULL"  + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ "," +
					//"subject VARCHAR NOT NULL," +
					"subject " +  URI_DATATYPE + " NOT NULL"  + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ "," +
					//"predicate VARCHAR NOT NULL," +
					"predicate " + URI_DATATYPE + " NOT NULL"  + /*" " + DEFAULT_STRING_ASSIGNMENT +*/ "," +
					//"object VARCHAR," +
					"object " +  URI_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT + "," +
					//"literal LONGVARCHAR," +
					"literal " + LITERAL_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT + "," +
					//"lang VARCHAR," +
					"lang " + LANGUAGE_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT + "," +
					//"datatype VARCHAR )");
					"datatype " + DATATYPE_DATATYPE + " " + DEFAULT_NULL_ASSIGNMENT +")");
		} catch (SQLException ex) {
			execute("DROP TABLE " + graphNamesTableName);
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
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#getEscapePattern()
	 */
	@Override
	public Pattern getEscapePattern() {
		return HSQLDB_ESCAPE_PATTERN;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#getEscapeReplacement()
	 */
	@Override
	public String getEscapeReplacement() {
		return HSQLDB_ESCAPE_REPLACEMENT;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#getVarcharName()
	 */
	@Override
	public String getVarcharName() {
		return VARCHAR_NAME;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility#preparedStatementsRequireTablesToExist()
	 */
	@Override
	public boolean preparedStatementsRequireTablesToExist() {
		// Instead use the version that creates the tables because
		// HSQLDB, at least in in-memory mode, has this problem otherwise:
		// java.lang.RuntimeException: Unable to initialize prepared statements for database 
		// de.fuberlin.wiwiss.ng4j.db.specific.HSQLCompatibility.  
		// Error code = -22: Table not found in statement [SELECT COUNT(*) FROM ng4j_test_graphs]
		
		return true;
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
