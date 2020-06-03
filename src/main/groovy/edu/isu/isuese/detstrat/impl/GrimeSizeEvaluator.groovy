package edu.isu.isuese.detstrat.impl

import com.google.common.collect.Sets
import edu.isu.isuese.datamodel.Measurable
import edu.isu.isuese.datamodel.Measure
import edu.isu.isuese.datamodel.PatternInstance
import edu.isu.isuese.datamodel.Project
import edu.isu.isuese.datamodel.RuleRepository
import edu.montana.gsoc.msusel.metrics.MetricEvaluator
import edu.montana.gsoc.msusel.metrics.annotations.MetricCategory
import edu.montana.gsoc.msusel.metrics.annotations.MetricDefinition
import edu.montana.gsoc.msusel.metrics.annotations.MetricProperties
import edu.montana.gsoc.msusel.metrics.annotations.MetricScale
import edu.montana.gsoc.msusel.metrics.annotations.MetricScope
import edu.montana.gsoc.msusel.metrics.annotations.MetricType

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
    def measure(Measurable measurable) {
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
