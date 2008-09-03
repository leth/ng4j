// $Id: QuadDB.java,v 1.10 2008/09/03 16:37:29 cyganiak Exp $
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
import de.fuberlin.wiwiss.ng4j.db.specific.DbCompatibility;
import de.fuberlin.wiwiss.ng4j.db.specific.HSQLCompatibility;
import de.fuberlin.wiwiss.ng4j.db.specific.MySQLCompatibility;
import de.fuberlin.wiwiss.ng4j.db.specific.OracleCompatibility;
import de.fuberlin.wiwiss.ng4j.db.specific.PostgreSQLCompatibility;

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
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QuadDB {

	private Pattern escapePattern = null;
	private String escapeReplacement = null;
	
	private final String tablePrefix;
	
	private final String graphNamesTableName;
	private final String quadsTableName;
	
	private DbCompatibility dbCompatibility;

	public QuadDB(Connection connection, String tablePrefix) {
		this.setDBtype(connection);
		this.setEscapePattern();
		this.tablePrefix = escape(tablePrefix);
		this.graphNamesTableName = this.tablePrefix + "_graphs";
		this.quadsTableName = this.tablePrefix + "_quads";
		
		dbCompatibility.initialize(tablePrefix, graphNamesTableName, quadsTableName);
	}
	
	public void insert(Node g, Node s, Node p, Node o) {
		if (find(g, s, p, o).hasNext()) {
			return;
		}
		String sql = "INSERT INTO " + quadsTableName +
				" (graph, subject, predicate, object, literal, lang, datatype) VALUES (" +
				"'" + escape(g.getURI()) + "', " +
				"'" + escapeResource(s) + "', " +
				"'" + escape(p.getURI()) + "', " +
				getObjectColumn(o) + ", " +
				getLiteralColumn(o) + ", " +
				getLangColumn(o) + ", " +
				getDatatypeColumn(o) + ")";
		dbCompatibility.execute(sql);
	}
	
	public void delete(Node g, Node s, Node p, Node o) {
		String sql = "DELETE FROM " + quadsTableName + " " +
				getWhereClause(g, s, p, o);
		dbCompatibility.execute(sql);
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
				"FROM " + quadsTableName + " " +
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
							cleanUp(results);
						}
					} catch (SQLException ex) {
						cleanUp(results);
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
					cleanUp(results);
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
		String sql = "SELECT COUNT(*) FROM " + quadsTableName;
		ResultSet results = executeQuery(sql);
		try {
			results.next();
			return results.getInt(1);
		} catch (SQLException ex) {
			throw new JenaException(ex);
		} finally {
			cleanUp(results);
		}
	}
	
	public void insertGraphName(Node graphName) {
		String sql = "INSERT INTO " + graphNamesTableName +
				" VALUES ('" + escape(graphName.getURI()) + "')";
		dbCompatibility.execute(sql);
	}
	
	public void deleteGraphName(Node graphName) {
		String sql = "DELETE FROM " + graphNamesTableName;
		if (!Node.ANY.equals(graphName)) {
			sql += " WHERE name='" + escape(graphName.getURI()) + "'";
		}
		dbCompatibility.execute(sql);
	}
	
	public boolean containsGraphName(Node graphName) {
		String sql = "SELECT COUNT(*) FROM " + graphNamesTableName;
		if (!Node.ANY.equals(graphName)) {
			sql += " WHERE name='" + escape(graphName.getURI()) + "'";
		}
		ResultSet results = executeQuery(sql);
		try {
			results.next();
			return results.getInt(1) > 0;
		} catch (SQLException ex) {
			throw new JenaException(ex);
		} finally {
			cleanUp(results);
		}
	}
	
	public Iterator listGraphNames() {
		String sql = "SELECT name FROM " + graphNamesTableName;
		final ResultSet results = executeQuery(sql);

		return new Iterator() {
			private boolean isOnNext = false;
			private boolean hasNext;

			public boolean hasNext() {
				if (!this.isOnNext) {
					try {
						this.hasNext = results.next();
						if (!this.hasNext) {
							cleanUp(results);
						}
					} catch (SQLException ex) {
						cleanUp(results);
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
					cleanUp(results);
					throw new JenaException(ex);
				}
			}

			public void remove() {
				throw new UnsupportedOperationException("Remove not supported");
			}
		};
	}
	
	public int countGraphNames() {
		String sql = "SELECT COUNT(*) FROM " + graphNamesTableName;
		ResultSet results = executeQuery(sql);
		try {
			results.next();
			return results.getInt(1);
		} catch (SQLException ex) {
			throw new JenaException(ex);
		} finally {
			cleanUp(results);
		}
	}

	private void cleanUp(ResultSet results) {
		try {
			results.getStatement().close();
		} catch (SQLException ex) {
			throw new JenaException("Cannot close result set", ex);
		}
	}
	
	public void createTables() {
		dbCompatibility.createTables();
	}
	
	public void deleteTables() {
		dbCompatibility.execute("DROP TABLE " + graphNamesTableName);
		dbCompatibility.execute("DROP TABLE " + quadsTableName);
	}
	
	public boolean tablesExist() {
		return dbCompatibility.tablesExist();
	}
	
	public void close() {
		dbCompatibility.close();
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

	private ResultSet executeQuery(String sql) {
		Statement stmt = null;
		try {
			stmt = dbCompatibility.getConnection().createStatement();
			return stmt.executeQuery(sql);
		} catch (SQLException ex) {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex2) {
					throw new JenaException(ex);
				}
			}
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
	
	private void setDBtype(Connection connection){
		String name = null;
		try {
			name = connection.getMetaData().getDatabaseProductName();
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		if (name.toLowerCase().indexOf("hsql")!= -1) {
			dbCompatibility = new HSQLCompatibility(connection);
		} else if (name.toLowerCase().indexOf("mysql")!= -1) {
			dbCompatibility = new MySQLCompatibility(connection);
		} else if (name.toLowerCase().indexOf("postgresql") != -1) {
			dbCompatibility = new PostgreSQLCompatibility(connection);
		} else if (name.toLowerCase().indexOf("oracle") != -1) {
			dbCompatibility = new OracleCompatibility(connection);
		} else {
			throw new RuntimeException("Unrecognized database type: " + name);
		}
	}
	
	private void setEscapePattern(){
		this.escapePattern = dbCompatibility.getEscapePattern();
		this.escapeReplacement = dbCompatibility.getEscapeReplacement();
	}

}

/*
 *  (c) Copyright 2004, 2005, 2006, 2007, 2008 Christian Bizer (chris@bizer.de)
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
