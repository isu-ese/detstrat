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
import edu.montana.gsoc.msusel.rbml.PatternLoader
import edu.montana.gsoc.msusel.rbml.model.SPS
import groovy.util.logging.Log4j2

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Log4j2
abstract class AbstractGrimeDetector implements GrimeDetector {

    Finding createFinding(String name, Reference ref) {
        if (!name)
            throw new IllegalArgumentException()
        if (!ref)
            throw new IllegalArgumentException()

        println "Finding of $name on ${ref.getRefKey()}"
        if (RuleProvider.instance.getRule(name))
            Finding.of(RuleProvider.instance.getRule(name).getKey()).on(ref)
        else
            null
    }

    Finding createFinding(String name, PatternInstance inst) {
        if (!name)
            throw new IllegalArgumentException()
        if (!inst)
            throw new IllegalArgumentException()

        log.info "Finding of $name on ${inst.getInstKey()}"
        if (RuleProvider.instance.getRule(name))
            Finding.of(RuleProvider.instance.getRule(name).getKey()).on(inst)
        else
            null
    }

    Finding createFinding(String name, Namespace ns) {
        if (!name)
            throw new IllegalArgumentException()
        if (!ns)
            throw new IllegalArgumentException()

        log.info "Finding of $name on ${ns.getNsKey()}"
        if (RuleProvider.instance.getRule(name))
            Finding.of(RuleProvider.instance.getRule(name).getKey()).on(ns)
        else
            null
    }

    Finding createFinding(String name, Component comp) {
        if (!name)
            throw new IllegalArgumentException()
        if (!comp)
            throw new IllegalArgumentException()

        log.info "Finding of $name on ${comp.getCompKey()}"
        if (RuleProvider.instance.getRule(name))
            Finding.of(RuleProvider.instance.getRule(name).getKey()).on(comp)
        else
            null
    }

    SPS loadRBML(PatternInstance inst) {
        if (!inst)
            throw new IllegalArgumentException()

        PatternLoader.instance.loadPattern(inst.getParentPattern().getName().toLowerCase())
    }
}
