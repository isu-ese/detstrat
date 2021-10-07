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

import spock.lang.Specification

class PathTest extends Specification {

    Path path

    void setup() {
        path = new Path()
    }

    void cleanup() {
    }

    def "LeftShift happy path"() {
        given:
        Node x = new Node(type: NodeType.Attribute)
        Node y = new Node(type: NodeType.Attribute)

        when:
        path << x
        path << y

        then:
        path.length() == 1
    }

    def "LeftShift null"() {
        given:
        Node x = null

        when:
        path << x

        then:
        thrown IllegalArgumentException
    }

    def "Contains happy path"() {
        given:
        Node x = new Node(name: "test", type: NodeType.Attribute)

        path << x

        when:
        boolean result = path.contains(x)

        then:
        result
    }

    def "Contains null"() {
        given:
        Node x = new Node(name: "test", type: NodeType.Attribute)

        path << x

        when:
        boolean result = path.contains(null)

        then:
        !result
    }

    def "Contains when empty"() {
        given:
        Node x = new Node(name: "test", type: NodeType.Attribute)

        when:
        boolean result = path.contains(x)

        then:
        !result
    }

    def "ContainsEdge"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)
        Node y = new Node(name: "y", type: NodeType.Attribute)
        Node z = new Node(name: "y", type: NodeType.Attribute)

        path << x
        path << y
        path << z

        when:
        boolean result1 = path.containsEdge(x, y)
        boolean result2 = path.containsEdge(y, z)
        boolean result3 = path.containsEdge(x, z)

        then:
        result1
        result2
        !result3
    }

    def "containsEdge empty"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)
        Node y = new Node(name: "y", type: NodeType.Attribute)

        when:
        boolean result = path.containsEdge(x, y)

        then:
        !result
    }

    def "containsEdge null ok"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)

        when:
        path.containsEdge(null, x)

        then:
        thrown IllegalArgumentException
    }

    def "containsEdge ok null"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)

        when:
        path.containsEdge(x, null)

        then:
        thrown IllegalArgumentException
    }

    def "containsEdge null null"() {
        given:
        path

        when:
        path.containsEdge(null, null)

        then:
        thrown IllegalArgumentException
    }

    def "Length"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)
        Node y = new Node(name: "y", type: NodeType.Attribute)
        Node z = new Node(name: "y", type: NodeType.Attribute)

        path << x
        path << y
        path << z

        when:
        int result = path.length()

        then:
        result == 2
    }

    def "Length when empty"() {
        given:
        path

        when:
        int result = path.length()

        then:
        result == 0
    }

    def "Length when single node"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)

        path << x

        when:
        int result = path.length()

        then:
        result == 0
    }

    def "Head"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)

        path << x

        when:
        def result = path.head()

        then:
        result == x
    }

    def "Head when empty"() {
        given:
        path

        when:
        def result = path.head()

        then:
        result == null
    }

    def "Tail"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)
        Node y = new Node(name: "y", type: NodeType.Attribute)

        path << x
        path << y

        when:
        def result = path.tail()

        then:
        result == y
    }

    def "Tail when empty"() {
        given:
        path

        when:
        def result = path.tail()

        then:
        result == null
    }

    def "Tail with single node"() {
        given:
        Node x = new Node(name: "x", type: NodeType.Attribute)

        path << x

        when:
        def result = path.tail()

        then:
        result == x
    }
}
