package de.fuberlin.wiwiss.trust;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * Tests for {@link de.fuberlin.wiwiss.trust.ConditionParser}.
 *
 * @version $Id: ConditionParserTest.java,v 1.2 2005/03/15 08:57:14 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ConditionParserTest extends TestCase {
    private Metric isFooMetric;
    private Metric equalsMetric;
    
    public void setUp() {
        this.isFooMetric = new IsFooMetric();
        this.isFooMetric.setup(new NamedGraphSetImpl());
        this.equalsMetric = new EqualsMetric();
        this.equalsMetric.setup(new NamedGraphSetImpl());
    }
    
    public void testTrueCondition() {
        assertTrue(ConditionFixture.getCondition("true").isSatisfiedBy(new HashMap()));
    }

    public void testFalseCondition() {
        assertFalse(ConditionFixture.getCondition("false").isSatisfiedBy(new HashMap()));
    }

    public void testMinimalCondition() {
        Condition condition = new ConditionParser(
                "(?a > 0)", new PrefixMappingImpl(), Collections.EMPTY_LIST).parse();
        
        Map map = new HashMap();
        map.put("a", Node.createLiteral("17", null, XSDDatatype.XSDinteger));
        assertTrue(condition.isSatisfiedBy(map));
        map.put("a", Node.createLiteral("-5", null, XSDDatatype.XSDinteger));
        assertFalse(condition.isSatisfiedBy(map));
    }
    
    public void testSimpleMetricCondition() {
        ConditionParser parser = new ConditionParser(
                "METRIC(<http://example.org/metrics#IsFoo>, ?a)",
                new PrefixMappingImpl(),
                Arrays.asList(new Metric[] {this.isFooMetric}));
        Condition condition = parser.parse();

        Map map = new HashMap();
        map.put("a", Node.createLiteral("foo"));
        assertTrue(condition.isSatisfiedBy(map));
        map.put("a", Node.createLiteral("bar"));
        assertFalse(condition.isSatisfiedBy(map));
    }
    
    public void testUnknownMetric() {
        ConditionParser parser = new ConditionParser(
                "METRIC(<http://example.com/metrics#unknown>, ?a)",
                new PrefixMappingImpl(),
                Collections.EMPTY_LIST);
        try {
            parser.parse();
            fail("Expected TPLException because metric ex:unknown doesn't exist");
        } catch (TPLException ex) {
            // is expected
        }
    }
    
    public void testQNameInMetricURI() {
        PrefixMapping prefixes = new PrefixMappingImpl();
        prefixes.setNsPrefix("ex", "http://example.org/metrics#");
        ConditionParser parser = new ConditionParser(
                "METRIC(ex:IsFoo, ?a)",
                prefixes,
                Arrays.asList(new Metric[] {this.isFooMetric}));
        parser.parse();
    }

    public void testMetricParameters() {
        ConditionParser parser = new ConditionParser(
                "METRIC(<http://example.org/metrics#Equals>, 5, 5)",
                new PrefixMappingImpl(),
                Arrays.asList(new Metric[] {this.equalsMetric}));
        Condition condition = parser.parse();
        assertTrue(condition.isSatisfiedBy(Collections.EMPTY_MAP));

        parser = new ConditionParser(
                "METRIC(<http://example.org/metrics#Equals>, 4, 5)",
                new PrefixMappingImpl(),
                Arrays.asList(new Metric[] {this.equalsMetric}));
        condition = parser.parse();
        assertFalse(condition.isSatisfiedBy(Collections.EMPTY_MAP));
    }
    
    class EqualsMetric implements Metric {
        public void setup(NamedGraphSet sourceData) {
            // do nothing
        }
        public String getURI() {
            return "http://example.org/metrics#Equals";
        }
        public MetricResult calculateMetric(final List arguments)
        			throws MetricException {
            return new MetricResult() {
                public boolean getResult() {
                    return arguments.get(0).equals(arguments.get(1));
                }
                public ExplanationPart getTextExplanation() {
                    return null;
                }
                public Graph getGraphExplanation() {
                    return null;
                }
            };
        }
    }
}
