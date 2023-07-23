package com.github.sloppylopez.moneypennyideaplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Service(Service.Level.PROJECT)
class GitService {
    fun getShortSha(filePath: String?): String? {
        var shortSHA: String? = null
        try {
            if (filePath.isNullOrEmpty()) {
                return null
            }
            val file = File(filePath)
            val directory = file.parentFile

            val processBuilder = ProcessBuilder("git", "log", "-n", "1", "--pretty=format:%h", "--", filePath)
            processBuilder.directory(directory)
            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            shortSHA = reader.readLine()?.trim()

            process.waitFor()
        } catch (e: Exception) {
            thisLogger().warn(e)
        }
        return shortSHA
    }
}
