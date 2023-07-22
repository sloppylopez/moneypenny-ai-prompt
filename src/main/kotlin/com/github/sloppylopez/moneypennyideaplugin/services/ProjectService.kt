package com.github.sloppylopez.moneypennyideaplugin.services

import com.github.sloppylopez.moneypennyideaplugin.Bundle
import com.github.sloppylopez.moneypennyideaplugin.actions.CopyPromptAction
import com.github.sloppylopez.moneypennyideaplugin.actions.RunAllPromptAction
import com.github.sloppylopez.moneypennyideaplugin.actions.RunPromptAction
import com.github.sloppylopez.moneypennyideaplugin.actions.SendToPromptFileFolderTreeAction
import com.github.sloppylopez.moneypennyideaplugin.client.ChatGptMessage
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.downerTabName
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.tabNameToContentPromptTextMap
import com.github.sloppylopez.moneypennyideaplugin.global.GlobalData.tabNameToFilePathMap
import com.github.sloppylopez.moneypennyideaplugin.helper.ToolWindowHelper.Companion.addTabbedPaneToToolWindow
import com.google.gson.Gson
import com.intellij.icons.AllIcons
import com.intellij.ide.CommonActionsManager
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.keymap.impl.ui.ActionsTree
import com.intellij.openapi.keymap.impl.ui.KeymapPanel.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiFile
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import javax.swing.*


@Service(Service.Level.PROJECT)
class ProjectService {
    private val CURRENT_PROCESS_PROMPT = Key.create<String>("Current Processed Prompt")
    private val pluginId = "MoneyPenny AI"

