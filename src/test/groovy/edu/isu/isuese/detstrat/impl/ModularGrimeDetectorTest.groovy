/**
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
package edu.isu.isuese.detstrat.impl

import com.google.common.graph.MutableNetwork
import com.google.common.graph.Network
import com.google.common.graph.NetworkBuilder
import edu.isu.isuese.datamodel.*
import edu.montana.gsoc.msusel.rbml.model.*
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.javalite.activejdbc.test.DBSpec
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner.class)
class ModularGrimeDetectorTest extends DBSpec {

    ModularGrimeDetector fixture
    Node a, b, c, d, e, f, g, h, i
    System sys
    Project proj
    Namespace ns1, ns2, ns3, ns4, ns5, ns6
    PatternInstance inst
    Type typeA, typeB, typeC, typeD, typeE, typeF, typeG, typeH, typeI

    @Before
    void setup() {
        fixture = new ModularGrimeDetector()
    }

    @After
    void cleanup() {
        RuleProvider.instance.reset()
    }

    @Test
    void "test detect happy path clean pattern"() {
        // given
        RuleProvider.instance.repository()
        RuleProvider.instance.rules()
        createModelComponents()
        createPatternInstance()

        // when
        List<Finding> results = fixture.detect(inst)

        // then
        the(results.size()).shouldBeEqual(0)
    }

    @Test
    void "test detect happy path with pig"() {
        // given
        RuleProvider.instance.repository()
        RuleProvider.instance.rules()
        createModelComponents()
        createPatternInstance()

        typeA.associatedTo(typeC)

        // when
        List<Finding> results = fixture.detect(inst)

        // then
        the(results.size()).shouldBeEqual(1)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test detect with null pattern"() {
        // given
        createModelComponents()

        // when
        fixture.detect(inst)
    }

    @Test
    void "test constructPatternGraph happy path"() {
        // given
        createModelComponents()
        createPatternInstance()

        // when
        Network<Node, Relationship> result = fixture.constructPatternGraph(inst)
        result.edges().each {
            println result.incidentNodes(it)
        }

        // then
        the(result.nodes().size()).shouldBeEqual(9)
        the(result.edges().size()).shouldBeEqual(10)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test constructPatternGraph inst is null"() {
        // given
        createModelComponents()

        // when
        fixture.constructPatternGraph(inst)
    }

    @Test
    void "test handleCreatingRelationships both nodes known"() {
        createModelComponents()
        fixture.nodes.put(typeA, GraphElementFactory.instance.createNode(typeA))
        fixture.nodes.put(typeB, GraphElementFactory.instance.createNode(typeB))
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        Type towards = typeA
        Type type = typeB
        RelationshipType rel = RelationshipType.Association

        // when
        fixture.createRelationship(towards, type, graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(2)
        the(graph.edges().size()).shouldBeEqual(1)
        the(graph.hasEdgeConnecting(fixture.nodes.get(type), fixture.nodes.get(towards)))
    }

    @Test
    void "test handleCreatingRelationships both nodes unknown"() {
        createModelComponents()
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        Type towards = typeA
        Type type = typeB
        RelationshipType rel = RelationshipType.Association

        // when
        fixture.handleCreatingRelationships(towards, type, graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(2)
        the(graph.edges().size()).shouldBeEqual(1)
        the(graph.hasEdgeConnecting(fixture.nodes.get(type), fixture.nodes.get(towards)))
    }

    @Test
    void "test handleCreatingRelationships only towards unknown"() {
        createModelComponents()
        fixture.nodes.put(typeB, GraphElementFactory.instance.createNode(typeB))
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        Type towards = typeA
        Type type = typeB
        RelationshipType rel = RelationshipType.Association

        // when
        fixture.handleCreatingRelationships(towards, type, graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(2)
        the(graph.edges().size()).shouldBeEqual(1)
        the(graph.hasEdgeConnecting(fixture.nodes.get(type), fixture.nodes.get(towards)))
    }

    @Test(expected = IllegalArgumentException.class)
    void "test handleCreatingRelationships towards is null"() {
        // given
        createModelComponents()
        Type towards = null
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = RelationshipType.Association

        // when
        fixture.handleCreatingRelationships(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test handleCreatingRelationships type is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = null
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = RelationshipType.Association

        // when
        fixture.handleCreatingRelationships(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test handleCreatingRelationships graph is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = null
        def rel = RelationshipType.Association

        // when
        fixture.handleCreatingRelationships(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test handleCreatingRelationship rel is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = null

        // when
        fixture.handleCreatingRelationships(towards, type, graph, rel)
    }

    @Test
    void "test createRelationship happy path"() {
        // given
        createModelComponents()
        fixture.nodes.put(typeA, GraphElementFactory.instance.createNode(typeA))
        fixture.nodes.put(typeB, GraphElementFactory.instance.createNode(typeB))
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        Type towards = typeA
        Type type = typeB
        RelationshipType rel = RelationshipType.Association

        // when
        fixture.createRelationship(towards, type, graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(2)
        the(graph.edges().size()).shouldBeEqual(1)
        the(graph.hasEdgeConnecting(fixture.nodes.get(type), fixture.nodes.get(towards)))
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createRelationship towards is null"() {
        // given
        createModelComponents()
        Type towards = null
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = RelationshipType.Association

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createRelationship type is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = null
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = RelationshipType.Association

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createRelationship graph is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = null
        def rel = RelationshipType.Association

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createRelationship rel is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = null

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test
    void "test createNodeAndRelationship happy path"() {
        // given
        createModelComponents()
        fixture.nodes.put(typeA, GraphElementFactory.instance.createNode(typeA))
        Network<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()

        // when
        fixture.createNodeAndRelationship(typeB, typeA, graph, RelationshipType.Association)

        // then
        the(graph.nodes().size()).shouldBeEqual(2)
        the(graph.edges().size()).shouldBeEqual(1)
        the(graph.hasEdgeConnecting(fixture.nodes.get(typeA), fixture.nodes.get(typeB)))
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createNodeAndRelationship towards is null"() {
        // given
        createModelComponents()
        Type towards = null
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = RelationshipType.Association

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createNodeAndRelationship type is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = null
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = RelationshipType.Association

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createNodeAndRelationship graph is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = null
        def rel = RelationshipType.Association

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createNodeAndRelationship rel is null"() {
        // given
        createModelComponents()
        Type towards = typeB
        Type type = typeA
        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()
        def rel = null

        // when
        fixture.createNodeAndRelationship(towards, type, graph, rel)
    }

    @Test
    void "test markInternalOrExternal happy path"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = fixture.constructPatternGraph(inst)

        // when
        fixture.markInternalOrExternal(graph, inst)

        // then
        the(fixture.nodes[typeA].internal).shouldBeTrue()
        the(fixture.nodes[typeB].internal).shouldBeTrue()
        the(fixture.nodes[typeC].internal).shouldBeTrue()
        the(fixture.nodes[typeD].internal).shouldBeTrue()
        the(fixture.nodes[typeE].internal).shouldBeTrue()
        the(fixture.nodes[typeF].internal).shouldBeFalse()
        the(fixture.nodes[typeG].internal).shouldBeFalse()
        the(fixture.nodes[typeH].internal).shouldBeFalse()
        the(fixture.nodes[typeI].internal).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markInternalOrExternal graph is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = null

        // when
        fixture.markInternalOrExternal(graph, inst)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markInternalOrExternal inst is null"() {
        // given
        createModelComponents()
        Network<Node, Relationship> graph = fixture.constructPatternGraph(inst)

        // when
        fixture.markInternalOrExternal(graph, inst)
    }

    @Test
    void "test markValidOrInvalid happy path"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        fixture.markInternalOrExternal(graph, inst)
        fixture.markTemporaryOrPersistent(graph)

        // when
        fixture.markValidOrInvalid(graph, inst)

        // then
        the(graph.edgeConnecting(fixture.nodes[typeF], fixture.nodes[typeA]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeA], fixture.nodes[typeB]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeB], fixture.nodes[typeH]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeC], fixture.nodes[typeB]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeD], fixture.nodes[typeB]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeE], fixture.nodes[typeB]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeG], fixture.nodes[typeB]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeC], fixture.nodes[typeI]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeD], fixture.nodes[typeI]).get().invalid).shouldBeFalse()
        the(graph.edgeConnecting(fixture.nodes[typeE], fixture.nodes[typeI]).get().invalid).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markValidOrInvalid null graph"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = null

        // when
        fixture.markValidOrInvalid(graph, inst)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markValidOrInvalid null pattern instance"() {
        // given
        createModelComponents()
        Network<Node, Relationship> graph = fixture.constructPatternGraph()

        // when
        fixture.markValidOrInvalid(graph, inst)
    }

    @Test
    void "test markTemporaryOrPersistent happy path"() {
        // given
        Network<Node, Relationship> graph = createGraph()

        // when
        fixture.markTemporaryOrPersistent(graph)

        // then
        the(graph.edgeConnecting(f, a).get().persistent).shouldBeTrue() // f->a
        the(graph.edgeConnecting(a, b).get().persistent).shouldBeTrue() // a->b
        the(graph.edgeConnecting(g, b).get().persistent).shouldBeFalse() // g->b
        the(graph.edgeConnecting(b, h).get().persistent).shouldBeTrue() // b->h
        the(graph.edgeConnecting(c, b).get().persistent).shouldBeTrue() // c->b
        the(graph.edgeConnecting(d, b).get().persistent).shouldBeTrue() // d->b
        the(graph.edgeConnecting(e, b).get().persistent).shouldBeTrue() // e->b
        the(graph.edgeConnecting(c, i).get().persistent).shouldBeFalse() // c->i
        the(graph.edgeConnecting(d, i).get().persistent).shouldBeFalse() // d->i
        the(graph.edgeConnecting(e, i).get().persistent).shouldBeTrue() // e->i
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markTemporaryOrPersistent graph is null"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.markTemporaryOrPersistent(graph)
    }

    @Test
    void "test calculateDeltas happy path"() {
        // given
        Network<Node, Relationship> graph = createGraph()

        // when
        fixture.calculateDeltas(graph)

        // then
        the(fixture.metrics.get(graph.edgeConnecting(f, a).get(), "Ca")).shouldBeEqual(1) // f->a
        the(fixture.metrics.get(graph.edgeConnecting(f, a).get(), "Ce")).shouldBeEqual(0)
        the(fixture.metrics.get(graph.edgeConnecting(a, b).get(), "Ca")).shouldBeEqual(0) // a->b
        the(fixture.metrics.get(graph.edgeConnecting(a, b).get(), "Ce")).shouldBeEqual(0)
        the(fixture.metrics.get(graph.edgeConnecting(g, b).get(), "Ca")).shouldBeEqual(1) // g->b
        the(fixture.metrics.get(graph.edgeConnecting(g, b).get(), "Ce")).shouldBeEqual(0)
        the(fixture.metrics.get(graph.edgeConnecting(b, h).get(), "Ca")).shouldBeEqual(0) // b->h
        the(fixture.metrics.get(graph.edgeConnecting(b, h).get(), "Ce")).shouldBeEqual(1)
        the(fixture.metrics.get(graph.edgeConnecting(c, b).get(), "Ca")).shouldBeEqual(0) // c->b
        the(fixture.metrics.get(graph.edgeConnecting(c, b).get(), "Ce")).shouldBeEqual(0)
        the(fixture.metrics.get(graph.edgeConnecting(d, b).get(), "Ca")).shouldBeEqual(0) // d->b
        the(fixture.metrics.get(graph.edgeConnecting(d, b).get(), "Ce")).shouldBeEqual(0)
        the(fixture.metrics.get(graph.edgeConnecting(e, b).get(), "Ca")).shouldBeEqual(0) // e->b
        the(fixture.metrics.get(graph.edgeConnecting(e, b).get(), "Ce")).shouldBeEqual(0)
        the(fixture.metrics.get(graph.edgeConnecting(c, i).get(), "Ca")).shouldBeEqual(0) // c->i
        the(fixture.metrics.get(graph.edgeConnecting(c, i).get(), "Ce")).shouldBeEqual(1)
        the(fixture.metrics.get(graph.edgeConnecting(d, i).get(), "Ca")).shouldBeEqual(0) // d->i
        the(fixture.metrics.get(graph.edgeConnecting(d, i).get(), "Ce")).shouldBeEqual(1)
        the(fixture.metrics.get(graph.edgeConnecting(e, i).get(), "Ca")).shouldBeEqual(0) // e->i
        the(fixture.metrics.get(graph.edgeConnecting(e, i).get(), "Ce")).shouldBeEqual(1)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test calculateDeltas graph is null"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.calculateDeltas(graph)
    }

    @Test
    void "test afferentCoupling happy path rel is null"() {
        // given
        Network<Node, Relationship> graph = createGraph()
        Relationship rel = null

        // when
        int result = fixture.afferentCoupling(graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(9)
        the(graph.edges().size()).shouldBeEqual(10)
        the(result).shouldBeEqual(2)
    }

    @Test
    void "test afferentCoupling happy path rel affects afferent coupling"() {
        // given
        Network<Node, Relationship> graph = createGraph()
        Relationship rel = graph.edgeConnecting(f, a).get()

        // when
        int result = fixture.afferentCoupling(graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(9)
        the(graph.edges().size()).shouldBeEqual(10)
        the(result).shouldBeEqual(1)
    }

    @Test
    void "test afferentCoupling happy path rel does not affect afferent coupling"() {
        // given
        Network<Node, Relationship> graph = createGraph()
        Relationship rel = graph.edgeConnecting(a, b).get()

        // when
        int result = fixture.afferentCoupling(graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(9)
        the(graph.edges().size()).shouldBeEqual(10)
        the(result).shouldBeEqual(2)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test afferentCoupling graph is null"() {
        // given
        Network<Node, Relationship> graph = null
        Relationship rel = null

        // when
        fixture.afferentCoupling(graph, rel)
    }

    @Test
    void "test efferentCoupling happy path rel is null"() {
        // given
        Network<Node, Relationship> graph = createGraph()
        Relationship rel = null

        // when
        int result = fixture.efferentCoupling(graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(9)
        the(graph.edges().size()).shouldBeEqual(10)
        the(result).shouldBeEqual(4)
    }

    @Test
    void "test efferentCoupling happy path rel affects afferent coupling"() {
        // given
        Network<Node, Relationship> graph = createGraph()
        Relationship rel = graph.edgeConnecting(c, i).get()

        // when
        int result = fixture.efferentCoupling(graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(9)
        the(graph.edges().size()).shouldBeEqual(10)
        the(result).shouldBeEqual(3)
    }

    @Test
    void "test efferentCoupling happy path rel does not affect afferent coupling"() {
        // given
        Network<Node, Relationship> graph = createGraph()
        Relationship rel = graph.edgeConnecting(a, b).get()

        // when
        int result = fixture.efferentCoupling(graph, rel)

        // then
        the(graph.nodes().size()).shouldBeEqual(9)
        the(graph.edges().size()).shouldBeEqual(10)
        the(result).shouldBeEqual(4)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test efferentCoupling graph is null"() {
        // given
        Network<Node, Relationship> graph = null
        Relationship rel = null

        // when
        fixture.efferentCoupling(graph, rel)
    }

    @Test
    @Parameters([
            "true,  true,  true,  true,  0, 0, isuese:grime:modular-grime:pig", // PIG
            "true,  true,  true,  false, 0, 0, isuese:grime:modular-grime:tig", // TIG
            "false, true,  true,  true,  1, 0, isuese:grime:modular-grime:peag", // PEAG
            "true,  false, true,  true,  0, 1, isuese:grime:modular-grime:peeg", // PEEG
            "false, true,  true,  false, 1, 0, isuese:grime:modular-grime:teag", // TEAG
            "true,  false, true,  false, 0, 1, isuese:grime:modular-grime:teeg", // TEEG
            "true,  false, true,  true,  1, 0, ", // nothing
            "false, true,  true,  true,  0, 1, ", // nothing
            "true,  false, true,  false, 1, 0, ", // nothing
            "false, true,  true,  false, 0, 1, ", // nothing
            "false, false, true,  true,  1, 0, ", // nothing
            "false, false, true,  true,  0, 1, ", // nothing
            "false, false, true,  false, 1, 0, ", // nothing
            "false, false, true,  false, 0, 1, ", // nothing
            "false, false, false, true,  1, 0, ", // nothing
            "false, false, false, true,  0, 1, ", // nothing
            "false, false, false, false, 1, 0, ", // nothing
            "false, false, false, false, 0, 1, ", // nothing
    ])
    void detectGrime(boolean srcInternal, boolean destInternal, boolean invalid, boolean persistent, int ca, int ce, String key) {
        // given
        RuleProvider.instance.repository()
        RuleProvider.instance.rules()
        Network<Node, Relationship> network = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(2)
                .expectedEdgeCount(1)
                .build()

        Node src = new Node(name: "src", type: NodeType.Class, internal: srcInternal)
        Node dest = new Node(name: "dest", type: NodeType.Class, internal: destInternal)
        Relationship rel = new Relationship(invalid: invalid, persistent: persistent, type: RelationshipType.Aggregation)
        fixture.metrics.put(rel, "Ca", ca)
        fixture.metrics.put(rel, "Ce", ce)
        network.addNode(src)
        network.addNode(dest)
        network.addEdge(src, dest, rel)
        fixture.nodes.inverse()[src] = Class.builder().name("src").compKey("src").create()
        fixture.nodes.inverse()[dest] = Class.builder().name("dest").compKey("dest").create()

        // when
        List<Finding> results = fixture.detectGrime(network)

        // then
        if (key) {
            the(results.size()).shouldBeEqual(1)
            the(results.first().getFindingKey()).shouldBeEqual(key)
        } else {
            the(results.size()).shouldBeEqual(0)
        }
    }

    @Test(expected = IllegalArgumentException.class)
    void "test detectGrime graph is null"() {
        // given
        Network<Node, Relationship> network = null

        // when
        fixture.detectGrime(network)
    }

    @Test
    void "test createBindingsMap happy path"() {
        // given
        createModelComponents()
        createPatternInstance()
        SPS sps = fixture.loadRBML(inst)
        PatternInstance pattern = inst

        // when
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> result = fixture.createBindingsMap(sps, pattern)

        // then
        the(result.size()).shouldBeEqual(3)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createBindingsMap sps is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        SPS sps = null
        PatternInstance pattern = inst

        // when
        fixture.createBindingsMap(sps, pattern)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createBindingsMap pattern is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        SPS sps = fixture.loadRBML(inst)
        PatternInstance pattern = null

        // when
        fixture.createBindingsMap(sps, pattern)
    }

    @Test
    void "test validateRoleMatchingRelationships happy path"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = createGraph()
        SPS sps = fixture.loadRBML(inst)
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = fixture.createBindingsMap(sps, inst)

        Relationship rel1 = graph.edgeConnecting(a, b).get()
        Relationship rel2 = graph.edgeConnecting(c, b).get()
        Relationship rel3 = graph.edgeConnecting(d, b).get()
        Relationship rel4 = graph.edgeConnecting(e, b).get()

        // when
        fixture.validateRoleMatchingRelationships(graph, sps, bindings)

        // then
        the(rel1.invalid).shouldBeFalse()
        the(rel2.invalid).shouldBeFalse()
        the(rel3.invalid).shouldBeFalse()
        the(rel4.invalid).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateRoleMatchingRelationships graph is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = null
        SPS sps = fixture.loadRBML(inst)
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = fixture.createBindingsMap(sps, inst)

        // when
        fixture.validateRoleMatchingRelationships(graph, sps, bindings)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateRoleMatchingRelationships sps is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = createGraph()
        SPS sps = null
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = fixture.createBindingsMap(sps, inst)

        // when
        fixture.validateRoleMatchingRelationships(graph, sps, bindings)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateRoleMatchingRelationships bindings is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        Network<Node, Relationship> graph = createGraph()
        SPS sps = fixture.loadRBML(inst)
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = null

        // when
        fixture.validateRoleMatchingRelationships(graph, sps, bindings)
    }

    @Test
    void "test markValidOrInvalidGenHierRelationships happy path"() {
        // given
        createModelComponents()
        createPatternInstance()
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        SPS sps = fixture.loadRBML(inst)
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = fixture.createBindingsMap(sps, inst)

        Relationship rel1 = graph.edgeConnecting(c, b).get()
        rel1.invalid = false
        rel1.marked = true
        Relationship rel2 = graph.edgeConnecting(d, b).get()
        rel2.invalid = false
        rel2.marked = true
        Relationship rel3 = graph.edgeConnecting(e, b).get()
        rel3.invalid = false
        rel3.marked = true
        Relationship rel4 = new Relationship(type: RelationshipType.Generalization)
        Relationship rel5 = new Relationship(type: RelationshipType.Generalization)
        Relationship rel6 = new Relationship(type: RelationshipType.Association)
        graph.addEdge(b, c, rel4)
        graph.addEdge(i, e, rel5)
        graph.addEdge(b, d, rel6)

        // when
        fixture.markValidOrInvalidGenHierRelationships(graph, sps, bindings)

        // then
        the(rel1.invalid).shouldBeFalse()
        the(rel2.invalid).shouldBeFalse()
        the(rel3.invalid).shouldBeFalse()
        the(rel4.invalid).shouldBeTrue()
        the(rel5.invalid).shouldBeTrue()
        the(rel6.invalid).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markValidOrInvalidGenHierRelationships graph is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        MutableNetwork<Node, Relationship> graph = null
        SPS sps = fixture.loadRBML(inst)
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = fixture.createBindingsMap(sps, inst)

        // when
        fixture.markValidOrInvalidGenHierRelationships(graph, sps, bindings)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markValidOrInvalidGenHierRelationships sps is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        SPS sps = null
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = fixture.createBindingsMap(sps, inst)

        // when
        fixture.markValidOrInvalidGenHierRelationships(graph, sps, bindings)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markValidOrInvalidGenHierRelationships bindings is null"() {
        // given
        createModelComponents()
        createPatternInstance()
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        SPS sps = fixture.loadRBML(inst)
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = null

        // when
        fixture.markValidOrInvalidGenHierRelationships(graph, sps, bindings)
    }

    @Test
    void "test validateCorrectInheritanceRelations happy path"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        Relationship rel1 = graph.edgeConnecting(c, b).get()
        Relationship rel2 = graph.edgeConnecting(d, b).get()
        Relationship rel3 = graph.edgeConnecting(e, b).get()

        // when
        fixture.validateCorrectInheritanceRelations(graph, superTypes, subTypes)

        // then
        the(rel1.invalid).shouldBeFalse()
        the(rel2.invalid).shouldBeFalse()
        the(rel3.invalid).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateCorrectInheritanceRelations graph is null"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = null

        // when
        fixture.validateCorrectInheritanceRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateCorrectInheritanceRelations superTypes is null"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = null
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.validateCorrectInheritanceRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateCorrectInheritanceRelations superTypes is empty"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = []
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.validateCorrectInheritanceRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateCorrectInheritanceRelations subTypes is null"() {
        // given
        createModelComponents()
        List<Type> subTypes = null
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.validateCorrectInheritanceRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateCorrectInheritanceRelations subTypes is empty"() {
        // given
        createModelComponents()
        List<Type> subTypes = []
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.validateCorrectInheritanceRelations(graph, superTypes, subTypes)
    }

    @Test
    void "test invalidateSuperToSubtypeRelations happy path"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        Relationship rel = new Relationship(type: RelationshipType.Generalization)
        Relationship rel2 = new Relationship(type: RelationshipType.Generalization)

        graph.addEdge(b, c, rel)
        graph.addEdge(b, d, rel2)

        // when
        fixture.invalidateSuperToSubtypeRelations(graph, superTypes, subTypes)

        // then
        the(rel.invalid).shouldBeTrue()
        the(rel2.invalid).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateSuperToSubtypeRelations subTypes is null"() {
        // given
        createModelComponents()
        List<Type> subTypes = null
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.invalidateSuperToSubtypeRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateSuperToSubtypeRelations subTypes is empty"() {
        // given
        createModelComponents()
        List<Type> subTypes = []
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.invalidateSuperToSubtypeRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateSuperToSubtypeRelations superTypes is null"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = null
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.invalidateSuperToSubtypeRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateSuperToSubtypeRelations superTypes is empty"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = []
        MutableNetwork<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()

        // when
        fixture.invalidateSuperToSubtypeRelations(graph, superTypes, subTypes)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateSuperToSubtypeRelations graph is null"() {
        // given
        createModelComponents()
        List<Type> subTypes = [typeC, typeD, typeE]
        List<Type> superTypes = [typeB]
        MutableNetwork<Node, Relationship> graph = null
        associateNodesAndTypes()

        // when
        fixture.invalidateSuperToSubtypeRelations(graph, superTypes, subTypes)
    }

    @Test
    void "test invalidateUnmarkedIncomingInheritanceRelations happy path"() {
        createModelComponents()
        Network<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        List<Type> types = [typeA, typeB, typeC, typeD, typeE]
        Relationship r1 = graph.edgeConnecting(e, b).get()
        Relationship r2 = graph.edgeConnecting(d, b).get()
        Relationship r3 = graph.edgeConnecting(c, b).get()

        // when
        fixture.invalidateUnmarkedIncomingInheritanceRelations(graph, types)

        // then
        the(r1.invalid).shouldBeTrue()
        the(r2.invalid).shouldBeTrue()
        the(r3.invalid).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateUnmarkedIncomingInheritanceRelations graph is null"() {
        createModelComponents()
        Network<Node, Relationship> graph = null
        associateNodesAndTypes()
        List<Type> types = [typeA, typeB, typeC, typeD, typeE]

        // when
        fixture.invalidateUnmarkedIncomingInheritanceRelations(graph, types)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateUnmarkedIncomingInheritanceRelations types is null"() {
        createModelComponents()
        Network<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        List<Type> types = null

        // when
        fixture.invalidateUnmarkedIncomingInheritanceRelations(graph, types)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateUnmarkedIncomingInheritanceRelations types is empty"() {
        createModelComponents()
        Network<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        List<Type> types = []

        // when
        fixture.invalidateUnmarkedIncomingInheritanceRelations(graph, types)
    }

    @Test
    void "test invalidateUnmarkedIncomingRelations happy path"() {
        // given
        createModelComponents()
        Network<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        List<Type> types = [typeA, typeB, typeC, typeD, typeE]
        Relationship r1 = graph.edgeConnecting(f, a).get()
        Relationship r2 = graph.edgeConnecting(g, b).get()

        // when
        fixture.invalidateUnmarkedIncomingRelations(graph, types)

        // then
        the(r1.invalid).shouldBeTrue()
        the(r2.invalid).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateUnmarkedIncomingRelations graph is null"() {
        // given
        createModelComponents()
        Network<Node, Relationship> graph = null
        associateNodesAndTypes()
        List<Type> types = [typeA, typeB, typeC, typeD, typeE]
        Relationship r1 = graph.edgeConnecting(f, a).get()
        Relationship r2 = graph.edgeConnecting(g, b).get()

        // when
        fixture.invalidateUnmarkedIncomingRelations(graph, types)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateUnmarkedIncomingRelations types is null"() {
        // given
        createModelComponents()
        Network<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        List<Type> types = null
        Relationship r1 = graph.edgeConnecting(f, a).get()
        Relationship r2 = graph.edgeConnecting(g, b).get()

        // when
        fixture.invalidateUnmarkedIncomingRelations(graph, types)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test invalidateUnmarkedIncomingRelations types is empty"() {
        // given
        createModelComponents()
        Network<Node, Relationship> graph = createGraph()
        associateNodesAndTypes()
        List<Type> types = []
        Relationship r1 = graph.edgeConnecting(f, a).get()
        Relationship r2 = graph.edgeConnecting(g, b).get()

        // when
        fixture.invalidateUnmarkedIncomingRelations(graph, types)
    }

    @Test
    @Parameters
    void relationMatch(edu.montana.gsoc.msusel.rbml.model.Relationship rel, Relationship edge, boolean expected) {
        // given
        rel
        edge
        expected

        // when
        boolean result = fixture.relationMatch(rel, edge)

        // then
        the(result).shouldBeEqual(expected)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test relationMatch rel is null"() {
        // given
        edu.montana.gsoc.msusel.rbml.model.Relationship rel = null
        Relationship edge = new Relationship(type: RelationshipType.Generalization)

        // when
        fixture.relationMatch(rel, edge)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test relationMatch edge is null"() {
        // given
        edu.montana.gsoc.msusel.rbml.model.Relationship rel = Generalization.builder().create()
        Relationship edge = null

        // when
        fixture.relationMatch(rel, edge)
    }

    private Iterable<Object[]> parametersForRelationMatch() {
        [
                [Generalization.builder().create(), new Relationship(type: RelationshipType.Generalization), true].toArray(),
                [Realization.builder().create(), new Relationship(type: RelationshipType.Realization), true].toArray(),
                [Association.builder().create(), new Relationship(type: RelationshipType.Association), true].toArray(),
                [Aggregation.builder().create(), new Relationship(type: RelationshipType.Aggregation), true].toArray(),
                [Composition.builder().create(), new Relationship(type: RelationshipType.Composition), true].toArray(),
                [Usage.builder().create(), new Relationship(type: RelationshipType.UseDependency), true].toArray(),
                [Usage.builder().create(), new Relationship(type: RelationshipType.Dependency), true].toArray(),
                [Realization.builder().create(), new Relationship(type: RelationshipType.Generalization), false].toArray(),
                [Association.builder().create(), new Relationship(type: RelationshipType.Realization), false].toArray(),
                [Aggregation.builder().create(), new Relationship(type: RelationshipType.Composition), false].toArray(),
                [Composition.builder().create(), new Relationship(type: RelationshipType.Aggregation), false].toArray(),
                [Usage.builder().create(), new Relationship(type: RelationshipType.Association), false].toArray()
        ]
    }

    @Test
    void "test validateRemainingUnMarkedEdges happy path"() {
        // given
        createModelComponents()
        Network<Node, Relationship> graph = createGraph()

        // when
        fixture.validateRemainingUnMarkedEdges(graph)

        // then
        graph.edges().each { the(it.invalid).shouldBeFalse() }
    }

    @Test(expected = IllegalArgumentException.class)
    void "test validateRemainingUnMarkedEdges graph is null"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.validateRemainingUnMarkedEdges(graph)
    }

    void createModelComponents() {
        sys = System.builder().name("testdata").key("TestData").basePath("testdata").create()
        proj = Project.builder().name("testproj").version("1.0").relPath("testproj").create()
        Module mod = Module.builder().name("testmod").relPath("testmod").srcPath("src/main/java").create()
        ns1 = Namespace.builder().name("test").nsKey("test1").relPath("test").create()
        ns2 = Namespace.builder().name("test").nsKey("test2").relPath("test/test").create()
        ns3 = Namespace.builder().name("test2").nsKey("test3").relPath("test/test2").create()
        ns4 = Namespace.builder().name("test3").nsKey("test4").relPath("test3").create()
        ns5 = Namespace.builder().name("test4").nsKey("test5").relPath("test4").create()
        ns6 = Namespace.builder().name("test5").nsKey("test6").relPath("test/test5").create()
        ns1.addNamespace(ns2)
        ns1.addNamespace(ns3)
        mod.addNamespace(ns1)
        mod.addNamespace(ns4)
        mod.addNamespace(ns5)
        ns1.addNamespace(ns6)
        proj.addModule(mod)
        sys.addProject(proj)

        File fileA = File.builder().name("TypeA.java").relPath("TypeA.java").type(FileType.SOURCE).start(1).end(8).create()
        typeA = Class.builder().name("TypeA").accessibility(Accessibility.PUBLIC).start(3).end(8).create()
        fileA.addType(typeA)
        ns4.addFile(fileA)

        File fileB = File.builder().name("TypeB.java").relPath("TypeB.java").type(FileType.SOURCE).start(1).end(7).create()
        typeB = Class.builder().name("TypeB").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
        typeB.addModifier("ABSTRACT")
        fileB.addType(typeB)
        ns2.addFile(fileB)

        File fileC = File.builder().name("TypeC.java").relPath("TypeC.java").type(FileType.SOURCE).start(1).end(8).create()
        typeC = Class.builder().name("TypeC").accessibility(Accessibility.PUBLIC).start(3).end(8).create()
        fileC.addType(typeC)
        ns5.addFile(fileC)

        File fileD = File.builder().name("TypeD.java").relPath("TypeD.java").type(FileType.SOURCE).start(1).end(7).create()
        typeD = Class.builder().name("TypeD").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
        fileD.addType(typeD)
        ns2.addFile(fileD)

        File fileE = File.builder().name("TypeE.java").relPath("TypeE.java").type(FileType.SOURCE).start(1).end(7).create()
        typeE = Class.builder().name("TypeE").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
        fileE.addType(typeE)
        ns2.addFile(fileE)

        File fileF = File.builder().name("TypeF.java").relPath("TypeF.java").type(FileType.SOURCE).start(1).end(7).create()
        typeF = Class.builder().name("TypeF").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
        fileF.addType(typeF)
        ns1.addFile(fileF)

        File fileG = File.builder().name("TypeG.java").relPath("TypeG.java").type(FileType.SOURCE).start(1).end(7).create()
        typeG = Class.builder().name("TypeG").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
        fileG.addType(typeG)
        ns2.addFile(fileG)

        File fileH = File.builder().name("TypeH.java").relPath("TypeH.java").type(FileType.SOURCE).start(1).end(5).create()
        typeH = Class.builder().name("TypeH").accessibility(Accessibility.PUBLIC).start(3).end(5).create()
        fileH.addType(typeH)
        ns3.addFile(fileH)

        File fileI = File.builder().name("TypeI.java").relPath("TypeI.java").type(FileType.SOURCE).start(1).end(5).create()
        typeI = Class.builder().name("TypeI").accessibility(Accessibility.PUBLIC).start(3).end(5).create()
        fileI.addType(typeI)
        ns3.addFile(fileI)

        sys.updateKeys()
        typeA.refresh()
        typeB.refresh()
        typeC.refresh()
        typeD.refresh()
        typeE.refresh()
        typeF.refresh()
        typeG.refresh()
        typeH.refresh()
        typeI.refresh()
        fileA.refresh()
        fileB.refresh()
        fileC.refresh()
        fileD.refresh()
        fileE.refresh()
        fileF.refresh()
        fileG.refresh()
        fileH.refresh()
        fileI.refresh()
        ns1.refresh()
        ns2.refresh()
        ns3.refresh()
        ns4.refresh()
        ns5.refresh()
        ns6.refresh()

        Field fldAB = Field.builder().name("typeab").type(typeB.createTypeRef()).accessibility(Accessibility.PRIVATE).start(5).end(5).create()
        typeA.addMember(fldAB)
        Field fldCB = Field.builder().name("typecb").type(typeB.createTypeRef()).accessibility(Accessibility.PRIVATE).start(5).end(5).create()
        typeC.addMember(fldCB)

        Method mA = Method.builder().name("methodA").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(6).end(7).create()
        typeA.addMember(mA)
        Method mB = Method.builder().name("methodB").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
        typeB.addMember(mB)
        Method mC = Method.builder().name("methodC").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(6).end(7).create()
        typeC.addMember(mC)
        Method mD = Method.builder().name("methodD").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
        typeD.addMember(mD)
        Method mE = Method.builder().name("methodE").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
        typeE.addMember(mE)
        Method mF = Method.builder().name("methodF").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
        typeF.addMember(mF)
        Method mG = Method.builder().name("methodG").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
        typeG.addMember(mG)

        typeA.associatedTo(typeB)
        typeF.associatedTo(typeA)
        typeB.associatedTo(typeH)
        typeG.associatedTo(typeB)
        typeC.generalizedBy(typeB)
        typeD.generalizedBy(typeB)
        typeE.generalizedBy(typeB)
        typeC.associatedTo(typeI)
        typeD.associatedTo(typeI)
        typeE.associatedTo(typeI)

        sys.updateKeys()
    }

    private void associateNodesAndTypes() {
        fixture.nodes.put(typeA, a)
        fixture.nodes.put(typeB, b)
        fixture.nodes.put(typeC, c)
        fixture.nodes.put(typeD, d)
        fixture.nodes.put(typeE, e)
        fixture.nodes.put(typeF, f)
        fixture.nodes.put(typeG, g)
        fixture.nodes.put(typeH, h)
        fixture.nodes.put(typeI, i)
    }

    private void createPatternInstance() {
        Pattern p = Pattern.findFirst("name = ?", "Strategy")
        inst = PatternInstance.builder().instKey().create()
        p.addInstance(inst)
        proj.addPatternInstance(inst)

        Role roleA = Role.builder().name("Context").type(RoleType.CLASSIFIER).create()
        Role roleB = Role.builder().name("AbstractStrategy").type(RoleType.CLASSIFIER).create()
        Role roleC = Role.builder().name("ConcreteStrategy").type(RoleType.CLASSIFIER).create()
        Role roleAB = Role.builder().name("RoleAB").type(RoleType.RELATION).create()
        Role roleCB = Role.builder().name("RoleCB").type(RoleType.RELATION).create()
        Role roleDB = Role.builder().name("RoleDC").type(RoleType.RELATION).create()
        Role roleEB = Role.builder().name("RoleEB").type(RoleType.RELATION).create()

        Role roleMA = Role.builder().name("RoleMA").type(RoleType.BEHAVE_FEAT).create()

        p.addRole(roleA)
        p.addRole(roleB)
        p.addRole(roleC)
//        p.addRole(roleAB)
//        p.addRole(roleCB)
//        p.addRole(roleDB)
//        p.addRole(roleEB)
        p.addRole(roleMA)

        Method methodA = Method.findFirst("name = ?", "methodA")

        RoleBinding rbA = RoleBinding.of(roleA, typeA.createReference())
        RoleBinding rbB = RoleBinding.of(roleB, typeB.createReference())
        RoleBinding rbC = RoleBinding.of(roleC, typeC.createReference())
        RoleBinding rbD = RoleBinding.of(roleC, typeD.createReference())
        RoleBinding rbE = RoleBinding.of(roleC, typeE.createReference())
        RoleBinding rbMA = RoleBinding.of(roleMA, methodA.createReference())
//        RoleBinding rbAB = RoleBinding.of(roleAB, Relation.findBetween(typeA, typeB, edu.isu.isuese.datamodel.RelationType.ASSOCIATION).createReference())
//        RoleBinding rbCB = RoleBinding.of(roleCB, Relation.findBetween(typeB, typeC, edu.isu.isuese.datamodel.RelationType.ASSOCIATION).createReference())
//        RoleBinding rbDB = RoleBinding.of(roleDB, Relation.findBetween(typeD, TypeB, edu.isu.isuese.datamodel.RelationType.GENERALIZATION).createReference())
//        RoleBinding rbEB = RoleBinding.of(roleEB, Relation.findBetween(typeE, TypeB, edu.isu.isuese.datamodel.RelationType.GENERALIZATION).createReference())

        inst.addRoleBinding(rbA)
        inst.addRoleBinding(rbB)
        inst.addRoleBinding(rbC)
        inst.addRoleBinding(rbD)
        inst.addRoleBinding(rbE)
        inst.addRoleBinding(rbMA)
//        inst.addRoleBinding(rbAB)
//        inst.addRoleBinding(rbCB)
//        inst.addRoleBinding(rbDB)
//        inst.addRoleBinding(rbEB)
    }

    private MutableNetwork<Node, Relationship> createGraph() {
        MutableNetwork<Node, Relationship> network = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(10)
                .expectedEdgeCount(10)
                .build()

        a = new Node(name: "A", type: NodeType.Class, internal: true)
        b = new Node(name: "B", type: NodeType.Class, internal: true)
        c = new Node(name: "C", type: NodeType.Class, internal: true)
        d = new Node(name: "D", type: NodeType.Class, internal: true)
        e = new Node(name: "E", type: NodeType.Class, internal: true)
        f = new Node(name: "F", type: NodeType.Class, internal: false)
        g = new Node(name: "G", type: NodeType.Class, internal: false)
        h = new Node(name: "H", type: NodeType.Class, internal: false)
        i = new Node(name: "I", type: NodeType.Class, internal: false)

        network.addEdge(f, a, new Relationship(type: RelationshipType.Association, persistent: true, invalid: false))
        network.addEdge(a, b, new Relationship(type: RelationshipType.Aggregation, persistent: true, invalid: false))
        network.addEdge(b, h, new Relationship(type: RelationshipType.Composition, persistent: true, invalid: false))
        network.addEdge(g, b, new Relationship(type: RelationshipType.Dependency, persistent: true, invalid: false))
        network.addEdge(c, i, new Relationship(type: RelationshipType.UseDependency, persistent: true, invalid: false))
        network.addEdge(d, i, new Relationship(type: RelationshipType.PackageDependency, persistent: true, invalid: false))
        network.addEdge(e, i, new Relationship(type: RelationshipType.Association, persistent: true, invalid: false))
        network.addEdge(c, b, new Relationship(type: RelationshipType.Generalization, persistent: true, invalid: false))
        network.addEdge(d, b, new Relationship(type: RelationshipType.Realization, persistent: true, invalid: false))
        network.addEdge(e, b, new Relationship(type: RelationshipType.Generalization, persistent: true, invalid: false))

        network
    }
}