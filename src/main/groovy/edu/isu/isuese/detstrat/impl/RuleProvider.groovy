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

import edu.isu.isuese.datamodel.Priority
import edu.isu.isuese.datamodel.Rule
import edu.isu.isuese.datamodel.RuleRepository

/**
 * This class is designed to provide the Issue Definitions and Issue Repository to the tool (if they are not yet defined)
 *
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Singleton
class RuleProvider {

    Map<String, Rule> ruleMap = [:]
    RuleRepository repo

    void rules() {
        if (!repo)
            throw new IllegalArgumentException("repository must be initialized before creating rules")

        // Modular Grime Rules
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:modular-grime:pig", "PIG - Persistent Internal Grime", "PIG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:modular-grime:tig", "TIG - Temporary Internal Grime", "TIG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:modular-grime:peeg", "PEEG - Persistent External Efferent Grime", "PEEG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:modular-grime:peag", "PEAG - Persistent External Afferent Grime", "PEAG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:modular-grime:teeg", "TEEG - Temporary External Efferent Grime", "TEEG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:modular-grime:teag", "TEAG - Temporary External Afferent Grime", "TEAG")

        // Class Grime Rules
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:depg", "DEPG - Direct External Pair Grime", "DEPG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:desg", "DESG - Direct External Single Grime", "DESG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:dipg", "DIPG - Direct Internal Pair Grime", "DIPG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:disg", "DISG - Direct Internal Single Grime", "DISG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:iepg", "IEPG - Indirect External Pair Grime", "IEPG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:iesg", "IESG - Indirect External Single Grime", "IESG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:iipg", "IIPG - Indirect Internal Pair Grime", "IIPG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:class-grime:iisg", "IISG - Indirect Internal Single Grime", "IISG")

        // Organizational Grime Rules
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:picg", "PICG - Package Internal Closure Grime", "PICG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:pirg", "PIRG - Package Internal Reuse Grime", "PIRG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:pecg", "PECG - Package External Closure Grime", "PECG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:perg", "PERG - Package External Reuse Grime", "PERG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mpicg", "MPICG - Modular Persistent Internal Cyclical Grime", "MPICG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mpiug", "MPIUG - Modular Persistent Internal Unstable Grime", "MPIUG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mpecg", "MPECG - Modular Persistent External Cyclical Grime", "MPECG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mpeug", "MPEUG - Modular Persistent External Unstable Grime", "MPEUG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mticg", "MTICG - Modular Temporary Internal Cyclical Grime", "MTICG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mtiug", "MTIUG - Modular Temporary Internal Unstable Grime", "MTIUG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mtecg", "MTECG - Modular Temporary External Cyclical Grime", "MTECG")
        createRule("${GrimeDetectorConstants.RULE_REPO_KEY}:org-grime:mteug", "MTEUG - Modular Temporary External Unstable Grime", "MTEUG")
    }

    private void createRule(String key, String name, String shortName, String desc = "") {
        def rule = Rule.findFirst("ruleKey = ?", key)
        if (!rule) {
            rule = Rule.builder()
                    .key(key)
                    .name(name)
                    .priority(Priority.MODERATE)
                    .description(desc)
                    .create()
            repo.addRule(rule)
            rule.refresh()
            ruleMap[shortName] = rule
        }
    }

    void repository() {
        RuleRepository found = RuleRepository.findFirst("repoKey = ? AND name = ?", GrimeDetectorConstants.RULE_REPO_KEY, GrimeDetectorConstants.RULE_REPO_NAME)
        if (!found)
            repo = RuleRepository.builder()
                    .key(GrimeDetectorConstants.RULE_REPO_KEY)
                    .name(GrimeDetectorConstants.RULE_REPO_NAME)
                    .create()
        else {
            repo = found
        }
    }

    Rule getRule(String shortName) {
        if (!shortName)
            throw new IllegalArgumentException()

        if (ruleMap[shortName])
            return ruleMap[shortName]
        else
            null
    }

    protected reset() {
        repo = null
        ruleMap = [:]
    }
}
