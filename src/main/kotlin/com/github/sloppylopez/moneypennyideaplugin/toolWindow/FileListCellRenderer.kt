package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import java.io.File
import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JList
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

    fun getFileList(): JFrame {
        // Create a JFrame to hold the JList
        val frame = JFrame("File List")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        // Create a JList to display the files
        val fileList = JList<File>()
        frame.contentPane.add(fileList)

        // Create a DefaultListModel to hold the files
        val model = DefaultListModel<File>()

        // Add some example files to the model
        model.addElement(File("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\kotlin\\com\\github\\sloppylopez\\moneypennyideaplugin\\toolWindow\\AIToolWindowFactory.kt"))
        model.addElement(File("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\kotlin\\com\\github\\sloppylopez\\moneypennyideaplugin\\services"))
//        model.addElement(File("path/to/folder1"))
//        model.addElement(File("path/to/folder2"))

        // Set the model on the JList
        fileList.model = model

        // Set the FileListCellRenderer as the cell renderer for the JList
        fileList.cellRenderer = FileListCellRenderer()

        // Display the JFrame
        frame.pack()
        frame.isVisible = true
        return frame
    }
}
