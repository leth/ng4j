package de.fuberlin.wiwiss.trust;

import java.util.Collections;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

/**
 * Tests for {@link de.fuberlin.wiwiss.trust.ConstraintParser}.
 *
 * @version $Id: ConstraintParserTest.java,v 1.2 2005/03/21 21:51:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConstraintParserTest extends TestCase {

    public void testTrueConstraint() {
        assertTrue(ConstraintFixture.getConstraint("true").evaluate(
                new VariableBinding()).getResult());
    }

    public void testFalseConstraint() {
        assertFalse(ConstraintFixture.getConstraint("false").evaluate(
                new VariableBinding()).getResult());
    }

    public void testMinimalConstraint() {
        Constraint condition = new ConstraintParser(
                "(?a > 0)", new PrefixMappingImpl(), Collections.EMPTY_LIST).parse();
        
        VariableBinding binding = new VariableBinding();
        binding.setValue("a", Node.createLiteral("17", null, XSDDatatype.XSDinteger));
        assertTrue(condition.evaluate(binding).getResult());
        binding.setValue("a", Node.createLiteral("-5", null, XSDDatatype.XSDinteger));
        assertFalse(condition.evaluate(binding).getResult());
    }
}
