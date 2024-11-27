package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.data.GlobalData
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.ui.JBColor
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.plaf.basic.BasicButtonUI

class ButtonTabComponent(
    private val pane: JTabbedPane,
    private val parentDisposable: Disposable
) : JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)), Disposable {

    init {
        requireNotNull(pane) { "TabbedPane is null" }
        isOpaque = false

        // Make JLabel read titles from JTabbedPane
        val label: JLabel = object : JLabel() {
            override fun getText(): String {
                val i = pane.indexOfTabComponent(this@ButtonTabComponent)
                return if (i != -1) pane.getTitleAt(i) else ""
            }
        }
        add(label)

        // Add more space between the label and the button
        label.border = BorderFactory.createEmptyBorder(0, 0, 0, 5)

        // Tab close button
        val button = TabButton()
        add(button)

        // Add more space to the top of the component
        border = BorderFactory.createEmptyBorder(2, 0, 0, 0)

        // Register this component with the parent disposable
        Disposer.register(parentDisposable, this)
    }

    override fun dispose() {
        // Perform cleanup
        pane.removeAll()
    }

    private inner class TabButton : JButton(), ActionListener, Disposable {
        private val buttonMouseListener = object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                val component = e.component
                if (component is AbstractButton) {
                    component.isBorderPainted = true
                }
            }

            override fun mouseExited(e: MouseEvent) {
                val component = e.component
                if (component is AbstractButton) {
                    component.isBorderPainted = false
                }
            }
        }

        init {
            val size = 17
            preferredSize = Dimension(size, size)
            toolTipText = "Close this tab"
            //Make the button looks the same for all Laf's
            setUI(BasicButtonUI())
            //Make it transparent
            isContentAreaFilled = false
            //No need to be focusable
            isFocusable = false
            border = BorderFactory.createEtchedBorder()
            isBorderPainted = false
            isRolloverEnabled = true
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener)
            addActionListener(this)

            // Register this button for disposal
            Disposer.register(parentDisposable, this)
        }

        override fun actionPerformed(e: ActionEvent) {
            val i = pane.indexOfTabComponent(this@ButtonTabComponent)
            if (i != -1) {
                val component = pane.getComponentAt(i)
                if (component is Disposable) {
                    Disposer.dispose(component)
                }
                pane.remove(i)
                GlobalData.tabCounter--
            }
        }

        override fun dispose() {
            // Clean up button-specific resources
            removeActionListener(this)
            removeMouseListener(buttonMouseListener)
        }

        override fun updateUI() {}

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2 = g.create() as Graphics2D
            if (model.isPressed) {
                g2.translate(1, 1)
            }
            g2.stroke = BasicStroke(2f)
            g2.color = JBColor.BLACK
            if (model.isRollover) {
                g2.color = JBColor.MAGENTA
            }
            val delta = 6
            g2.drawLine(delta, delta, width - delta - 1, height - delta - 1)
            g2.drawLine(width - delta - 1, delta, delta, height - delta - 1)
            g2.dispose()
        }
    }
}
