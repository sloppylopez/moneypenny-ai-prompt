import com.intellij.openapi.application.Result
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.uiDesigner.core.GridConstraints
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JPanel


@Service(Service.Level.PROJECT)
class RandomCodeToolWindowFactory(project: Project) : ToolWindowFactory {
    //TODO add try catch so we can see the error
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        try {
//            val manager = FileEditorManager.getInstance(project)
//            val editor: Editor = manager.selectedTextEditor!!
//            val cursorOffset: Int = editor.caretModel.offset
//            val document: Document = editor.document
////            document.insertString(cursorOffset, "holaaaaa1234555")
//            object : WriteCommandAction<Any?>(project) {
//                @Throws(Throwable::class)
//                override fun run(result: Result<in Any?>) {
//                    document.insertString(cursorOffset, "holaaaaa1234555")
//                }
//            }.execute()
/////////////////////////
            val codeText = generateRandomCodeText(project)
            val editorComponent = createEditorComponent(codeText, project, toolWindow)

            val contentFactory = ContentFactory.getInstance()
            val toolWindowContent = contentFactory.createContent(editorComponent, "Editoooooor", false)
            toolWindow.contentManager.addContent(toolWindowContent)



        } catch (e: Exception) {
            Messages.showInfoMessage(
                e.stackTraceToString(), "Error",
            )
        }
    }

    private fun generateRandomCodeText(project: Project): String {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val selectedEditor = fileEditorManager.selectedTextEditor ?: return "// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.\n" +
                "package com.intellij.openapi.wm;\n" +
                "\n" +
                "import com.intellij.util.ArrayUtil;\n" +
                "import org.jetbrains.annotations.NotNull;\n" +
                "import org.jetbrains.annotations.Nullable;\n" +
                "\n" +
                "import javax.swing.*;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "/**\n" +
                " * This interface represents components to be added on the {@link WelcomeScreen} tab view\n" +
                " * see {@link com.intellij.openapi.wm.impl.welcomeScreen.TabbedWelcomeScreen}\n" +
                " */\n" +
                "public interface WelcomeScreenTab {\n" +
                "\n" +
                "  /**\n" +
                "   * @return component presents list item on the {@link WelcomeScreen} tab view\n" +
                "   */\n" +
                "  @NotNull\n" +
                "  JComponent getKeyComponent(@NotNull JComponent parent);\n" +
                "\n" +
                "  /**\n" +
                "   * @return component shown when related key component is selected\n" +
                "   */\n" +
                "  @NotNull\n" +
                "  JComponent getAssociatedComponent();\n" +
                "\n" +
                "  /**\n" +
                "   * @return list of children welcome screen tabs\n" +
                "   */\n" +
                "  default @NotNull List<WelcomeScreenTab> getChildTabs() { return new ArrayList<>(); }\n" +
                "\n" +
                "  default @Nullable String getChildTabsName() { return null; }\n" +
                "\n" +
                "  default void updateComponent() {}\n" +
                "}\n"

        return selectedEditor.document.text
    }

    private fun createEditorComponent(codeText: String, project: Project, toolWindow: ToolWindow): JComponent {
        val editorFactory = EditorFactory.getInstance()
        val editor = editorFactory.createEditor(editorFactory.createDocument(codeText), project)

        val colorsScheme = EditorColorsManager.getInstance().globalScheme

        editor.settings.isLineNumbersShown = true
        editor.settings.isFoldingOutlineShown = true
        editor.settings.isAdditionalPageAtBottom = false
        editor.settings.isVirtualSpace = false
        editor.component.font = Font(colorsScheme.editorFontName, Font.PLAIN, 14)

        val editorPanel = JPanel()
        editorPanel.add(editor.component)

        //////////////////

        ////////////////////////holaaaaa1234555
//        toolWindow.getContentManager().addContent(
//            ContentFactory.SERVICE.getInstance().createContent(editorPanel, "", false)
//        )
//
//        val document2:Document = EditorFactory.getInstance().createDocument("public static void main(String... args) {\n}");
//        document2.setReadOnly(false)
//        EditorFactory.getInstance().createEditor(document2)
//        val editorComponent2 = EditorComponentImpl(EditorFactory.getInstance().createEditor(document2) as EditorImpl);
//
//        editorPanel.add(editorComponent2, GridConstraints())
        ////////////////////////

        return editorPanel
    }
}
