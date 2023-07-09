package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow

class ToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            addTabbedPaneToToolWindow(project, toolWindow)
        } catch (e: Exception) {
            Logger.getInstance("ToolWindowFactory").error(e.stackTraceToString())
        }
    }

    override fun shouldBeAvailable(project: Project) = true


}
