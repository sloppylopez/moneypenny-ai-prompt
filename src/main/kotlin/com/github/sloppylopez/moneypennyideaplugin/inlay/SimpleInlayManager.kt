package com.github.sloppylopez.moneypennyideaplugin.inlay

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.InlayModel
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class SimpleInlayManager {

    fun addHelloWorldInlay(editor: Editor) {
        val inlayModel: InlayModel = editor.inlayModel
        val offset = 0 // Add the inlay at the very beginning of the document

        val inlay = inlayModel.addInlineElement(offset, true, object : com.intellij.openapi.editor.EditorCustomElementRenderer {
            override fun calcWidthInPixels(inlay: Inlay<*>): Int {
                val fontMetrics =
                    editor.contentComponent.getFontMetrics(editor.colorsScheme.getFont(com.intellij.openapi.editor.colors.EditorFontType.PLAIN))
                return fontMetrics.stringWidth("Hello World")
            }

            override fun paint(
                inlay: Inlay<*>,
                g: Graphics,
                targetRegion: Rectangle,
                textAttributes: TextAttributes
            ) {
                g.color = Color.BLUE
                g.font = Font("Arial", Font.PLAIN, editor.colorsScheme.editorFontSize)
                g.drawString("Hello World", targetRegion.x, targetRegion.y + g.fontMetrics.ascent)
            }
        })

        // Add click handling
        editor.contentComponent.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val point: Point = e.point
                val inlayBounds = inlay?.bounds ?: return

                if (inlayBounds.contains(point)) {
                    // Display notification when the inlay is clicked
                    val notification = Notification(
                        "SimpleInlayManager",
                        "Hello World Clicked",
                        "You clicked the 'Hello World' inlay!",
                        NotificationType.INFORMATION
                    )
                    Notifications.Bus.notify(notification, editor.project)
                }
            }
        })
    }
}
