package de.fuberlin.wiwiss.trust;

import java.util.Collections;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @version $Id: MetricTest.java,v 1.2 2005/03/21 00:23:24 cyganiak Exp $
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MetricTest extends TestCase {

    public void testIsFooMetric() throws MetricException {
        Metric id = new IsFooMetric();
        id.setup(new NamedGraphSetImpl());
        assertTrue(id.calculateMetric(Collections.singletonList(
                Node.createLiteral("foo"))).getResult());
        assertFalse(id.calculateMetric(Collections.singletonList(
                Node.createLiteral("bar"))).getResult());
        assertEquals(new ExplanationPart(
                Collections.singletonList(Node.createLiteral("foo"))),
                id.calculateMetric(Collections.singletonList(
                Node.createLiteral("foo"))).getTextExplanation());
    }
}
