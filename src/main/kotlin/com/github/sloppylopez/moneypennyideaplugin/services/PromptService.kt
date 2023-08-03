package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.tabNameToInnerPanel
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBTabbedPane
import java.awt.Component
import java.awt.Container
import javax.swing.JScrollPane
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class PromptService(project: Project) {
    private val service = project.service<ProjectService>()
    fun getPrompts(): MutableMap<String, Map<String, List<String>>> {
        GlobalData.prompts.clear()
        val contentManager = service.getToolWindow()?.contentManager
        val contentCount = contentManager?.contentCount
        val textAreas = mutableListOf<String>()
        for (upperTabIndex in 0 until contentCount!!) {
            val content = contentManager.getContent(upperTabIndex)
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
                for (tabbedPaneIndex in 0 until nestedJBTabbedPanes.size) {
                    val tabbedPane = nestedJBTabbedPanes[tabbedPaneIndex]
                    for (tabIndex in 0 until tabbedPane.tabCount) {
                        val tabComponents = (tabbedPane.getComponentAt(tabIndex) as Container)
                            .components[0] as Container
                        val tabName = tabbedPane.getTitleAt(tabIndex)
                        val upperTabName =//TODO refactor this, maybe it an be recursive
                            (tabNameToInnerPanel[tabName]?.parent?.parent?.parent?.parent?.parent as JBTabbedPane).getTitleAt(
                                tabbedPaneIndex
                            )
                        tabComponents.components.forEach { tabComponent ->
                            getPromptInfo(tabComponent, textAreas, tabIndex, tabName, upperTabName)
                        }
                    }
                }
                val promptsAsJson = service.getPromptsAsJson(GlobalData.prompts)
                service.saveDataToExtensionFolder(promptsAsJson)
                return GlobalData.prompts
            }
        }
        return emptyMap<String, Map<String, List<String>>>().toMutableMap()
    }

    fun setInChat(text: String, tabName: String, currentRole: String, upperTabName: String?, promptList: List<String>?) {
        val contentManager = service.getToolWindow()?.contentManager
        val contentCount = contentManager?.contentCount
        for (index in 0 until contentCount!!) {
            val content = contentManager.getContent(index)
            val simpleToolWindowPanel = content?.component as? SimpleToolWindowPanel
            simpleToolWindowPanel?.components?.forEach { component ->
//                component.getComponentAt(0, 0)
                service.addChatWindowContentListModelToGlobalData(
                    component as Container,
                    text,
                    currentRole,
                    tabName,
                    upperTabName,
                    promptList
                )
            }
        }
    }

    private fun getPromptInfo(
        tabComponent: Component?,
        textAreas: MutableList<String>,
        tabIndex: Int,
        tabName: String,
        upperTabName: String
    ) {
        if (tabComponent is JScrollPane) {
            val textArea = tabComponent.viewport.view as? JTextArea
            textArea?.let {
                textAreas.add(it.text)
                extractPromptInfo(tabName, textAreas, tabIndex, it.text, upperTabName)
            }
        }
    }

    private fun extractPromptInfo(
        tabName: String,
        textAreas: MutableList<String>,
        index: Int,
        text: String,
        upperTabName: String
    ) {
        try {
//            val filePaths = GlobalData.tabNameToFilePathMap[tabName]
//            val shortSha = gitService.getShortSha(filePaths) ?: index.toString() TBI
            val promptMap = GlobalData.prompts.getOrDefault(upperTabName, mutableMapOf())
            val promptList = promptMap.getOrDefault(tabName, listOf())
            GlobalData.prompts[upperTabName] = promptMap + (tabName to promptList.plus(text))
            textAreas.add(text)
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }

//    fun setAllCheckboxesNotSelected(toolwindow: ToolWindow) {
//        toolwindow.content.forEach { content ->
//            if (content is Checkbox) {
//                content.isSelected = false
//            }
//        }
//    }
}