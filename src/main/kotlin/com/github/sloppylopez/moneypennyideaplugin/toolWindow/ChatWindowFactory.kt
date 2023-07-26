package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.components.ChatWindowContent
import com.intellij.openapi.diagnostic.thisLogger


class ChatWindowFactory {
    fun getChatWindowContent(): ChatWindowContent? {
        try {

            //Add checkboxes
            return ChatWindowContent()
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
        return null
    }
}
