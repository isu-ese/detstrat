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
import edu.isu.isuese.detstrat.impl.Relationship
import edu.montana.gsoc.msusel.rbml.model.*
import groovy.util.logging.Log4j2

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Log4j2
class ModularGrimeDetector extends AbstractGrimeDetector {

    PatternInstance instance
    Table<Relationship, String, Integer> metrics = HashBasedTable.create()
    BiMap<Type, Node> nodes = HashBiMap.create()

    /**
     * {@inheritDoc}
     */
    @Override
    List<Finding> detect(PatternInstance pattern) {
        this.instance = pattern
        metrics = HashBasedTable.create()
        nodes = HashBiMap.create()

        List<Finding> findings
        // 1. Construct Pattern Graph
        Network<Node, Relationship> graph = constructPatternGraph(pattern)
        // 2. Mark relationships in graph
        markInternalOrExternal(graph, pattern)
        markTemporaryOrPersistent(graph)
        markValidOrInvalid(graph, pattern)
        // 3. Calculate delta-Ca
        // 4. Calculate delta-Ce
        calculateDeltas(graph)
        // 5. Detect Grime
        findings = detectGrime(graph)
        // 6. Return results
        findings
    }

    protected Network<Node, Relationship> constructPatternGraph(PatternInstance pattern) {
        if (!pattern)
            throw new IllegalArgumentException()

        MutableNetwork<Node, Relationship> graph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .expectedNodeCount(1000)
                .expectedEdgeCount(10000)
                .build()

        GraphElementFactory factory = GraphElementFactory.instance

        List<Type> types = pattern.getTypes()

        types.each { type ->
            Node node = factory.createNode(type)
            nodes[type] = node
            graph.addNode(node)
        }

        types.each { type ->
            type.getGeneralizes().each { towards ->
                if (!nodes[towards]) {
                    nodes[towards] = factory.createNode(towards)
                    graph.addNode(nodes[towards])
                }
                handleCreatingRelationships(type, towards, graph, RelationshipType.Generalization)
            }
            type.getRealizes().each { towards ->
                if (!nodes[towards]) {
                    nodes[towards] = factory.createNode(towards)
                    graph.addNode(nodes[towards])
                }
                handleCreatingRelationships(towards, type, graph, RelationshipType.Realization)
            }
            type.getAssociatedTo().each { towards ->
                if (!nodes[towards]) {
                    nodes[towards] = factory.createNode(towards)
                    graph.addNode(nodes[towards])
                }
                method(graph, type, towards, RelationshipType.Association)
            }
            type.getAssociatedFrom().each { from ->
                if (!nodes[from]) {
                    nodes[from] = factory.createNode(from)
                    graph.addNode(nodes[from])
                }
                method(graph, from, type, RelationshipType.Association)
            }
            type.getAggregatedTo().each { towards ->
                if (!nodes[towards]) {
                    nodes[towards] = factory.createNode(towards)
                    graph.addNode(nodes[towards])
                }
                method(graph, type, towards, RelationshipType.Aggregation)
            }
            type.getAggregatedFrom().each { from ->
                if (!nodes[from]) {
                    nodes[from] = factory.createNode(from)
                    graph.addNode(nodes[from])
                }
                method(graph, from, type, RelationshipType.Aggregation)
            }
            type.getComposedTo().each { towards ->
                if (!nodes[towards]) {
                    nodes[towards] = factory.createNode(towards)
                    graph.addNode(nodes[towards])
                }
                method(graph, type, towards, RelationshipType.Composition)
            }
            type.getComposedFrom().each { from ->
                if (!nodes[from]) {
                    nodes[from] = factory.createNode(from)
                    graph.addNode(nodes[from])
                }
                method(graph, from, type, RelationshipType.Composition)
            }
        }

        graph
    }

    def method(MutableNetwork<Node, Relationship> graph, from, towards, relType) {
        if (!nodes[from] || !nodes[towards] || !graph.hasEdgeConnecting(nodes[from], nodes[towards]) ||
                (graph.hasEdgeConnecting(nodes[from], nodes[towards]) &&
                        (hasEdgeConnectingWithType(graph.edgesConnecting(nodes[from], nodes[towards]), RelationshipType.Generalization) ||
                                (hasEdgeConnectingWithType(graph.edgesConnecting(nodes[from], nodes[towards]), RelationshipType.Realization)))))
            handleCreatingRelationships(towards, from, graph, relType)
    }

    def hasEdgeConnectingWithType(Set<Relationship> set, RelationshipType type) {
        return !set.findAll { it.type == type }.isEmpty()
    }

