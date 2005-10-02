package de.fuberlin.wiwiss.trust;


/**
 * @version $Id: CountConstraint.java,v 1.2 2005/10/02 21:59:28 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CountConstraint {
	private String variableName;
	private String operator;
	private int value;
	private Comparable comparator;
	
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

	public String variableName() {
		return this.variableName;
	}
	
	public String operator() {
		return this.operator;
	}
	
	public int value() {
		return this.value;
	}
	
	public boolean isMatchingCount(int count) {
		return this.comparator.compare(count, this.value);
	}
	
	private interface Comparable {
		public boolean compare(int v1, int v2);
	}
}