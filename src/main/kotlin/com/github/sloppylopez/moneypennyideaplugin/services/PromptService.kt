package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
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
    private val logger = thisLogger()

    fun getPrompts(): MutableMap<String, Map<String, List<String>>> {
        GlobalData.prompts.clear()

        val contentManager = service.getToolWindow()?.contentManager ?: run {
            logger.warn("Tool window content manager not found")
            return mutableMapOf()
        }

        try {
            for (upperTabIndex in 0 until contentManager.contentCount) {
                val content = contentManager.getContent(upperTabIndex) ?: continue
                val simpleToolWindowPanel = content.component as? SimpleToolWindowPanel ?: continue

                val jBTabbedPanes = findJBTabbedPanes(simpleToolWindowPanel)
                val nestedJBTabbedPanes = jBTabbedPanes.flatMap { findNestedJBTabbedPanes(it) }

                for ((tabbedPaneIndex, tabbedPane) in nestedJBTabbedPanes.withIndex()) {
                    for (tabIndex in 0 until tabbedPane.tabCount) {
                        val tabComponents =
                            (tabbedPane.getComponentAt(tabIndex) as? Container)?.components[1] as? Container//TODO this code is flaky
                        val tabName = tabbedPane.getTitleAt(tabIndex)

                        val upperTabName = GlobalData.tabNameToUpperTabNameMap[tabName] ?: ""

                        logger.info("Processing tabIndex=$tabIndex with tabName='$tabName' and upperTabName='$upperTabName'")
                        tabComponents?.components?.forEach { tabComponent ->
                            logger.debug("Extracting prompt info from tabComponent: ${tabComponent.javaClass.name}")
                            if (tabComponent is JScrollPane) {
                                getPromptInfo(tabComponent, tabIndex, tabName, upperTabName)
                            }
                        }
                    }
                }
            }

            val promptsAsJson = service.getPromptsAsJson(GlobalData.prompts)
            service.saveDataToExtensionFolder(promptsAsJson)
            return GlobalData.prompts
        } catch (e: Exception) {
            logger.error("Error in getPrompts: ${e.message}")
        }

        return mutableMapOf()
    }

    fun setInChat(
        text: String,
        tabName: String,
        currentRole: String,
        upperTabName: String?,
        promptList: List<String>?
    ) {
        val contentManager = service.getToolWindow()?.contentManager ?: return

        for (index in 0 until contentManager.contentCount) {
            val content = contentManager.getContent(index) ?: continue
            val simpleToolWindowPanel = content.component as? SimpleToolWindowPanel ?: continue

            simpleToolWindowPanel.components.forEach { component ->
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
        tabComponent: JScrollPane?, tabIndex: Int,
        tabName: String, upperTabName: String
    ) {
        (tabComponent?.viewport?.view as? JTextArea)?.let {
            runCatching {
                extractPromptInfo(tabName, it.text, upperTabName)
            }.onFailure {
                logger.error("Error in getPromptInfo: ${it.message}")
            }
        }
    }

    private fun extractPromptInfo(
        tabName: String,
        text: String,
        upperTabName: String
    ) {
        val promptMap = GlobalData.prompts.getOrPut(upperTabName) { mutableMapOf() }
        val promptList = promptMap.getOrDefault(tabName, listOf())
        GlobalData.prompts[upperTabName] = promptMap + (tabName to promptList.plus(text))
    }

    private fun findJBTabbedPanes(container: Container): List<JBTabbedPane> {
        val tabbedPanes = mutableListOf<JBTabbedPane>()
        container.components.forEach { component ->
            if (component is JBTabbedPane) {
                tabbedPanes.add(component)
            } else if (component is Container) {
                tabbedPanes.addAll(findJBTabbedPanes(component))
            }
        }
        return tabbedPanes
    }

    private fun findNestedJBTabbedPanes(tabbedPane: JBTabbedPane): List<JBTabbedPane> {
        val nestedPanes = mutableListOf<JBTabbedPane>()
        for (i in 0 until tabbedPane.tabCount) {
            val component = tabbedPane.getComponentAt(i)
            if (component is JBTabbedPane) {
                nestedPanes.add(component)
            } else if (component is Container) {
                nestedPanes.addAll(findJBTabbedPanes(component))
            }
        }
        return nestedPanes
    }
}
