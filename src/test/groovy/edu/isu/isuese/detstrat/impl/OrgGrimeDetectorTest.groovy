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

import com.google.common.graph.Network
import edu.isu.isuese.datamodel.Accessibility
import edu.isu.isuese.datamodel.Class
import edu.isu.isuese.datamodel.Field
import edu.isu.isuese.datamodel.File
import edu.isu.isuese.datamodel.FileType
import edu.isu.isuese.datamodel.Finding
import edu.isu.isuese.datamodel.Method
import edu.isu.isuese.datamodel.Module
import edu.isu.isuese.datamodel.Namespace
import edu.isu.isuese.datamodel.Pattern
import edu.isu.isuese.datamodel.PatternInstance
import edu.isu.isuese.datamodel.Project
import edu.isu.isuese.datamodel.Role
import edu.isu.isuese.datamodel.RoleBinding
import edu.isu.isuese.datamodel.RoleType
import edu.isu.isuese.datamodel.System
import edu.isu.isuese.datamodel.Type
import edu.isu.isuese.datamodel.TypeRef
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.javalite.activejdbc.test.DBSpec
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner.class)
class OrgGrimeDetectorTest extends DBSpec {

    OrgGrimeDetector fixture

    PatternInstance inst
    System sys
    Project proj
    Namespace ns1, ns2, ns3, ns4, ns5, ns6
    Type typeA, typeB, typeC, typeD, typeE, typeF, typeG, typeH, typeI

    Node a, b, c, d, e, f, g, h, i
    Node nsN1, nsN2, nsN3, nsN4, nsN5, nsN6
    Map<String, Node> nsNodeMap = [:]

    @Before
    void setup() {
        RuleProvider.instance.repository()
        RuleProvider.instance.rules()
        fixture = new OrgGrimeDetector()
        createModelComponents()
        createGraphs()
        associateNodesAndTypes()
        createPatternInstance()
    }

    @Test
    void "test detect happy path"() {
        // given
        PatternInstance pattern = inst

        // when
        List<Finding> findings = fixture.detect(inst)

        // then
        the(findings.isEmpty()).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test detect pattern is null"() {
        // given
        PatternInstance pattern = null

        // when
        fixture.detect(inst)
    }

    @Test
    void "test markTypes happy path"() {
        // given
        PatternInstance pattern = inst

        // when
        fixture.markTypes(pattern)

        // then
        the(a.patternInternal).shouldBeTrue()
        the(b.patternInternal).shouldBeTrue()
        the(c.patternInternal).shouldBeTrue()
        the(d.patternInternal).shouldBeTrue()
        the(e.patternInternal).shouldBeTrue()
        the(f.patternInternal).shouldBeFalse()
        the(g.patternInternal).shouldBeFalse()
        the(h.patternInternal).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markTypes pattern is null"() {
        // given
        PatternInstance pattern = null

        // when
        fixture.markTypes(pattern)
    }

    @Test
    void "test markNamespaces happy path"() {
        // given
        def list = [ns4, ns2, ns5]

        // when
        fixture.markNamespaces(list)

        // then
        the(nsN1.patternInternal).shouldBeFalse()
        the(nsN2.patternInternal).shouldBeTrue()
        the(nsN3.patternInternal).shouldBeFalse()
        the(nsN4.patternInternal).shouldBeTrue()
        the(nsN5.patternInternal).shouldBeTrue()
        the(nsN6.patternInternal).shouldBeFalse()

        the(a.patternNSInternal).shouldBeTrue()
        the(b.patternNSInternal).shouldBeTrue()
        the(c.patternNSInternal).shouldBeTrue()
        the(d.patternNSInternal).shouldBeTrue()
        the(e.patternNSInternal).shouldBeTrue()
        the(f.patternNSInternal).shouldBeFalse()
        the(g.patternNSInternal).shouldBeTrue()
        the(h.patternNSInternal).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markNamespaces list is null"() {
        // given
        def list = null

        // when
        fixture.markNamespaces(list)
    }

    @Test
    void detectGrime() {
        Assert.fail()
    }

    @Test
    void "test createFinding happy path"() {
        // given
        String name = "PECG"
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN1, nsN4).get()
        Network<Node, NamespaceRelation> graph = fixture.nsGraph

        // when
        Finding result = fixture.createFinding(name, rel, graph)

