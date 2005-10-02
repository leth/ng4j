package de.fuberlin.wiwiss.trust;

import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;

/**
 * @version $Id: CountTest.java,v 1.1 2005/10/02 21:59:28 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CountTest extends TestCase {
	private ResultTable table;
	private ResultTable empty;
	
	public void setUp() {
		table = ResultTableBuilder.build("a", "1|2|3");
		empty = ResultTableBuilder.build("a", null);
	}
	
	public void testIllegalOperator() {
		try {
			new CountConstraint("a", "???", 27);
			fail("Expected IllegalArgumentException because '???' is not a legal operator");
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}
	
	public void testEqual() {
		assertEquals(empty, table.filterByCount(new CountConstraint("a", "=", 2), null));
		assertEquals(table, table.filterByCount(new CountConstraint("a", "=", 3), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("a", "=", 4), null));
	}

	public void testNotEqual() {
		assertEquals(table, table.filterByCount(new CountConstraint("a", "!=", 2), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("a", "!=", 3), null));
		assertEquals(table, table.filterByCount(new CountConstraint("a", "!=", 4), null));
	}
	
	public void testLessThan() {
		assertEquals(empty, table.filterByCount(new CountConstraint("a", "<", 2), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("a", "<", 3), null));
		assertEquals(table, table.filterByCount(new CountConstraint("a", "<", 4), null));
	}

	public void testLessThanOrEqual() {
		assertEquals(empty, table.filterByCount(new CountConstraint("a", "<=", 2), null));
		assertEquals(table, table.filterByCount(new CountConstraint("a", "<=", 3), null));
		assertEquals(table, table.filterByCount(new CountConstraint("a", "<=", 4), null));
	}

	public void testGreaterThan() {
		assertEquals(table, table.filterByCount(new CountConstraint("a", ">", 2), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("a", ">", 3), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("a", ">", 4), null));
	}

	public void testGreaterThanOrEqual() {
		assertEquals(table, table.filterByCount(new CountConstraint("a", ">=", 2), null));
		assertEquals(table, table.filterByCount(new CountConstraint("a", ">=", 3), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("a", ">=", 4), null));
	}
	
	public void testFilterNonMatching() {
		table = ResultTableBuilder.build("id,a",
				"1,1|2,1|3,2|4,3|5,3|6,3|7,4|8,4|9,5|10,6");
		ResultTable count1 = ResultTableBuilder.build("id,a",
				"3,2|9,5|10,6");
		ResultTable count2 = ResultTableBuilder.build("id,a",
				"1,1|2,1|7,4|8,4");
		ResultTable count3 = ResultTableBuilder.build("id,a",
				"4,3|5,3|6,3");
		assertEquals(empty, table.filterByCount(new CountConstraint("id", "=", 9), null));
		assertEquals(table, table.filterByCount(new CountConstraint("id", "=", 10), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("id", "=", 11), null));

		assertEquals(empty, table.filterByCount(new CountConstraint("a", "=", 5), null));
		assertEquals(table, table.filterByCount(new CountConstraint("a", "=", 6), null));
		assertEquals(empty, table.filterByCount(new CountConstraint("a", "=", 7), null));

		Collection onlyA = Collections.singleton("a");
		assertEquals(count1, table.filterByCount(new CountConstraint("id", "=", 1), onlyA));
		assertEquals(count2, table.filterByCount(new CountConstraint("id", "=", 2), onlyA));
		assertEquals(count3, table.filterByCount(new CountConstraint("id", "=", 3), onlyA));
		assertEquals(empty, table.filterByCount(new CountConstraint("id", "=", 4), onlyA));
	}
}
