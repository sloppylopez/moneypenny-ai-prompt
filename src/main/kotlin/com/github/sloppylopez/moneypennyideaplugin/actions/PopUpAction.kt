package com.github.sloppylopez.moneypennyideaplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.AnActionButton
import java.awt.Component
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JComponent

class PopUpAction(
    private var project: Project,
    val actionGroup: DefaultActionGroup,
    icon: Icon,
    text: String
) : AnActionButton(), CustomComponentAction {

    init {
        templatePresentation.icon = icon
        templatePresentation.text = text
    }

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
//        val icon =
//            ToolWindowManager.getInstance(project)
//                .getLocationIcon(ToolWindowId.FIND, AllIcons.General.Pin_tab)
//        return ToolbarDecorator.ElementActionButton(icon)
//        return IconButton(IdeBundle.message("show.in.find.window.button.name"), icon)
        val modelStrings = arrayOf("Davinci", "Curie", "Babbage", "Ada")
        val models = ComboBox(modelStrings)
        val selectedIndex = 0
        models.selectedIndex = selectedIndex
        return models
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
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}

class FirstAction(private val project: Project, private val popUpAction: PopUpAction) : AnAction("First Action") {
    override fun actionPerformed(e: AnActionEvent) {
        popUpAction.actionGroup.replaceAction(
            popUpAction, PopUpAction(
                project,
                popUpAction.actionGroup,
                AllIcons.Icons.Ide.NextStepInverted,
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
                AllIcons.Actions.Execute,
                "1 Engine Selection"
            )
        )
    }
}