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
package edu.isu.isuese.detstrat.impl

import com.google.common.collect.*
import com.google.common.graph.EndpointPair
import com.google.common.graph.MutableNetwork
import com.google.common.graph.Network
import com.google.common.graph.NetworkBuilder
import edu.isu.isuese.datamodel.*
import groovy.util.logging.Log4j2
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Log4j2
class ClassGrimeDetector extends AbstractGrimeDetector {

    PatternInstance instance
    BiMap<Field, Node> fieldBiMap = HashBiMap.create()
    BiMap<Method, Node> methodBiMap = HashBiMap.create()
    Map<Triple<Node, Node, Node>, Boolean> methodPairs = Maps.newHashMap()
    Map<Triple<Node, Node, Node>, Pair<Boolean, Boolean>> methodPairsIndirect = Maps.newHashMap()
    Table<Node, String, Double> methodDeltas = HashBasedTable.create()
    Table<Pair<Node, Node>, String, Double> pairDeltas = HashBasedTable.create()

    Type current

    /**
     * {@inheritDoc}
     */
    @Override
    List<Finding> detect(PatternInstance pattern) {

        this.instance = pattern
        List<Finding> findings = Lists.newLinkedList()

        // 2. Mark each method as either internal or external
        markMethods(pattern)

        pattern.getTypes().each { Type type ->
            current = type
            // 1. Construct method-attribute bipartite graph
            Network<Node, Relationship> graph = constructGraph(type)

            // 3. Mark each method pair as either internal or external
            markMethodPairs(graph)

            // 4. Calculate delta-TCC and delta-RCI for each method and method pair
            calculateDeltas(graph)
            // 5. Detect Grime
            findings += detectGrime(graph)
            // end for
        }
        // for each classifier in the pattern do

        findings
    }

    protected Network<Node, Relationship> constructGraph(Type c) {
        if (!c)
            throw new IllegalArgumentException()

        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .expectedNodeCount(1000)
                .expectedEdgeCount(10000)
                .build()

        fieldBiMap = HashBiMap.create()
        methodBiMap = HashBiMap.create()

        def findGetterSetters = { Method m ->
            m.getName().startsWith("get") || m.getName().startsWith("set") ||
                    m.getName().startsWith("is") || m.getName().startsWith("has")
        }

        // Need a list of the getter (including has and is) and setter methods
        // Need a mapping between the getter/setter methods and the fields they get/set
        // Need to also exclude constructors
        // Once this is done then create the direct connections via getFieldsUsedSameClass
        // Then setup indirect field uses (via get/set) using getMethodsUsedSameClass filtered on get/set/is/has

        c.getMethods().each { Method m ->
            GraphElementFactory factory = GraphElementFactory.instance

            if (!findGetterSetters(m)) {
                Node n = factory.createNode(m)
                methodBiMap[m] = n

                m.getFieldsUsedSameClass().each { Field x ->
                    connectMethodToField(x, factory, graph, n, false)
                }

                m.getMethodsUsedSameClass().findAll(findGetterSetters).each { Method z ->
                    z.getFieldsUsedSameClass().each { Field x ->
                        connectMethodToField(x, factory, graph, n, true)
                    }
                }
            }
        }

        graph
    }

    protected void connectMethodToField(Field x, GraphElementFactory factory, MutableNetwork<Node, Relationship> graph, Node n, boolean indirect) {
        if (!x || !graph || !n)
            throw new IllegalArgumentException()

        Node y = fieldBiMap[x]
        if (y) {
            Relationship rel = factory.createRelationship(RelationshipType.FieldAccess)
            rel.indirect = indirect
            graph.addEdge(n, y, rel)
        } else {
            y = factory.createNode(x)
            fieldBiMap[x] = y
            Relationship rel = factory.createRelationship(RelationshipType.FieldAccess)
            rel.indirect = indirect
            graph.addEdge(n, y, rel)
        }
    }

    protected void markMethods(PatternInstance inst) {
        if (!inst)
            throw new IllegalArgumentException()

        // if method m is associated with a role in pattern p then mark m as internal
        // else mark method m as external

        List<RoleBinding> bindings = inst.roleBindings.findAll() { RoleBinding rb ->
            rb.getReference()?.getType() == RefType.METHOD
        }

        bindings.each { RoleBinding rb ->
            String refKey = rb.getReference() != null ? rb.getReference().getRefKey() : ""
            methodBiMap.keySet().findAll { Method m -> m.getRefKey() == refKey }.each { Method m ->
                methodBiMap[m].internal = true
            }
        }
    }

    /* arr[]  ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Staring and Ending indexes in arr[]
    index  ---> Current index in data[]
    r ---> Size of a combination to be printed */

