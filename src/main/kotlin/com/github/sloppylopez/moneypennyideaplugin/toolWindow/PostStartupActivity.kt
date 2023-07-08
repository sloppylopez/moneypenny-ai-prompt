package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ToolWindowFactory
import com.intellij.openapi.diagnostic.thisLogger

class MyPostStartUPActivity : StartupActivity {
    override fun runActivity(project: Project) {
        thisLogger().info("MyPostStartUPActivity.runActivity")
        thisLogger().info("Init Actions here??")
//        val toolWindowFactory = ToolWindowFactory()
//        toolWindowFactory.createToolWindowContent(project, project.dummyToolWindow)

        // Perform any other post-startup actions here

        // Note: It's important to note that invoking UI-related operations from the StartupActivity
        // may require running them on the event dispatch thread (EDT) using invokeLater() or similar methods if necessary.
    }
}

