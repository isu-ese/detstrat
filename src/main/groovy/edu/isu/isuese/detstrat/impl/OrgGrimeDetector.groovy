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

import com.google.common.collect.BiMap
import com.google.common.collect.HashBasedTable
import com.google.common.collect.HashBiMap
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Queues
import com.google.common.collect.Sets
import com.google.common.collect.Table
import com.google.common.graph.EndpointPair
import com.google.common.graph.MutableNetwork
import com.google.common.graph.Network
import com.google.common.graph.NetworkBuilder
import edu.isu.isuese.datamodel.*
import edu.isu.isuese.detstrat.GraphUtils

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class OrgGrimeDetector extends AbstractGrimeDetector {

    MutableNetwork<Node, NamespaceRelation> nsGraph
    MutableNetwork<Node, Relationship> typeGraph

    BiMap<Type, Node> typeBiMap = HashBiMap.create()
    BiMap<Namespace, Node> nsBiMap = HashBiMap.create()
    Map<Node, List<Node>> nsTypeMap = Maps.newHashMap()

    Table<Node, String, Double> nsMetrics = HashBasedTable.create()
    Table<Node, String, Double> typeMetrics = HashBasedTable.create()

    /**
     * {@inheritDoc}
     */
    @Override
    List<Finding> detect(PatternInstance pattern) {
        if (!pattern)
            throw new IllegalArgumentException()

        constructGraphs(pattern)

        // mark
        mark(pattern)

        calculateDeltas(null)

        detectGrime(null)
    }

    void mark(PatternInstance p) {
        List<Namespace> l = findPatternNamespaces(p)
        markNamespaces(l)

        markTypes(p)

        markCycles()
    }

    protected void markNamespaces(List<Namespace> list) {
        if (list == null)
            throw new IllegalArgumentException()

        list.each { Namespace ns ->
            Node nsNode = nsBiMap[ns]
            nsTypeMap[nsNode].each { Node n ->
                n.patternNSInternal = true
            }
            nsNode.patternInternal = true
        }
    }

    protected void markTypes(PatternInstance p) {
        if (!p)
            throw new IllegalArgumentException()
        p.types.each { Type type ->
            Node t = typeBiMap[type]
            t.patternInternal = true
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void calculateDeltas(Network<Node, Relationship> graph) {
        measureCa()
        measureCe()
        measureNa()
        measureNc()
        measureI()
        measureA()
        measureD()
        measureCohesionQ()
        measureCouplingQ()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Finding> detectGrime(Network<Node, Relationship> graph) {
        List<Finding> findings = []

        // Package
        typeGraph.nodes().each { Node n ->
            // PECG
            if (!n.patternInternal && n.patternNSInternal && typeMetrics.get(n, "CohesionQ") < 0) {
                findings << createFinding("PECG", n)
            }
            // PICG
            else if (n.patternInternal && n.patternNSInternal && typeMetrics.get(n, "CohesionQ") < 0) {
                findings << createFinding("PICG", n)
            }
            // PERG
            else if (!n.patternInternal && n.patternNSInternal && typeMetrics.get(n, "CouplingQ") < 0) {
                findings << createFinding("PERG", n)
            }
            // PIRG
            else if (n.patternInternal && n.patternNSInternal && typeMetrics.get(n, "CouplingQ") < 0) {
                findings << createFinding("PIRG", n)
            }
        }

        // Modular
        nsGraph.edges().each { NamespaceRelation rel ->
            EndpointPair<Node> pair = nsGraph.incidentNodes(rel)
            Node src = pair.source()
            Node dest = pair.target()

            // MPECG
            if (rel.persistent && ((dest.patternInternal && !src.patternInternal) || (src.patternInternal && !dest.patternInternal)) &&
                    cycle(src, dest, rel)) {
                findings << createFinding("MPECG", rel, typeGraph)
            }
            // MTECG
            else if (!rel.persistent && ((dest.patternInternal && !src.patternInternal) || (src.patternInternal && !dest.patternInternal)) &&
                    cycle(src, dest, rel)) {
                findings << createFinding("MTECG", rel, typeGraph)
            }
            // MPICG
            else if (rel.persistent && (dest.patternInternal && src.patternInternal) && cycle(src, dest, rel)) {
                findings << createFinding("MPICG", rel, typeGraph)
            }
            // MTICG
            else if (!rel.persistent && (dest.patternInternal && src.patternInternal) && cycle(src, dest, rel)) {
                findings << createFinding("MTICG", rel, typeGraph)
            }
            // MPEUG
            else if (rel.persistent && ((dest.patternInternal && !src.patternInternal) || (src.patternInternal && !dest.patternInternal)) &&
                    dropInstability(rel)) {
                findings << createFinding("MPEUG", rel, nsGraph)
            }
            // MTEUG
            else if (!rel.persistent && ((dest.patternInternal && !src.patternInternal) || (src.patternInternal && !dest.patternInternal)) &&
                    dropInstability(rel)) {
                findings << createFinding("MTEUG", rel, nsGraph)
            }
            // MPIUG
            else if (rel.persitent && (dest.patternInternal && src.patternInternal) && dropInstability(rel)) {
                findings << createFinding("MPIUG", rel, nsGraph)
            }
            // MTIUG
            else if (!rel.persistent && (dest.patternInternal && src.patternInternal) && dropInstability(rel)) {
                findings << createFinding("MTIUG", rel, nsGraph)
            }
        }

        findings
    }

    Finding createFinding(String name, NamespaceRelation rel, Network<Node, NamespaceRelation> graph) {
        if (!name || !rel || graph == null)
            throw new IllegalArgumentException()

        Namespace t = nsBiMap.inverse()[graph.incidentNodes(rel).source()]
        Reference ref = t.createReference()
        createFinding(name, ref)
    }

    Finding createFinding(String name, Node node) {
        if (!name || !node)
            throw new IllegalArgumentException()

        Type t = typeBiMap.inverse()[node]
        Reference ref = t.createReference()
        createFinding(name, ref)
    }

    def findPatternNamespaces(PatternInstance p) {
        if (!p)
            throw new IllegalArgumentException()

        Set<Namespace> set = Sets.newHashSet()
        p.getTypes().each { Type t ->
            set << t.getParentNamespaces().first()
        }

        set.asList()
    }

    def cycle(Node src, Node dest, NamespaceRelation rel) {
        if (!src || !dest || !rel)
            throw new IllegalArgumentException()

        if (rel.cyclic) {
            double baseSrcCycDQ, baseDestCycDQ, newSrcCycDQ, newDestCycDQ
            (baseSrcCycDQ, newSrcCycDQ) = measureCycleQuality(src, rel)
            (baseDestCycDQ, newDestCycDQ) = measureCycleQuality(dest, rel)

            println "baseSrcCycDQ: $baseSrcCycDQ"
            println "newSrcCycDQ: $newSrcCycDQ"
            println "baseDestCycDQ: $baseDestCycDQ"
            println "newDestCycDQ: $newDestCycDQ"

            return (baseSrcCycDQ - newSrcCycDQ) < 0 || (baseDestCycDQ - newDestCycDQ) < 0
        }

        return false
    }

    def measureCycleQuality(Node ns, NamespaceRelation rel) {
        if (!ns || !rel)
            throw new IllegalArgumentException()

        double basePD = 0
        double baseCycD = 0
        double newPD = 0
        double newCycD = 0

        nsGraph.incidentEdges(ns).each {
            basePD += it.contained.size()
            if (it.cyclic) {
                baseCycD += it.contained.size()
            }
            if (it != rel) {
                newPD += it.contained.size()
                if (it.cyclic)
                    newCycD += it.contained.size()
            }
        }

        double baseRatio = basePD == 0 ? 0 : (baseCycD / basePD)
        double newRatio = newPD == 0 ? 0 : (newCycD / newPD)

        double baseCycDQ = 1 - baseRatio
        double newCycDQ = 1 - newRatio

        [baseCycDQ, newCycDQ]
    }

    def dropInstability(NamespaceRelation rel) {
        if (!rel)
            throw new IllegalArgumentException()

        EndpointPair<Node> pair = nsGraph.incidentNodes(rel)

        // current instability
        double baseSrcD = nsMetrics.get(pair.source(), "D")
        double baseDestD = nsMetrics.get(pair.target(), "D")

        // D without relation
        double newSrcD = measureDWithoutRelation(pair.source(), rel)
        double newDestD = measureDWithoutRelation(pair.target(), rel)

        // if currently, direction points towards instability D(s) < D(t), and
        // without the relationship it was towards stability D(s, r) >= D(t, r), then
        // this implies that the relationship causes or contributes to the instability and
        // points towards instability which is a violation of the SAP
        return baseSrcD < baseDestD && newSrcD >= newDestD
    }

    def measureCohesionQ() {
        nsGraph.nodes().each { Node p ->
            double base = cohesionQ(p, null)
            nsMetrics.put(p, "CohesionQ", base)
            nsTypeMap[p].each { Node t ->
                double change = cohesionQ(p, t)
                typeMetrics.put(t, "CohesionQ", base - change)
            }
        }
    }

    def cohesionQ(Node p, Node t) {
        if (!p)
            throw new IllegalArgumentException()

        Set<Relationship> pintD = findPIntDSet(p, t)

        double pD = findPD(p, t, pintD)

        if (pD == 0)
            return 0
        (double) pintD.size() / pD
    }

    protected double findPD(Node p, Node t, Set<Relationship> pintD) {
        if (!p || pintD == null)
            throw new IllegalArgumentException()

        Set<Relationship> pextD = findPExtDSet(nsGraph.outEdges(p), t) + findPExtDSet(nsGraph.inEdges(p), t)

        (double) (pextD + pintD).size()
    }

    protected Set<Relationship> findPExtDSet(Set<NamespaceRelation> edges, Node t) {
        if (edges == null)
            throw new IllegalArgumentException()

        Set<Relationship> pextD = new HashSet<>()

        edges.each { NamespaceRelation r ->
            if (t == null) {
                r.contained.each { pextD << it }
            } else {
                r.contained.each {
                    if (typeGraph.incidentNodes(it).target() != t && typeGraph.incidentNodes(it).source() != t)
                        pextD << it
                }
            }
        }

        pextD
    }

    protected HashSet<Relationship> findPIntDSet(Node p, Node t) {
        if (!p)
            throw new IllegalArgumentException()

        Set<Node> pintD = Sets.newHashSet()

        nsTypeMap[p].each { Node n ->
            pintD += typeGraph.predecessors(n).findAll { Node pred -> nsTypeMap[p].contains(pred) }
            pintD += typeGraph.successors(n).findAll { Node succ -> nsTypeMap[p].contains(succ) }
        }

        Set<Relationship> rels = [].toSet()
        pintD.each { u ->
            if (!t || u != t) {
                pintD.each { v ->
                    if (!t || v != t) {
                        if (typeGraph.hasEdgeConnecting(u, v))
                            rels << typeGraph.edgeConnecting(u, v).get()
                    }
                }
            }
        }

        rels
    }

    def measureCouplingQ() {
        nsGraph.nodes().each { Node p ->
            double base = couplingQ(p, null)
            nsMetrics.put(p, "CouplingQ", base)
            nsTypeMap[p].each { Node t ->
                double change = couplingQ(p, t)
                typeMetrics.put(t, "CouplingQ", base - change)
            }
        }
    }

    def couplingQ(Node p, Node t) {
        if (!p)
            throw new IllegalArgumentException()

        Set<Relationship> pintD = findPIntDSet(p, t)

        double pD = findPD(p, t, pintD)

        Set<Node> pcliPproP = getPcliP(p, t) + getPproP(p, t)

        if (pD == 0)
            return 1
        else
            1 - ((double) pcliPproP.size() / pD)
    }

    protected Set<Node> getPproP(Node p, Node t) {
        if (!p)
            throw new IllegalArgumentException()

        Node toRemove = null
        nsGraph.successors(p).each {
            if (nsTypeMap[it].contains(t)) {
                if (nsGraph.hasEdgeConnecting(p, it)) {
                    if (nsGraph.edgeConnecting(p, it).get().contained.size() == 1) {
                        EndpointPair<Node> pair = typeGraph.incidentNodes(nsGraph.edgeConnecting(p, it).get().contained[0])
                        if (pair.target() == t)
                            toRemove = it
                    }
                }
            }
        }

        Set<Node> pprop = new HashSet<>(nsGraph.successors(p))
        if (toRemove)
            pprop.remove(toRemove)

        pprop
    }

    protected Set<Node> getPcliP(Node p, Node t) {
        if (!p)
            throw new IllegalArgumentException()

        Node toRemove = null
        nsGraph.predecessors(p).each {
            if (nsTypeMap[it].contains(t)) {
                if (nsGraph.hasEdgeConnecting(it, p)) {
                    if (nsGraph.edgeConnecting(it, p).get().contained.size() == 1) {
                        EndpointPair<Node> pair = typeGraph.incidentNodes(nsGraph.edgeConnecting(it, p).get().contained[0])
                        if (pair.source() == t)
                            toRemove = it
                    }
                }
            }
        }

        Set<Node> pclip = new HashSet<>(nsGraph.predecessors(p))
        if (toRemove)
            pclip.remove(toRemove)

        pclip
    }

    void constructGraphs(PatternInstance inst) {
        if (!inst)
            throw new IllegalArgumentException()

        // Initialize Variables
        Project proj = inst.getParentProjects().first()
        List<Namespace> namespaces = proj.getNamespaces()
        GraphElementFactory factory = GraphElementFactory.instance
        typeBiMap = HashBiMap.create()
        nsBiMap = HashBiMap.create()

        createGraphs()
        addNodes(namespaces, factory)

        // Add Edges
        typeGraph.nodes().each { Node n ->
            Type t = typeBiMap.inverse().get(n)

            addEdges(t, n, t.getAssociatedTo(), RelationshipType.Association, factory)
            addEdges(t, n, t.getGeneralizedBy(), RelationshipType.Generalization, factory)
            addEdges(t, n, t.getRealizes(), RelationshipType.Realization, factory)
            addEdges(t, n, t.getAggregatedTo(), RelationshipType.Aggregation, factory)
            addEdges(t, n, t.getComposedTo(), RelationshipType.Composition, factory)
            addEdges(t, n, t.getUseFrom(), RelationshipType.UseDependency, factory)
            addEdges(t, n, t.getDependencyTo(), RelationshipType.Dependency, factory)
        }
    }

    protected void addNodes(List<Namespace> namespaces, GraphElementFactory factory) {
        if (namespaces == null || !factory)
            throw new IllegalArgumentException()

        namespaces.each { Namespace ns ->
            Node nsNode = factory.createNode(ns)
            nsBiMap[ns] = nsNode
            nsTypeMap[nsNode] = []

            nsGraph.addNode(nsNode)

            ns.getFiles().each {
                it.getAllTypes().each { Type t ->
                    Node tNode = factory.createNode(t)
                    typeBiMap[t] = tNode
                    if (nsTypeMap[nsNode]) {
                        nsTypeMap[nsNode] << tNode
                    } else {
                        nsTypeMap[nsNode] = [tNode]
                    }

                    typeGraph.addNode(tNode)
                }
            }
        }
    }

    protected createGraphs() {
        nsGraph = initializeGraph()
        typeGraph = initializeGraph()
    }

    protected <T> MutableNetwork<Node, T> initializeGraph() {
        NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .expectedNodeCount(1000)
                .expectedEdgeCount(10000)
                .build()
    }

    protected addEdges(Type type, Node node, Set<Type> types, RelationshipType relType, GraphElementFactory factory) {
        types.each { Type other ->
            Node otherNode = typeBiMap[other]

            Relationship rel = factory.createRelationship(relType)
            rel.persistent = isPersistent(relType)
            typeGraph.addEdge(node, otherNode, rel)

            Namespace ns1 = type.getParentNamespaces().first()
            Namespace ns2 = other.getParentNamespaces().first()
            if (ns1 == ns2) {
                rel.sameNamespace = true
            } else {
                Node nsNode1 = nsBiMap[ns1]
                Node nsNode2 = nsBiMap[ns2]

                Relationship nsRel = factory.createRelationship(relType)
                nsRel.persistent = isPersistent(relType)

                if (nsGraph.hasEdgeConnecting(nsNode1, nsNode2)) {
                    nsGraph.edgeConnecting(nsNode1, nsNode2).ifPresent { Relationship r ->
                        if (!r.persistent && nsRel.persistent)
                            r.persistent = true
                        if (r instanceof NamespaceRelation) {
                            r.contained << nsRel
                        }
                    }
                } else {
                    NamespaceRelation nsr = new NamespaceRelation()
                    nsGraph.addEdge(nsNode1, nsNode2, nsr)
                    nsr.contained << nsRel
                    if (!nsr.persistent && nsRel.persistent)
                        nsr.persistent = true
                }
            }
        }
    }

    protected boolean isPersistent(RelationshipType type) {
        if (!type)
            throw new IllegalArgumentException()

        return type != RelationshipType.UseDependency && type != RelationshipType.Dependency
    }

    def measureI() {
        nsGraph.nodes().each { Node p ->
            double ce = nsMetrics.get(p, "Ce")
            double ca = nsMetrics.get(p, "Ca")
            if ((ca + ce) == 0)
                nsMetrics.put(p, "I", 0)
            else
                nsMetrics.put(p, "I", ce / (ca + ce))
        }
    }

    def measureIWithoutRelation(Node p, NamespaceRelation rel) {
        if (!p || !rel)
            throw new IllegalArgumentException()

        double ce = measureCeWithoutRelation(p, rel)
        double ca = measureCaWithoutRelation(p, rel)

        (ce / (ca + ce))
    }

    /*
     * Where, afferent coupling is the number of classes outside (external to the pattern instance)
     * that depend on classes within the pattern instance (those fitting a role of the pattern).
     */
    def measureCa() {
        nsGraph.nodes().each { Node p ->
            int ca = 0
            nsGraph.inEdges(p).each {
                ca += it.contained.size()
            }

            nsMetrics.put(p, "Ca", ca)
        }
    }

    def measureCaWithoutRelation(Node p, NamespaceRelation rel) {
        if (!p || !rel)
            throw new IllegalArgumentException()

        int ca = 0
        nsGraph.inEdges(p).each {
            if (it != rel)
                ca += it.contained.size()
        }

        ca
    }

    def measureCe() {
        nsGraph.nodes().each { Node p ->
            int ce = 0
            nsGraph.outEdges(p).each {
                ce += it.contained.size()
            }

            nsMetrics.put(p, "Ce", ce)
        }
    }

    def measureCeWithoutRelation(Node p, NamespaceRelation rel) {
        if (!p || !rel)
            throw new IllegalArgumentException()

        int ce = 0
        nsGraph.outEdges(p).each {
            if (it != rel)
                ce += it.contained.size()
        }

        ce
    }

    def measureA() {
        nsGraph.nodes().each { Node p ->
            double na = nsMetrics.get(p, "Na")
            double nc = nsMetrics.get(p, "Nc")
            if (na == 0 && nc == 0)
                nsMetrics.put(p, "A", 0)
            else
                nsMetrics.put(p, "A", na / nc)
        }
    }

    def measureNa() {
        nsGraph.nodes().each { Node p ->
            int na = 0
            nsTypeMap.get(p).each {
                if (typeBiMap.inverse().get(it).isAbstract())
                    na += 1
            }
            nsMetrics.put(p, "Na", na)
        }
    }

    def measureNc() {
        nsGraph.nodes().each { Node p ->
            println "P: ${p.name}"
            int nc = nsTypeMap.get(p).size()
            nsMetrics.put(p, "Nc", nc)
        }
    }

    protected void measureD() {
        nsGraph.nodes().each { Node p ->
            double I = nsMetrics.get(p, "I")
            double A = nsMetrics.get(p, "A")
            nsMetrics.put(p, "D", Math.abs(A + I - 1))
        }
    }

    protected double measureDWithoutRelation(Node ns, NamespaceRelation rel) {
        double A = nsMetrics.get(ns, "A")

        double dI = measureIWithoutRelation(ns, rel)

        Math.abs(dI + A - 1)
    }

    // Creates a lists of cycles List<List<Node>> for a given graph based on the algorithm by Liu and Wang described
    // in the paper at doi:10.1109/AICT-ICIW.2006.22
    def markCycles() {
        GraphUtils.instance.markCycles(nsGraph)
    }
}
