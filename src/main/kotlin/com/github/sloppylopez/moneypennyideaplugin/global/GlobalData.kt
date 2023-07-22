package com.github.sloppylopez.moneypennyideaplugin.global

import com.intellij.ui.components.JBTabbedPane
import javax.swing.JCheckBox
import javax.swing.JPanel

object GlobalData {
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
}
