package com.github.sloppylopez.moneypennyideaplugin.services

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class ProjectServiceTest : BasePlatformTestCase() {
    private val projectService = ProjectService()

    fun test_trim_code_with_explanations_single() {
        val response = "Here is the modified code with the added method:\n" +
                "```\n" +
                "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                "\n" +
                "class ClassThatNeedsRefactor {\n" +
                "    fun isString(value: Any): Boolean {\n" +
                "        return value is String\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "I have added the `isString` method that takes an `Any` parameter and returns `true` if the parameter is a string, and `false` otherwise."
        val result = projectService.extractCommentsFromCode(response)
        assertEquals(
            result, "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                    "\n" +
                    "class ClassThatNeedsRefactor {\n" +
                    "    fun isString(value: Any): Boolean {\n" +
                    "        return value is String\n" +
                    "    }\n" +
                    "}"
        )
    }

    fun test_trim_code_with_explanations_single_and_language() {
        val response = "Here is the modified code with the added method:\n" +
                "```java\n" +
                "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                "\n" +
                "class ClassThatNeedsRefactor {\n" +
                "    fun isString(value: Any): Boolean {\n" +
                "        return value is String\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "I have added the `isString` method that takes an `Any` parameter and returns `true` if the parameter is a string, and `false` otherwise."
        val result = projectService.extractCommentsFromCode(response)
        assertEquals(
            result, "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                    "\n" +
                    "class ClassThatNeedsRefactor {\n" +
                    "    fun isString(value: Any): Boolean {\n" +
                    "        return value is String\n" +
                    "    }\n" +
                    "}"
        )
    }

    fun test_trim_code_with_explanations_single_and_language_without_previous_sentence() {
        val response = "```kotlin\n" +
                "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                "\n" +
                "class ClassThatNeedsRefactor {\n" +
                "    fun isString(value: Any): Boolean {\n" +
                "        return value is String\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "I have added the `isString` method that takes an `Any` parameter and returns `true` if the parameter is a string, and `false` otherwise."
        val result = projectService.extractCommentsFromCode(response)
        assertEquals(
            result, "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                    "\n" +
                    "class ClassThatNeedsRefactor {\n" +
                    "    fun isString(value: Any): Boolean {\n" +
                    "        return value is String\n" +
                    "    }\n" +
                    "}"
        )
    }

    fun test_trim_code_with_explanations_single_and_language_without_sentences_or_language() {
        val response = "```\n" +
                "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                "\n" +
                "class ClassThatNeedsRefactor {\n" +
                "    fun isString(value: Any): Boolean {\n" +
                "        return value is String\n" +
                "    }\n" +
                "}\n" +
                "```\n"
        val result = projectService.extractCommentsFromCode(response)
        assertEquals(
            result, "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                    "\n" +
                    "class ClassThatNeedsRefactor {\n" +
                    "    fun isString(value: Any): Boolean {\n" +
                    "        return value is String\n" +
                    "    }\n" +
                    "}"
        )
    }

    fun test_trim_code_with_explanations_single_and_language_with_sentences() {
        val response = "Topic: Code refactoring\n" +
                "Context: Refactoring code in the `NumbersCalculator` class to improve its efficiency and readability.\n" +
                "Intent: To dry the code and use best practices, including using one-liners if possible.\n" +
                "Named Entities: None\n" +
                "Keywords: `package`, `class`, `fun`, `Array`, `List`, `getPrimeNumbers`, `val`, `mutableListOf`, `for`, `if`, `else`, `in`, `..`, `%`, `break`, `add`\n" +
                "\n" +
                "Sentiment: The sentiment is neutral as the prompt is requesting a code refactoring.\n" +
                "\n" +
                "Refactoring code in the `NumbersCalculator` class to improve its efficiency and readability. DRY it following best practices and using one-liners if possible.\n" +
                "\n" +
                "```kotlin\n" +
                "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n" +
                "\n" +
                "class NumbersCalculator {\n" +
                "    fun getPrimeNumbers(numbers: Array<Int>): List<Int> = numbers.filter { number ->\n" +
                "        (number >= 2) && (2..number / 2).none { number % it == 0 }\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "Follow Up: What are the changes made to the code?"
        val result = projectService.extractCommentsFromCode(response)
        assertEquals(
            result, "package com.github.sloppylopez.moneypennyideaplugin.gineapigs\n\nclass NumbersCalculator {\n    fun getPrimeNumbers(numbers: Array\u003cInt\u003e): List\u003cInt\u003e \u003d numbers.filter { number -\u003e\n        (number \u003e\u003d 2) \u0026\u0026 (2..number / 2).none { number % it \u003d\u003d 0 }\n    }\n}"
        )
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