package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.Component
import javax.swing.Icon

class PopUpAction(
    private var project: Project,
    val actionGroup: DefaultActionGroup,
    icon: Icon,
    text: String
) : AnAction() {

    init {
        templatePresentation.icon = icon
        templatePresentation.text = text
    }

    override fun actionPerformed(e: AnActionEvent) {
        project = e.project!!
        showPopup(e.inputEvent.component)
    }

    private fun showPopup(component: Component) {
        val group = DefaultActionGroup()
        group.add(FirstAction(project, this))
        group.add(SecondAction(project, this))
        val popup = JBPopupFactory.getInstance()
            .createActionGroupPopup(
                "Choose Engine",
                group,
                DataManager.getInstance().getDataContext(component),
                false,
                null,
                10
            )
        popup.showUnderneathOf(component)
    }
}

class FirstAction(private val project: Project, private val popUpAction: PopUpAction) : AnAction("First Action") {
    override fun actionPerformed(e: AnActionEvent) {
        popUpAction.actionGroup.replaceAction(
            popUpAction, PopUpAction(
                project,
                popUpAction.actionGroup,
                AllIcons.Icons.Ide.NextStep,
                "2 Engine Selection"
            )
        )
    }
}

class SecondAction(private val project: Project, private val popUpAction: PopUpAction) : AnAction("Second Action") {
    override fun actionPerformed(e: AnActionEvent) {
        popUpAction.actionGroup.replaceAction(
            popUpAction, PopUpAction(
                project,
                popUpAction.actionGroup,
                AllIcons.Icons.Ide.NextStep,
                "1 Engine Selection"
            )
        )
    }
}