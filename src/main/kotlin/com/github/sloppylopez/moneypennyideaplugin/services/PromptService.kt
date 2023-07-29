package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBTabbedPane
import java.awt.Component
import java.awt.Container
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class PromptService(project: Project) {
    private val service = project.service<ProjectService>()
    private val gitService = project.service<GitService>()
    fun getPrompts(): MutableMap<String, Map<String, List<String>>> {
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
                    for (tabIndex in 0 until tabbedPane.tabCount) {
                        val tabComponents =
                            (tabbedPane.getComponentAt(tabIndex) as Container).components[1] as Container
                        tabComponents.components.forEach { tabComponent ->
                            getPromptInfo(tabComponent, textAreas, tabbedPane, tabIndex)
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

    fun setInChat(text: String, tabName: String, currentRole: String): MutableMap<String, JList<String>> {
        val contentManager = service.getToolWindow()?.contentManager
        val contentCount = contentManager?.contentCount
        for (i in 0 until contentCount!!) {
            val content = contentManager.getContent(i)
            val simpleToolWindowPanel = content?.component as? SimpleToolWindowPanel
            if (simpleToolWindowPanel != null) {
                simpleToolWindowPanel.components.forEach { component ->
                    service.addChatWindowContentListModelToGlobalData(
                        component as Container,
                        text,
                        currentRole,
                        tabName
                    )
                }
//                GlobalData.tabNameToChatWindowContent[tabName]?.addElement("$currentRole:\n$text")
                return GlobalData.tabNameToChatWindowContent//TODO we should not return this, it does not make sense
            }
        }
        return emptyMap<String, JList<String>>().toMutableMap()
    }

    private fun getPromptInfo(
        tabComponent: Component?,
        textAreas: MutableList<String>,
        tabbedPane: JBTabbedPane,
        tabIndex: Int
    ) {
        if (tabComponent is JScrollPane) {
            val textArea = tabComponent.viewport.view as? JTextArea
            textArea?.let {
                textAreas.add(it.text)
                val tabName = tabbedPane.getTitleAt(tabIndex)
                extractPromptInfo(tabName, textAreas, tabIndex, it.text)
            }
        }
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

//    fun setAllCheckboxesNotSelected(toolwindow: ToolWindow) {
//        toolwindow.content.forEach { content ->
//            if (content is Checkbox) {
//                content.isSelected = false
//            }
//        }
//    }
}