    protected void handleCreatingRelationships(Type towards, Type type, MutableNetwork<Node, Relationship> graph, RelationshipType reltype) {
        if (!towards || !type || graph == null || !reltype)
            throw new IllegalArgumentException()

        if (!nodes[type])
            nodes[type] = GraphElementFactory.instance.createNode(type)
        if (nodes[towards]) {
            createRelationship(towards, type, graph, reltype)
        } else {
            createNodeAndRelationship(towards, type, graph, reltype)
        }
    }

    protected void createRelationship(Type towards, Type type, MutableNetwork<Node, Relationship> graph, RelationshipType relationshipType) {
        if (!towards || !type || graph == null || !relationshipType)
            throw new IllegalArgumentException()

        GraphElementFactory factory = GraphElementFactory.instance
        graph.addEdge(nodes[type], nodes[towards], factory.createRelationship(relationshipType))
    }

    protected void createNodeAndRelationship(Type towards, Type type, MutableNetwork<Node, Relationship> graph, RelationshipType relationshipType) {
        if (!towards || !type || graph == null || !relationshipType)
            throw new IllegalArgumentException()

        GraphElementFactory factory = GraphElementFactory.instance
        Node node = factory.createNode(towards)
        nodes[towards] = node
        graph.addNode(node)
        createRelationship(towards, type, graph, relationshipType)
    }

    protected void markInternalOrExternal(Network<Node, Relationship> graph, PatternInstance pattern) {
        if (graph == null || !pattern)
            throw new IllegalArgumentException()

        // mark as internal if both sides of the relationship are members of the pattern instance
        // mark as external otherwise
        List<Type> patternTypes = pattern.getTypes()

        graph.nodes().each { node ->
            Type type = nodes.inverse()[node]
            node.internal = patternTypes.contains(type)
        }
    }

    protected void markTemporaryOrPersistent(Network<Node, Relationship> graph) {
        if (graph == null)
            throw new IllegalArgumentException()

        // mark as temporary if relationship type is a form of dependency (i.e., use dependency)
        // mark as persistent otherwise
        graph.edges().each { edge ->
            switch (edge.type) {
                case RelationshipType.Aggregation:
                case RelationshipType.Composition:
                case RelationshipType.Association:
                case RelationshipType.Generalization:
                case RelationshipType.Realization:
                    edge.persistent = true
                    break
                case RelationshipType.Dependency:
                case RelationshipType.UseDependency:
                case RelationshipType.PackageDependency:
                    edge.persistent = false
                    break
            }
        }
    }

    protected void markValidOrInvalid(Network<Node, Relationship> graph, PatternInstance pattern) {
        if (graph == null || !pattern)
            throw new IllegalArgumentException()

        SPS sps = loadRBML(pattern)
        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = createBindingsMap(sps, pattern)

        validateRoleMatchingRelationships(graph, sps, bindings)
        markValidOrInvalidGenHierRelationships(graph, sps, bindings)
        validateRemainingUnMarkedEdges(graph)
    }

    protected Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> createBindingsMap(SPS sps, PatternInstance pattern) {
        if (!sps || !pattern)
            throw new IllegalArgumentException()

        Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings = [:]
        pattern.getRoleBindings().each { RoleBinding rb ->
            Reference ref = rb.getReference()
            if (ref != null && ref.type == RefType.TYPE) {
                edu.montana.gsoc.msusel.rbml.model.Role role = sps.findTypeRoleByName(rb.getRole().getName())
                if (bindings[role]) {
                    Type t = findType(ref)
                    if (t)
                        bindings[role] << t
                } else {
                    Type t = findType(ref)
                    if (t)
                        bindings[role] = [t]
                }
            }
        }

        bindings
    }

    protected void validateRoleMatchingRelationships(Network<Node, Relationship> graph, SPS sps, Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings) {
        if (!graph || !sps || bindings == null) {
            throw new IllegalArgumentException()
        }

        sps.relations.each { edu.montana.gsoc.msusel.rbml.model.Role r ->
            edu.montana.gsoc.msusel.rbml.model.Relationship rel = (edu.montana.gsoc.msusel.rbml.model.Relationship) r

            edu.montana.gsoc.msusel.rbml.model.Role src = rel.source()
            edu.montana.gsoc.msusel.rbml.model.Role dest = rel.dest()

            if (bindings[src] && bindings[dest]) {
                bindings[src].each { s ->
                    bindings[dest].each { d ->
                        Relationship edge = graph.edgesConnecting(nodes[s], nodes[d]).find { relationMatch(rel, it) }
                        if (edge) {
                            edge.marked = true
                            edge.invalid = false
                        }
                    }
                }
            }
        }
    }