        // then
        the(result).shouldNotBeNull()
    }

    @Test
    void "test createFinding happy path for unknown name"() {
        // given
        String name = "Other"
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN1, nsN4).get()
        Network<Node, NamespaceRelation> graph = fixture.nsGraph

        // when
        Finding result = fixture.createFinding(name, rel, graph)

        // then
        the(result).shouldBeNull()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding name is null"() {
        // given
        String name = null
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN1, nsN4).get()
        Network<Node, NamespaceRelation> graph = fixture.nsGraph

        // when
        fixture.createFinding(name, rel, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding name is empty"() {
        // given
        String name = ""
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN1, nsN4).get()
        Network<Node, NamespaceRelation> graph = fixture.nsGraph

        // when
        fixture.createFinding(name, rel, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding rel is null"() {
        // given
        String name = "PECG"
        NamespaceRelation rel = null
        Network<Node, Relationship> graph = fixture.nsGraph

        // when
        fixture.createFinding(name, rel, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding graph is null"() {
        // given
        String name = "PECG"
        NamespaceRelation rel = new NamespaceRelation()
        Network<Node, NamespaceRelation> graph = null

        // when
        fixture.createFinding(name, rel, graph)
    }

    @Test
    void "test createFinding for nodes happy path"() {
        // given
        String name = "PECG"
        Node node = a

        // when
        Finding result = fixture.createFinding(name, node)

        // then
        the(result).shouldNotBeNull()
    }

    @Test
    void "test createFinding for nodes happy path for unknown name"() {
        // given
        String name = "Other"
        Node node = a

        // when
        Finding result = fixture.createFinding(name, node)

        // then
        the(result).shouldBeNull()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding for nodes name is null"() {
        // given
        String name = null
        Node node = a

        // when
        fixture.createFinding(name, node)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding for nodes name is empty"() {
        // given
        String name = ""
        Node node = a

        // when
        fixture.createFinding(name, node)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding for nodes node is null"() {
        // given
        String name = "PECG"
        Node node = null

        // when
        fixture.createFinding(name, node)
    }

    @Test
    void "test findPatternNamespaces happy path"() {
        // given
        PatternInstance pattern = inst

        // when
        Set<Namespace> result = fixture.findPatternNamespaces(pattern)

        // then
        the(result).shouldContain(ns4)
        the(result).shouldContain(ns2)
        the(result).shouldContain(ns5)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test findPatternNamespaces null instance"() {
        // given
        PatternInstance pattern = null

        // when
        fixture.findPatternNamespaces(pattern)
    }

    @Test
    @Parameters([
            "nsN1, nsN4, false",
            "nsN4, nsN2, false",
            "nsN2, nsN3, true",
            "nsN5, nsN2, true",
            "nsN5, nsN3, false",
            "nsN3, nsN5, false",
    ])
    void "test cycle happy path"(String srcName, String destName, boolean expected) {
        // given
        fixture.nsGraph.addEdge(nsN3, nsN5, GraphElementFactory.instance.createNamespaceRelation([]))
        fixture.markCycles()
        Node src = nsNodeMap[srcName]
        Node dest = nsNodeMap[destName]
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(src, dest).get()

        // when
        boolean result = fixture.cycle(src, dest, rel)

        // then
        the(result).shouldBeEqual(expected)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test cycle src is null"() {
        // given
        fixture.nsGraph.addEdge(nsN3, nsN5, GraphElementFactory.instance.createNamespaceRelation([]))
        fixture.markCycles()
        Node src = null
        Node dest = nsN4
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN1, dest).get()

        // when
        fixture.cycle(src, dest, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test cycle dest is null"() {
        // given
        fixture.nsGraph.addEdge(nsN3, nsN5, GraphElementFactory.instance.createNamespaceRelation([]))
        fixture.markCycles()
        Node src = nsN1
        Node dest = null
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN1, nsN4).get()

        // when
        fixture.cycle(src, dest, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test cycle rel is null"() {
        // given
        fixture.nsGraph.addEdge(nsN3, nsN5, GraphElementFactory.instance.createNamespaceRelation([]))
        Node src = nsN1
        Node dest = nsN4
        NamespaceRelation rel = null

        // when
        fixture.cycle(src, dest, rel)
    }

    @Test
    void "test dropInstability happy path"() {
        // given
        fixture.mark(inst)
        fixture.measureCe()
        fixture.measureCa()
        fixture.measureI()
        fixture.measureNa()
        fixture.measureNc()
        fixture.measureA()
        fixture.measureD()

        NamespaceRelation rel14 = fixture.nsGraph.edgeConnecting(nsN1, nsN4).get()
        NamespaceRelation rel42 = fixture.nsGraph.edgeConnecting(nsN4, nsN2).get()
        NamespaceRelation rel52 = fixture.nsGraph.edgeConnecting(nsN5, nsN2).get()
        NamespaceRelation rel53 = fixture.nsGraph.edgeConnecting(nsN5, nsN3).get()
        NamespaceRelation rel23 = fixture.nsGraph.edgeConnecting(nsN2, nsN3).get()

        // when
        boolean result14 = fixture.dropInstability(rel14)
        boolean result42 = fixture.dropInstability(rel42)
        boolean result52 = fixture.dropInstability(rel52)
        boolean result53 = fixture.dropInstability(rel53)
        boolean result23 = fixture.dropInstability(rel23)

        // then
        the(result14).shouldBeTrue()
        the(result42).shouldBeFalse()
        the(result52).shouldBeTrue()
        the(result53).shouldBeFalse()
        the(result23).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test dropInstability relation is null"() {
        // given
        NamespaceRelation rel = null

        // when
        fixture.dropInstability(rel)
    }

    @Test
    void "test measureCohesionQ happy path"() {
        // given

        // when
        fixture.measureCohesionQ()

        fixture.cohesionQ(nsN2, null)
        println "pint.d: " + fixture.findPIntDSet(nsN2, null).size()

        // then
        the(fixture.nsMetrics.get(nsN1, "CohesionQ")).shouldBeEqual(0 / 1)
        the(fixture.nsMetrics.get(nsN2, "CohesionQ")).shouldBeEqual(3 / 8)
        the(fixture.nsMetrics.get(nsN3, "CohesionQ")).shouldBeEqual(0 / 4)
        the(fixture.nsMetrics.get(nsN4, "CohesionQ")).shouldBeEqual(0 / 2)
        the(fixture.nsMetrics.get(nsN5, "CohesionQ")).shouldBeEqual(0 / 2)
        the(fixture.nsMetrics.get(nsN6, "CohesionQ")).shouldBeEqual(0)
    }

    @Test
    void "test cohesionQ happy path null t"() {
        // given
        Node p = nsN2
        Node t = null

        // when
        double result = fixture.cohesionQ(p, t)

        // then
        the(result).shouldBeEqual((double) 3 / 8.0d)
    }

    @Test
    void "test cohesionQ happy path known t"() {
        // given
        Node p = nsN2
        Node t = b

        // when
        double result = fixture.cohesionQ(p, t)

        // then
        the(result).shouldBeEqual((double) 0 / 2.0d)
    }

    @Test
    void "test cohesionQ happy path unknown t"() {
        // given
        Node p = nsN2
        Node t = f

        // when
        double result = fixture.cohesionQ(p, t)

        // then
        the(result).shouldBeEqual((double) 3 / 8.0d)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test cohesionQ null p"() {
        // given
        Node p = null
        Node t = null

        // when
        fixture.cohesionQ(p, t)
    }

    @Test
    void "test measureCouplingQ happy path"() {
        // given

        // when
        fixture.measureCouplingQ()

        // then
        the(fixture.nsMetrics.get(nsN1, "CouplingQ")).shouldBeEqual(1 - 1 / 1)
        the(fixture.nsMetrics.get(nsN2, "CouplingQ")).shouldBeEqual(1 - 3 / 8)
        the(fixture.nsMetrics.get(nsN3, "CouplingQ")).shouldBeEqual(1 - 2 / 4)
        the(fixture.nsMetrics.get(nsN4, "CouplingQ")).shouldBeEqual(1 - 2 / 2)
        the(fixture.nsMetrics.get(nsN5, "CouplingQ")).shouldBeEqual(1 - 2 / 2)
        the(fixture.nsMetrics.get(nsN6, "CouplingQ")).shouldBeEqual(1 - 0)
    }

    @Test
    void "test couplingQ happy path null t"() {
        // given
        Node p = nsN2
        Node t = null

        // when
        double result = fixture.couplingQ(p, t)

        // then
        the(result).shouldBeEqual(1 - ((double) 3 / 8))
    }

    @Test
    void "test couplingQ happy path known t"() {
        // given
        Node p = nsN2
        Node t = b

        // when
        double result = fixture.couplingQ(p, t)

        // then
        the(result).shouldBeEqual(1 - 3 / 2)
    }

    @Test
    void "test couplingQ happy path unknown t"() {
        // given
        Node p = nsN2
        Node t = f

        // when
        double result = fixture.couplingQ(p, t)

        // then
        the(result).shouldBeEqual(1 - ((double) 3 / 8))
    }

    @Test(expected = IllegalArgumentException.class)
    void "test couplingQ null p"() {
        // given
        Node p = null
        Node t = null

        // when
        fixture.couplingQ(p, t)
    }

    @Test
    void "test getPcliP happy path"() {
        // given
        Node p = nsN2
        Node t = null

        // when
        Set<Node> result = fixture.getPcliP(p, t)

        // then
        the(result.size()).shouldBeEqual(2)
        the(result).shouldContain(nsN4)
        the(result).shouldContain(nsN5)
    }

    @Test
    void "test getPcliP happy path known t"() {
        // given
        Node p = nsN3
        Node t = c

        // when
        Set<Node> result = fixture.getPcliP(p, t)

        // then
        the(result.size()).shouldBeEqual(1)
        the(result).shouldContain(nsN2)
    }

    @Test
    void "test getPcliP happy path unknown t"() {
        // given
        Node p = nsN3
        Node t = f

        // when
        Set<Node> result = fixture.getPcliP(p, t)

        // then
        the(result.size()).shouldBeEqual(2)
        the(result).shouldContain(nsN2)
        the(result).shouldContain(nsN5)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test getPcliP null p"() {
        // given
        Node p = null
        Node t = f

        // when
        fixture.getPcliP(p, t)
    }

    @Test
    void "test getPproP happy path null t"() {
        // given
        Node p = nsN2
        Node t = null

        // when
        Set<Node> result = fixture.getPproP(p, t)

        // then
        the(result.size()).shouldBeEqual(1)
        the(result).shouldContain(nsN3)
    }

    @Test
    void "test getPproP happy path known t"() {
        // given
        Node p = nsN5
        Node t = i

        // when
        Set<Node> result = fixture.getPproP(p, t)

        // then
        the(result.size()).shouldBeEqual(1)
        the(result).shouldContain(nsN2)
    }

    @Test
    void "test getPproP happy path unknown t"() {
        // given
        Node p = nsN5
        Node t = f

        // when
        Set<Node> result = fixture.getPproP(p, t)

        // then
        the(result.size()).shouldBeEqual(2)
        the(result).shouldContain(nsN2)
        the(result).shouldContain(nsN3)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test getPproP null p"() {
        // given
        Node p = null
        Node t = f

        // when
        fixture.getPproP(p, t)
    }

    @Test
    void "test findPIntDSet with a null"() {
        // given
        Node p = nsN2
        Node t = null

        // when
        Set<Relationship> result = fixture.findPIntDSet(p, t)

        result.each {
            println it
        }

        // then
        the(result.size()).shouldBeEqual(3)
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(g, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(d, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(e, b).get())
    }

    @Test
    void "test findPIntDSet with a known type"() {
        // given
        Node p = nsN2
        Node t = g

        // when
        Set<Relationship> result = fixture.findPIntDSet(p, t)

        result.each {
            println it
        }

        // then
        the(result.size()).shouldBeEqual(2)
        the(result).shouldNotContain(fixture.typeGraph.edgeConnecting(g, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(d, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(e, b).get())
    }

    @Test
    void "test findPIntDSet with an unknown type"() {
        // given
        Node p = nsN2
        Node t = a

        // when
        Set<Node> result = fixture.findPIntDSet(p, t)

        result.each {
            println it
        }

        // then
        the(result.size()).shouldBeEqual(3)
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(g, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(d, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(e, b).get())
    }

    @Test(expected = IllegalArgumentException.class)
    void "test findPIntDSet with a null p"() {
        // given
        Node p = null
        Node t = null

        // when
        fixture.findPIntDSet(p, t)
    }

    @Test
    void "test findPExtDSet happy path"() {
        // given
        Set<NamespaceRelation> edges = fixture.nsGraph.outEdges(nsN5)
        Node t = null

        // when
        Set<Relationship> result = fixture.findPExtDSet(edges, t)

        // then
        the(result.size()).shouldBeEqual(2)
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(c, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(c, i).get())
    }

    @Test
    void "test findPExtDSet happy path when t is known"() {
        // given
        Set<NamespaceRelation> edges = fixture.nsGraph.outEdges(nsN5)
        Node t = c

        // when
        Set<Relationship> result = fixture.findPExtDSet(edges, t)

        // then
        the(result.size()).shouldBeEqual(0)
        the(result).shouldNotContain(fixture.typeGraph.edgeConnecting(c, b).get())
        the(result).shouldNotContain(fixture.typeGraph.edgeConnecting(c, i).get())
    }

    @Test
    void "test findPExtDSet happy path when t is known external"() {
        // given
        Set<NamespaceRelation> edges = fixture.nsGraph.outEdges(nsN5)
        Node t = i

        // when
        Set<Relationship> result = fixture.findPExtDSet(edges, t)

        // then
        the(result.size()).shouldBeEqual(1)
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(c, b).get())
        the(result).shouldNotContain(fixture.typeGraph.edgeConnecting(c, i).get())
    }

    @Test
    void "test findPExtDSet happy path when out is false"() {
        // given
        Set<NamespaceRelation> edges = fixture.nsGraph.inEdges(nsN2)
        Node t = null

        // when
        Set<Relationship> result = fixture.findPExtDSet(edges, t)

        // then
        the(result.size()).shouldBeEqual(2)
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(a, b).get())
        the(result).shouldContain(fixture.typeGraph.edgeConnecting(c, b).get())
    }

    @Test
    void "test findPExtDSet when edges is empty"() {
        // given
        Set<NamespaceRelation> edges = []
        Node t = null

        // when
        Set<Relationship> result = fixture.findPExtDSet(edges, t)

        // then
        the(result.size()).shouldBeEqual(0)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test findPExtDSet when edges is null"() {
        // given
        Set<NamespaceRelation> edges = null
        Node t = null

        // when
        fixture.findPExtDSet(edges, t)
    }

    @Test
    void "test findPD happy path with null t"() {
        // given
        Node p = nsN2
        Node t = null
        Set<Relationship> pintD = fixture.findPIntDSet(p, t)

        // when
        double result = fixture.findPD(p, t, pintD)

        // then
        the(result).shouldBeEqual(8.0d)
    }

    @Test
    void "test findPD happy path with known t"() {
        // given
        Node p = nsN2
        Node t = i
        Set<Relationship> pintD = fixture.findPIntDSet(p, t)

        // when
        double result = fixture.findPD(p, t, pintD)

        // then
        the(result).shouldBeEqual(6.0d)
    }

    @Test
    void "test findPD happy path with unknown t"() {
        // given
        Node p = nsN2
        Node t = f
        Set<Relationship> pintD = fixture.findPIntDSet(p, t)

        // when
        double result = fixture.findPD(p, t, pintD)

        // then
        the(result).shouldBeEqual(8.0d)
    }

    @Test
    void "test findPD happy path with empty pintD"() {
        // given
        Node p = nsN2
        Node t = null
        Set<Relationship> pintD = [].toSet()

        // when
        double result = fixture.findPD(p, t, pintD)

        // then
        the(result).shouldBeEqual(5.0d)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test findPD with null p"() {
        // given
        Node p = null
        Node t = null
        Set<Relationship> pintD = fixture.findPIntDSet(p, t)

        // when
        fixture.findPD(p, t, pintD)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test findPD with null pintD"() {
        // given
        Node p = nsN2
        Node t = null
        Set<Relationship> pintD = null

        // when
        fixture.findPD(p, t, pintD)
    }

    @Test
    void "test createGraphs happy path"() {
        // given
        fixture = new OrgGrimeDetector()

        // when
        fixture.createGraphs()

        // then
        the(fixture.nsGraph).shouldNotBeNull()
        the(fixture.typeGraph).shouldNotBeNull()
    }

    @Test
    void "test initializeGraph happy path"() {
        // given
        fixture = new OrgGrimeDetector()

        // when
        Network<Node, Relationship> result = fixture.initializeGraph()

        // then
        the(result).shouldNotBeNull()
        the(result.allowsSelfLoops()).shouldBeTrue()
        the(result.allowsParallelEdges()).shouldBeTrue()
    }

    @Test
    void "test addNodes happy path"() {
        // given
        List<Namespace> namespaces = [ns1, ns2, ns3, ns4, ns5, ns6]
        GraphElementFactory factory = GraphElementFactory.instance
        fixture = new OrgGrimeDetector()
        fixture.createGraphs()

        // when
        fixture.addNodes(namespaces, factory)

        // then
        the(fixture.nsBiMap.size()).shouldBeEqual(6)
        the(fixture.nsGraph.nodes().size()).shouldBeEqual(6)
        the(fixture.nsTypeMap.size()).shouldBeEqual(6)
        the(fixture.typeGraph.nodes().size()).shouldBeEqual(9)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test addNodes namespaces is null"() {
        // given
        List<Namespace> namespaces = null
        GraphElementFactory factory = GraphElementFactory.instance
        fixture = new OrgGrimeDetector()
        fixture.createGraphs()

        // when
        fixture.addNodes(namespaces, factory)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test addNodes factory is null"() {
        // given
        List<Namespace> namespaces = [ns1, ns2, ns3, ns4, ns5, ns6]
        GraphElementFactory factory = null
        fixture = new OrgGrimeDetector()
        fixture.createGraphs()

        // when
        fixture.addNodes(namespaces, factory)
    }

    @Test
    void "test addEdges happy path"() {
        // given
        fixture = new OrgGrimeDetector()
        Type type = typeB
        Node node = b
        Set<Type> types = typeB.getAssociatedTo()
        RelationshipType relType = RelationshipType.Association
        GraphElementFactory factory = GraphElementFactory.instance
        associateNodesAndTypes()
        fixture.createGraphs()
        fixture.typeGraph.addNode(b)
        fixture.typeGraph.addNode(h)
        fixture.nsGraph.addNode(nsN2)
        fixture.nsGraph.addNode(nsN3)

        // when
        fixture.addEdges(type, node, types, relType, factory)

        // then
        the(fixture.typeGraph.hasEdgeConnecting(b, h)).shouldBeTrue()
        the(fixture.nsGraph.hasEdgeConnecting(nsN2, nsN3)).shouldBeTrue()
        the(fixture.nsGraph.edgeConnecting(nsN2, nsN3).get().persistent).shouldBeTrue()
    }

    @Test
    void "test constructGraphs happy path"() {
        // given
        fixture = new OrgGrimeDetector()
        PatternInstance pattern = inst

        // when
        fixture.constructGraphs(pattern)

        // then
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeF], fixture.typeBiMap[typeA])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeA], fixture.typeBiMap[typeB])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeC], fixture.typeBiMap[typeB])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeD], fixture.typeBiMap[typeB])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeE], fixture.typeBiMap[typeB])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeG], fixture.typeBiMap[typeB])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeB], fixture.typeBiMap[typeH])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeD], fixture.typeBiMap[typeI])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeE], fixture.typeBiMap[typeI])).shouldBeTrue()
        the(fixture.typeGraph.hasEdgeConnecting(fixture.typeBiMap[typeC], fixture.typeBiMap[typeI])).shouldBeTrue()

        the(fixture.nsGraph.hasEdgeConnecting(fixture.nsBiMap[ns1], fixture.nsBiMap[ns4])).shouldBeTrue()
        the(fixture.nsGraph.hasEdgeConnecting(fixture.nsBiMap[ns4], fixture.nsBiMap[ns2])).shouldBeTrue()
        the(fixture.nsGraph.hasEdgeConnecting(fixture.nsBiMap[ns5], fixture.nsBiMap[ns2])).shouldBeTrue()
        the(fixture.nsGraph.hasEdgeConnecting(fixture.nsBiMap[ns5], fixture.nsBiMap[ns3])).shouldBeTrue()
        the(fixture.nsGraph.hasEdgeConnecting(fixture.nsBiMap[ns2], fixture.nsBiMap[ns3])).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test constructGraphs with null pattern"() {
        // given
        fixture = new OrgGrimeDetector()
        PatternInstance pattern = null

        // when
        fixture.constructGraphs(pattern)
    }

    @Test
    void "test addEdges same namespace"() {
        // given
        fixture = new OrgGrimeDetector()
        Type type = typeD
        Node node = d
        Set<Type> types = typeD.getGeneralizedBy()
        RelationshipType relType = RelationshipType.Generalization
        GraphElementFactory factory = GraphElementFactory.instance
        associateNodesAndTypes()
        fixture.createGraphs()
        fixture.typeGraph.addNode(b)
        fixture.typeGraph.addNode(d)
        fixture.nsGraph.addNode(nsN2)

        // when
        fixture.addEdges(type, node, types, relType, factory)

        // then
        the(fixture.typeGraph.hasEdgeConnecting(d, b)).shouldBeTrue()
        the(fixture.typeGraph.edgeConnecting(d, b).get().persistent).shouldBeTrue()
    }

    @Test
    @Parameters(method = "parametersForIsPersistent")
    void "test isPersistent happy path"(RelationshipType type, boolean expected) {
        // when
        boolean result = fixture.isPersistent(type)

        // then
        the(result).shouldBeEqual(expected)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test isPersistent null type"() {
        // when
        fixture.isPersistent(null)
    }

    private Object[][] parametersForIsPersistent() {
        [
                [RelationshipType.Association, true],
                [RelationshipType.Realization, true],
                [RelationshipType.Generalization, true],
                [RelationshipType.Aggregation, true],
                [RelationshipType.Composition, true],
                [RelationshipType.Dependency, false],
                [RelationshipType.UseDependency, false]
        ]
    }

    @Test
    @Parameters(method = "measureCycleQualityParams")
    void "test measureCycleQuality happy path"(String src, String dest, double baseExp, double revisedExp) {
        // given
        fixture.nsGraph.addEdge(nsN3, nsN5, GraphElementFactory.instance.createNamespaceRelation([]))
        fixture.markCycles()
        Node ns = nsNodeMap[src]
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsNodeMap[src], nsNodeMap[dest]).get()

        // when
        double base, revised
        (base, revised) = fixture.measureCycleQuality(ns, rel)

        // then
        the(base).shouldBeEqual(baseExp)
        the(revised).shouldBeEqual(revisedExp)
    }

    private Object[][] measureCycleQualityParams() {
        [
                ["nsN1", "nsN4", 1, 1],
                ["nsN4", "nsN2", 1, 1],
                ["nsN2", "nsN3", new Double(0.19999999999999996), new Double(0.5)],
                ["nsN5", "nsN2", 0, 0],
                ["nsN5", "nsN3", 0, 0],
                ["nsN3", "nsN5", 0, 0]
        ]
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureCycleQuality ns is null"() {
        // given
        Node ns = null
        NamespaceRelation rel = null

        // when
        fixture.measureCycleQuality(ns, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureCycleQuality rel is null"() {
        // given
        Node ns = null
        NamespaceRelation rel = null

        // when
        fixture.measureCycleQuality(ns, rel)
    }

    @Test
    void "test measureD happy path"() {
        // given
        fixture.measureCe()
        fixture.measureCa()
        fixture.measureI()
        fixture.measureNa()
        fixture.measureNc()
        fixture.measureA()

        // when
        fixture.measureD()

        // then
        the(fixture.nsMetrics.get(nsN1, "D") <=> Math.abs(0 + 1 - 1)).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN3, "D") <=> Math.abs(0 + 0 - 1)).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN4, "D") <=> Math.abs(0 + (1 / 2) - 1)).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN5, "D") <=> Math.abs(0 + 1 - 1)).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN6, "D") <=> Math.abs(0 + 0 - 1)).shouldBeEqual(0)
    }

    @Test
    void "test measureI happy path"() {
        // given
        fixture.measureCe()
        fixture.measureCa()

        // when
        fixture.measureI()

        // then
        the(fixture.nsMetrics.get(nsN1, "I")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN2, "I")).shouldBeEqual(3 / 5)
        the(fixture.nsMetrics.get(nsN3, "I")).shouldBeEqual(0 / 4)
        the(fixture.nsMetrics.get(nsN4, "I")).shouldBeEqual(1 / 2)
        the(fixture.nsMetrics.get(nsN5, "I")).shouldBeEqual(2 / 2)
        the(fixture.nsMetrics.get(nsN6, "I")).shouldBeEqual(0)
    }

    @Test
    void "test measureIWithoutRelation happy path ns5 to ns3"() {
        // given
        Node p = nsN4
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN1, nsN4).get()

        // when
        double result = fixture.measureIWithoutRelation(p, rel)

        // then
        the(result).shouldBeEqual(1)
    }

    @Test
    void "test measureIWithoutRelation happy path ns2 to ns3"() {
        // given
        Node p = nsN2
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN2, nsN3).get()

        // when
        double result = fixture.measureIWithoutRelation(p, rel)

        // then
        the(result).shouldBeEqual(0)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureIWithoutRelation p is null"() {
        // given
        Node p = null
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN2, nsN3).get()

        // when
        fixture.measureIWithoutRelation(p, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureIWithoutRelation rel is null"() {
        // given
        Node p = nsN3
        NamespaceRelation rel = null

        // when
        fixture.measureIWithoutRelation(p, rel)
    }

    @Test
    void "test measureCa happy path"() {
        // given

        // when
        fixture.measureCa()

        // then
        the(fixture.nsMetrics.get(nsN1, "Ca")).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN2, "Ca")).shouldBeEqual(2)
        the(fixture.nsMetrics.get(nsN3, "Ca")).shouldBeEqual(4)
        the(fixture.nsMetrics.get(nsN4, "Ca")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN5, "Ca")).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN6, "Ca")).shouldBeEqual(0)
    }

    @Test
    void "test measureCaWithoutRelation happy path ns5 to ns3"() {
        // given
        Node p = nsN3
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN5, nsN3).get()

        // when
        int result = fixture.measureCaWithoutRelation(p, rel)

        // then
        the(result).shouldBeEqual(3)
    }

    @Test
    void "test measureCaWithoutRelation happy path ns2 to ns3"() {
        // given
        Node p = nsN3
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN2, nsN3).get()

        // when
        int result = fixture.measureCaWithoutRelation(p, rel)

        // then
        the(result).shouldBeEqual(1)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureCaWithoutRealtion p is null"() {
        // given
        Node p = null
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN2, nsN3).get()

        // when
        fixture.measureCaWithoutRelation(p, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureCaWithoutRealtion rel is null"() {
        // given
        Node p = nsN3
        NamespaceRelation rel = null

        // when
        fixture.measureCaWithoutRelation(p, rel)
    }

    @Test
    void "test measureCe happy path"() {
        // given

        // when
        fixture.measureCe()

        // then
        the(fixture.nsMetrics.get(nsN1, "Ce")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN2, "Ce")).shouldBeEqual(3)
        the(fixture.nsMetrics.get(nsN3, "Ce")).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN4, "Ce")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN5, "Ce")).shouldBeEqual(2)
        the(fixture.nsMetrics.get(nsN6, "Ce")).shouldBeEqual(0)
    }

    @Test
    void "test measureCeWithoutRelation happy path ns5 to ns3"() {
        // given
        Node p = nsN5
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN5, nsN3).get()

        // when
        int result = fixture.measureCeWithoutRelation(p, rel)

        // then
        the(result).shouldBeEqual(1)
    }

    @Test
    void "test measureCeWithoutRelation happy path ns2 to ns3"() {
        // given
        Node p = nsN2
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN2, nsN3).get()

        // when
        int result = fixture.measureCeWithoutRelation(p, rel)

        // then
        the(result).shouldBeEqual(0)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureCeWithoutRelation p is null"() {
        // given
        Node p = null
        NamespaceRelation rel = fixture.nsGraph.edgeConnecting(nsN2, nsN3).get()

        // when
        fixture.measureCeWithoutRelation(p, rel)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test measureDeltaCe rel is null"() {
        // given
        Node p = nsN3
        NamespaceRelation rel = null

        // when
        fixture.measureCeWithoutRelation(p, rel)
    }

    @Test
    void "test measureA happy path"() {
        // given
        fixture.measureNa()
        fixture.measureNc()

        // when
        fixture.measureA()

        // then
        the(fixture.nsMetrics.get(nsN1, "A")).shouldBeEqual(0 / 1)
        the(fixture.nsMetrics.get(nsN2, "A")).shouldBeEqual(1 / 4)
        the(fixture.nsMetrics.get(nsN3, "A")).shouldBeEqual(0 / 2)
        the(fixture.nsMetrics.get(nsN4, "A")).shouldBeEqual(0 / 1)
        the(fixture.nsMetrics.get(nsN5, "A")).shouldBeEqual(0 / 1)
        the(fixture.nsMetrics.get(nsN6, "A")).shouldBeEqual(0)
    }

    @Test
    void "test measureNa happy path"() {
        // given

        // when
        fixture.measureNa()

        // then
        the(fixture.nsMetrics.get(nsN1, "Na")).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN2, "Na")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN3, "Na")).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN4, "Na")).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN5, "Na")).shouldBeEqual(0)
        the(fixture.nsMetrics.get(nsN6, "Na")).shouldBeEqual(0)
    }

    @Test
    void "test measureNc happy path"() {
        // given

        // when
        fixture.measureNc()

        // then
        the(fixture.nsMetrics.get(nsN1, "Nc")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN2, "Nc")).shouldBeEqual(4)
        the(fixture.nsMetrics.get(nsN3, "Nc")).shouldBeEqual(2)
        the(fixture.nsMetrics.get(nsN4, "Nc")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN5, "Nc")).shouldBeEqual(1)
        the(fixture.nsMetrics.get(nsN6, "Nc")).shouldBeEqual(0)
    }

    @Test
    void "test markCycles happy path with no cycle"() {
        // given

        // when
        fixture.markCycles()

        // then
        the(fixture.nsGraph.edgeConnecting(nsN1, nsN4).get().cyclic).shouldBeFalse()
        the(fixture.nsGraph.edgeConnecting(nsN4, nsN2).get().cyclic).shouldBeFalse()
        the(fixture.nsGraph.edgeConnecting(nsN2, nsN3).get().cyclic).shouldBeFalse()
        the(fixture.nsGraph.edgeConnecting(nsN5, nsN2).get().cyclic).shouldBeFalse()
        the(fixture.nsGraph.edgeConnecting(nsN5, nsN3).get().cyclic).shouldBeFalse()
    }

    @Test
    void "test markCycles happy path with a simple cycle"() {
        // given
        fixture.nsGraph.addEdge(nsN3, nsN5, GraphElementFactory.instance.createNamespaceRelation([]))

        // when
        fixture.markCycles()

        // then
        the(fixture.nsGraph.edgeConnecting(nsN1, nsN4).get().cyclic).shouldBeFalse()
        the(fixture.nsGraph.edgeConnecting(nsN4, nsN2).get().cyclic).shouldBeFalse()
        the(fixture.nsGraph.edgeConnecting(nsN2, nsN3).get().cyclic).shouldBeTrue()
        the(fixture.nsGraph.edgeConnecting(nsN5, nsN2).get().cyclic).shouldBeTrue()
        the(fixture.nsGraph.edgeConnecting(nsN5, nsN3).get().cyclic).shouldBeTrue()
        the(fixture.nsGraph.edgeConnecting(nsN3, nsN5).get().cyclic).shouldBeTrue()
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
        typeB.setAbstract(true)
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

    private void createGraphs() {
        GraphElementFactory factory = GraphElementFactory.instance
        fixture.createGraphs()

        a = new Node(name: "A", type: NodeType.Class, internal: true)
        b = new Node(name: "B", type: NodeType.Class, internal: true)
        c = new Node(name: "C", type: NodeType.Class, internal: true)
        d = new Node(name: "D", type: NodeType.Class, internal: true)
        e = new Node(name: "E", type: NodeType.Class, internal: true)
        f = new Node(name: "F", type: NodeType.Class, internal: false)
        g = new Node(name: "G", type: NodeType.Class, internal: false)
        h = new Node(name: "H", type: NodeType.Class, internal: false)
        i = new Node(name: "I", type: NodeType.Class, internal: false)

        fixture.typeGraph.addEdge(f, a, new Relationship(type: RelationshipType.Association, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(a, b, new Relationship(type: RelationshipType.Aggregation, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(b, h, new Relationship(type: RelationshipType.Composition, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(g, b, new Relationship(type: RelationshipType.Dependency, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(c, i, new Relationship(type: RelationshipType.UseDependency, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(d, i, new Relationship(type: RelationshipType.PackageDependency, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(e, i, new Relationship(type: RelationshipType.Association, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(c, b, new Relationship(type: RelationshipType.Generalization, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(d, b, new Relationship(type: RelationshipType.Realization, persistent: true, invalid: false))
        fixture.typeGraph.addEdge(e, b, new Relationship(type: RelationshipType.Generalization, persistent: true, invalid: false))

        nsN1 = factory.createNode(ns1)
        nsN2 = factory.createNode(ns2)
        nsN3 = factory.createNode(ns3)
        nsN4 = factory.createNode(ns4)
        nsN5 = factory.createNode(ns5)
        nsN6 = factory.createNode(ns6)

        nsNodeMap["nsN1"] = nsN1
        nsNodeMap["nsN2"] = nsN2
        nsNodeMap["nsN3"] = nsN3
        nsNodeMap["nsN4"] = nsN4
        nsNodeMap["nsN5"] = nsN5
        nsNodeMap["nsN6"] = nsN6

        fixture.nsGraph.addNode(nsN1)
        fixture.nsGraph.addNode(nsN2)
        fixture.nsGraph.addNode(nsN3)
        fixture.nsGraph.addNode(nsN4)
        fixture.nsGraph.addNode(nsN5)
        fixture.nsGraph.addNode(nsN6)

        fixture.nsGraph.addEdge(nsN1, nsN4, factory.createNamespaceRelation([fixture.typeGraph.edgeConnecting(f, a).get()]))
        fixture.nsGraph.addEdge(nsN4, nsN2, factory.createNamespaceRelation([fixture.typeGraph.edgeConnecting(a, b).get()]))
        fixture.nsGraph.addEdge(nsN2, nsN3, factory.createNamespaceRelation([
                fixture.typeGraph.edgeConnecting(b, h).get(),
                fixture.typeGraph.edgeConnecting(d, i).get(),
                fixture.typeGraph.edgeConnecting(e, i).get()
        ]))
        fixture.nsGraph.addEdge(nsN5, nsN2, factory.createNamespaceRelation([fixture.typeGraph.edgeConnecting(c, b).get()]))
        fixture.nsGraph.addEdge(nsN5, nsN3, factory.createNamespaceRelation([fixture.typeGraph.edgeConnecting(c, i).get()]))
    }

    private void associateNodesAndTypes() {
        fixture.typeBiMap.put(typeA, a)
        fixture.typeBiMap.put(typeB, b)
        fixture.typeBiMap.put(typeC, c)
        fixture.typeBiMap.put(typeD, d)
        fixture.typeBiMap.put(typeE, e)
        fixture.typeBiMap.put(typeF, f)
        fixture.typeBiMap.put(typeG, g)
        fixture.typeBiMap.put(typeH, h)
        fixture.typeBiMap.put(typeI, i)

        fixture.nsBiMap.put(ns1, nsN1)
        fixture.nsBiMap.put(ns2, nsN2)
        fixture.nsBiMap.put(ns3, nsN3)
        fixture.nsBiMap.put(ns4, nsN4)
        fixture.nsBiMap.put(ns5, nsN5)
        fixture.nsBiMap.put(ns6, nsN6)

        fixture.nsTypeMap.put(nsN1, [f])
        fixture.nsTypeMap.put(nsN2, [b, d, e, g])
        fixture.nsTypeMap.put(nsN3, [i, h])
        fixture.nsTypeMap.put(nsN4, [a])
        fixture.nsTypeMap.put(nsN5, [c])
        fixture.nsTypeMap.put(nsN6, [])
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
}