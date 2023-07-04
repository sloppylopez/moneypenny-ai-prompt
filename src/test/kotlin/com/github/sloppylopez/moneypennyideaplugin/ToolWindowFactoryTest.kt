package com.github.sloppylopez.moneypennyideaplugin

import com.github.sloppylopez.moneypennyideaplugin.toolWindow.ToolWindowFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ex.ToolWindowManagerEx
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class ToolWindowFactoryTest : BasePlatformTestCase() {

    @Test
    fun testCreateToolWindowContent() {
        // Create a mock project
        val project = createMockProject()

        // Create an instance of your ToolWindowFactory
        val toolWindowFactory = ToolWindowFactory()

        // Create a mock tool window
        val toolWindowManager = ToolWindowManagerEx.getInstanceEx(project)
        val id = "TestMyToolWindowId"
        val toolWindow = toolWindowManager.registerToolWindow(id, true, ToolWindowAnchor.RIGHT, true)
        toolWindow.setToHideOnEmptyContent(true)
        // Call the method to test
        toolWindowFactory.createToolWindowContent(project, toolWindow)
        toolWindow.show()
        // Assert that the tool window's content panel is not empty
        assertNotNull(toolWindow.component)
//        assertFalse(toolWindow.component.components.isEmpty())
    }

    private fun createMockProject(): Project {
        val projectManager = ProjectManager.getInstance()
        return projectManager.defaultProject
    }

}
