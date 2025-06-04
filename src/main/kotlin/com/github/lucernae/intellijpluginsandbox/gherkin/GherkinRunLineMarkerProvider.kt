package com.github.lucernae.intellijpluginsandbox.gherkin

import com.github.lucernae.intellijpluginsandbox.gotest.GoTestRunConfiguration
import com.github.lucernae.intellijpluginsandbox.gotest.GoTestRunConfigurationType
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore

/**
 * Provides a Run button next to Scenario lines in Gherkin files.
 */
class GherkinRunLineMarkerProvider : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        // Only process leaf elements (text)
        if (!element.isValid || element.textLength == 0) {
            return null
        }

        val file = element.containingFile
        if (file.fileType != GherkinFileType.INSTANCE) {
            return null
        }

        val lineText = element.text
        val scenarioMatch = GherkinParser.Companion.SCENARIO_REGEX.matchEntire(lineText)
        val scenarioOutlineMatch = GherkinParser.Companion.SCENARIO_OUTLINE_REGEX.matchEntire(lineText)

        if (scenarioMatch != null || scenarioOutlineMatch != null) {
            val title = (scenarioMatch?.groupValues?.get(1) ?: scenarioOutlineMatch?.groupValues?.get(1))?.trim() ?: return null

            return Info(
                AllIcons.RunConfigurations.TestState.Run,
                arrayOf(RunScenarioAction(title, file)),
                { "Run Go test for scenario: $title" }
            )
        }

        return null
    }

    /**
     * Action to run a Go test for a specific Scenario.
     */
    private class RunScenarioAction(
        private val scenarioTitle: String,
        private val file: PsiFile
    ) : AnAction("Run Go test for scenario: $scenarioTitle") {
        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            val virtualFile = PsiUtilCore.getVirtualFile(file) ?: return

            // Create and run a Go test configuration for this scenario
            val runManager = com.intellij.execution.RunManager.getInstance(project)
            val configurationType = GoTestRunConfigurationType.getInstance()

            val factory = configurationType.configurationFactories[0]
            val runConfiguration = runManager.createConfiguration(
                "Go test: $scenarioTitle",
                factory
            )

            val configuration = runConfiguration.configuration as GoTestRunConfiguration
            configuration.scenarioTitle = scenarioTitle
            configuration.featureFile = virtualFile.path

            runManager.addConfiguration(runConfiguration)
            runManager.selectedConfiguration = runConfiguration

            val executor = com.intellij.execution.executors.DefaultRunExecutor.getRunExecutorInstance()
            val environment = com.intellij.execution.runners.ExecutionEnvironmentBuilder.create(executor, configuration)
                .build()

            environment.runner.execute(environment)
        }
    }
}
