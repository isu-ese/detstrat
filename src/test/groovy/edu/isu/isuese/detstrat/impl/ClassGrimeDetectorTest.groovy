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
import junitparams.JUnitParamsRunner
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple
import org.javalite.activejdbc.test.DBSpec
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner.class)
class ClassGrimeDetectorTest extends DBSpec {

    ClassGrimeDetector fixture

    PatternInstance inst
    System sys
    Project proj
    Namespace ns1, ns2, ns3, ns4, ns5, ns6
    Type typeA, typeB, typeC, typeD, typeE, typeF, typeG, typeH, typeI
    Field fld1, fld2, fld3
    Method meth1, meth2, meth3, meth4, getter
    Node a1, a2, a3, m1, m2, m3, m4, get
    MutableNetwork<Node, Relationship> graph

    @Before
    void setup() {
        RuleProvider.instance.repository()
        RuleProvider.instance.rules()
        fixture = new ClassGrimeDetector()
        createModelComponents()
        createGraphs()
        associateNodesAndTypes()
        createPatternInstance()
    }

    @Test
    void detect() {
        // given

        // when

        // then
        Assert.fail()
    }

    @Test
    void "test constructGraph happy path"() {
        // given
        Type type = typeA

        // when
        Network<Node, Relationship> result = fixture.constructGraph(type)

        // then
        the(result.nodes().size()).shouldBeEqual(7)
        the(result.edges().size()).shouldBeEqual(7)

        // add assertions about connections
    }

    @Test(expected = IllegalArgumentException.class)
    void "test constructGraph type is null"() {
        // given
        Type type = null

        // when
        fixture.constructGraph(type)
    }

    @Test
    void "test connectMethodToField happy path indirect is false"() {
        // given
        Field field = fld1
        GraphElementFactory factory = GraphElementFactory.instance
        MutableNetwork<Node, Relationship> graph = graph
        Node node = m1
        boolean indirect = false

        // when
        fixture.connectMethodToField(field, factory, graph, node, indirect)

        // then
        the(fixture.fieldBiMap.keySet()).shouldContain(field)
        the(graph.hasEdgeConnecting(node, fixture.fieldBiMap[field])).shouldBeTrue()
        the(graph.edgeConnecting(node, fixture.fieldBiMap[field]).get().indirect).shouldBeEqual(indirect)
    }

    @Test
    void "test connectMethodToField happy path indirect is true"() {
        // given
        Field field = fld1
        GraphElementFactory factory = GraphElementFactory.instance
        MutableNetwork<Node, Relationship> graph = graph
        Node node = m1
        boolean indirect = true

        // when
        fixture.connectMethodToField(field, factory, graph, node, indirect)

        // then
        the(fixture.fieldBiMap.keySet()).shouldContain(field)
        the(graph.hasEdgeConnecting(node, fixture.fieldBiMap[field])).shouldBeTrue()
        the(graph.edgeConnecting(node, fixture.fieldBiMap[field]).get().indirect).shouldBeEqual(indirect)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test connectMethodToField field is null"() {
        // given
        Field field = null
        GraphElementFactory factory = GraphElementFactory.instance
        MutableNetwork<Node, Relationship> graph = null
        Node node = null
        boolean indirect = false

        // when
        fixture.connectMethodToField(field, factory, graph, node, indirect)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test connectMethodToField factory is null"() {
        // given
        Field field = null
        GraphElementFactory factory = GraphElementFactory.instance
        MutableNetwork<Node, Relationship> graph = null
        Node node = null
        boolean indirect = false

        // when
        fixture.connectMethodToField(field, factory, graph, node, indirect)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test connectMethodToField graph is null"() {
        // given
        Field field = null
        GraphElementFactory factory = GraphElementFactory.instance
        MutableNetwork<Node, Relationship> graph = null
        Node node = null
        boolean indirect = false

        // when
        fixture.connectMethodToField(field, factory, graph, node, indirect)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test connectMethodToField node is null"() {
        // given
        Field field = null
        GraphElementFactory factory = GraphElementFactory.instance
        MutableNetwork<Node, Relationship> graph = null
        Node node = null
        boolean indirect = false

        // when
        fixture.connectMethodToField(field, factory, graph, node, indirect)
    }

