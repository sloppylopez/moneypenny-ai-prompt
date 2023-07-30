package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.data.Event
import com.intellij.ui.JBColor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JOptionPane
import javax.swing.JPanel
import kotlin.math.abs

class TimeLine(events: MutableList<Event>) : JPanel() {
    private val events: MutableList<Event> = events.sortedBy { it.time }.toMutableList()
    private val eventPoints = events.map { Triple(it.time.hour, it.description, it.isUser) }.toMutableList()

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.color = JBColor.BLACK
        g.drawLine(50, height / 2, width - 50, height / 2)
        eventPoints.forEachIndexed { i, (hour, description, isUser) ->
            val x = 50 + i * (width - 100) / (eventPoints.size - 1)
            g.color = if (isUser) JBColor.RED else JBColor.BLUE
            g.fillOval(x - 5, height / 2 - 5, 10, 10)
            g.color = JBColor.BLACK
            g.drawString(description, x, height / 2 + 20) // Inverted position for text labels
            g.drawString(hour.toString(), x, height / 2 - 20) // Inverted position for number labels
        }
    }

    fun refresh(): TimeLine {
        val timeline = TimeLine(events)
        timeline.preferredSize = Dimension(600, 60)
        add(timeline)
        return this
    }

    fun addPointInTimeLine(newEvent: Event) {
        removeAll()
        events.add(newEvent)
        eventPoints.add(Triple(newEvent.time.hour, newEvent.description, newEvent.isUser))
        repaint()
    }

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val x = e.x
                val y = e.y
                eventPoints.forEachIndexed { i, (hour, description, isUser) ->
                    val dotX = 50 + i * (width - 100) / (eventPoints.size - 1)
                    val dotY = height / 2
                    if (abs(dotX - x) <= 5 && abs(dotY - y) <= 5) {
                        JOptionPane.showMessageDialog(this@TimeLine, "Dot Clicked!")
                        return
                    }
                }
            }
        })
    }
}