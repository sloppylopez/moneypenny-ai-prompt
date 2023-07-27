package com.github.sloppylopez.moneypennyideaplugin.components

import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField

class TextAreaExample : JPanel() {
    private val tarea = JTextArea(10, 10)
    private val tfield = JTextField(10)

    init {
        tarea.text = "Hello there\n"
        tarea.append("Hello student://")
        val scroll = JBScrollPane(tarea)

        tfield.addActionListener { tarea.append(tfield.text + "\n") }

        tarea.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(me: MouseEvent) {
                val x = me.x
                val y = me.y
                println("X : $x")
                println("Y : $y")
                val startOffset = tarea.viewToModel2D(Point(x, y))
                println("Start Offset : $startOffset")
                val text = tarea.text
                val searchLocation = text.indexOf("student://", startOffset)
                println("Search Location : $searchLocation")
                if (searchLocation == startOffset) {
                    JOptionPane.showMessageDialog(this@TextAreaExample, "BINGO you found me.")
                }
            }
        })

        layout = BorderLayout()
        add(scroll, BorderLayout.CENTER)
        add(tfield, BorderLayout.PAGE_END)
    }
}