    protected void markValidOrInvalidGenHierRelationships(Network<Node, Relationship> graph, SPS sps, Map<edu.montana.gsoc.msusel.rbml.model.Role, List<Type>> bindings) {
        if (!graph || !sps || bindings == null)
            throw new IllegalArgumentException()

        if (!bindings) {
            sps.genHierarchies.each { r ->
                GeneralizationHierarchy gh = (GeneralizationHierarchy) r
                def classes = gh.children.findAll { it instanceof ClassRole }
                def classifiers = gh.children.findAll { it instanceof edu.montana.gsoc.msusel.rbml.model.Classifier && !(it instanceof ClassRole) }

                List<Type> classBindings = []
                List<Type> classifierBindings = []

                classifiers.each { if (bindings[it]) classifierBindings += bindings[it] }
                classes.each { if (bindings[it]) classBindings += bindings[it] }

                validateCorrectInheritanceRelations(graph, classifierBindings, classBindings)
                invalidateSuperToSubtypeRelations(graph, classifierBindings, classBindings)
                invalidateUnmarkedIncomingInheritanceRelations(graph, classifierBindings)
                invalidateUnmarkedIncomingInheritanceRelations(graph, classBindings)
                invalidateUnmarkedIncomingRelations(graph, classBindings)
            }
        }
    }

    protected void validateCorrectInheritanceRelations(Network<Node, Relationship> graph, List<Type> superTypes, List<Type> subTypes) {
        if (graph == null || superTypes == null || subTypes == null) {
            throw new IllegalArgumentException("graph, supertypes, or subtypes cannot be null")
        }

        superTypes.each { cl ->
            subTypes.each { cls ->
                graph.edgesConnecting(nodes[cls], nodes[cl]).findAll { !it.marked && (it.type == RelationshipType.Generalization || it.type == RelationshipType.Realization) }.each {
                    it.marked = true
                    it.invalid = false
                }
            }
        }
    }

    protected void invalidateSuperToSubtypeRelations(Network<Node, Relationship> graph, List<Type> superTypes, List<Type> subTypes) {
        if (graph == null || superTypes == null || subTypes == null)
            throw new IllegalArgumentException("Graph, superTypes, or subTypes cannot be null")

        superTypes.each { cl ->
            subTypes.each { cls ->
                graph.edgesConnecting(nodes[cl], nodes[cls]).findAll { !it.marked }.each {
                    it.marked = true
                    it.invalid = true
                }
            }
        }
    }

    protected void invalidateUnmarkedIncomingInheritanceRelations(Network<Node, Relationship> graph, List<Type> types) {
        if (graph == null || types == null)
            throw new IllegalArgumentException("graph and types cannot be null")

        types.each {
            graph.inEdges(nodes[it]).findAll { (it.type == RelationshipType.Realization || it.type == RelationshipType.Generalization) && !it.marked }.each {
                it.marked = true
                it.invalid = true
            }
        }
    }

    protected void invalidateUnmarkedIncomingRelations(Network<Node, Relationship> graph, List<Type> types) {
        if (graph == null || types == null)
            throw new IllegalArgumentException("graph and types cannot be null")

        types.each {
            graph.inEdges(nodes[it]).findAll { !(it.type == RelationshipType.Realization || it.type == RelationshipType.Generalization) && !it.marked }.each {
                it.marked = true
                it.invalid = true
            }
        }
    }

    protected boolean relationMatch(edu.montana.gsoc.msusel.rbml.model.Relationship rel, Relationship edge) {
        if (!rel || !edge)
            throw new IllegalArgumentException()

        (rel instanceof Generalization && edge.type == RelationshipType.Generalization) ||
                (rel instanceof Realization) && edge.type == RelationshipType.Realization ||
                (rel instanceof Association) && edge.type == RelationshipType.Association ||
                (rel instanceof Aggregation) && edge.type == RelationshipType.Aggregation ||
                (rel instanceof Composition) && edge.type == RelationshipType.Composition ||
                (rel instanceof Usage && (edge.type == RelationshipType.UseDependency || edge.type == RelationshipType.Dependency))
    }

    protected boolean validateRemainingUnMarkedEdges(Network<Node, Relationship> graph) {
        if (graph == null)
            throw new IllegalArgumentException()

        graph.edges().findAll { !it.marked }.each { edge ->
            edge.marked = true
//            edge.invalid = false
            edge.invalid = true
        }
    }

    private Type findType(Reference ref) {
        nodes.keySet().find { Type t -> t.getRefKey() == ref.getRefKey() }
    }

