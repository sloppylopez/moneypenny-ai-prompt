package com.github.sloppylopez.moneypennyideaplugin.global

import com.intellij.ui.components.JBTabbedPane
import javax.swing.JCheckBox
import javax.swing.JPanel

object GlobalData {
    var engine: String = "gpt-3.5-turbo-16k"
    var role: String = "refactor-machine"
    var downerTabName: Int = 1
    var tabCounter: Int = 1
    var index: Int = 1
    val tabNameToFilePathMap = mutableMapOf<String, String>()
    val tabNameToContentPromptTextMap = mutableMapOf<String, String>()
    val prompts = mutableMapOf<String, Map<String, List<String>>>()
    val apiKey: String? = System.getenv("OPENAI_API_KEY")
    var explanationButton: JCheckBox? = null
    var innerPanel: JPanel? = null
    var nestedPanel: JPanel? = null
    var tabbedPane: JBTabbedPane? = null
    val engineModelStrings = arrayOf(
//        "gpt-4-32k",
//        "gpt-4",
        "gpt-3.5-turbo-16k",
        "gpt-3.5-turbo",
//        "text-davinci-003",
//        "text-curie-001",
//        "text-babbage-001",
//        "text-ada-001"
    )
    val roleModelStrings = arrayOf(
        "🤖 refactor-machine",
        "🧠 helpful-assistant",
        "✨ code-completer",
        "🤓 code-reviewer",
    )
}
