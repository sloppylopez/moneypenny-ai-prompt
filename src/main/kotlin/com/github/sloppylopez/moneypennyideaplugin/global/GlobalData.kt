package com.github.sloppylopez.moneypennyideaplugin.global

object GlobalData {
    var downerTabName: Int = 0
    var tabCounter: Int = 0
    val tabNameToFilePathMap = mutableMapOf<String, String>()
    val tabNameToContentPromptTextMap = mutableMapOf<String, String>()
    val prompts = mutableMapOf<String, List<String>>()//TODO: Maybe this data structure will have to change in the future
}
