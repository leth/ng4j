// $Id: QuadDB.java,v 1.1 2004/11/02 02:00:23 cyganiak Exp $
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
 * TODO: Currently, this works only with MySQL.
 * 		Must factor out DB-specific stuff, e.g. table creation.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QuadDB {
	private final static Pattern escapePattern = Pattern.compile("([\\\\'])");
	private final static String escapeReplacement = "\\\\$1";
	private String tablePrefix;
	private Connection connection;

	public QuadDB(Connection connection, String tablePrefix) {
		this.connection = connection;
		this.tablePrefix = escape(tablePrefix);
	}

	public void insert(Node g, Node s, Node p, Node o) {
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
		String sql = "SELECT graph, subject, predicate, object, literal, lang, datatype " +
				"FROM " + getQuadsTableName() + " " +
				getWhereClause(g, s, p, o);
		final ResultSet results = executeQuery(sql);
		return new Iterator() {
			private boolean hasNext;
			private Quad current = null;

			public boolean hasNext() {
				if (this.current == null) {
					try {
						this.hasNext = results.next();
						if (this.hasNext) {
							this.current = makeQuad();
						} else {
							results.close();
						}
					} catch (SQLException ex) {
						throw new JenaException(ex);
					}
				}
				return this.hasNext;
			}
			public Object next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				Quad result = this.current;
				this.current = null;
				return result;
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
		execute("CREATE TABLE " + getGraphNamesTableName() + " (" +
				"name varchar(160) NOT NULL default '', " +
				"PRIMARY KEY  (`name`)) TYPE=MyISAM");
		execute("CREATE TABLE " + getQuadsTableName() + " (" +
				"graph varchar(160) NOT NULL default ''," +
				"subject varchar(160) NOT NULL default ''," +
				"predicate varchar(160) NOT NULL default ''," +
				"object varchar(160) default NULL," +
				"literal text," +
				"lang varchar(10) default NULL," +
				"datatype varchar(160) default NULL," +
				"PRIMARY KEY  (`graph`,`subject`,`predicate`)," +
				"KEY subject (`subject`)," +
				"KEY predicate (`predicate`)," +
				"KEY object (`object`)" +
				") TYPE=MyISAM;");
	}
	
	public void deleteTables() {
		execute("DROP TABLE " + getGraphNamesTableName());
		execute("DROP TABLE " + getQuadsTableName());
	}
	
	public boolean tablesExist() {
		try {
			ResultSet results = this.connection.getMetaData().getTables(
					null, null, getGraphNamesTableName(), null);
			return results.next();
		} catch (SQLException ex) {
			throw new JenaException(ex);
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
	private static String escape(String s) {
		return QuadDB.escapePattern.matcher(s).
				replaceAll(QuadDB.escapeReplacement);
	}

	private static String escapeResource(Node resource) {
		if (resource.isURI()) {
			return escape(resource.getURI());
		}
		return "_:" + escape(resource.getBlankNodeId().toString());
	}

	private void execute(String sql) {
		try {
			this.connection.createStatement().execute(sql);
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