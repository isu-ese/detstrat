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

import edu.isu.isuese.datamodel.*
import junitparams.Parameters
import org.javalite.activejdbc.test.DBSpec
import org.junit.After
import org.junit.Before
import org.junit.Test

class GraphElementFactoryTest extends DBSpec {

    GraphElementFactory fixture

    @Before
    void setUp() throws Exception {
        fixture = GraphElementFactory.instance
    }

    @After
    void tearDown() throws Exception {
    }

    @Test
    @Parameters(method = "params")
    void "test createNode with component happy paths"() {
        // given
        def params = [
                [Type.builder().type(Type.CLASS).name("test").create(), NodeType.Class],
                [Type.builder().type(Type.INTERFACE).name("test").create(), NodeType.Class],
                [Type.builder().type(Type.ENUM).name("test").create(), NodeType.Class],
                [Initializer.builder().name("test").create(), NodeType.Method],
                [Literal.builder().name("test").create(), NodeType.Attribute],
                [Field.builder().name("test").create(), NodeType.Attribute],
                [Constructor.creator().name("test").create(), NodeType.Method],
                [Destructor.creator().name("test").create(), NodeType.Method],
                [Method.builder().name("test").create(), NodeType.Method]
        ]

        // when
        params.each {
            Node result = fixture.createNode((Component) it[0])

            // then
            the(result).shouldNotBeNull()
            the(result.name).shouldBeEqual(((Component) it[0]).getName())
            the(result.type).shouldBeEqual(it[1])
        }
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createNode for null component"() {
        // given
        Component comp = null

        // when
        fixture.createNode((Component) comp)
    }

    @Test
    void "test createNode for namespace happy path"() {
        // given
        Namespace ns = Namespace.builder().name("test").create()

        // when
        Node result = fixture.createNode(ns)

        // then
        the(result).shouldNotBeNull()
        the(result.name).shouldBeEqual(ns.getName())
        the(result.type).shouldBeEqual(NodeType.Package)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createNode for namespace null namespace"() {
        // given
        Namespace ns = null

        // when
        fixture.createNode((Namespace) ns)
    }

    @Test
    void "test createRelationship happy path"() {
        // given
        RelationshipType type = RelationshipType.Aggregation

        // when
        Relationship result = fixture.createRelationship(type)

        // then
        the(result).shouldNotBeNull()
        the(result.type).shouldBeEqual(type)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createRelationship null RelationshipType"() {
        // given
        RelationshipType type = null

        // when
        fixture.createRelationship(type)
    }
}