package de.fuberlin.wiwiss.trust;


/**
 * <p>A constraint on the number of distinct values for a variable.
 * Is used to implement the COUNT feature of TriQL.P and is used
 * on {@link ResultTable}s. Occurrences of the constrained variable
 * will be counted and compared to a fixed integer value with a
 * comparision operator.</p>
 * 
 * <p>The supported operators are encoded as strings:</p>
 * 
 * <ul>
 * <li>=</li>
 * <li>!=</li>
 * <li>&lt;</li>
 * <li>&lt;=</li>
 * <li>></li>
 * <li>>=</li>
 * </ul>
 * 
 * @version $Id: CountConstraint.java,v 1.3 2005/10/04 00:03:44 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CountConstraint {
	private String variableName;
	private String operator;
	private int value;
	private Comparable comparator;
	
	/**
	 * Creates a new count constraint.
	 * 
	 * @param variableName The name of the constrained variable, for example "GRAPH"
	 * @param operator Must be one of the supported comparision operators
	 * @param value The actual count will be compared to this number.
	 */
	public CountConstraint(String variableName, String operator, int value) {
		this.variableName = variableName;
		this.operator = operator;
		this.value = value;
		if ("=".equals(this.operator)) {
			this.comparator = new Comparable() {
				public boolean compare(int v1, int v2) { return v1 == v2; }
			};
		} else if ("!=".equals(this.operator)) {
			this.comparator = new Comparable() {
				public boolean compare(int v1, int v2) { return v1 != v2; }
			};
		} else if ("<".equals(this.operator)) {
			this.comparator = new Comparable() {
				public boolean compare(int v1, int v2) { return v1 < v2; }
			};
		} else if (">".equals(this.operator)) {
			this.comparator = new Comparable() {
				public boolean compare(int v1, int v2) { return v1 > v2; }
			};
		} else if ("<=".equals(this.operator)) {
			this.comparator = new Comparable() {
				public boolean compare(int v1, int v2) { return v1 <= v2; }
			};
		} else if (">=".equals(this.operator)) {
			this.comparator = new Comparable() {
				public boolean compare(int v1, int v2) { return v1 >= v2; }
			};
		} else {
			throw new IllegalArgumentException("Unknown operator: '" + this.operator + "'");
		}
	}

	/**
	 * @return The name of the constrained variable, for example "GRAPH"
	 */
	public String variableName() {
		return this.variableName;
	}

	/**
	 * @return The comparision operator of the constraint 
	 */
	public String operator() {
		return this.operator;
	}
	
	/**
	 * @return The fixed value to be tested against
	 */
	public int value() {
		return this.value;
	}
	
	/**
	 * Compares an actual number of occurrences of the constrained
	 * variable to the fixed value. 
	 * @param count The number of occurrences of the variable
	 * @return true if it is in the accepted range
	 */
	public boolean isMatchingCount(int count) {
		return this.comparator.compare(count, this.value);
	}
	
	private interface Comparable {
		public boolean compare(int v1, int v2);
	}
}