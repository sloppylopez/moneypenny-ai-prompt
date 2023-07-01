package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import java.io.File
import javax.swing.*

@Service(Service.Level.PROJECT)
class FileListCellRenderer : ColoredListCellRenderer<File>() {
    override fun customizeCellRenderer(
        list: JList<out File>,
        file: File?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
        if (file != null) {
            append(file.name)
            if (file.isDirectory) {
                append(" (Folder)", SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
            } else {
                append(" (File)", SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
            }
        }
    }

    fun getFileList(project: Project): JComponent {
        // Create a JPanel to hold the JList
        val panel = JPanel()

        // Create a JList to display the files
        val fileList = JList<File>()

        // Create a DefaultListModel to hold the files
        val model = DefaultListModel<File>()

        // Add some example files to the model
        model.addElement(File("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\kotlin\\com\\github\\sloppylopez\\moneypennyideaplugin\\toolWindow\\AIToolWindowFactory.kt"))
        model.addElement(File("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\kotlin\\com\\github\\sloppylopez\\moneypennyideaplugin\\services"))

        // Set the model on the JList
        fileList.model = model

        // Set the FileListCellRenderer as the cell renderer for the JList
        fileList.cellRenderer = FileListCellRenderer()

        // Add the JList to the panel
        panel.add(fileList)

        // Return the panel as JComponent
        return panel
    }

}