    fun getFileContents(filePath: String?) = filePath?.let {
        try {
            File(it).readText()
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    } as String

    fun getRandomNumber() = (1..100).random()

    fun fileToVirtualFile(file: File?): VirtualFile? {
        val localFileSystem = LocalFileSystem.getInstance()
        return file?.let { localFileSystem.findFileByIoFile(it) }
    }

    fun virtualFileToFile(virtualFile: VirtualFile?): File? {
        return virtualFile?.let { File(it.path) }
    }

    fun psiFileToFile(file: PsiFile?): File? {
        return file?.virtualFile?.let { virtualFile ->
            File(virtualFile.path)
        }
    }

    fun getText(file: File?, message: String?): String {
        if (file != null && message == null) {
            val reader = BufferedReader(FileReader(file))
            reader.use {
                val contents = StringBuilder()
                var line: String? = reader.readLine()
                while (line != null) {
                    contents.append(line).append(System.lineSeparator())
                    line = reader.readLine()
                }
                return contents.toString()
            }
        } else if (message != null) {
            return message
        }
        return ""
    }

    fun showDialog(
        message: String,
        title: String,
        buttons: Array<String>? = emptyArray<String>(),
        defaultOptionIndex: Int? = 0,
        icon: Icon? = AllIcons.Icons.Ide.NextStep
    ) {
        Messages.showDialog(
            message,
            title,
            buttons!!,
            defaultOptionIndex!!,
            icon
        )
    }

    fun readFile(fileList: List<*>, i: Int): File? {
        try {
            if (i < fileList.size && fileList.isNotEmpty() &&
                null != fileList[i]
            ) {
                val file = fileList[i] as File
                thisLogger().info(Bundle.message("projectService", "File $file"))
                return file
            } else {
                thisLogger().info(Bundle.message("projectService", "File is null"))
            }
        } catch (e: Exception) {
            thisLogger().error(Bundle.message("projectService", e))
        }
        return null
    }

    fun showNotification(
        @NlsSafe title: String,
        @NlsSafe message: String,
        notificationType: NotificationType?,
        customIcon: Icon? = null,
        imageName: String? = null
    ) {
        val notification = Notification(
            "MoneyPennyAI",
            title,
            message,
            notificationType ?: NotificationType.INFORMATION
        )
        notification.setIcon(customIcon ?: createCustomIcon(imageName ?: "/images/MoneyPenny-Icon_13x13.jpg"))
        Notifications.Bus.notify(notification, this.getProject()!!)
    }

    private fun createCustomIcon(imageName: String): Icon {
        // Code to generate a random image and create an icon from it
        return ImageIcon(SendToPromptFileFolderTreeAction::class.java.getResource(imageName))
    }

    fun showMessage(
        message: String, title: String
    ) {
        Messages.showInfoMessage(
            message, title,
        )
    }

    fun highlightTextInEditor(contentPromptText: String) {
        val editor = getCurrentEditor()
        editor?.let {
            val document = editor.document
            val normalizedContentPromptText = contentPromptText.replace("\r\n", "\n")
            val textOffset = document.text.indexOf(normalizedContentPromptText)
            if (textOffset != -1) {
                editor.caretModel.moveToOffset(textOffset)
                editor.selectionModel.setSelection(textOffset, textOffset + normalizedContentPromptText.length)
            }
        }
    }

    private fun getCurrentEditor(): Editor? {
        val project = this.getProject()!!
        val file = FileEditorManager.getInstance(project)?.selectedFiles?.firstOrNull()
        return file?.let { FileEditorManager.getInstance(project).selectedTextEditor }
    }

    fun modifySelectedTextInEditorByFile(
        newText: ChatGptMessage,
        file: VirtualFile,
    ) {
        try {
            val project = this.getProject() ?: return

            ApplicationManager.getApplication().invokeLater {
                val editorManager = FileEditorManager.getInstance(project)
                val editor = editorManager.openTextEditor(OpenFileDescriptor(project, file), false)
                editor?.let {
                    val document = editor.document
                    val selectionModel = editor.selectionModel
                    val startOffset = selectionModel.selectionStart
                    val endOffset = selectionModel.selectionEnd

                    if (startOffset != endOffset) {
                        ApplicationManager.getApplication().runWriteAction {
                            WriteCommandAction.runWriteCommandAction(project) {
                                document.replaceString(startOffset, endOffset, newText.content)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            thisLogger().error(Bundle.message("projectService", e))
        }
    }


    fun expandFolders(fileList: List<*>? = null): List<File> {
        if (fileList == null) {
            return emptyList()
        }
        val expandedFileList = mutableListOf<File>()
        val stack = Stack<Any>()
        stack.addAll(fileList)

        while (stack.isNotEmpty()) {
            expand(stack, expandedFileList)
        }

        return expandedFileList
    }

    private fun expand(
        stack: Stack<Any>,
        expandedFileList: MutableList<File>
    ) {
        val file = stack.pop()
        try {
            when (file) {
                is File -> {
                    if (file.isDirectory) {
                        val files = file.listFiles()
                        if (files != null) {
                            stack.addAll(files.toList())
                        }
                    } else {
                        expandedFileList.add(file)
                    }
                }

                is VirtualFile -> {
                    if (file.isDirectory) {
                        val children = file.children.toList()
                        stack.addAll(children)
                    } else {
                        expandedFileList.add(virtualFileToFile(file)!!)
                    }
                }
            }
        } catch (e: Exception) {
            thisLogger().error(e)
        }
    }

    private fun getSelectedTextFromOpenedFileInEditor(
        selectedEditor: Editor
    ): @NlsSafe String? {
        var selectedText: @NlsSafe String? = ""
        val project: Project? = selectedEditor.project
        val fileEditorManager = FileEditorManager.getInstance(project!!)
        val selectedFile = fileEditorManager.selectedFiles.firstOrNull()
        if (selectedFile != null) {
            val virtualFile = VirtualFileManager.getInstance().findFileByUrl(selectedFile.url)
            val openFileDescriptor = OpenFileDescriptor(project, virtualFile!!)
            val document = openFileDescriptor.file.let { FileDocumentManager.getInstance().getDocument(it) }
            selectedText = document?.text
        }
        return selectedText
    }

    fun setTabName(
        i: Int,
        fileList: List<*>,
        file: File?,
        tabbedPane: JBTabbedPane,
        panel: JPanel,
        contentPromptText: String?
    ) {
        if (i < fileList.size && file != null) {
            val tabName = "${getNextTabName()}) ${file.name}"
            tabbedPane.addTab(tabName, panel)
            tabNameToFilePathMap[tabName] = file.canonicalPath
            if (contentPromptText != null) {
                tabNameToContentPromptTextMap[tabName] = contentPromptText
            } else {
                tabNameToContentPromptTextMap[tabName] = file.readText()
            }
        } else {
            tabbedPane.addTab("No File", panel)
        }

        if (contentPromptText != null && file != null) {
            val tabName = "$downerTabName) ${file.name}"
            tabNameToContentPromptTextMap[tabName] = contentPromptText
        }
    }

    private fun getNextTabName(): String {
        return downerTabName++.toString()
    }

    fun getProject(): Project? {
        return ProjectManager.getInstance().openProjects.firstOrNull()
    }

    fun getToolWindow(): ToolWindow? {
        return ToolWindowManager.getInstance(getProject()!!).getToolWindow(pluginId)
    }

    fun sendFileToContentPrompt(
        editor: Editor?,
        file: File?,
    ) {
        try {
            editor?.let { selectedEditor ->
                var selectedTextFromEditor = selectedEditor.selectionModel.selectedText
                if (selectedTextFromEditor.isNullOrEmpty()) {
                    selectedTextFromEditor = this.getSelectedTextFromOpenedFileInEditor(selectedEditor)
                }
                if (!selectedTextFromEditor.isNullOrEmpty()) {
                    addTabbedPaneToToolWindow(
                        this.getProject()!!,
                        listOf(file),
                        selectedTextFromEditor
                    )
                }
            }
        } catch (e: Exception) {
            thisLogger().error(e.stackTraceToString())
        }
    }

    fun invokeLater(functionsToRunLater: () -> Unit) {
        SwingUtilities.invokeLater {
            ApplicationManager.getApplication().invokeLater(
                functionsToRunLater,
                ModalityState.NON_MODAL
            )
        }
    }

    fun copyToClipboard(text: String? = null) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val stringSelection = StringSelection(text)
        clipboard.setContents(stringSelection, null)
    }

    fun putUserDataInProject(fileList: List<*>) {
        this.getProject()?.putUserData(Key.create("All Processed Prompt"), fileList)
    }

    fun getUserDataFromProject(key: Key<List<*>>) {
        this.getProject()?.getUserData(key)
    }

    fun putUserDataInComponent(fileList: List<*>, content: Content) {
        val myfiles = ArrayList<String>()
        myfiles.add("hello")
        content.putUserData(CURRENT_PROCESS_PROMPT, myfiles.toString())
    }

    fun getUserDataFromComponent(key: Key<List<*>>, content: Content) {
        content.getUserData(key)
    }

    fun findNestedJBTabbedPanes(tabbedPane: JBTabbedPane): List<JBTabbedPane> {
        val nestedTabbedPanes = mutableListOf<JBTabbedPane>()

        fun findNestedTabbedPanesRecursive(container: Container) {
            for (component in container.components) {
                if (component is JBTabbedPane) {
                    nestedTabbedPanes.add(component)
                    findNestedTabbedPanesRecursive(component)
                } else if (component is Container) {
                    findNestedTabbedPanesRecursive(component)
                }
            }
        }

        findNestedTabbedPanesRecursive(tabbedPane)
        return nestedTabbedPanes
    }


    fun findJBTabbedPanes(container: Container): List<JBTabbedPane> {
        val tabbedPanes = mutableListOf<JBTabbedPane>()

        fun findTabbedPanesRecursive(component: Component) {
            if (component is JBTabbedPane) {
                tabbedPanes.add(component)
            } else if (component is Container) {
                for (child in component.components) {
                    findTabbedPanesRecursive(child)
                }
            }
        }

        findTabbedPanesRecursive(container)
        return tabbedPanes
    }


    fun getPromptsAsJson(prompts: MutableMap<String, Map<String, List<String>>>): String {
        return Gson().toJson(prompts)
    }

    fun saveDataToExtensionFolder(data: String) {
        val extensionFolder = File(PathManager.getPluginsPath(), pluginId)
        if (!extensionFolder.exists()) {
            extensionFolder.mkdir()
        }
        val dataFile = File(extensionFolder, "prompt_history.json")
        dataFile.writeText(data)
    }

    fun getPromptListByKey(prompts: MutableMap<String, Map<String, List<String>>>, key: String): List<String> {
        for (outerKey in prompts.keys) {
            val innerMap = prompts[outerKey] ?: continue
            if (innerMap.containsKey(key)) {
                return innerMap[key] ?: emptyList()
            }
        }
        return emptyList()
    }

    fun addToolBar(toolWindowContent: SimpleToolWindowPanel) {
        val actionGroup = DefaultActionGroup()
        val project = this.getProject()!!
        actionGroup.add(SendToPromptFileFolderTreeAction(project))
        actionGroup.add(RunPromptAction(project))
        actionGroup.add(RunAllPromptAction(project))
        actionGroup.add(CopyPromptAction(project))
        actionGroup.addSeparator()

        val toolBar = ActionManager.getInstance().createActionToolbar(
            "MoneyPennyAI.MainPanel",
            actionGroup,
            true // Specify that the toolbar should be vertical
        )
        toolWindowContent.toolbar = toolBar.component
    }

    fun createToolbarPanel(toolWindowContent: SimpleToolWindowPanel) {
        val myActionsTree = ActionsTree()
        var group = DefaultActionGroup()
        val toolbar = ActionManager.getInstance().createActionToolbar("MoneyPennyAI.MainPanel", group, true)
        toolbar.targetComponent = myActionsTree.tree
        val commonActionsManager = CommonActionsManager.getInstance()
        val treeExpander = createTreeExpander(myActionsTree)
        group.add(commonActionsManager.createExpandAllAction(treeExpander, myActionsTree.tree))
        group.add(commonActionsManager.createCollapseAllAction(treeExpander, myActionsTree.tree))
//        group.add(EditShortcutAction())
//        myShowOnlyConflictsButton = object : ToggleActionButton(
//            KeyMapBundle.messagePointer("keymap.show.system.conflicts"),
//            AllIcons.General.ShowWarning
//        ) {
//            override fun isSelected(e: AnActionEvent): Boolean {
//                return myShowOnlyConflicts
//            }
//
//            override fun setSelected(e: AnActionEvent, state: Boolean) {
//                myShowOnlyConflicts = state
//                myActionsTree.setBaseFilter(
//                    if (myShowOnlyConflicts) SystemShortcuts.getInstance().createKeymapConflictsActionFilter() else null
//                )
//                myActionsTree.filter(null, myQuickLists)
//                val tree: JTree = myActionsTree.getTree()
//                if (myShowOnlyConflicts) {
//                    TreeUtil.expandAll(tree)
//                } else {
//                    TreeUtil.collapseAll(tree, 0)
//                }
//            }
//        }
//        group.add(myShowOnlyConflictsButton)
        group = DefaultActionGroup()
        val actionToolbar = ActionManager.getInstance().createActionToolbar("Keymap", group, true)
        actionToolbar.targetComponent = myActionsTree.tree
        actionToolbar.setReservePlaceAutoPopupIcon(false)
        val searchToolbar = actionToolbar.component
        //TODO use alarms to cancel requests to chatGPT as
//        val alarm = Alarm()
//        myFilterComponent = object : FilterComponent("KEYMAP", 5) {
//            override fun filter() {
//                alarm.cancelAllRequests()
//                alarm.addRequest({
//                    if (!myFilterComponent.isShowing()) return@addRequest
//                    myTreeExpansionMonitor.freeze()
//                    myFilteringPanel.setShortcut(null)
//                    val filter = filter
//                    myActionsTree.filter(filter, myQuickLists)
//                    val tree: JTree = myActionsTree.getTree()
//                    TreeUtil.expandAll(tree)
//                    if (filter == null || filter.length == 0) {
//                        TreeUtil.collapseAll(tree, 0)
//                        myTreeExpansionMonitor.restore()
//                    } else {
//                        myTreeExpansionMonitor.unfreeze()
//                    }
//                }, 300)
//            }
//        }
//        myFilterComponent.reset()
//        group.add(FindByShortcutAction(searchToolbar))
//        group.add(ClearFilteringAction())
        val panel = JPanel(GridLayout(1, 2))
        panel.add(toolbar.component)
        panel.add(BorderLayoutPanel().addToRight(searchToolbar))
        toolWindowContent.toolbar = panel
    }

    fun addPanelsToGlobalData(
        nestedPanel: JPanel,
        innerPanel: JPanel,
        tabbedPane: JBTabbedPane
    ) {
        GlobalData.nestedPanel = nestedPanel
        GlobalData.innerPanel = innerPanel
        GlobalData.tabbedPane = tabbedPane
    }

    fun loadDataFromExtensionFolder(): String {
        val extensionFolder = File(PathManager.getPluginsPath(), pluginId)
        val dataFile = File(extensionFolder, "prompt_history.txt")
        return dataFile.readText()
    }

//    fun createToolBar(toolWindow: JComponent?): JComponent {
//        val actionGroup = DefaultActionGroup()
//        actionGroup.add(SendToPromptFileFolderTreeAction(this.getProject()!!))
//        actionGroup.addSeparator()
//        val actionManager: ActionManager = ActionManager.getInstance()
//        val toolbar: ActionToolbar = actionManager.createActionToolbar("MoneyPennyAI.MainPanel", actionGroup, true)
//        toolbar.targetComponent = toolWindow
//        return toolbar.component
//    }

//    JOptionPane.showMessageDialog(
//    this@SQLPluginPanel, "Can't read file '$file'", "Error",
//    JOptionPane.ERROR_MESSAGE
//    )


    ////Write a kotlin class that will do the same as this one but instead of adding buttons, it will add Actions to a ToolBar, the actions will match with the buttons so "Run", "Run All" and "Copy Prompt", assume we are using Intellij Idea SDK
}