package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.actions.PopUpHooverAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeActionConcat
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeActionParallel
import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.github.sloppylopez.moneypennyideaplugin.intentions.RefactorIntentionFactory
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFrame
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import javax.swing.SwingUtilities

class ToolWindowFactory : ToolWindowFactory, ApplicationActivationListener {
    override fun applicationActivated(ideFrame: IdeFrame) {
        thisLogger().info("App activated")
    }

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            val sendToPromptFileFolderTreeActionParallel = SendToPromptFileFolderTreeActionParallel(project)
            sendToPromptFileFolderTreeActionParallel.registerFolderTreeAction()
            val sendToPromptFileFolderTreeActionConcat = SendToPromptFileFolderTreeActionConcat(project)
            sendToPromptFileFolderTreeActionConcat.registerFolderTreeAction()
            val popUpHooverAction = PopUpHooverAction()
            popUpHooverAction.addActionsToEditor()
            val refactorIntentionFactory = project.service<RefactorIntentionFactory>()
            SwingUtilities.invokeLater {
                ApplicationManager.getApplication().invokeLater(
                    {
                        refactorIntentionFactory.removeIntentionsFromEditor()
                        refactorIntentionFactory.addIntentionToEditor()
                    },
                    ModalityState.any()
                )
            }
            addTabbedPaneToToolWindow(project)
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }

    override fun shouldBeAvailable(project: Project) = true
}