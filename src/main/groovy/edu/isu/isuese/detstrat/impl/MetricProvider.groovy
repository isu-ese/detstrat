package edu.isu.isuese.detstrat.impl

import edu.isu.isuese.datamodel.Metric
import edu.isu.isuese.datamodel.MetricRepository
import edu.isu.isuese.datamodel.Rule
import edu.montana.gsoc.msusel.metrics.MetricEvaluator
import edu.montana.gsoc.msusel.metrics.annotations.MetricDefinition

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
