package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBTabbedPane
import java.awt.Container
import javax.swing.JScrollPane
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class PromptService(project: Project) {
    private val service = project.service<ProjectService>()
    private val gitService = project.service<GitService>()
    fun getPrompts(): MutableMap<String, Map<String, List<String>>>? {
        GlobalData.prompts.clear()
        val contentManager = service.getToolWindow()?.contentManager
        val contentCount = contentManager?.contentCount
        val textAreas = mutableListOf<String>()

        for (i in 0 until contentCount!!) {
            val content = contentManager.getContent(i)
            val simpleToolWindowPanel = content?.component as? SimpleToolWindowPanel
            if (simpleToolWindowPanel != null) {
                val jBTabbedPanes = mutableListOf<JBTabbedPane>()
                simpleToolWindowPanel.components.forEach { component ->
                    jBTabbedPanes.addAll(service.findJBTabbedPanes(component as Container))
                }

                val nestedJBTabbedPanes = mutableListOf<JBTabbedPane>()
                jBTabbedPanes.forEach { tabbedPane ->
                    nestedJBTabbedPanes.addAll(service.findNestedJBTabbedPanes(tabbedPane))
                }

                for (tabbedPane in nestedJBTabbedPanes) {
                    for (e in 0 until tabbedPane.tabCount) {
                        val tabComponents = (tabbedPane.getComponentAt(e) as Container).components[1] as Container
                        tabComponents.components.forEach { tabComponent ->
                            if (tabComponent is JScrollPane) {
                                val textArea = tabComponent.viewport.view as? JTextArea
                                textArea?.let {
                                    textAreas.add(it.text)
                                    val tabName = tabbedPane.getTitleAt(e)
                                    extractPromptInfo(tabName, textAreas, e, it.text)
                                }
                            }
                        }
                    }
                }
                val promptsAsJson = service.getPromptsAsJson(GlobalData.prompts)
                service.saveDataToExtensionFolder(promptsAsJson)
                return GlobalData.prompts
            }
        }
        return null
    }

    private fun extractPromptInfo(
        tabName: String,
        textAreas: MutableList<String>,
        index: Int,
        text: String
    ) {
        try {
            val shortSha = gitService.getShortSha(GlobalData.tabNameToFilePathMap[tabName]) ?: index.toString()
            val promptMap = GlobalData.prompts.getOrDefault(shortSha, mutableMapOf())
            val promptList = promptMap.getOrDefault(tabName, listOf())
            GlobalData.prompts[shortSha] = promptMap + (tabName to promptList.plus(text))
            textAreas.add(text)
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }
}