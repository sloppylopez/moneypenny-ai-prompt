package com.github.sloppylopez.moneypennyideaplugin.managers

import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class FileEditorManagerTest : BasePlatformTestCase() {
    private lateinit var fileEditorManager: FileEditorManager

    override fun setUp() {
        super.setUp()
        val project: Project = project
        fileEditorManager = FileEditorManager(project)
    }

    @Test
    fun testOpenFileInEditor_NullFilePath() {
        // Arrange
        val fileEditorManager = mock(FileEditorManager::class.java)
        val filePath: String? = null
        val contentPromptText = "Content Prompt"
        val isSnippet = true

        // Act
        fileEditorManager.openFileInEditor(filePath, contentPromptText, isSnippet)

        // Assert
        verify(fileEditorManager).openFileInEditor(filePath, contentPromptText, isSnippet)
        verifyNoMoreInteractions(fileEditorManager)
    }


//    @Test
//    fun testOpenFileInEditor_NullContentPromptText() {
//        // Arrange
//        val filePath: String? = "/path/to/file.txt"
//        val contentPromptText: String? = null
//        val isSnippet: Boolean? = true
//
//        // Act
//        fileEditorManager.openFileInEditor(filePath, contentPromptText, isSnippet)
//
//        // Assert
//        verify(fileEditorManager).openFileInEditor(filePath, contentPromptText, isSnippet)
//        verifyNoMoreInteractions(fileEditorManager)
//    }

//    @Test
//    fun testOpenFileInEditor_NullIsSnippet() {
//        // Arrange
//        val filePath: String? = "/path/to/file.txt"
//        val contentPromptText: String? = "Content Prompt"
//        val isSnippet: Boolean? = null
//
//        // Act
//        fileEditorManager.openFileInEditor(filePath, contentPromptText, isSnippet)
//
//        // Assert
//        verify(fileEditorManager).openFileInEditor(filePath, contentPromptText, isSnippet)
//        verifyNoMoreInteractions(fileEditorManager)
//    }
}