    protected void combinationUtil(Node field, List<Node> arr, List<Node> data, int start,
                                   int end, int index, Network<Node, Relationship> graph) {
        if (!field || arr == null || data == null || !graph || start < 0 || end < 0 || index < 0)
            throw new IllegalArgumentException()

        // Current combination is ready to be printed, print it
        if (index == 2) {
            Triple<Node, Node, Node> triple = Triple.of(data[0], data[1], field)
            methodPairs[triple] = false

            Relationship rel1 = null
            Relationship rel2 = null
            if (graph.edgeConnecting(triple.left, field).isPresent())
                rel1 = graph.edgeConnecting(triple.left, field).get()
            if (graph.edgeConnecting(triple.middle, field).isPresent())
                rel2 = graph.edgeConnecting(triple.middle, field).get()

            if (rel1 && rel2)
                methodPairsIndirect[triple] = Pair.of(rel1.indirect, rel2.indirect)
            return
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i = start; i <= end && end - i + 1 >= 2 - index; i++) {
            data[index] = arr[i]
            combinationUtil(field, arr, data, i + 1, end, index + 1, graph)
        }
    }

    // The main function that prints all combinations of size r
    // in arr[] of size n. This function mainly uses combinationUtil()
    protected void constructMethodPairs(Network<Node, Relationship> graph) {
        if (!graph)
            throw new IllegalArgumentException()

        methodPairs = [:]
        methodPairsIndirect = [:]

        fieldBiMap.values().each { Node n ->
            List<Node> arr = graph.predecessors(n).toList()

            if (arr.size() >= 2) {
                // A temporary array to store all combination one by one
                List<Node> data = []

                // Print all combination using temporary array 'data[]'
                combinationUtil(n, arr, data, 0, arr.size() - 1, 0, graph)
            }
        }
    }

    protected void markMethodPairs(Network<Node, Relationship> graph) {
        if (!graph)
            throw new IllegalArgumentException()

        // if for method pair (m, n) both m and n are associated with a role in pattern p, then mark the pair as internal
        // else mark method pair (m, n) as external
        constructMethodPairs(graph)

        methodPairs.keySet().each { Triple<Node, Node, Node> triple ->
            methodPairs[triple] = triple.getLeft().internal && triple.getMiddle().internal
        }
    }

    protected boolean isInternal(Node m1, Node m2, Node field) {
        if (methodPairs.containsKey(Triple.of(m1, m2, field)))
            return methodPairs[Triple.of(m1, m2, field)]
        else if (methodPairs.containsKey(Triple.of(m2, m1, field)))
            return methodPairs[Triple.of(m2, m1, field)]
        else
            false
    }

