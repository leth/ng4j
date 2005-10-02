package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.hp.hpl.jena.graph.Node;

/**
 * @version $Id: ResultTableBuilder.java,v 1.1 2005/10/02 21:59:28 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ResultTableBuilder {
	private String[] variables;
	private ResultTable table;
	
	public static ResultTable build(String heading, String results) {
		ResultTableBuilder builder = new ResultTableBuilder(heading);
		if ("".equals(results)) {
			builder.addBinding(new VariableBinding());
		} else if (results != null) {
			String[] rows = split(results, "|");
			for (int i = 0; i < rows.length; i++) {
				builder.addCommaSeparatedLiteralRow(rows[i]);
			}
		}
		return builder.table();
	}
	
	public ResultTableBuilder(String commaSeparatedVariableNames) {
		this(split(commaSeparatedVariableNames, ","));
	}
	
	public ResultTableBuilder(String[] variableNames) {
		this.variables = variableNames;
		this.table = new ResultTable();
	}
	
	public void addCommaSeparatedLiteralRow(String commaSeparated) {
		addLiteralRow(split(commaSeparated, ","));
	}
	
	public void addLiteralRow(String[] literals) {
		VariableBinding binding = new VariableBinding();
		for (int i = 0; i < this.variables.length; i++) {
			binding.setValue(this.variables[i], Node.createLiteral(literals[i]));
		}
		addBinding(binding);
	}
	
	public void addNodeRow(Node[] nodes) {
		VariableBinding binding = new VariableBinding();
		for (int i = 0; i < this.variables.length; i++) {
			binding.setValue(this.variables[i], nodes[i]);
		}
		addBinding(binding);		
	}
	
	public void addBinding(VariableBinding binding) {
		this.table.addBinding(binding);
	}
	
	public ResultTable table() {
		return this.table;
	}
	
	private static String[] split(String s, String delim) {
		StringTokenizer tokens = new StringTokenizer(s, delim);
		List result = new ArrayList();
		while (tokens.hasMoreTokens()) {
			result.add(tokens.nextToken());
		}
		return (String[]) result.toArray(new String[result.size()]);
	}
}
