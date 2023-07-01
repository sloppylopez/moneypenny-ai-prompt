package com.github.sloppylopez.moneypennyideaplugin.toolWindow

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import java.awt.Font
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.JLabel
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class GitDiffPanelFactory(private val project: Project) {

    private fun createGitDiffPanel(panel: JPanel): JPanel {
        val diffFiles = getDiffFilesFromRemoteBranch(project)

        var yOffset = 10
        val lineHeight = 20
        val font = Font(Font.MONOSPACED, Font.PLAIN, 14)

        for (file in diffFiles) {
            val deletedLines = file.deletedLines
            val modifiedLines = file.modifiedLines

            val fileLabel = JLabel(file.fileName)
            fileLabel.setBounds(10, yOffset, 200, lineHeight)
            panel.add(fileLabel)

            val deletedLabel = JLabel("-$deletedLines")
            deletedLabel.foreground = JBColor.RED
            deletedLabel.font = font
            deletedLabel.setBounds(220, yOffset, 50, lineHeight)
            panel.add(deletedLabel)

            val modifiedLabel = JLabel("+$modifiedLines")
            modifiedLabel.foreground = JBColor.GREEN
            modifiedLabel.font = font
            modifiedLabel.setBounds(280, yOffset, 50, lineHeight)
            panel.add(modifiedLabel)

            yOffset += lineHeight + 5
        }

        return panel
    }

    private data class DiffFile(val fileName: String, val deletedLines: Int, val modifiedLines: Int)

    private fun getDiffFilesFromRemoteBranch(project: Project): List<DiffFile> {
        val gitRoot = getGitRoot(project)
        val remoteBranch = getRemoteBranch(project)

        if (gitRoot == null || remoteBranch == null) {
            return emptyList()
        }

        val processBuilder = ProcessBuilder("git", "diff", "--numstat", remoteBranch)
        processBuilder.directory(gitRoot.let { java.io.File(it) })
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val diffFiles = mutableListOf<DiffFile>()

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val parts = line!!.split("\t")
            if (parts.size == 3) {
                val deletedLines = parts[0].toIntOrNull()
                val modifiedLines = parts[1].toIntOrNull()
                val fileName = parts[2]

                if (deletedLines != null && modifiedLines != null) {
                    diffFiles.add(DiffFile(fileName, deletedLines, modifiedLines))
                }
            }
        }

        process.waitFor()
        reader.close()

        return diffFiles
    }

    private fun getGitRoot(project: Project): String? {
        val processBuilder = ProcessBuilder("git", "rev-parse", "--show-toplevel")
        processBuilder.directory(project.basePath?.let { java.io.File(it) })
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val gitRoot = reader.readLine()?.trim()

        process.waitFor()
        reader.close()

        return gitRoot
    }

    private fun getRemoteBranch(project: Project): String? {
        val processBuilder = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD@{u}")
        processBuilder.directory(project.basePath?.let { java.io.File(it) })
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val remoteBranch = reader.readLine()?.trim()

        process.waitFor()
        reader.close()

        return remoteBranch
    }
}
