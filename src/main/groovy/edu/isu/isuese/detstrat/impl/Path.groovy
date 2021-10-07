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

import com.google.common.collect.Lists
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@EqualsAndHashCode
@ToString
class Path {

    List<Node> nodes

    Path() {
        nodes = []
    }

    Path(Path p) {
        nodes = Lists.newArrayList(p.nodes)
    }

    Path leftShift(Node n) {
        if (!n)
            throw new IllegalArgumentException()

        nodes << n
        this
    }

    boolean contains(Node n) {
        if (!n)
            return false
        nodes.contains(n)
    }

    boolean containsEdge(Node x, Node y) {
        if (!x || !y)
            throw new IllegalArgumentException()
        if (length() <= 1)
            return false

        if (nodes.contains(x) && nodes.contains(y)) {
            if (nodes.indexOf(y) == nodes.indexOf(x) + 1)
                return true
        }
        false
    }

    int length() {
        if (!nodes)
            return 0
        nodes.size() - 1
    }

    Node head() {
        if (!nodes)
            return null
        nodes.first()
    }

    Node tail() {
        if (!nodes)
            return null
        nodes.last()
    }
}
