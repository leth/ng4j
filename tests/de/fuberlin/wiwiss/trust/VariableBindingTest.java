package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;

/**
 * @version $Id: VariableBindingTest.java,v 1.3 2005/03/26 23:56:46 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class VariableBindingTest extends TestCase {

    public void testEmptyBinding() {
        VariableBinding binding = new VariableBinding();
        assertFalse(binding.containsName("a"));
        assertNull(binding.value("a"));
        assertTrue(binding.variableNames().isEmpty());
    }
    
    public void testAddValue() {
        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("foo"));
        assertTrue(binding.containsName("a"));
        assertEquals(Node.createLiteral("foo"), binding.value("a"));
        assertEquals(1, binding.variableNames().size());
        assertTrue(binding.variableNames().contains("a"));
    }
    
    public void testEquals() {
        VariableBinding binding1 = new VariableBinding();
        binding1.setValue("foo", Node.createLiteral("bar"));
        assertTrue(new VariableBinding().equals(new VariableBinding()));
        assertFalse(new VariableBinding().equals(binding1));
    }
    
    public void testSubset() {
        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("a"));
        binding.setValue("b", Node.createLiteral("b"));
        VariableBinding subset = binding.selectSubset(
                Arrays.asList(new String[] {"b", "c"}));
        VariableBinding expected = new VariableBinding();
        expected.setValue("b", Node.createLiteral("b"));
        assertEquals(expected, subset);
    }
    
    public void testHashCode() {
        assertTrue(new VariableBinding().equals(new VariableBinding()));
    }
    
    public void testIsSubset() {
        VariableBinding binding = new VariableBinding();
        binding.setValue("foo", Node.createLiteral("fooValue"));
        binding.setValue("bar", null);
        assertTrue(new VariableBinding().isSubsetOf(binding));
        assertTrue(binding.isSubsetOf(binding));
        VariableBinding candidate = new VariableBinding();
        candidate.setValue("baz", null);
        assertTrue(candidate.isSubsetOf(binding));
        candidate.setValue("foo", Node.createLiteral("fooValue"));
        assertTrue(candidate.isSubsetOf(binding));
        candidate.setValue("foo", Node.createLiteral("fooValue1"));
        assertFalse(candidate.isSubsetOf(binding));
    }
    
    public void testAsMap() {
        VariableBinding binding = new VariableBinding();
        Node fooValue = Node.createLiteral("fooValue");
        Node barValue = Node.createLiteral("barValue");
        binding.setValue("foo", fooValue);
        binding.setValue("bar", barValue);
        Map expected = new HashMap();
        expected.put("foo", fooValue);
        expected.put("bar", barValue);
        assertEquals(expected, binding.asMap());
    }
}
