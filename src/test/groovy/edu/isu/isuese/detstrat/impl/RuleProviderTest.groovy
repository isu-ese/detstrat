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

import edu.isu.isuese.datamodel.Rule
import org.javalite.activejdbc.test.DBSpec
import org.junit.After
import org.junit.Before
import org.junit.Test

class RuleProviderTest extends DBSpec {

    RuleProvider fixture

    @Before
    void setUp() throws Exception {
        fixture = RuleProvider.instance
    }

    @After
    void tearDown() throws Exception {
        fixture.reset()
    }

    @Test
    void "rules"() {
        // given
        fixture.repository()

        // when
        fixture.rules()

        // then
        the(fixture.ruleMap).shouldNotBeNull()
        the(fixture.ruleMap["PIG"]).shouldNotBeNull()
    }

    @Test(expected = IllegalArgumentException.class)
    void "test rules without initializing repo first"() {
        // given
        fixture

        // when
        fixture.rules()
    }

    @Test
    void "repository"() {
        // given
        fixture

        // when
        fixture.repository()

        // then
        the(fixture.repo).shouldNotBeNull()
    }

    @Test
    void "repository double call"() {
        // given
        fixture.repository()
        def repo = fixture.getRepo()

        // when
        fixture.repository()

        // then
        the(repo).shouldNotBeNull()
    }

    @Test
    void "getRule"() {
        // given
        fixture.repository()
        fixture.rules()
        def name = "PIG"

        // when
        Rule result = fixture.getRule(name)

        // then
        the(result).shouldNotBeNull()
    }

    @Test
    void "getRule unknown name"() {
        // given
        fixture.repository()
        fixture.rules()
        def name = "Other"

        // when
        Rule result = fixture.getRule(name)

        // then
        the(result).shouldBeNull()
    }

    @Test(expected = IllegalArgumentException.class)
    void "getRule empty name"() {
        // given
        fixture.repository()
        fixture.rules()
        def name = ""

        // when
        fixture.getRule(name)
    }

    @Test(expected = IllegalArgumentException.class)
    void "getRule null name"() {
        // given
        fixture.repository()
        fixture.rules()
        def name = null

        // when
        fixture.getRule(name)
    }
}