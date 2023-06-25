package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFocusManager
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.UIManager

class YourTextEditorComponent(private val project: Project) : JComponent() {

    private lateinit var editor: EditorEx
    private lateinit var document: Document

    init {
        createEditor()
    }

    private fun createEditor() {
        // Set up the editor settings
        val editorSettings = EditorSettingsExternalizable.getInstance()
        editorSettings.isLineNumbersShown = true

        // Create the document and editor
//        document = Document()
        editor = EditorFactory.getInstance().createEditor(document, project) as EditorEx
//        editor.settings = editorSettings

        // Set the editor component as the Swing component for rendering
        editor.component.putClientProperty(UIManager.getDefaults()["EditorPane.useEditorFonts"], true)
        add(editor.component)

        // Register focus listener to ensure editor gets focus properly
//        val focusChangeListener = FocusChangeListener {
//            IdeFocusManager.getInstance(project).doWhenFocusSettlesDown {
//                if (!Disposer.isDisposed(this)) {
//                    editor.contentComponent.requestFocusInWindow()
//                }
//            }
//        }
//        editor.addFocusListener(focusChangeListener)
//        Disposer.register(this, Disposable {
//            editor.removeFocusListener(focusChangeListener)
//        })
    }

    fun getText(): String {
        return document.text
    }

    fun setText(text: String) {
        document.setText(text)
    }

    override fun requestFocus() {
        SwingUtilities.invokeLater {
//            if (!Disposer.isDisposed(this)) {
//                editor.contentComponent.requestFocusInWindow()
//            }
        }
    }
}
