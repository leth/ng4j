package de.fuberlin.wiwiss.trust;

import java.util.Collections;

import junit.framework.TestCase;

/**
 * @version $Id: ResultTableTest.java,v 1.2 2005/10/02 21:59:28 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ResultTableTest extends TestCase {
    
    public void testEquals() {
        assertTrue(new ResultTable().equals(new ResultTable()));
        assertFalse(new ResultTable().equals(
        		ResultTableBuilder.build("foo", "1")));
        assertTrue(ResultTableBuilder.build("foo", "1").equals(
        		ResultTableBuilder.build("foo", "1")));
        assertFalse(ResultTableBuilder.build("foo", "1").equals(
        		ResultTableBuilder.build("foo", "2")));
    }

    public void testHashCode() {
        assertEquals(new ResultTable().hashCode(), new ResultTable().hashCode());
    }
    
    public void testAddAll() {
        ResultTable empty1 = new ResultTable();
        ResultTable empty2 = new ResultTable();
        ResultTable foo1 = ResultTableBuilder.build("foo", "1");
        ResultTable foo2 = ResultTableBuilder.build("foo", "2");
        ResultTable foo12 = ResultTableBuilder.build("foo", "1|2");
        empty1.addAll(empty2);
        assertEquals(0, empty1.countBindings());
        empty1.addAll(foo1);
        assertEquals(empty1, foo1);
        foo1.addAll(foo2);
        assertEquals(foo12, foo1);
        empty2.addAll(foo12);
        assertEquals(foo12, empty2);
    }
    
    public void testSelectDistinct() {
        ResultTable table = ResultTableBuilder.build(
        		"a,b,c", "1,1,1|1,2,2|1,1,3|1,1,4");
        ResultTable distinctA = ResultTableBuilder.build("a", "1");
        ResultTable distinctB = ResultTableBuilder.build("b", "1|2");
        ResultTable distinctC = ResultTableBuilder.build("c", "1|2|3|4");
        ResultTable distinctD = ResultTableBuilder.build("d", "");
        assertEquals(distinctA, table.selectDistinct(Collections.singleton("a")));
        assertEquals(distinctB, table.selectDistinct(Collections.singleton("b")));
        assertEquals(distinctC, table.selectDistinct(Collections.singleton("c")));
        assertEquals(distinctD, table.selectDistinct(Collections.singleton("d")));
    }
    
    public void testCountDistinct() {
        ResultTable table = ResultTableBuilder.build(
        		"a,b,c", "1,1,1|1,2,2|1,1,3|1,1,4");
        assertEquals(1, table.countDistinct("a"));
        assertEquals(2, table.countDistinct("b"));
        assertEquals(4, table.countDistinct("c"));
        assertEquals(0, table.countDistinct("d"));
    }
}
