package com.github.sloppylopez.moneypennyideaplugin.components

import com.github.sloppylopez.moneypennyideaplugin.data.Event
import com.intellij.ui.JBColor
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JPanel

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
            g.drawString(description, x, height / 2 - 20)
            g.drawString(hour.toString(), x, height / 2 + 20)
        }
    }

    fun getTimeLine(): TimeLine {
        val timeline = TimeLine(events)
        timeline.preferredSize = Dimension(400, 60)
        add(timeline)
        return this
    }

    fun addPointInTimeLine(newEvent: Event) {
        events.add(newEvent)
        eventPoints.add(Triple(newEvent.time.hour, newEvent.description, newEvent.isUser))
        repaint()
    }
}