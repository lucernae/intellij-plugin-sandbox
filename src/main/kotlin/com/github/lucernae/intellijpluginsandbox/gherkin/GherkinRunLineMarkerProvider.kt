package com.github.lucernae.intellijpluginsandbox.gherkin

import com.goide.execution.testing.GoTestRunConfiguration
import com.intellij.execution.RunManager
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import com.goide.execution.testing.GoTestRunConfigurationType
import com.goide.execution.testing.frameworks.gotest.GotestFramework
import org.jetbrains.plugins.cucumber.psi.GherkinFileType
import com.intellij.openapi.diagnostic.logger
import com.intellij.patterns.PsiElementPattern
import org.jetbrains.plugins.cucumber.psi.GherkinElementTypes
import org.jetbrains.plugins.cucumber.psi.GherkinFeature
import org.jetbrains.plugins.cucumber.psi.GherkinScenario
import org.jetbrains.plugins.cucumber.psi.GherkinTokenTypes
import java.lang.annotation.ElementType

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
        when {
            file.fileType != GherkinFileType.INSTANCE -> return null
            GherkinElementTypes.FEATURE == element.node.elementType -> {
                val gherkinFeature = element.node.psi as GherkinFeature
                val featureName = gherkinFeature.featureName.trim()
                return Info(
                    AllIcons.RunConfigurations.TestState.Run_run,
                    arrayOf(RunScenarioAction(featureName, "", file)),
                    { "Run all scenarios in feature: $featureName" }
                )
            }
            GherkinElementTypes.SCENARIO == element.node.elementType -> {
                val gherkinFeature = element.parent.node.psi as GherkinFeature
                val featureName = gherkinFeature.featureName.trim()
                val gherkinScenario = element.node.psi as GherkinScenario
                val scenarioTitle = gherkinScenario.scenarioName.trim()
                return Info(
                    AllIcons.RunConfigurations.TestState.Run_run,
                    arrayOf(RunScenarioAction(featureName, scenarioTitle, file)),
                    { "Run scenario: $scenarioTitle in feature: $featureName" }
                )
            }
        }

        return null
    }

    /**
     * Action to run a Go test for a specific Scenario.
     */
    private class RunScenarioAction(
        private val featureName: String,
        private val scenarioTitle: String,
        private val file: PsiFile
    ): AnAction("Run Go test for scenario: /$featureName/$scenarioTitle") {

        fun getSanitizedGoTestName(name: String): String {
            return name
                .replace(Regex("[^a-zA-Z0-9]"), "_")
                .replace(Regex("_+"), "_")
                .trim('_')
        }

        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            val virtualFile = PsiUtilCore.getVirtualFile(file) ?: return

            val configurationType = GoTestRunConfigurationType.getInstance()
            val runManager = RunManager.getInstance(project)
            val templateSettings = runManager.allSettings.find { settings ->
                settings.type.id == configurationType.id
                        && settings.isTemplate
                        && settings.name.startsWith("Godog tests template")
            } ?: runManager.createConfiguration(
                "Godog tests template",
                configurationType.configurationFactories[0]
            ).also { configurationSettings ->
                runManager.addConfiguration(configurationSettings)
            }

            val testPattern = "/${getSanitizedGoTestName(featureName)}/${getSanitizedGoTestName(scenarioTitle)}"
            val configurationSettings = runManager.createConfiguration(
                "Godog tests: $testPattern",
                templateSettings.factory
            )
            configurationSettings.isActivateToolWindowBeforeRun = true
            (configurationSettings.configuration as GoTestRunConfiguration).apply {
                this.pattern = testPattern
                this.testFramework = GotestFramework.INSTANCE
                this.workingDirectory = virtualFile.parent.parent.path
            }
            runManager.addConfiguration(configurationSettings)
            runManager.selectedConfiguration = configurationSettings
            val executor = com.intellij.execution.executors.DefaultRunExecutor.getRunExecutorInstance()
            val environment = com.intellij.execution.runners.ExecutionEnvironmentBuilder.create(executor, configurationSettings)
                .build()

            environment.runner.execute(environment)
        }
    }
}
