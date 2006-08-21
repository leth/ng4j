// $Id: QuadDB.java,v 1.7 2006/08/21 20:20:50 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.NullIterator;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * <p>Database persistence for a set of Quads and a set of graph names, with a
 * {@link Node} based interface. The class manages two collections of objects:
 * a set of quads, and a set of graph names. The latter is needed because we
 * want to support empty graphs.</p>
 * 
 * <p>All operations but {@link #insert} allow {@link Node#ANY} wildcards.</p>
 *
 * <p>The implementation is na•ve: a <tt>graphs</tt> table and a <tt>quads</tt>
 * table. A table prefix can be supplied in order to support multiple QuadDBs
 * in a single database.</p>
 * 
 * TODO: Factor out DB-specific stuff, e.g. table creation.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QuadDB {

	private Pattern escapePattern = null;
	private String escapeReplacement = null;
	
	private String tablePrefix;
	private Connection connection;
	private int type;

	public static final int HSQL_TYPE = 0;
	public static final int MYSQL_TYPE = 1;
	public static final int POSTGRESQL_TYPE = 2;

	public QuadDB(Connection connection, String tablePrefix) {
		this.connection = connection;
		this.setDBtype();
		this.setEscapePattern();
		this.tablePrefix = escape(tablePrefix);
	}
	
	public void insert(Node g, Node s, Node p, Node o) {
		if (find(g, s, p, o).hasNext()) {
			return;
		}
		String sql = "INSERT INTO " + getQuadsTableName() +
				" (graph, subject, predicate, object, literal, lang, datatype) VALUES (" +
				"'" + escape(g.getURI()) + "', " +
				"'" + escapeResource(s) + "', " +
				"'" + escape(p.getURI()) + "', " +
				getObjectColumn(o) + ", " +
				getLiteralColumn(o) + ", " +
				getLangColumn(o) + ", " +
				getDatatypeColumn(o) + ")";
		execute(sql);
	}
	
	public void delete(Node g, Node s, Node p, Node o) {
		String sql = "DELETE FROM " + getQuadsTableName() + " " +
				getWhereClause(g, s, p, o);
		execute(sql);
	}
	
	public Iterator find(Node g, Node s, Node p, Node o) {
		if (g == null) {
			g = Node.ANY;
		}
		if (s == null) {
			s = Node.ANY;
		}
		if (p == null) {
			p = Node.ANY;
		}
		if (o == null) {
			o = Node.ANY;
		}
		if ((!g.isURI() && !g.equals(Node.ANY))
				|| (!s.isURI() && !s.isBlank() && !s.equals(Node.ANY))
				|| (!p.isURI() && !p.equals(Node.ANY))) {
			return new NullIterator();
		}
		String sql = "SELECT graph, subject, predicate, object, literal, lang, datatype " +
				"FROM " + getQuadsTableName() + " " +
				getWhereClause(g, s, p, o);
		final ResultSet results = executeQuery(sql);
		return new Iterator() {
			private boolean hasReadNext = false;
			private Quad current = null;
			private Quad next = null;

			public boolean hasNext() {
				if (!this.hasReadNext) {
					try {
						if (results.next()) {
							this.next = makeQuad();
						} else {
							this.next = null;
						}
					} catch (SQLException ex) {
						throw new JenaException(ex);
					}
					this.hasReadNext = true;
				}
				return (this.next != null);
			}
			public Object next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				this.current = this.next;
				this.next = null;
				this.hasReadNext = false;
				return this.current;
			}
			public void remove() {
				if (this.current == null) {
					throw new IllegalStateException();
				}
				delete(this.current.getGraphName(), this.current.getSubject(),
						this.current.getPredicate(), this.current.getObject());
				this.current = null;
			}
			private Quad makeQuad() {
				Node object;
				try {
					String dt = results.getString(7);
					if (results.getString(4) == null) {
						object = Node.createLiteral(results.getString(5), results.getString(6),
								((dt == null) ?
										null :
										TypeMapper.getInstance().getSafeTypeByName(dt)));
					} else {
						object = toResource(results.getString(4));
					}
					return new Quad(Node.createURI(results.getString(1)),
							toResource(results.getString(2)),
							Node.createURI(results.getString(3)),
							object);
				} catch (SQLException ex) {
					throw new JenaException(ex);
				}
			}
			private Node toResource(String str) {
				if (str.startsWith("_:")) {
					return Node.createAnon(new AnonId(str.substring(2)));
				}
				return Node.createURI(str);
			}
		};
	}
	
	public int count() {
		String sql = "SELECT COUNT(*) FROM " + getQuadsTableName();
		ResultSet results = executeQuery(sql);
		try {
			results.next();
			return results.getInt(1);
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}
	
	public void insertGraphName(Node graphName) {
		String sql = "INSERT INTO " + getGraphNamesTableName() +
				" VALUES ('" + escape(graphName.getURI()) + "')";
		execute(sql);
	}
	
	public void deleteGraphName(Node graphName) {
		String sql = "DELETE FROM " + getGraphNamesTableName();
		if (!Node.ANY.equals(graphName)) {
			sql += " WHERE name='" + escape(graphName.getURI()) + "'";
		}
		execute(sql);
	}
	
	public boolean containsGraphName(Node graphName) {
		String sql = "SELECT COUNT(*) FROM " + getGraphNamesTableName();
		if (!Node.ANY.equals(graphName)) {
			sql += " WHERE name='" + escape(graphName.getURI()) + "'";
		}
		ResultSet results = executeQuery(sql);
		try {
			results.next();
			return results.getInt(1) > 0;
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}
	
	public Iterator listGraphNames() {
		String sql = "SELECT name FROM " + getGraphNamesTableName();
		final ResultSet results = executeQuery(sql);

		return new Iterator() {
			private boolean isOnNext = false;
			private boolean hasNext;

			public boolean hasNext() {
				if (!this.isOnNext) {
					try {
						this.hasNext = results.next();
						if (!this.hasNext) {
							results.close();
						}
					} catch (SQLException ex) {
						throw new JenaException(ex);
					}
					this.isOnNext = true;
				}
				return this.hasNext;
			}

			public Object next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				this.isOnNext = false;
				try {
					return Node.createURI(results.getString(1));
				} catch (SQLException ex) {
					throw new JenaException(ex);
				}
			}

			public void remove() {
				throw new UnsupportedOperationException("Remove not supported");
			}
		};
	}
	
	public int countGraphNames() {
		String sql = "SELECT COUNT(*) FROM " + getGraphNamesTableName();
		ResultSet results = executeQuery(sql);
		try {
			results.next();
			return results.getInt(1);
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}

	public void createTables() {
		switch(this.type){
			case HSQL_TYPE:
				this.createTablesHSQLDB();
				break;
			case MYSQL_TYPE:
				this.createTablesMySQL();
				break;
			case POSTGRESQL_TYPE:
				this.createTablesPostgreSQL();
				break;
			default:
				this.createTablesMySQL();
				break;
		}
	}
	
	public void deleteTables() {
		execute("DROP TABLE " + getGraphNamesTableName());
		execute("DROP TABLE " + getQuadsTableName());
	}
	
	public boolean tablesExist() {
		switch(this.type){
			case HSQL_TYPE:
				return this.tableExistHSQLDB();
			case MYSQL_TYPE:
				return this.tableExistMySQL();
			case POSTGRESQL_TYPE:
				return this.tableExistPostgreSQL();
			default:
				return this.tableExistMySQL();
		}
	}
	
	public void close() {
		try {
			this.connection.close();
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}
	
	private String getGraphNamesTableName() {
		return this.tablePrefix + "_graphs";
	}
	
	private String getQuadsTableName() {
		return this.tablePrefix + "_quads";
	}

	/**
	 * Escape special characters in database literals to avoid
	 * SQL injection
	 */
	private String escape(String s) {
		return this.escapePattern.matcher(s).
				replaceAll(this.escapeReplacement);
	}

	private String escapeResource(Node resource) {
		if (resource.isURI()) {
			return escape(resource.getURI());
		}
		return "_:" + escape(resource.getBlankNodeId().toString());
	}

	private void executeNoErrorHandling(String sql) throws SQLException {
		this.connection.createStatement().execute(sql);		
	}

	private void execute(String sql) {
		try {
			executeNoErrorHandling(sql);
		} catch (SQLException ex) {
			if (ex.getErrorCode() != 1062) {
				throw new JenaException(ex);
			}
		}
	}

	private ResultSet executeQuery(String sql) {
		try {
			Statement stmt = this.connection.createStatement();
			return stmt.executeQuery(sql);
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}
	
	private String getObjectColumn(Node o) {
		if (o.isLiteral()) {
			return "NULL";
		}
		return "'" + escapeResource(o) + "'";
	}
	
	private String getLiteralColumn(Node o) {
		if (!o.isLiteral()) {
			return "NULL";
		}
		return "'" + escape(o.getLiteral().getLexicalForm()) + "'";
	}
	
	private String getLangColumn(Node o) {
		if (!o.isLiteral() || o.getLiteral().language() == null || "".equals(o.getLiteral().language())) {
			return "NULL";
		}
		return "'" + escape(o.getLiteral().language()) + "'";
	}
	
	private String getDatatypeColumn(Node o) {
		if (!o.isLiteral() || o.getLiteral().getDatatypeURI() == null) {
			return "NULL";
		}
		return "'" + escape(o.getLiteral().getDatatypeURI()) + "'";
	}

	private String getWhereClause(Node g, Node s, Node p, Node o) {
		List clauses = new ArrayList();
		if (!Node.ANY.equals(g)) {
			clauses.add("graph='" + escape(g.getURI()) + "'");
		}
		if (!Node.ANY.equals(s)) {
			clauses.add("subject='" + escapeResource(s) + "'");
		}
		if (!Node.ANY.equals(p)) {
			clauses.add("predicate='" + escape(p.getURI()) + "'");
		}
		if (!Node.ANY.equals(o)) {
			if (o.isLiteral()) {
				clauses.add("literal='" + escape(o.getLiteral().getLexicalForm()) + "'");
				if (o.getLiteral().language() == null || "".equals(o.getLiteral().language())) {
					clauses.add("lang IS NULL");
				} else {
					clauses.add("lang='" + escape(o.getLiteral().language()) + "'");
				}
				if (o.getLiteral().getDatatypeURI() == null) {
					clauses.add("datatype IS NULL");
				} else {
					clauses.add("datatype='" + escape(o.getLiteral().getDatatypeURI()) + "'");
				}
			} else {
				clauses.add("object='" + escapeResource(o) + "'");
			}
		}
		if (clauses.isEmpty()) {
			return "";
		}
		String result = "";
		Iterator it = clauses.iterator();
		while (it.hasNext()) {
			String clause = (String) it.next();
			if (!"".equals(result)) {
				result += " AND ";
			}
			result += clause;
		}
		return "WHERE " + result;
	}
	
	private void setDBtype(){
		String name = null;
		try {
			name = this.connection.getMetaData().getDatabaseProductName();
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		if (name.toLowerCase().indexOf("hsql")!= -1) {
			this.type = HSQL_TYPE;
		} else if (name.toLowerCase().indexOf("mysql")!= -1) {
			this.type = MYSQL_TYPE;
		} else if (name.toLowerCase().indexOf("postgresql") != -1) {
			this.type = POSTGRESQL_TYPE;
		} else {
			this.type = -1;
		}
	}
	
	private void setEscapePattern(){
		switch(this.type){
			case HSQL_TYPE:
				this.escapePattern     = Pattern.compile("([\\'])");
				this.escapeReplacement = "$1$1";
				break;
			case MYSQL_TYPE:
			case POSTGRESQL_TYPE:
			default:
				this.escapePattern     = Pattern.compile("([\\\\'])");
				this.escapeReplacement = "\\\\$1";
				break;
		}
	}
	
	private void createTablesHSQLDB(){
		execute("CREATE TABLE " + getGraphNamesTableName() + " (name VARCHAR , PRIMARY KEY(name)) ");
		try {
			executeNoErrorHandling(
					"CREATE TABLE " + getQuadsTableName() + " (" +
					"graph VARCHAR NOT NULL," +
					"subject VARCHAR NOT NULL," +
					"predicate VARCHAR NOT NULL," +
					"object VARCHAR," +
					"literal LONGVARCHAR," +
					"lang VARCHAR," +
					"datatype VARCHAR )");
		} catch (SQLException ex) {
			execute("DROP TABLE " + getGraphNamesTableName());
			throw new JenaException(ex);
		}
	}
	
	private void createTablesMySQL(){
		execute("CREATE TABLE " + getGraphNamesTableName() + " (" +
				"name varchar(160) NOT NULL default '', " +
				"PRIMARY KEY  (`name`)) TYPE=MyISAM");
		try {
			executeNoErrorHandling(
					"CREATE TABLE " + getQuadsTableName() + " (" +
					"graph varchar(160) NOT NULL default ''," +
					"subject varchar(160) NOT NULL default ''," +
					"predicate varchar(160) NOT NULL default ''," +
					"object varchar(160) default NULL," +
					"literal text," +
					"lang varchar(10) default NULL," +
					"datatype varchar(160) default NULL," +
					"KEY graph (`graph`)," +
					"KEY subject (`subject`)," +
					"KEY predicate (`predicate`)," +
					"KEY object (`object`)" +
					") TYPE=MyISAM;");
		} catch (SQLException ex) {
			execute("DROP TABLE " + getGraphNamesTableName());
			throw new JenaException(ex);
		}
	}

  private void createTablesPostgreSQL(){
		execute("CREATE TABLE " + getGraphNamesTableName() + " (" +
				"name text PRIMARY KEY default '')");
		try {
			executeNoErrorHandling(
					"CREATE TABLE " + getQuadsTableName() + " (" +
					"graph text NOT NULL default ''," +
					"subject text NOT NULL default ''," +
					"predicate text NOT NULL default ''," +
					"object text default NULL," +
					"literal text," +
					"lang text default NULL," +
					"datatype text default NULL)");
		} catch (SQLException ex) {
			execute("DROP TABLE " + getGraphNamesTableName());
			throw new JenaException(ex);
		}
		execute("CREATE INDEX " + tablePrefix + "_graph_index ON " +
				getQuadsTableName() + " (graph)");
		execute("CREATE INDEX " + tablePrefix + "_subject_index ON " +
				getQuadsTableName() + " (subject)");
		execute("CREATE INDEX " + tablePrefix + "_predicate_index ON " +
				getQuadsTableName() + " (predicate)");
		execute("CREATE INDEX " + tablePrefix + "_object_index ON " +
				getQuadsTableName() + " (object)");
	}

	private boolean tableExistHSQLDB(){
		try {
			ResultSet results = this.connection.getMetaData().getTables(
					null, null, getGraphNamesTableName().toUpperCase(), null);
			return results.next();
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}
	
	private boolean tableExistMySQL(){
		try {
			ResultSet results = this.connection.getMetaData().getTables(
					null, null, getGraphNamesTableName(), null);
			return results.next();
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}
	
	private boolean tableExistPostgreSQL(){
		try {
			ResultSet results = this.connection.getMetaData().getTables(
					null, null, getGraphNamesTableName(), null);
			return results.next();
		} catch (SQLException ex) {
			throw new JenaException(ex);
		}
	}
	
	
}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
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
