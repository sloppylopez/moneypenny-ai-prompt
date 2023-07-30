package com.github.sloppylopez.moneypennyideaplugin.data

import com.intellij.ui.components.JBTabbedPane
import javax.swing.JCheckBox
import javax.swing.JList
import javax.swing.JPanel

object GlobalData {
    var engine: String = "gpt-3.5-turbo"
    var followUpActive: Boolean = true
    var role: String = "🤖 refactor-machine"
    var userRole: String = "🤓 user"
    var downerTabName: Int = 1
    var tabCounter: Int = 1
    var index: Int = 1
    val tabNameToFilePathMap = mutableMapOf<String, String>()
    val tabNameToContentPromptTextMap = mutableMapOf<String, String>()
    val tabNameToChatWindowContent = mutableMapOf<String, JList<String>>()
    val upperTabNameToTimeLine = mutableMapOf<String, JPanel>()
    val tabNameToInnerPanel = mutableMapOf<String, JPanel>()
    val prompts = mutableMapOf<String, Map<String, List<String>>>()
    val apiKey: String? = System.getenv("OPENAI_API_KEY")
    var emptyCheckBoxButton: JCheckBox? = null
    var innerPanel: JPanel? = null
    var nestedPanel: JPanel? = null
    var selectedTabbedPane: JBTabbedPane? = null
    val engineModelStrings = arrayOf(
        "gpt-3.5-turbo",
        "gpt-3.5-turbo-16k",
        "gpt-4-32k",
        "gpt-4"
    )
    val roleModelStrings = arrayOf(
        "🤖 refactor-machine",
        "🐶 helpful-assistant",
//        "✨ code-completer",
        "🤓 code-reviewer",
    )
}
