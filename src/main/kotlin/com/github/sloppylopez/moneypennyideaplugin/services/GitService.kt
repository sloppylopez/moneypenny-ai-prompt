package com.github.sloppylopez.moneypennyideaplugin.services

import java.io.BufferedReader
import java.io.InputStreamReader

class GitService {
    fun getShortSha(filePath: String): String {
        val processBuilder = ProcessBuilder("git", "rev-parse", "--short", "HEAD:$filePath")
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val sha = reader.readLine().trim()
        process.waitFor()
        return sha
    }
}
