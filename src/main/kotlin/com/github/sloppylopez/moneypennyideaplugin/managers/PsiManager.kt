    package com.github.sloppylopez.moneypennyideaplugin.managers

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

@Service(Service.Level.PROJECT)
class PsiManager(private val project: Project) {
    //    private fun createNewLinkElement(project: AllIcons.Welcome.Project, linkText: String, linkDestination: String): PsiElement? {
//        val markdownText = "[$linkText]($linkDestination)"
//        val newFile = MarkdownPsiElementFactory.createFile(project, markdownText)
//        val newParentLinkElement = findChildElement(newFile, MarkdownTokenTypeSets.LINKS)
//        return newParentLinkElement
//    }
    fun getListOfProjectVirtualFilesByName(
        caseSensitivity: Boolean = true,
        fileName: String = "Lambdas.kt"
    ): MutableCollection<VirtualFile> {
        return FilenameIndex.getVirtualFilesByName(fileName, caseSensitivity, GlobalSearchScope.projectScope(project))
    }

    fun getListOfProjectVirtualFilesByExt(
        caseSensitivity: Boolean = true,
        extName: String = "kt"
    ): MutableCollection<VirtualFile> {
        return FilenameIndex.getAllFilesByExt(project, extName, GlobalSearchScope.projectScope(project))
    }

    fun getListOfAllProjectVFiles(): MutableCollection<VirtualFile> {
        val collection = mutableListOf<VirtualFile>()
        ProjectFileIndex.getInstance(project).iterateContent {
            collection += it
            // Return true to process all the files (no early escape).
            true
        }
        return collection
    }

    /**
     * VFS listeners are application level and will receive events for changes happening in all the projects opened by the
     * user. You may need to filter out events that aren’t relevant to your task (e.g., via
     * `ProjectFileIndex#isInContent()`). A listener for VFS events, invoked inside write-action.
     */
    private fun attachListenerForProjectVFileChanges(): Unit {
        println("MyPlugin: attachListenerForProjectFileChanges()")

        val connection = project.messageBus.connect(/*parentDisposable=*/ project)
        connection.subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) = doAfter(events, project)
            })
    }

    private fun handleEvent(event: VFileEvent) {
        when (event) {
            is VFilePropertyChangeEvent -> {
                println("VFile property change event: $event")
            }

            is VFileContentChangeEvent -> {
                println("VFile content change event: $event")
            }
        }
    }

    fun doAfter(events: List<VFileEvent>, project: Project) {
        println("VFS_CHANGES: #events: ${events.size}")
        val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
        events.withIndex().forEach { (index, event) ->
            println("$index. VFile event: $event")
            // Filter out file events that are not in the project's content.
            events
                .filter { it.file != null && projectFileIndex.isInContent(it.file!!) }
                .forEach { handleEvent(it) }
        }
    }
}