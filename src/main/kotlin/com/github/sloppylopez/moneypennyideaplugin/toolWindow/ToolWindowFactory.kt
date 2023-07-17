package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptTextEditorAction
import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.github.sloppylopez.moneypennyideaplugin.intentions.RefactorIntentionFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import javax.swing.SwingUtilities

class ToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            val sendToPromptFileFolderTreeAction = SendToPromptFileFolderTreeAction(project)
            sendToPromptFileFolderTreeAction.registerFolderTreeAction()
            val sendToPromptTextEditorAction = SendToPromptTextEditorAction(project)
            sendToPromptTextEditorAction.registerFileEditorAction()
            val refactorIntentionFactory = project.service<RefactorIntentionFactory>()
            SwingUtilities.invokeLater {
                ApplicationManager.getApplication().invokeLater(
                    {
                        refactorIntentionFactory.removeIntentionsFromEditor()
                        refactorIntentionFactory.addIntentionToEditor()
                    },
                    ModalityState.NON_MODAL
                )
            }
            addTabbedPaneToToolWindow(project)
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }

    override fun shouldBeAvailable(project: Project) = true
}
