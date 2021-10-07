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

import edu.isu.isuese.datamodel.Metric
import edu.isu.isuese.datamodel.MetricRepository
import edu.isu.isuese.datamodel.Rule
import edu.montana.gsoc.msusel.metrics.MetricEvaluator
import edu.montana.gsoc.msusel.metrics.annotations.MetricDefinition

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class MetricProvider {

    Map<String, Metric> metricMap = [:]
    MetricRepository repo

    void rules() {
        if (!repo)
            throw new IllegalArgumentException("repository must be initialized before creating metrics")

        // Modular Grime Rules
        createMetric()
        createMetric("${GrimeDetectorConstants.RULE_REPO_KEY}:", "TIG - Temporary Internal Grime", "GSZ")

    }

    private void createMetric(Class<? extends MetricEvaluator> evaluator) {
        MetricDefinition mdef = evaluator.getAnnotation(MetricDefinition.class)
        String description = mdef.description()
        String name = mdef.name()
        String handle = mdef.primaryHandle()
        String key = "${GrimeDetectorConstants.METRIC_REPO_KEY}:$handle"

        def metric = Metric.findFirst("metricKey = ?", key)
        if (!metric) {
            metric = Metric.builder()
                    .key(key)
                    .name(name)
                    .description(description)
                    .evaluator(evaluator.name)
                    .create()
            repo.addMetric(metric)
            metric.refresh()
            metricMap[handle] = metric
        }
    }

    void repository() {
        MetricRepository found = MetricRepository.findFirst("repoKey = ? AND name = ?",
                GrimeDetectorConstants.METRIC_REPO_KEY, GrimeDetectorConstants.METRIC_REPO_NAME)
        if (!found)
            repo = MetricRepository.builder()
                    .key(GrimeDetectorConstants.METRIC_REPO_KEY)
                    .name(GrimeDetectorConstants.METRIC_REPO_NAME)
                    .create()
        else {
            repo = found
        }
    }

    Rule getMetric(String handle) {
        if (!handle)
            throw new IllegalArgumentException()

        if (metricMap[handle])
            return metricMap[handle]
        else
            null
    }

    protected reset() {
        repo = null
        metricMap = [:]
    }
}
