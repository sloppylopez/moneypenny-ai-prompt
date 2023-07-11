package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.intellij.ui.JBColor
import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.plaf.basic.BasicButtonUI

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



/**
 * Component to be used as tabComponent;
 * Contains a JLabel to show the text and
 * a JButton to close the tab it belongs to
 */
class ButtonTabComponent(pane: JTabbedPane?) : JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)) {
    private val pane: JTabbedPane

    private inner class TabButton : JButton(), ActionListener {
        init {
            val size = 17
            preferredSize = Dimension(size, size)
            toolTipText = "close this tab"
            //Make the button looks the same for all Laf's
            setUI(BasicButtonUI())
            //Make it transparent
            isContentAreaFilled = false
            //No need to be focusable
            isFocusable = false
            border = BorderFactory.createEtchedBorder()
            isBorderPainted = false
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener)
            isRolloverEnabled = true
            //Close the proper tab by clicking the button
            addActionListener(this)
        }

        override fun actionPerformed(e: ActionEvent) {
            val i = pane.indexOfTabComponent(this@ButtonTabComponent)
            if (i != -1) {
                pane.remove(i)
                GlobalData.tabCounter--
            }
        }

        //we don't want to update UI for this button
        override fun updateUI() {}

        //paint the cross
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            val g2 = g.create() as Graphics2D
            //shift the image for pressed buttons
            if (getModel().isPressed) {
                g2.translate(1, 1)
            }
            g2.stroke = BasicStroke(2f)
            g2.color = JBColor.BLACK
            if (getModel().isRollover) {
                g2.color = JBColor.MAGENTA
            }
            val delta = 6
            g2.drawLine(delta, delta, width - delta - 1, height - delta - 1)
            g2.drawLine(width - delta - 1, delta, delta, height - delta - 1)
            g2.dispose()
        }
    }

    init {
        //unset default FlowLayout' gaps
        if (pane == null) {
            throw NullPointerException("TabbedPane is null")
        }
        this.pane = pane
        isOpaque = false

        //make JLabel read titles from JTabbedPane
        val label: JLabel = object : JLabel() {
            override fun getText(): String {
                val i = pane.indexOfTabComponent(this@ButtonTabComponent)
                return if (i != -1) {
                    pane.getTitleAt(i)
                } else ""
            }
        }
        add(label)
        //add more space between the label and the button
        label.border = BorderFactory.createEmptyBorder(0, 0, 0, 5)
        //tab button
        val button: JButton = TabButton()
        add(button)
        //add more space to the top of the component
        border = BorderFactory.createEmptyBorder(2, 0, 0, 0)
    }

    companion object {
        private val buttonMouseListener: MouseListener = object : MouseAdapter() {
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
    }
}


