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


import edu.isu.isuese.datamodel.Finding
import edu.isu.isuese.datamodel.Pattern
import edu.isu.isuese.datamodel.PatternInstance
import edu.isu.isuese.datamodel.Reference
import edu.montana.gsoc.msusel.rbml.model.SPS
import org.javalite.activejdbc.test.DBSpec
import org.junit.After
import org.junit.Before
import org.junit.Test

class AbstractGrimeDetectorTest extends DBSpec {

    AbstractGrimeDetector fixture

    @Before
    void setUp() throws Exception {
        RuleProvider.instance.repository()
        RuleProvider.instance.rules()
        fixture = new ModularGrimeDetector()
    }

    @After
    void tearDown() throws Exception {
        RuleProvider.instance.reset()
    }

    @Test
    void "test createFinding happy path"() {
        // given
        String name = "TIG"
        Reference ref = Type.builder().type(Type.CLASS).name("test").compKey("test").create().createReference()

        // when
        Finding result = fixture.createFinding(name, ref)

        // then
        the(result).shouldNotBeNull()
        the(result.getFindingKey()).shouldBeEqual("isuese:grime:modular-grime:tig")
    }

    @Test
    void "test createFinding unknown grime name"() {
        // given
        String name = "Other"
        Reference ref = Type.builder().type(Type.CLASS).name("test").compKey("test").create().createReference()

        // when
        Finding result = fixture.createFinding(name, ref)

        // then
        the(result).shouldBeNull()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding null name"() {
        // given
        String name = null
        Reference ref = Type.builder().type(Type.CLASS).name("test").compKey("test").create().createReference()

        // when
        fixture.createFinding(name, ref)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding empty name"() {
        // given
        String name = ""
        Reference ref = Type.builder().type(Type.CLASS).name("test").compKey("test").create().createReference()

        // when
        fixture.createFinding(name, ref)
    }

    @Test(expected = IllegalArgumentException.class)
    void "test createFinding null ref"() {
        // given
        String name = "TIG"
        Reference ref = null

        // when
        fixture.createFinding(name, ref)
    }

    @Test
    void "test loadRBML happy path"() {
        // given
        Pattern p = Pattern.findFirst("name = ?", "Visitor")
        PatternInstance inst = PatternInstance.builder().instKey(p.patternKey).create()
        p.addInstance(inst)

        // when
        SPS result = fixture.loadRBML(inst)

        // then
        the(result).shouldNotBeNull()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test loadRBML null instance"() {
        // given
        PatternInstance inst = null

        // when
        fixture.loadRBML(inst)
    }
}