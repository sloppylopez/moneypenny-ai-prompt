package com.github.sloppylopez.moneypennyideaplugin.components;

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JTextArea


class MoneyPennyTextArea : JTextArea(20, 20) {
    private var image: BufferedImage? = null

    init {
        isOpaque = false
        try {
            image = ImageIO.read(File("C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\resources\\images\\moneypenny4.jpg"))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun paintComponent(g: Graphics) {
        val g2d = g.create() as Graphics2D
        g2d.color = background
        g2d.fillRect(0, 0, width, height)
        if (image != null) {
            val x = width - image!!.width
            val y = height - image!!.height
            g2d.drawImage(image, x, y, this)
        }
        super.paintComponent(g2d)
        g2d.dispose()
    }
}
