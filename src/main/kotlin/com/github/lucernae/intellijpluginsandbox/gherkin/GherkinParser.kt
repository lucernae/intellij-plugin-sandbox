package com.github.lucernae.intellijpluginsandbox.gherkin

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

/**
 * A simple parser for Gherkin files that extracts Scenario information.
 */
class GherkinParser {
    companion object {
        val SCENARIO_REGEX = Regex("\\s*Scenario:?\\s+(.+)\\s*")
        val SCENARIO_OUTLINE_REGEX = Regex("\\s*Scenario Outline:?\\s+(.+)\\s*")

        /**
         * Parses a Gherkin file and returns a list of Scenario objects.
         */
        fun parseFile(project: Project, file: VirtualFile): List<Scenario> {
            val psiFile = PsiManager.getInstance(project).findFile(file) ?: return emptyList()
            val text = psiFile.text
            val lines = text.lines()
            val scenarios = mutableListOf<Scenario>()

            for ((lineNumber, line) in lines.withIndex()) {
                val scenarioMatch = SCENARIO_REGEX.matchEntire(line)
                val scenarioOutlineMatch = SCENARIO_OUTLINE_REGEX.matchEntire(line)

                if (scenarioMatch != null) {
                    val title = scenarioMatch.groupValues[1]
                    scenarios.add(Scenario(title, lineNumber, false))
                } else if (scenarioOutlineMatch != null) {
                    val title = scenarioOutlineMatch.groupValues[1]
                    scenarios.add(Scenario(title, lineNumber, true))
                }
            }

            return scenarios
        }
    }

    /**
     * Represents a Scenario in a Gherkin file.
     */
    data class Scenario(
        val title: String,
        val lineNumber: Int,
        val isOutline: Boolean
    )
}
