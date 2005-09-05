package de.fuberlin.wiwiss.ng4j.sparql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.core.Binding;
import com.hp.hpl.jena.query.core.BindingMap;
import com.hp.hpl.jena.query.core.ResultBinding;
import com.hp.hpl.jena.rdf.model.Model;


public class TestResultSet implements ResultSet {
	private Model model;
	private List resultVars = new ArrayList();
	private List bindings = new ArrayList();
	private Binding currentBinding = new BindingMap();
	private Iterator bindingIterator = null;
	private int rowNumber = 0;
	
	public TestResultSet(Model model) {
		this.model = model;
	}
	
	public void addVar(String varName, Node node) {
		if (node != null) {
			this.currentBinding.add(varName, node);
		}
		if (!this.resultVars.contains(varName)) {
			this.resultVars.add(varName);
		}
	}

	public void addSolution() {
		this.bindings.add(this.currentBinding);
		this.currentBinding = new BindingMap();
	}
	
	public boolean hasNext() {
		ensureIteratorInitialized();
		return this.bindingIterator.hasNext();
	}

	public Object next() {
		ensureIteratorInitialized();
		this.rowNumber++;
		return new ResultBinding(this.model, (Binding) this.bindingIterator.next());
	}

	public QuerySolution nextSolution() {
		return (QuerySolution) next();
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public List getResultVars() {
		return this.resultVars;
	}

	public void remove() {
		throw new UnsupportedOperationException(
				"TestResultSet doesn't support the remove() operation");
	}

	public boolean isOrdered() {
		return false;
	}
	
	private void ensureIteratorInitialized() {
		if (this.bindingIterator != null) {
			return;
		}
		this.bindingIterator = this.bindings.iterator();
	}

	public boolean isDistinct() {
		return false;
	}
}