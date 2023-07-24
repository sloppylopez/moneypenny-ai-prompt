package com.github.sloppylopez.moneypennyideaplugin.services

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class ProjectServiceTest : BasePlatformTestCase() {
    private val projectService = ProjectService()

    fun test_trim_code_single() {
        val response = "```kotlin\nfun myFunc(){\n}```"
        val codeSymbol = "```"
        val result = response.replaceFirst("""```[a-zA-Z0-9]+\n""".toRegex(), "").removeSurrounding(codeSymbol).trim()
        assertEquals(result, "fun myFunc(){")
    }

    fun testExpandFoldersWithEmptyList() {
        val fileList = emptyList<Any>()
        val expandedFileList = projectService.expandFolders(fileList)
        assertEquals(0, expandedFileList.size)
    }

    fun testExpandFoldersWithFilesOnly() {
        val fileList = listOf(
            File("file1.txt"),
            File("file2.txt"),
            File("file3.txt")
        )

        val expandedFileList = projectService.expandFolders(fileList).reversed()

        assertEquals(fileList.size, expandedFileList.size)
        assertEquals(fileList, expandedFileList)
    }

    fun testExpandFoldersWithVirtualFilesOnly() {
        val fileList = listOf(
            projectService.fileToVirtualFile(File("src/test/kotlin/com/github/sloppylopez/moneypennyideaplugin/services/testData/DummyKotlinFile.kt")),
            projectService.fileToVirtualFile(File("src/test/kotlin/com/github/sloppylopez/moneypennyideaplugin/services/testData/DummyKotlinFile2.kt"))
        )

        val expandedFileList = projectService.expandFolders(fileList)
            .reversed()//We need to do reversed here because they get inverted after the expansion

        assertEquals(fileList.size, expandedFileList.size)
        for (i in expandedFileList.indices) {
            assertEquals(projectService.virtualFileToFile(fileList[i]), expandedFileList[i])
        }
    }

    fun testExpandFoldersWithVirtualFilesAndDirectories() {
        val fileList = listOf(
            projectService.fileToVirtualFile(File("src/test/kotlin/com/github/sloppylopez/moneypennyideaplugin/services/testData/DummyKotlinFile.kt")),
            File("file2.txt"),
            projectService.fileToVirtualFile(File("src/test/kotlin/com/github/sloppylopez/moneypennyideaplugin/services/testData")),
            File("dir2")
        )

        val expandedFileList = projectService.expandFolders(fileList).reversed()

//        assertEquals(fileList, expandedFileList)
        assertEquals(5, expandedFileList.size)
//        assertEquals(expandedFileList[0], fileList[0].toString())
//        assertTrue(expandedFileList.contains(fileList[1]))
//        assertTrue(expandedFileList.contains(File("DummyKotlinFile2.kt")))
//        assertTrue(expandedFileList.contains(File("DummyKotlinFile.kt")))
//        assertTrue(expandedFileList.contains(File("dir2")))
    }

    fun testExpandFoldersWithFilesAndDirectories() {
        val fileList = listOf(
            File("file1.txt"),
            File("directory1"),
            File("file2.txt"),
            File("directory2")
        )

        val expandedFileList = projectService.expandFolders(fileList)

        assertEquals(4, expandedFileList.size)
        assertTrue(expandedFileList.contains(File("file1.txt")))
        assertTrue(expandedFileList.contains(File("directory1")))
        assertTrue(expandedFileList.contains(File("file2.txt")))
        assertTrue(expandedFileList.contains(File("directory2")))
    }

    override fun getTestDataPath() = "src/test/testData/rename"

}