    @Test
    void "test markMethods happy path"() {
        // given
        fixture.constructGraph(typeA)
        PatternInstance pattern = inst

        // when
        fixture.markMethods(pattern)

        // then
        the(fixture.methodBiMap[meth1].internal).shouldBeTrue()
        the(fixture.methodBiMap[meth2].internal).shouldBeTrue()
        the(fixture.methodBiMap[meth3].internal).shouldBeFalse()
        the(fixture.methodBiMap[meth4].internal).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markMethods instance is null"() {
        // given
        fixture.constructGraph(typeA)
        PatternInstance pattern = null

        // when
        fixture.markMethods(pattern)
    }

    @Test
    void "test combinationUtil happy path"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        Node field = fixture.fieldBiMap[fld1]
        List<Node> arr = [fixture.methodBiMap[meth1], fixture.methodBiMap[meth2], fixture.methodBiMap[meth3]]
        List<Node> data = []
        int start = 0
        int end = arr.size() - 1
        int index = 0

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)

        // then
        the(fixture.methodPairsIndirect.size()).shouldBeEqual(3)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test combinationUtil field is null"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        Node field = null
        List<Node> arr = [fixture.methodBiMap[meth1], fixture.methodBiMap[meth2], fixture.methodBiMap[meth3]]
        List<Node> data = []
        int start = 0
        int end = arr.size() - 1
        int index = 0

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test combinationUtil arr is null"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        Node field = fixture.fieldBiMap[fld1]
        List<Node> arr = null
        List<Node> data = []
        int start = 0
        int end = 0
        int index = 0

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test combinationUtil data is null"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        Node field = fixture.fieldBiMap[fld1]
        List<Node> arr = [fixture.methodBiMap[meth1], fixture.methodBiMap[meth2], fixture.methodBiMap[meth3]]
        List<Node> data = null
        int start = 0
        int end = arr.size() - 1
        int index = 0

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test combinationUtil start is less than 0"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        Node field = fixture.fieldBiMap[fld1]
        List<Node> arr = [fixture.methodBiMap[meth1], fixture.methodBiMap[meth2], fixture.methodBiMap[meth3]]
        List<Node> data = []
        int start = -1
        int end = arr.size() - 1
        int index = 0

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test combinationUtil end is less than zero"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        Node field = fixture.fieldBiMap[fld1]
        List<Node> arr = [fixture.methodBiMap[meth1], fixture.methodBiMap[meth2], fixture.methodBiMap[meth3]]
        List<Node> data = []
        int start = 0
        int end = -1
        int index = 0

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test combinationUtil index is less than 0"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        Node field = fixture.fieldBiMap[fld1]
        List<Node> arr = [fixture.methodBiMap[meth1], fixture.methodBiMap[meth2], fixture.methodBiMap[meth3]]
        List<Node> data = []
        int start = 0
        int end = arr.size() - 1
        int index = -1

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test combinationUtil graph is null"() {
        // given
        Network<Node, Relationship> graph = null
        Node field = fixture.fieldBiMap[fld1]
        List<Node> arr = [fixture.methodBiMap[meth1], fixture.methodBiMap[meth2], fixture.methodBiMap[meth3]]
        List<Node> data = []
        int start = 0
        int end = arr.size() - 1
        int index = 0

        // when
        fixture.combinationUtil(field, arr, data, start, end, index, graph)
    }

    @Test
    void "test constructMethodPairs happy path"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)

        // when
        fixture.constructMethodPairs(graph)

        // then
        the(fixture.methodPairs.size()).shouldBeEqual(5)
        the(fixture.methodPairsIndirect.size()).shouldBeEqual(5)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test constructMethodPairs graph is null"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.constructMethodPairs(graph)
    }

    @Test
    void "test markMethodPairs happy path"() {
        // given
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        fixture.markMethods(inst)
        m1 = fixture.methodBiMap[meth1]
        m2 = fixture.methodBiMap[meth2]
        m3 = fixture.methodBiMap[meth3]
        m4 = fixture.methodBiMap[meth4]
        a1 = fixture.fieldBiMap[fld1]
        a2 = fixture.fieldBiMap[fld2]
        a3 = fixture.fieldBiMap[fld3]

        // when
        fixture.markMethodPairs(graph)

        // then
        the(fixture.isInternal(m1, m2, a1)).shouldBeTrue()
        the(fixture.isInternal(m1, m3, a1)).shouldBeFalse()
        the(fixture.isInternal(m2, m3, a1)).shouldBeFalse()
        the(fixture.isInternal(m2, m3, a2)).shouldBeFalse()
        the(fixture.isInternal(m3, m4, a3)).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test markMethodPairs graph is null"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.markMethodPairs(graph)
    }

    @Test
    void "test calculateMethodDeltas happy path"() {
        // given
        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        fixture.markMethodPairs(graph)
        fixture.current = typeA

        // when
        fixture.calculateMethodDeltas(graph)

        // then
        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth1], "TCC") - (4/9 - 5/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth2], "TCC") - (3/9 - 5/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth3], "TCC") - (3/9 - 5/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth4], "TCC") - (5/9 - 5/12)) < 0.0001).shouldBeTrue()

        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth1], "RCI") - (6/9 - 7/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth2], "RCI") - (5/9 - 7/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth3], "RCI") - (4/9 - 7/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.methodDeltas.get(fixture.methodBiMap[meth4], "RCI") - (6/9 - 7/12)) < 0.0001).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test calculateMethodDeltas graph is null"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.calculateMethodDeltas(graph)
    }

    @Test
    void "test calculateTCC happy path no exclusions"() {
        // given
        Type node = typeA

        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(node)
        fixture.markMethodPairs(graph)

        // when
        double result = fixture.calculateTCC(graph)

        // then
        the(Math.abs(result - 5 / 12) < 0.0001).shouldBeTrue()
    }

    @Test
    void "test calculateTCC happy path excluding method2"() {
        // given
        Type node = typeA
        Method excluded = meth2
        Pair<Node, Node> excludedTriple = null

        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(node)
        fixture.markMethodPairs(graph)

        // when
        double result = fixture.calculateTCC(graph, excluded, excludedTriple)

        // then
        the(Math.abs(result - 3 / 9) < 0.0001).shouldBeTrue()
    }

    @Test
    void "test calculateTCC happy path excluding triple"() {
        // given
        Type node = typeA
        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(node)
        fixture.markMethodPairs(graph)

        Method excluded = null
        Pair<Node, Node> excludedTriple = Pair.of(fixture.methodBiMap[meth1], fixture.methodBiMap[meth2])

        // when
        double result = fixture.calculateTCC(graph, excluded, excludedTriple)

        // then
        the(Math.abs(result - 3 / 6) < 0.0001).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test calculateTCC null graph"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.calculateTCC(graph)
    }

    @Test
    void "test calculateRCI happy path"() {
        // given
        Type node = typeA

        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(node)
        fixture.markMethodPairs(graph)

        // when
        double result = fixture.calculateRCI(graph)

        // then
        the(Math.abs(result - 7 / 12) < 0.0001).shouldBeTrue()
    }

    @Test
    void "test calculateRCI happy path excluding method2"() {
        // given
        Type node = typeA
        Method excluded = meth2
        Triple<Node, Node, Node> excludedTriple = null

        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(node)
        fixture.markMethodPairs(graph)

        // when
        double result = fixture.calculateRCI(graph, excluded, excludedTriple)

        // then
        the(Math.abs(result - 5 / 9) < 0.0001).shouldBeTrue()
    }

    @Test
    void "test calculateRCI happy path excluding triple"() {
        // given
        Type node = typeA
        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(node)
        fixture.markMethodPairs(graph)

        Method excluded = null
        Pair<Node, Node> excludedTriple = Pair.of(fixture.methodBiMap[meth1], fixture.methodBiMap[meth2])

        // when
        double result = fixture.calculateRCI(graph, excluded, excludedTriple)

        // then
        the(Math.abs(result - 5 / 6) < 0.0001).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test calculateRCI null graph"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.calculateRCI(graph)
    }

    @Test
    void "test calculateMethodPairDeltas happy path"() {
        // given
        fixture.markMethods(inst)
        Network<Node, Relationship> graph = fixture.constructGraph(typeA)
        fixture.markMethodPairs(graph)
        fixture.current = typeA

        Pair<Node, Node> t1, t2, t3, t4
        Node m1 = fixture.methodBiMap[meth1]
        Node m2 = fixture.methodBiMap[meth2]
        Node m3 = fixture.methodBiMap[meth3]
        Node m4 = fixture.methodBiMap[meth4]

        // when
        fixture.calculateMethodPairDeltas(graph)
        t1 = fixture.isInPairDeltas(m1, m2) ? Pair.of(m1, m2) : Pair.of(m2, m1)
        t2 = fixture.isInPairDeltas(m1, m3) ? Pair.of(m1, m3) : Pair.of(m3, m1)
        t3 = fixture.isInPairDeltas(m2, m3) ? Pair.of(m2, m3) : Pair.of(m3, m2)
        t4 = fixture.isInPairDeltas(m3, m4) ? Pair.of(m3, m4) : Pair.of(m4, m3)

        // then
        the(fixture.pairDeltas.get(t1, "TCC")).shouldNotBeNull()
        the(fixture.pairDeltas.get(t2, "TCC")).shouldNotBeNull()
        the(fixture.pairDeltas.get(t3, "TCC")).shouldNotBeNull()
        the(fixture.pairDeltas.get(t4, "TCC")).shouldNotBeNull()

        the(Math.abs(fixture.pairDeltas.get(t1, "TCC") - (3/6 - 5/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.pairDeltas.get(t2, "TCC") - (3/6 - 5/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.pairDeltas.get(t3, "TCC") - (1/6 - 5/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.pairDeltas.get(t4, "TCC") - (5/6 - 5/12)) < 0.0001).shouldBeTrue()

        the(Math.abs(fixture.pairDeltas.get(t1, "RCI") - (5/6 - 7/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.pairDeltas.get(t2, "RCI") - (5/6 - 7/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.pairDeltas.get(t3, "RCI") - (3/6 - 7/12)) < 0.0001).shouldBeTrue()
        the(Math.abs(fixture.pairDeltas.get(t4, "RCI") - (5/6 - 7/12)) < 0.0001).shouldBeTrue()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test calculateMethodPairDeltas null graph"() {
        // given
        Network<Node, Relationship> graph = null

        // when
        fixture.calculateMethodPairDeltas(graph)
    }

    @Test
    void detectGrime() {
        // given

        // when

        // then
        Assert.fail()
    }

    @Test
    void detectPairMethods() {
        // given

        // when

        // then
        Assert.fail()
    }

    @Test
    void detectIndividualMethods() {
        // given

        // when

        // then
        Assert.fail()
    }

    void createModelComponents() {
        sys = System.builder().name("testdata").key("TestData").basePath("testdata").create()
        proj = Project.builder().name("testproj").projKey("testproj").version("1.0").relPath("testproj").create()
        Module mod = Module.builder().name("testmod").moduleKey("testmod").relPath("testmod").srcPath("src/main/java").create()
        ns1 = Namespace.builder().name("test").nsKey("test1").relPath("test").create()
        ns2 = Namespace.builder().name("test").nsKey("test2").relPath("test/test").create()
//        ns3 = Namespace.builder().name("test2").nsKey("test3").relPath("test/test2").create()
//        ns4 = Namespace.builder().name("test3").nsKey("test4").relPath("test3").create()
//        ns5 = Namespace.builder().name("test4").nsKey("test5").relPath("test4").create()
//        ns6 = Namespace.builder().name("test5").nsKey("test6").relPath("test/test5").create()
        ns1.addNamespace(ns2)
//        ns1.addNamespace(ns3)
        mod.addNamespace(ns1)
//        mod.addNamespace(ns4)
//        mod.addNamespace(ns5)
//        ns1.addNamespace(ns6)
        proj.addNamespace(ns1)
        proj.addNamespace(ns2)
        proj.addModule(mod)
        sys.addProject(proj)

        File fileA = File.builder().name("TypeA.java").fileKey("TypeA.java").relPath("TypeA.java").type(FileType.SOURCE).start(1).end(19).create()
        typeA = Type.builder().type(Type.CLASS).name("TypeA").compKey("TypeA").accessibility(Accessibility.PUBLIC).start(3).end(19).create()
        fileA.addType(typeA)
        ns2.addType(typeA)
        proj.addFile(fileA)

//        File fileB = File.builder().name("TypeB.java").relPath("TypeB.java").type(FileType.SOURCE).start(1).end(7).create()
//        typeB = Type.builder().type(Type.CLASS).name("TypeB").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
//        typeB.addModifier("ABSTRACT")
//        typeB.setAbstract(true)
//        fileB.addType(typeB)
//        ns2.addFile(fileB)
//
//        File fileC = File.builder().name("TypeC.java").relPath("TypeC.java").type(FileType.SOURCE).start(1).end(8).create()
//        typeC = Type.builder().type(Type.CLASS).name("TypeC").accessibility(Accessibility.PUBLIC).start(3).end(8).create()
//        fileC.addType(typeC)
//        ns5.addFile(fileC)
//
//        File fileD = File.builder().name("TypeD.java").relPath("TypeD.java").type(FileType.SOURCE).start(1).end(7).create()
//        typeD = Type.builder().type(Type.CLASS).name("TypeD").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
//        fileD.addType(typeD)
//        ns2.addFile(fileD)
//
//        File fileE = File.builder().name("TypeE.java").relPath("TypeE.java").type(FileType.SOURCE).start(1).end(7).create()
//        typeE = Type.builder().type(Type.CLASS).name("TypeE").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
//        fileE.addType(typeE)
//        ns2.addFile(fileE)
//
//        File fileF = File.builder().name("TypeF.java").relPath("TypeF.java").type(FileType.SOURCE).start(1).end(7).create()
//        typeF = Type.builder().type(Type.CLASS).name("TypeF").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
//        fileF.addType(typeF)
//        ns1.addFile(fileF)
//
//        File fileG = File.builder().name("TypeG.java").relPath("TypeG.java").type(FileType.SOURCE).start(1).end(7).create()
//        typeG = Type.builder().type(Type.CLASS).name("TypeG").accessibility(Accessibility.PUBLIC).start(3).end(7).create()
//        fileG.addType(typeG)
//        ns2.addFile(fileG)
//
//        File fileH = File.builder().name("TypeH.java").relPath("TypeH.java").type(FileType.SOURCE).start(1).end(5).create()
//        typeH = Type.builder().type(Type.CLASS).name("TypeH").accessibility(Accessibility.PUBLIC).start(3).end(5).create()
//        fileH.addType(typeH)
//        ns3.addFile(fileH)
//
//        File fileI = File.builder().name("TypeI.java").relPath("TypeI.java").type(FileType.SOURCE).start(1).end(5).create()
//        typeI = Type.builder().type(Type.CLASS).name("TypeI").accessibility(Accessibility.PUBLIC).start(3).end(5).create()
//        fileI.addType(typeI)
//        ns3.addFile(fileI)

        sys.updateKeys()
        typeA.refresh()
//        typeB.refresh()
//        typeC.refresh()
//        typeD.refresh()
//        typeE.refresh()
//        typeF.refresh()
//        typeG.refresh()
//        typeH.refresh()
//        typeI.refresh()
//        fileA.refresh()
//        fileB.refresh()
//        fileC.refresh()
//        fileD.refresh()
//        fileE.refresh()
//        fileF.refresh()
//        fileG.refresh()
//        fileH.refresh()
//        fileI.refresh()
        ns1.refresh()
        ns2.refresh()
//        ns3.refresh()
//        ns4.refresh()
//        ns5.refresh()
//        ns6.refresh()

        fld1 = Field.builder().name("field1").type(TypeRef.createPrimitiveTypeRef("int")).accessibility(Accessibility.PRIVATE).start(5).end(5).create()
        typeA.addMember(fld1)
        fld2 = Field.builder().name("field2").type(TypeRef.createPrimitiveTypeRef("int")).accessibility(Accessibility.PRIVATE).start(6).end(6).create()
        typeA.addMember(fld2)
        fld3 = Field.builder().name("field3").type(TypeRef.createPrimitiveTypeRef("int")).accessibility(Accessibility.PRIVATE).start(7).end(7).create()
        typeA.addMember(fld3)
//        Field fldCB = Field.builder().name("typecb").type(typeB.createTypeRef()).accessibility(Accessibility.PRIVATE).start(5).end(5).create()
//        typeC.addMember(fldCB)

        meth1 = Method.builder().name("method1").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(9).end(10).create()
        typeA.addMember(meth1)
        meth2 = Method.builder().name("method2").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(12).end(13).create()
        typeA.addMember(meth2)
        meth3 = Method.builder().name("method3").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(14).end(15).create()
        typeA.addMember(meth3)
        meth4 = Method.builder().name("method4").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(16).end(17).create()
        typeA.addMember(meth4)
        getter = Method.builder().name("getField1").type(TypeRef.createPrimitiveTypeRef("int")).accessibility(Accessibility.PUBLIC).start(18).end(19).create()
        typeA.addMember(getter)
//        Method mB = Method.builder().name("methodB").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
//        typeB.addMember(mB)
//        Method mC = Method.builder().name("methodC").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(6).end(7).create()
//        typeC.addMember(mC)
//        Method mD = Method.builder().name("methodD").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
//        typeD.addMember(mD)
//        Method mE = Method.builder().name("methodE").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
//        typeE.addMember(mE)
//        Method mF = Method.builder().name("methodF").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
//        typeF.addMember(mF)
//        Method mG = Method.builder().name("methodG").type(TypeRef.createPrimitiveTypeRef("void")).accessibility(Accessibility.PUBLIC).start(5).end(6).create()
//        typeG.addMember(mG)

//        typeA.associatedTo(typeB)
//        typeF.associatedTo(typeA)
//        typeB.associatedTo(typeH)
//        typeG.associatedTo(typeB)
//        typeC.generalizedBy(typeB)
//        typeD.generalizedBy(typeB)
//        typeE.generalizedBy(typeB)
//        typeC.associatedTo(typeI)
//        typeD.associatedTo(typeI)
//        typeE.associatedTo(typeI)

        sys.updateKeys()

        meth1.refresh()
        meth2.refresh()
        meth3.refresh()
        meth4.refresh()
        fld1.refresh()
        fld2.refresh()
        fld3.refresh()
        getter.refresh()

        meth1.usesField(fld1)
        meth2.usesField(fld1)
        meth2.usesField(fld2)
        meth3.usesField(fld1)
        meth3.usesField(fld2)
        meth3.callsMethod(getter)
        meth4.callsMethod(getter)
        getter.usesField(fld3)

        meth1.refresh()
        meth2.refresh()
        meth3.refresh()
        meth4.refresh()
        fld1.refresh()
        fld2.refresh()
        fld3.refresh()
        getter.refresh()
    }

    private void createPatternInstance() {
        Pattern p = Pattern.findFirst("name = ?", "Strategy")
        inst = PatternInstance.builder().instKey().create()
        p.addInstance(inst)
        proj.addPatternInstance(inst)

        Role roleA = Role.builder().name("Context").type(RoleType.CLASSIFIER).create()
//        Role roleB = Role.builder().name("AbstractStrategy").type(RoleType.CLASSIFIER).create()
//        Role roleC = Role.builder().name("ConcreteStrategy").type(RoleType.CLASSIFIER).create()
//        Role roleAB = Role.builder().name("RoleAB").type(RoleType.RELATION).create()
//        Role roleCB = Role.builder().name("RoleCB").type(RoleType.RELATION).create()
//        Role roleDB = Role.builder().name("RoleDC").type(RoleType.RELATION).create()
//        Role roleEB = Role.builder().name("RoleEB").type(RoleType.RELATION).create()

        Role roleM1 = Role.builder().name("RoleM1").type(RoleType.BEHAVE_FEAT).create()
        Role roleM2 = Role.builder().name("RoleM2").type(RoleType.BEHAVE_FEAT).create()
        Role roleM3 = Role.builder().name("RoleM3").type(RoleType.BEHAVE_FEAT).create()
        Role roleM4 = Role.builder().name("RoleM4").type(RoleType.BEHAVE_FEAT).create()
        Role roleF1 = Role.builder().name("RoleF1").type(RoleType.STRUCT_FEAT).create()
        Role roleF2 = Role.builder().name("RoleF2").type(RoleType.STRUCT_FEAT).create()
        Role roleF3 = Role.builder().name("RoleF3").type(RoleType.STRUCT_FEAT).create()

        p.addRole(roleA)
//        p.addRole(roleB)
//        p.addRole(roleC)
//        p.addRole(roleAB)
//        p.addRole(roleCB)
//        p.addRole(roleDB)
//        p.addRole(roleEB)
        p.addRole(roleM1)
        p.addRole(roleM2)
        p.addRole(roleF1)
        p.addRole(roleF3)

        RoleBinding rbA = RoleBinding.of(roleA, typeA.createReference())
        RoleBinding rbM1 = RoleBinding.of(roleM1, meth1.createReference())
        RoleBinding rbM2 = RoleBinding.of(roleM2, meth2.createReference())
        RoleBinding rbF1 = RoleBinding.of(roleF1, fld1.createReference())
        RoleBinding rbF3 = RoleBinding.of(roleF3, fld3.createReference())
//        RoleBinding rbB = RoleBinding.of(roleB, typeB.createReference())
//        RoleBinding rbC = RoleBinding.of(roleC, typeC.createReference())
//        RoleBinding rbD = RoleBinding.of(roleC, typeD.createReference())
//        RoleBinding rbE = RoleBinding.of(roleC, typeE.createReference())
//        RoleBinding rbMA = RoleBinding.of(roleMA, methodA.createReference())
//        RoleBinding rbAB = RoleBinding.of(roleAB, Relation.findBetween(typeA, typeB, edu.isu.isuese.datamodel.RelationType.ASSOCIATION).createReference())
//        RoleBinding rbCB = RoleBinding.of(roleCB, Relation.findBetween(typeB, typeC, edu.isu.isuese.datamodel.RelationType.ASSOCIATION).createReference())
//        RoleBinding rbDB = RoleBinding.of(roleDB, Relation.findBetween(typeD, TypeB, edu.isu.isuese.datamodel.RelationType.GENERALIZATION).createReference())
//        RoleBinding rbEB = RoleBinding.of(roleEB, Relation.findBetween(typeE, TypeB, edu.isu.isuese.datamodel.RelationType.GENERALIZATION).createReference())

        inst.addRoleBinding(rbA)
        inst.addRoleBinding(rbM1)
        inst.addRoleBinding(rbM2)
        inst.addRoleBinding(rbF1)
        inst.addRoleBinding(rbF3)
//        inst.addRoleBinding(rbB)
//        inst.addRoleBinding(rbC)
//        inst.addRoleBinding(rbD)
//        inst.addRoleBinding(rbE)
//        inst.addRoleBinding(rbMA)
//        inst.addRoleBinding(rbAB)
//        inst.addRoleBinding(rbCB)
//        inst.addRoleBinding(rbDB)
//        inst.addRoleBinding(rbEB)
    }

    private void createGraphs() {
        graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(1000)
                .expectedEdgeCount(10000)
                .build()

        GraphElementFactory factory = GraphElementFactory.instance
        a1 = factory.createNode(fld1)
        a2 = factory.createNode(fld2)
        a3 = factory.createNode(fld3)
        m1 = factory.createNode(meth1)
        m2 = factory.createNode(meth2)
        m3 = factory.createNode(meth3)
        m4 = factory.createNode(meth4)
        get = factory.createNode(getter)
    }

    private void associateNodesAndTypes() {
        fixture.fieldBiMap[fld1] = a1
        println("Fixture: $fixture")
        println("fieldBiMap: ${fixture.fieldBiMap.size()}")
        println("a2: $a2")
        println("mapping: ${fixture.fieldBiMap.get(fld2)}")
        fixture.fieldBiMap[fld2] = a2
        fixture.fieldBiMap[fld3] = a3
        fixture.methodBiMap[meth1] = m1
        fixture.methodBiMap[meth2] = m2
        fixture.methodBiMap[meth3] = m3
        fixture.methodBiMap[meth4] = m4
        fixture.methodBiMap[getter] = get
    }
}