package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.JLabel
import javax.swing.JPanel

class ClickableTextInlayHintsProvider : InlayHintsProvider<NoSettings> {

    override val name: String
        get() = "Clickable Text Inlay Hints"

    override fun createSettings(): NoSettings = NoSettings()

    override val key = SettingsKey<NoSettings>("clickable.text.inlay.hints")

    override val previewText: String?
        get() = "class Example {\n    fun greet() {}\n}"

    override val description: String?
        get() = "Adds a clickable 'Hello World' inlay hint next to specific elements."

    override val isVisibleInSettings: Boolean
        get() = true

    override val group: InlayGroup
        get() = InlayGroup.CODE_VISION_GROUP

    override fun isLanguageSupported(language: Language): Boolean {
        return language.id == "JAVA" || language.id == "kotlin"
    }

    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable {
        return object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener): JPanel {
                return JPanel().apply {
                    add(JLabel("No configurable settings available."))
                }
            }
        }
    }

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector? {
        return object : FactoryInlayHintsCollector(editor) {
            override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
                // Add inline inlay near the first named element (class or method)
//                if (element.text.startsWith("class") || element.text.startsWith("fun")) {
                    val factory = PresentationFactory(editor)

                    // Create a clickable text presentation
                    val presentation = factory.reference(
                        factory.text(" [Hello World] ")
                    ) {
                        Messages.showMessageDialog(
                            editor.project,
                            "Hello World clicked!",
                            "Information",
                            Messages.getInformationIcon()
                        )
                    }

                    // Place the inlay at the beginning of the element
                    sink.addInlineElement(element.textOffset, false, presentation, false)
//                }
                return true
            }
        }
    }
}
