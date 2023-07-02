package com.github.sloppylopez.moneypennyideaplugin.toolWindow
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.openapi.components.Service
import java.io.File
import javax.swing.SwingUtilities
import javax.swing.SwingUtilities.invokeLater

@Service(Service.Level.PROJECT)
class FileEditorManager(private val project: Project) {

    fun openFileInEditor(filePath: String?) {
        if (filePath == null) return
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(filePath))
        val fileEditorManager = FileEditorManager.getInstance(project)

        if (virtualFile != null) {
            val openFileDescriptor = OpenFileDescriptor(project, virtualFile)
            fileEditorManager.openEditor(openFileDescriptor, true)

            invokeLater {
                highlightTextInEditor(
                    "override fun applicationActivated(ideFrame: IdeFrame) {\n" +
                            "        thisLogger().warn(\"Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.\")\n" +
                            "    }"
                )
//                val editor = FileEditorManager.getInstance(project).selectedTextEditor
                val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
                if (psiFile != null) {
                    ApplicationManager.getApplication().invokeLater({
                        addCustomIntention()
                    }, ModalityState.NON_MODAL)
                }
            }
        }
    }

    private fun addCustomIntention() {
        val intentionManager = IntentionManager.getInstance()

        // Create a new IntentionAction for "MoneyPenny Refactor"
        val customIntention = object : IntentionAction {
            override fun getText(): String = "MoneyPenny Refactor1"

            override fun getFamilyName(): String = "Custom Intentions"

            override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

            override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
                // Show a message when the intention is selected
                val message = "Hello World"
                ApplicationManager.getApplication().runWriteAction {
                    editor?.let { selectedEditor ->
                        val offset = selectedEditor.caretModel.offset
                        selectedEditor.document.insertString(offset, message)
                    }
                }
            }

            override fun startInWriteAction(): Boolean = false
        }
        intentionManager.addAction(customIntention)
    }

    private fun highlightTextInEditor(text: String) {
        val editor = getCurrentEditor(project)
        editor?.let {
            val document = editor.document
            val textOffset = document.text.indexOf(text)
            if (textOffset != -1) {
                editor.caretModel.moveToOffset(textOffset)
                editor.selectionModel.setSelection(textOffset, textOffset + text.length)
            }
        }
    }

    private fun getCurrentEditor(project: Project): Editor? {
        val file = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
        return file?.let { FileEditorManager.getInstance(project).selectedTextEditor }
    }
}
