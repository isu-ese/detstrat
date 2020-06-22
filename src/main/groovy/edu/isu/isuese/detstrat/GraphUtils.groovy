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
package edu.isu.isuese.detstrat

import com.google.common.collect.Lists
import com.google.common.graph.EndpointPair
import com.google.common.graph.MutableNetwork
import com.google.common.graph.Network
import com.google.common.graph.NetworkBuilder
import edu.isu.isuese.detstrat.impl.Node
import edu.isu.isuese.detstrat.impl.Path
import edu.isu.isuese.detstrat.impl.Relationship
import org.apache.commons.lang3.tuple.Pair

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Singleton
class GraphUtils {

    Network<Node, Relationship> spanningTree(Network<Node, ? extends Relationship> network) {
        MutableNetwork<Node, ? extends Relationship> spanningTree = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .expectedNodeCount(1000)
                .expectedEdgeCount(10000)
                .build()

        network.nodes().each {
            spanningTree.addNode(it)
        }

        network.edges().each { Relationship rel ->
            EndpointPair<Node> pair = network.incidentNodes(rel)
            spanningTree.addEdge(pair, rel)
            if (hasCycle(spanningTree))
                spanningTree.removeEdge(rel)
        }

        spanningTree
    }

    List<Node> topologicalSort(Network<Node, ? extends Relationship> network) {
        PriorityQueue<Pair<Node, Integer>> q = new PriorityQueue<>(new Comparator<Pair<Node, Integer>>() {
            @Override
            int compare(Pair<Node, Integer> e, Pair<Node, Integer> t1) {
                return Integer.compare(e.getValue(), t1.getValue())
            }
        })

        network.nodes().each { Node n ->
            q.offer(Pair.of(n, network.inDegree(n)))
        }

        List<Node> order = []

        while (!q.isEmpty()) {
            Pair<Node, Integer> x = q.poll()
            order << x.key
            network.successors(x.key).each { Node n ->
                Pair<Node, Integer> pair = q.find { it.key == n }
                q.remove(pair)
                q.offer(Pair.of(pair.key, pair.value - 1))
            }
        }

        return order
    }

    boolean hasCycle(Network<Node, ? extends Relationship> network) {
        Stack<Node> recStack = new Stack<>()
        List<Node> visited = []

        for (Node start : network.nodes())
            if (isCyclicUtil(network, start, visited, recStack))
                return true

        return false
    }

    boolean isCyclicUtil(Network<Node, ? extends Relationship> network, Node n, List<Node> visited, Stack<Node> recStack) {
        if (recStack.contains(n))
            return true
        if (visited.contains(n))
            return false

        visited << n
        recStack.push(n)

        for (Node c : network.successors(n))
            if (isCyclicUtil(network, c, visited, recStack))
                return true

        recStack.pop()

        return false
    }

    void reorderNodes(Network<Node, ? extends Relationship> network) {
        Network<Node, Relationship> spanningTree = spanningTree(network)
        List<Node> order = topologicalSort(spanningTree)
        order.eachWithIndex { it, ndx ->
            it.order = ndx
        }
    }

    def markCycles(Network<Node, ? extends Relationship> graph) {
        reorderNodes(graph)
        Queue<Path> que = Lists.newLinkedList()
        List<Path> cycles = []

        // put all vertices v_1,v_2,...,v_n into que
        graph.nodes().each { Node n ->
            Path p = new Path()
            p << n
            que.offer(p)
        }

        while (!que.isEmpty()) {
            // Get an open path P from Q, its head is v_h, its tail is v_t
            Path p = que.poll()
            // k = length(P)
            int k = p.length()
            // if (hasEdgeConnecting(v_t, v_h)) {
            if (graph.hasEdgeConnecting(p.tail(), p.head())) {
                //     output P + e as a cycle
                cycles << p
            }

            // get an adjacent edge of the tail whose end does not occur in the open path and the order of
            // its end is greater than the order of the head. This edge and the k length open path construct
            // a new k + 1 length open path. Put this new open path into the queue
            graph.successors(p.tail()).each { Node n ->
                if (!p.contains(n) && n.order > p.head().order) {
                    Path x = new Path(p)
                    x << n
                    que.offer(x)
                }
            }
        }

        cycles.each { Path p ->
            for (int i = 0; i < p.nodes.size() - 1; i++) {
                graph.edgeConnecting(p.nodes[i], p.nodes[i + 1]).get().cyclic = true
            }
            graph.edgeConnecting(p.tail(), p.head()).get().cyclic = true
        }
    }
}
