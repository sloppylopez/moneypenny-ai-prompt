package com.github.sloppylopez.moneypennyideaplugin.services

import com.intellij.openapi.components.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service(Service.Level.PROJECT)
class GitService {
    fun getShortSha(filePath: String): String {
        val processBuilder = ProcessBuilder("git", "log", "-n", "1", "--pretty=format:%h", "--", filePath)
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val shortSHA = reader.readLine().trim()
        process.waitFor()
        return shortSHA
    }
}
