import com.github.sloppylopez.moneypennyideaplugin.services.ProjectService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.JEditorPane
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class FileEditorFactory(private val project: Project) {
    private val service = project.service<ProjectService>()
    fun getCurrentEditorPane(innerPanel: JPanel) {

        try {
            val fileEditorManager = FileEditorManager.getInstance(project)
            val selectedEditor = fileEditorManager.selectedEditor
            fileEditorManager.openFiles.forEach {
                Messages.showInfoMessage(
                    it.name, "FileEditorFactory:",
                )
            }
            if (selectedEditor != null) {
                val editorComponent = selectedEditor.component
                if (editorComponent is JEditorPane) {
                    innerPanel.add(editorComponent)
                }
            } else {
                val jEditorPane = JEditorPane()
                jEditorPane.text = "No file selected"
    //            jEditorPane.add(innerPanel)
                innerPanel.add(jEditorPane)
            }
        } catch (e: Exception) {
            service.logError("FileEditorFactory", e)
        }
    }
}
