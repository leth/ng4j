package de.fuberlin.wiwiss.trust;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

import de.fuberlin.wiwiss.trust.Condition;
import de.fuberlin.wiwiss.trust.ConditionParser;

/**
 * Tests for {@link de.fuberlin.wiwiss.trust.ConditionParser}.
 *
 * @version $Id: ConditionParserTest.java,v 1.1 2005/02/18 01:44:59 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConditionParserTest extends TestCase {

    public void testTrueCondition() {
        assertTrue(ConditionFixture.getCondition("true").isSatisfiedBy(new HashMap()));
    }

    public void testFalseCondition() {
        assertFalse(ConditionFixture.getCondition("false").isSatisfiedBy(new HashMap()));
    }

    public void testMinimalCondition() {
        Condition condition = new ConditionParser(
                "(?a > 0)", new PrefixMappingImpl()).parse();
        
        Map map = new HashMap();
        map.put("a", Node.createLiteral("17", null, XSDDatatype.XSDinteger));
        assertTrue(condition.isSatisfiedBy(map));
        map.put("a", Node.createLiteral("-5", null, XSDDatatype.XSDinteger));
        assertFalse(condition.isSatisfiedBy(map));
    }
}
