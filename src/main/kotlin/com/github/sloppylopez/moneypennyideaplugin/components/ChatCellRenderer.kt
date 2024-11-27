package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData.role
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.thisLogger
import java.awt.Component
import java.awt.event.KeyListener
import javax.swing.*

class ChatCellRenderer : JTextArea(), ListCellRenderer<String?>, Disposable {
    private val logger = thisLogger()

    init {
        isOpaque = true
        lineWrap = true
        wrapStyleWord = true
        border = BorderFactory.createEmptyBorder(0, 0, 1, 8)
        font = UIManager.getFont("List.font")
    }

    override fun getListCellRendererComponent(
        list: JList<out String?>,
        value: String?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val valueWithCarryReturn = value + "\n"
        val sanitizedValue = valueWithCarryReturn.replace("\r\n", "\n").trim()
        var removedFollowUp: List<String>? = emptyList()
        if (role == "🤖 refactor-machine") {
            removedFollowUp = sanitizedValue.split("\n")
        }

        this.apply {
            text = removedFollowUp?.joinToString("\n")
            background = if (isSelected) list.selectionBackground else list.background
            foreground = if (isSelected) list.selectionForeground else list.foreground
            preferredSize = null
            rows = (removedFollowUp?.size?.plus(1)) ?: 1000 // TODO: Investigate this magic number
        }
        return this
    }

    override fun dispose() {
        logger.info("Disposing ChatCellRenderer resources.")
        // Cleanup listeners, if any
        listenersToRemove()?.forEach { removeKeyListener(it) }

        // Clear text and reset configurations
        text = null
        border = null
        font = null
        preferredSize = null

        // Nullify any custom references (if applicable)
    }

    /**
     * Helper function to retrieve all key listeners for cleanup.
     */
    private fun listenersToRemove(): Array<KeyListener>? {
        return keyListeners
    }
}
