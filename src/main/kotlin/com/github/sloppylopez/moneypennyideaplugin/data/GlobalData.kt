package com.github.sloppylopez.moneypennyideaplugin.data

import com.intellij.ui.components.JBTabbedPane
import javax.swing.JCheckBox
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
//    val upperTabNameToTimeLine = mutableMapOf<String, JPanel>()
    val tabNameToInnerPanel = mutableMapOf<String, JPanel>()
    val prompts = mutableMapOf<String, Map<String, List<String>>>()
    val apiKey: String? = System.getenv("OPENAI_API_KEY")
    var emptyCheckBoxButton: JCheckBox? = null
    var innerPanel: JPanel? = null
    var nestedPanel: JPanel? = null
    const val refactorMachineRolePromptDescription: String = "You are a refactor-machine that can also answer questions about any topic. When you are asked about programming code, always respond with code. Otherwise, when asked about anything else, you will answer like regular chatGPT"
    const val virtuousCircleRolePromptDescription: String = "First, Analyse the Prompt and using NLP return topic, context, intent, named entities, keywords and sentiment ending each sentence with a full stop. Then answer the question taking in account previous analysis, after that, return a follow up question.\n"
    var selectedTabbedPane: JBTabbedPane? = null
    val engineModelStrings = arrayOf(
        "gpt-3.5-turbo",
        "gpt-3.5-turbo-16k",
        "gpt-4-32k",
        "gpt-4"
    )
    val roleModelStrings = arrayOf(
        "🤖 refactor-machine",
//        "🐶 helpful-assistant",
//        "✨ code-completer",
//        "🤓 code-reviewer",
    )
}
