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

import com.google.common.collect.Sets
import edu.isu.isuese.datamodel.*
import edu.montana.gsoc.msusel.metrics.MetricEvaluator
import edu.montana.gsoc.msusel.metrics.annotations.*

/**
 * Grime Size is a count of the instances of all types of grime
 * as detected across all components of a pattern instance.
 *
 * @author Isaac Griffith
 * @version 1.3.0
 */
@MetricDefinition(
        name = "Grime Size",
        primaryHandle = "GSZ",
        description = "Grime Size is a count of the instances of all types of grime as detected across all components of a pattern instance.",
        properties = @MetricProperties(
                range = "Positive Integers",
                aggregation = [],
                scope = MetricScope.PATTERN,
                type = MetricType.Model,
                scale = MetricScale.Ordinal,
                category = MetricCategory.Size
        ),
        references = [
                'Griffith, I. "Design Pattern Decay -- A Study of Design Pattern Grime and Its Impact on Quality and Technical Debt." Doctoral Dissertation.'
        ]
)
class GrimeSizeEvaluator extends MetricEvaluator {

    GrimeSizeEvaluator() {
    }

    @Override
    def measureValue(Measurable measurable) {
        int count = 0

        if (measurable instanceof PatternInstance) {
            PatternInstance inst = (PatternInstance) measurable
            RuleRepository repo = RuleRepository.findFirst("repoKey = ?", GrimeDetectorConstants.RULE_REPO_KEY)
            Project proj = inst.getParentProject()

            Set<String> refKeys = Sets.newHashSet()
            inst.getRoleBindings().each {
                refKeys << it.getReference().getRefKey()
            }

            repo.getRules().each { rule ->
                proj.getFindings(rule.getName()).each {
                    it.getReferences().each { ref ->
                        if (refKeys.contains(ref.getRefKey()))
                            count += 1
                    }
                }
            }

            Measure.of("${GrimeDetectorConstants.METRIC_REPO_KEY}:GSZ").on(inst).withValue(count)
        }

        count
    }
}
