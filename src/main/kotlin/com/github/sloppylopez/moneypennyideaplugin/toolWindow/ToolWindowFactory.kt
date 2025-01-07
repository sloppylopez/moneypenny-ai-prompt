package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.actions.PopUpHooverAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeActionConcat
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeActionParallel
import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.github.sloppylopez.moneypennyideaplugin.listeners.FileOpenListener
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFrame
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.util.PsiUtilBase

class ToolWindowFactory : ToolWindowFactory, ApplicationActivationListener {

    override fun applicationActivated(ideFrame: IdeFrame) {
        thisLogger().info("App activated")
    }

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        try {
            // Register actions
            registerActions(project)

            // Add the tabbed pane to the tool window
            addTabbedPaneToToolWindow(project)

            // Register file open listener for applying inlays
            val fileOpenListener = FileOpenListener(project)
            fileOpenListener.register()

            // Apply inlays or markers for existing open files
            applyInlaysToOpenFiles(project)
        } catch (e: Exception) {
            thisLogger().error("Error in createToolWindowContent: ${e.stackTraceToString()}")
        }
    }

    private fun registerActions(project: Project) {
        try {
            val sendToPromptFileFolderTreeActionParallel = SendToPromptFileFolderTreeActionParallel(project)
            sendToPromptFileFolderTreeActionParallel.registerFolderTreeAction()

            val sendToPromptFileFolderTreeActionConcat = SendToPromptFileFolderTreeActionConcat(project)
            sendToPromptFileFolderTreeActionConcat.registerFolderTreeAction()

            val popUpHooverAction = PopUpHooverAction()
            popUpHooverAction.addActionsToEditor()
        } catch (e: Exception) {
            thisLogger().error("Error registering actions: ${e.stackTraceToString()}")
        }
    }

    private fun applyInlaysToOpenFiles(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            try {
                val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@invokeLater
                val psiFile = PsiUtilBase.getPsiFileInEditor(editor, project) ?: return@invokeLater

                thisLogger().info("Adding inlays manually for file: ${psiFile.name}")

                val inlayModel = editor.inlayModel

                // Create the clickable presentation
                val factory = com.intellij.codeInsight.hints.presentation.PresentationFactory(editor)
                val presentation = factory.referenceOnHover(
                    factory.text("Hello World - Click Me")
                ) { _, _ ->
                    com.intellij.openapi.ui.Messages.showMessageDialog(
                        editor.project,
                        "Hello World clicked!",
                        "Information",
                        com.intellij.openapi.ui.Messages.getInformationIcon()
                    )
                }

                // Use the PresentationRenderer to wrap the presentation
                val renderer = com.intellij.codeInsight.hints.presentation.PresentationRenderer(presentation)

                // Add the inlay at the beginning of the file
                inlayModel.addInlineElement(0, renderer)

                thisLogger().info("Inlay added successfully for file: ${psiFile.name}")
            } catch (e: Exception) {
                thisLogger().error("Error adding inlays to open files: ${e.stackTraceToString()}")
            }
        }
    }

    override fun shouldBeAvailable(project: Project) = true
}