    @Override
    void calculateDeltas(Network<Node, Relationship> graph) {
        if (graph == null)
            throw new IllegalArgumentException()

        // Calculate base Ca and Ce
        int baseCa = afferentCoupling(graph, null)
        int baseCe = efferentCoupling(graph, null)
        // foreach relationship, r, in graph do
        graph.edges().each { edge ->
            // 1. calculate Ca withholding the relationship
            int dCa = baseCa - afferentCoupling(graph, edge)
            // 2. calculate Ce withholding the relationship
            int dCe = baseCe - efferentCoupling(graph, edge)
            // 3. mark the deltas
            metrics.put(edge, "Ca", dCa)
            metrics.put(edge, "Ce", dCe)
            // endfor
        }
    }

/**
 * Calculates the afferent coupling of the pattern instance defined by the provided graph. Where, afferent coupling
 * is the number of classes outside (external to the pattern instance) that depend on classes within the pattern instance
 * (those fitting a role of the pattern).
 * @param graph Graph representing the pattern instance.
 * @param rel a relationship to exclude from the calculation, if null then the actual Ca is calculated
 * @return Measure for the afferent coupling
 */
    protected int afferentCoupling(Network<Node, Relationship> graph, Relationship rel) {
        if (graph == null)
            throw new IllegalArgumentException()

        int ca = 0

        // 1.  collect list of non-pattern types
        def nonPatternTypes = graph.nodes().findAll { !it.internal }
        // 2.  for each of the collected non-pattern types, t
        nonPatternTypes.each { type ->
            // 3.    for each predecessor of t, r, (source nodes of incoming edges of t)
            graph.successors(type).each { outgoing ->
                // 4.      if r is internal to the pattern
                if (outgoing.internal) {
                    Set<Relationship> set = graph.edgesConnecting(type, outgoing)
                    set.each {
                        if (it != rel)
                            ca += 1
                    }
                }
                // 6. endfor
            }
            // 7. endfor
        }
        ca
    }

    /**
     * Calculates the efferent coupling of the pattern instance defined by the provided graph. Where, efferent coupling
     * is the number of classes inside (those fitting a role of the pattern) that depend on classes outside (external to
     * pattern instance).
     * @param graph Graph representing the pattern instance.
     * @param rel a relationship to exclude from the calculation, if null then the actual Ce is calculated
     * @return
     */
    protected int efferentCoupling(Network<Node, Relationship> graph, Relationship rel) {
        if (graph == null)
            throw new IllegalArgumentException()

        int ce = 0

        // 1.  collect list of pattern types
        def patternTypes = graph.nodes().findAll { it.internal }
        // 2.  for each of the collected pattern types, t
        patternTypes.each { type ->
            // 3.    for each predecessor of t, r, (source nodes of incoming edges of t)
            graph.successors(type).each { incoming ->
                // 4.      if r is not internal to the pattern, then
                if (!incoming.internal) {
                    Set<Relationship> set = graph.edgesConnecting(type, incoming)
                    set.each {
                        if (it != rel)
                            ce += 1
                    }
                }
                // 6. endfor
            }
            // 7. endfor
        }

        ce
    }

/**
 * This method detects grime instances
 * @param graph
 * @return
 */
    @Override
    List<Finding> detectGrime(Network<Node, Relationship> graph) {
        log.info "Detecting Modular Grime"
        if (graph == null)
            throw new IllegalArgumentException()

        List<Finding> findings = Lists.newArrayList()

        // for each relationship r in graph
        graph.edges().each { edge ->
            //   if r is invalid then
            if (edge.invalid) {
                // if r is internal then
                EndpointPair<Node> nodePair = graph.incidentNodes(edge)
                Type src = nodes.inverse().get(nodePair.source())
                Type dest = nodes.inverse().get(nodePair.target())
                if (nodePair.source().internal && nodePair.target().internal && (metrics.get(edge, "Ca") < 0 || metrics.get(edge, "Ce") < 0)) {
                    // if r persistent then
                    if (edge.persistent && edge.invalid) {
                        // add PIG issue for that relationship
                        findings << createFinding("PIG", src, dest, instance)
                    }
                    // else
                    else if (edge.invalid) {
                        // add TIG issue for that relationship
                        findings << createFinding("TIG", src, dest, instance)
                    }
                }
                // else
                else if (nodePair.source().internal || nodePair.target().internal) {
                    // if r is persistent
                    if (edge.persistent && edge.invalid) {
                        // if Ca increases
                        if (nodePair.target().internal && metrics.get(edge, "Ca") < 0) {
                            // add PEAG issue for that relationship
                            findings << createFinding("PEAG", src, dest, instance)
                        }
                        // else if Ce increases
                        else if (nodePair.source().internal && metrics.get(edge, "Ce") < 0) {
                            // add PEEG issue for that relationship
                            findings << createFinding("PEEG", src, dest, instance)
                        }
                    }
                    // else
                    else if (edge.invalid) {
                        // if Ca increases
                        if (nodePair.target().internal && metrics.get(edge, "Ca") < 0) {
                            // add TEAG issue for that relationship
                            findings << createFinding("TEAG", src, dest, instance)
                        }
                        // else if Ce increases
                        else if (nodePair.source().internal && metrics.get(edge, "Ce") < 0) {
                            // add TEEG issue for that relationship
                            findings << createFinding("TEEG", src, dest, instance)
                        }
                    }
                }
            }
            // endfor
        }

        findings
    }

}