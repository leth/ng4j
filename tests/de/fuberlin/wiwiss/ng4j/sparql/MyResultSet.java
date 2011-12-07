package de.fuberlin.wiwiss.ng4j.sparql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;


public class MyResultSet implements ResultSet {
	private Model model;
	private List<String> resultVars = new ArrayList<String>();
	private List<Binding> bindings = new ArrayList<Binding>();
	private Binding currentBinding = new BindingMap();
	private Iterator<Binding> bindingIterator = null;
	private int rowNumber = 0;
	
	public MyResultSet(Model model) {
		this.model = model;
	}
	
	public void addVar(String varName, Node node) {
		if (node != null) {
			this.currentBinding.add(Var.alloc(varName), node);
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

  public QuerySolution next() {                                                                                 
		ensureIteratorInitialized();
		this.rowNumber++;
    Binding b = this.bindingIterator.next();
    return new ResultBinding(this.model, b);          
	}

	public QuerySolution nextSolution() {
		return next();
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public List<String> getResultVars() {
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

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.ResultSet#getResourceModel()
	 */
	public Model getResourceModel() {
		return model;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.query.ResultSet#nextBinding()
	 */
	public Binding nextBinding() {
		return bindingIterator.next();
	}
}