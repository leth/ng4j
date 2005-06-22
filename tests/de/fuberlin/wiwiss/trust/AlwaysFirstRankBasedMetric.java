/*
 * AlwaysFirstRankBasedMetric.java
 *
 * Created on 21. Juni 2005, 14:12
 */

package de.fuberlin.wiwiss.trust;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

/**
 *
 * @author  Oliver Maresch (oliver-maresch@gmx.de)
 */
public class AlwaysFirstRankBasedMetric implements RankBasedMetric {

    public static final String URI = "http://example.org/metrics#AlwaysFirstRankBasedMetric";
    
    /** Creates a new instance of AllwaysFirstRankBasedMetric */
    public AlwaysFirstRankBasedMetric() {
    }

    public ExplanationPart explain(int row) {
        List text = new ArrayList();
        text.add(Node.createLiteral("AlwaysFirstRankBasedMetric"));
        return new ExplanationPart(text);
    }

    public com.hp.hpl.jena.graph.Graph explainRDF(int row) {
        return Graph.emptyGraph;
    }

    public String getURI() {
        return URI;
    }

    public void init(de.fuberlin.wiwiss.ng4j.NamedGraphSet source, java.util.List inputTable) throws MetricException {
    }

    public boolean isAccepted(int row) {
        return true;
    }

    public int rows() {
        return 1;
    }
}
