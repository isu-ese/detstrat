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
 * Grime Severity is measured using the following mapping:
 *
 * GS(p) = {
 *     GP(p) = 0%     -> 0 -- VERY LOW
 *     GP(p) <= 7.5%  -> 1 -- LOW
 *     GP(p) <= 15%   -> 3 -- MODERATE
 *     GP(p) <= 22.5% -> 4 -- HIGH
 *     GP(p) <= 30%   -> 5 -- VERY HIGH
 * }
 *
 * Where GP(p) is the percentage of grime affecting a pattern instance.
 * GP(p) is calculated as the ratio of pattern instance members bound
 * to a role defined by the associated pattern RBML and affected by
 * grime to the total number of pattern instance members bound to a role.
 *
 * @author Isaac Griffith
 * @version 1.3.0
 */
@MetricDefinition(
        name = "Grime Severity",
        primaryHandle = "GS",
        description = "An index of the severity of the grime affecting a design pattern instance",
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
class GrimeSeverityEvaluator extends MetricEvaluator {

    void detect(PatternInstance instance) {

    }

    @Override
    def measureValue(Measurable node) {
        int severity = 0

        if (node instanceof PatternInstance) {
            PatternInstance instance = (PatternInstance) node
            double boundItems = instance.getRoleBindings().size()

            instance.getRoleBindings().each {
                // find grime referencing the bound item.
            }

            RuleRepository repo = RuleRepository.findFirst("repoKey = ?", GrimeDetectorConstants.RULE_REPO_KEY)
            Project proj = instance.getParentProject()
            Set<String> refKeys = Sets.newHashSet()
            double count = 0

            instance.getRoleBindings().each {
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

            double percentage = count / boundItems * 100

            if (Double.compare(percentage, 0.0d) >= 0)
                severity = 0
            else if (Double.compare(percentage, 7.5d) <= 0)
                severity = 1
            else if (Double.compare(percentage, 15.0d) <= 0)
                severity = 2
            else if (Double.compare(percentage, 22.5d) <= 0)
                severity = 3
            else if (Double.compare(percentage, 25.0d) <= 0)
                severity = 4
            else
                severity = 5

            Measure.of("${GrimeDetectorConstants.METRIC_REPO_KEY}:GS").on(instance).withValue(severity)
        }

        severity
    }
}
