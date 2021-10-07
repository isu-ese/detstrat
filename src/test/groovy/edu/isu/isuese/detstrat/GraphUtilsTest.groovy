/*
 * The MIT License (MIT)
 *
 * ISUESE Detection Strategies
 * Copyright (c) 2019 Idaho State University, Informatics and Computer Science,
 * Empirical Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.isu.isuese.detstrat

import com.google.common.graph.EndpointPair
import com.google.common.graph.Network
import com.google.common.graph.NetworkBuilder
import edu.isu.isuese.detstrat.impl.GraphElementFactory
import edu.isu.isuese.detstrat.impl.Node
import edu.isu.isuese.detstrat.impl.NodeType
import edu.isu.isuese.detstrat.impl.Relationship
import edu.isu.isuese.detstrat.impl.RelationshipType
import org.javalite.activejdbc.test.DBSpec
import org.junit.Before
import org.junit.Test

class GraphUtilsTest extends DBSpec {

    Network<Node, Relationship> graph
    GraphUtils fixture

    Node a, b, c, d, e, f, g

    @Before
    void setup() {
        fixture = GraphUtils.instance
        GraphElementFactory factory = GraphElementFactory.instance
        a = new Node(name: "A", type: NodeType.Class)
        b = new Node(name: "B", type: NodeType.Class)
        c = new Node(name: "C", type: NodeType.Class)
        d = new Node(name: "D", type: NodeType.Class)
        e = new Node(name: "E", type: NodeType.Class)
        f = new Node(name: "F", type: NodeType.Class)
        g = new Node(name: "G", type: NodeType.Class)

        graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .expectedNodeCount(1000)
                .expectedEdgeCount(10000)
                .build()

        graph.addNode(a)
        graph.addNode(b)
        graph.addNode(c)
        graph.addNode(d)
        graph.addNode(e)
        graph.addNode(f)
        graph.addNode(g)

        graph.addEdge(a, c, factory.createRelationship(RelationshipType.Dependency))
        graph.addEdge(b, c, factory.createRelationship(RelationshipType.Dependency))
        graph.addEdge(c, d, factory.createRelationship(RelationshipType.Dependency))
        graph.addEdge(d, e, factory.createRelationship(RelationshipType.Dependency))
        graph.addEdge(e, e, factory.createRelationship(RelationshipType.Dependency))
        graph.addEdge(e, c, factory.createRelationship(RelationshipType.Dependency))
    }

    @Test
    void spanningTree() {
        // given

        // when
        Network<Node, Relationship> result = fixture.spanningTree(graph)

        // then
        result.edges().each { Relationship rel ->
            EndpointPair<Node> pair = result.incidentNodes(rel)
            println "${pair.source().name} -> ${pair.target().name}"
        }
        the(result.edges().size()).shouldBeEqual(4)
        the(result.hasEdgeConnecting(a, c)).shouldBeTrue()
        the(result.hasEdgeConnecting(b, c)).shouldBeTrue()
    }

    @Test
    void topologicalSort() {
    }

    @Test
    void hasCycle() {
        // given

        // when
        boolean result = fixture.hasCycle(graph)

        // then
        the(result).shouldBeTrue()
    }

    @Test
    void reorderNodes() {
        // given

        // when
        fixture.reorderNodes(graph)

        // then
        the(a.order < c.order).shouldBeTrue()
        the(b.order < c.order).shouldBeTrue()
        the(c.order < d.order).shouldBeTrue()
        the(d.order < e.order).shouldBeTrue()
    }

    @Test
    void markCycles() {
        // given

        // when
        fixture.markCycles(graph)

        // then
        the(graph.edgeConnecting(a, c).get().cyclic).shouldBeFalse()
        the(graph.edgeConnecting(b, c).get().cyclic).shouldBeFalse()
        the(graph.edgeConnecting(c, d).get().cyclic).shouldBeTrue()
        the(graph.edgeConnecting(e, c).get().cyclic).shouldBeTrue()
        the(graph.edgeConnecting(e, e).get().cyclic).shouldBeTrue()
        the(graph.edgeConnecting(d, e).get().cyclic).shouldBeTrue()
    }
}