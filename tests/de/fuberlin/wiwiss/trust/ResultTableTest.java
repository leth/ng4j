package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.trust.ResultTable;
import de.fuberlin.wiwiss.trust.VariableBinding;

/**
 * @version $Id: ResultTableTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ResultTableTest extends TestCase {
    private static final String[] fooArray = new String[] {"foo"};
    private static final Node fooValue1 = Node.createLiteral("fooValue1");
    private static final Node fooValue2 = Node.createLiteral("fooValue2");
    private static final List fooValues1 = Arrays.asList(
            new Node[][] {new Node[] {fooValue1}});
    private static final List fooValues2 = Arrays.asList(
            new Node[][] {new Node[] {fooValue2}});
    
    public void testEquals() {
        assertTrue(new ResultTable().equals(new ResultTable()));
        assertFalse(new ResultTable().equals(createResultTable(fooArray, fooValues1)));
        assertTrue(createResultTable(fooArray, fooValues1).equals(
                createResultTable(fooArray, fooValues1)));
        assertFalse(createResultTable(fooArray, fooValues1).equals(
                createResultTable(fooArray, fooValues2)));
    }

    public void testHashCode() {
        assertEquals(new ResultTable().hashCode(), new ResultTable().hashCode());
    }
    
    public static ResultTable createResultTable(String[] variableNames, List rows) {
        ResultTable result = new ResultTable();
        Iterator it = rows.iterator();
        while (it.hasNext()) {
            Node[] row = (Node[]) it.next();
            VariableBinding binding = new VariableBinding();
            for (int i = 0; i < variableNames.length; i++) {
                binding.setValue(variableNames[i], row[i]);
            }
            result.addBinding(binding);
        }
        return result;
    }
}
