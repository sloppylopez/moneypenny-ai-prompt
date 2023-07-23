package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.AnActionButton
import java.awt.Component
import javax.swing.JButton
import javax.swing.JComponent

class MyDropDownAction(private val project: Project) : AnActionButton(), CustomComponentAction {
    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        return JButton("MyDropDown").apply {
            addActionListener {
                showPopup(componentUnder = this)
            }
        }
    }

    private fun showPopup(componentUnder: Component) {
        val group = DefaultActionGroup()
        group.add(FirstAction(project))
        group.add(SecondAction(project))
        // Add more actions as needed

        val popup = JBPopupFactory.getInstance()
            .createActionGroupPopup("MyDropDown", group, DataManager.getInstance().dataContext, false, null, 10)

        popup.showUnderneathOf(componentUnder)
    }

    override fun actionPerformed(e: AnActionEvent) {
        TODO("Not yet implemented")
    }
}

class FirstAction(private val project: Project) : AnAction("First Action") {
    override fun actionPerformed(e: AnActionEvent) {
        // Handle first action event
    }
}

class SecondAction(private val project: Project) : AnAction("Second Action") {
    override fun actionPerformed(e: AnActionEvent) {
        // Handle second action event
    }
}