    protected boolean isInPairDeltas(Node m1, Node m2) {
        pairDeltas.containsRow(Pair.of(m1, m2))
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void calculateDeltas(Network<Node, Relationship> graph) {
        // Calculate delta-TCC and delta-RCI for each method
        calculateMethodDeltas(graph)
        // Calculate delta-TCC and delta-RCI for each method pair
        calculateMethodPairDeltas(graph)
    }

    void calculateMethodDeltas(Network<Node, Relationship> graph) {
        if (!graph)
            throw new IllegalArgumentException()

        // calculate TCC
        double baseTCC = calculateTCC(graph)

        // calculate RCI
        double baseRCI = calculateRCI(graph)

        methodDeltas = HashBasedTable.create()

        methodBiMap.keySet().each { Method m ->
            double deltaTCC = calculateTCC(graph, m) - baseTCC
            double deltaRCI = calculateRCI(graph, m) - baseRCI

            methodDeltas.put(methodBiMap[m], "TCC", deltaTCC)
            methodDeltas.put(methodBiMap[m], "RCI", deltaRCI)
        }
    }

    protected void calculateMethodPairDeltas(Network<Node, Relationship> graph) {
        if (!graph)
            throw new IllegalArgumentException()

        // calculate TCC
        double baseTCC = calculateTCC(graph)

        // calculate RCI
        double baseRCI = calculateRCI(graph)

        pairDeltas = HashBasedTable.create()

        methodPairs.keySet().each { Triple<Node, Node, Node> pair ->
            double deltaTCC = calculateTCC(graph, null, Pair.of(pair.left, pair.middle)) - baseTCC
            double deltaRCI = calculateRCI(graph, null, Pair.of(pair.left, pair.middle)) - baseRCI

            pairDeltas.put(Pair.of(pair.left, pair.middle), "TCC", deltaTCC)
            pairDeltas.put(Pair.of(pair.left, pair.middle), "RCI", deltaRCI)
        }
    }

    protected def calculateTCC(Network<Node, Relationship> graph, Method excludedMethod = null, Pair<Node, Node> excludedPair = null) {
        if (!graph)
            throw new IllegalArgumentException()

        int size = graph.nodes().findAll { it.type == NodeType.Method }.size()
        int attrs = graph.nodes().findAll { it.type == NodeType.Attribute }.size()
        if (excludedMethod)
            size -= 1
        double np = size * attrs
        if (excludedPair)
            np -= 2 * attrs

        int ndc = 0
        graph.edges().each {
            if (!it.indirect) {
                if (excludedMethod) {
                    EndpointPair<Node> ep = graph.incidentNodes(it)
                    if (ep.source() != methodBiMap[excludedMethod] && ep.target() != methodBiMap[excludedMethod])
                        ndc += 1
                } else if (excludedPair) {
                    EndpointPair<Node> ep = graph.incidentNodes(it)
                    if (ep.source() == excludedPair.left && graph.predecessors(ep.target()).contains(excludedPair.right))
                        ndc += 0
                    else if (ep.source() == excludedPair.right && graph.predecessors(ep.target()).contains(excludedPair.left))
                        ndc += 0
                    else
                        ndc += 1
                } else {
                    ndc += 1
                }
            }
        }

        (double) ndc / np
    }

    /**
     * Calculates a modified form or RCI based solely on the interactions between methods and attributes declared within the provided type
     * @param node
     * @param exclude
     * @param excludedPair
     * @return
     */
    protected def calculateRCI(Network<Node, Relationship> graph, Method exclude = null, Pair<Node, Node> excludedPair = null) {
        if (!graph)
            throw new IllegalArgumentException()

        int size = graph.nodes().findAll { it.type == NodeType.Method }.size()
        int attrs = graph.nodes().findAll { it.type == NodeType.Attribute }.size()
        if (exclude)
            size -= 1
        double max = size * attrs
        if (excludedPair)
            max -= 2 * attrs

        int ci = 0
        graph.edges().each {
            if (exclude) {
                EndpointPair<Node> ep = graph.incidentNodes(it)
                if (ep.source() != methodBiMap[exclude] && ep.target() != methodBiMap[exclude])
                    ci += 1
            } else if (excludedPair) {
                EndpointPair<Node> ep = graph.incidentNodes(it)
                if (ep.source() == excludedPair.left && graph.predecessors(ep.target()).contains(excludedPair.right))
                    ci += 0
                else if (ep.source() == excludedPair.right && graph.predecessors(ep.target()).contains(excludedPair.left))
                    ci += 0
                else
                    ci += 1
            } else {
                ci += 1
            }
        }

        (double) ci / max
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Finding> detectGrime(Network<Node, Relationship> graph) {
        List<Finding> findings = []

        // Method Pairs
        findings = detectPairMethods(graph)

        // Individual Methods
        findings += detectIndividualMethods(graph)

        findings
    }

    protected List<Finding> detectPairMethods(Network<Node, Relationship> graph) {
        List<Finding> findings = Lists.newArrayList()

        methodPairs.each { Triple<Node, Node, Node> triple, boolean internal ->
            Method m1 = methodBiMap.inverse().get(triple.getLeft())
            Method m2 = methodBiMap.inverse().get(triple.getMiddle())

            if (methodPairsIndirect[triple].getLeft() && methodPairsIndirect[triple].getRight()) {
                if (internal) {
                    if (pairDeltas.get(triple, "TCC") < 0) {
                        findings << createFinding("DIPG", m1)
                        findings << createFinding("DIPG", m2)
                        findings << createFinding("DIPG", instance)
                    }
                } else {
                    if (pairDeltas.get(triple, "TCC") < 0 && (m1.getMethodsCalling().isEmpty() || m2.getMethodsCalling().isEmpty())) {
                        findings << createFinding("DEPG", m1)
                        findings << createFinding("DEPG", m2)
                        findings << createFinding("DEPG", instance)
                    }
                }
            } else {
                if (internal) {
                    if (pairDeltas.get(triple, "TCC") < 0) {
                        findings << createFinding("IIPG", m1)
                        findings << createFinding("IIPG", m2)
                        findings << createFinding("IIPG", instance)
                    }
                } else {
                    if (pairDeltas.get(triple, "TCC") < 0 && (m1.getMethodsCalling().isEmpty() || m2.getMethodsCalling().isEmpty())) {
                        findings << createFinding("IEPG", m1)
                        findings << createFinding("IEPG", m2)
                        findings << createFinding("IEPG", instance)
                    }
                }
            }
        }

        findings
    }

    protected List<Finding> detectIndividualMethods(Network<Node, Relationship> graph) {
        List<Finding> findings = Lists.newArrayList()

        graph.edges().each { Relationship r ->
            EndpointPair<Node> points = graph.incidentNodes(r)
            if (!r.indirect) {
                if (points.source().internal) {
                    if (methodDeltas.get(points.source(), "RCI") < 0) {
                        findings << createFinding("DISG", methodBiMap.inverse().get(graph.incidentNodes(r).source()))
                        findings << createFinding("DISG", instance)
                    }
                } else {
                    if (methodDeltas.get(points.source(), "RCI") < 0 && methodBiMap.inverse().get(points.source()).getMethodsCalling().isEmpty()) {
                        findings << createFinding("DESG", methodBiMap.inverse().get(graph.incidentNodes(r).source()))
                        findings << createFinding("DESG", instance)
                    }
                }
            } else {
                if (points.source().internal) {
                    if (methodDeltas.get(points.source(), "RCI") < 0) {
                        findings << createFinding("IISG", methodBiMap.inverse().get(graph.incidentNodes(r).source()))
                        findings << createFinding("IISG", instance)
                    }
                } else {
                    if (methodDeltas.get(points.source(), "RCI") < 0 && methodBiMap.inverse().get(points.source()).getMethodsCalling().isEmpty()) {
                        findings << createFinding("IESG", methodBiMap.inverse().get(graph.incidentNodes(r).source()))
                        findings << createFinding("IESG", instance)
                    }
                }
            }
        }

        findings
    }
}
