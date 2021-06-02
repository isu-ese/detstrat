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

import edu.isu.isuese.datamodel.*

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Singleton
class GraphElementFactory {

    Node createNode(Component comp) {
        if (!comp)
            throw new IllegalArgumentException()

        NodeType type = null
        switch (comp) {
            case Type:
                type = NodeType.Class
                break
            case Literal:
            case Field:
                type = NodeType.Attribute
                break
            case Initializer:
            case Constructor:
            case Destructor:
            case Method:
                type = NodeType.Method
                break
        }

        return new Node(name: comp.name, type: type)
    }

    Node createNode(Namespace ns) {
        if (!ns)
            throw new IllegalArgumentException()

        return new Node(name: ns.name, type: NodeType.Package)
    }

    Relationship createRelationship(RelationshipType type) {
        if (!type)
            throw new IllegalArgumentException()

        return new Relationship(type: type)
    }

    NamespaceRelation createNamespaceRelation(List<Relationship> contained = []) {
        return new NamespaceRelation(type: RelationshipType.PackageDependency, contained: contained)
    }
}
