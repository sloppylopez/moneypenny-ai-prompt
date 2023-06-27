# moneypenny-idea-plugin

![Build](https://github.com/sloppylopez/moneypenny-idea-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `PLUGIN_ID` in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "moneypenny-idea-plugin"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/sloppylopez/moneypenny-idea-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation

## Debugging the plugin

    If your IntelliJ IDEA plugin doesn't have a main class or an entry point that you can directly run and debug, you can still debug it by attaching the debugger to the running IntelliJ IDEA instance. Here's how you can do it:
    
    Build and package your plugin into a JAR or an IntelliJ IDEA plugin format (such as a ZIP or JAR with specific plugin structure).
    Install or enable your plugin in IntelliJ IDEA. You can do this by going to "File" -> "Settings" -> "Plugins" and selecting "Install Plugin from Disk" or "Enable" if your plugin is already installed.
    Run the IntelliJ IDEA instance that has your plugin enabled.
    In your IntelliJ IDEA instance, go to "Run" -> "Attach to Process" to open the "Attach to Process" dialog.
    In the "Attach to Process" dialog, select the IntelliJ IDEA process that you want to attach the debugger to. Make sure you choose the correct process if multiple instances are running.
    Click the "OK" button to attach the debugger to the selected process.
    IntelliJ IDEA will connect the debugger to the running instance, and you can now set breakpoints in your plugin code.
    Trigger the functionality of your plugin within the running IntelliJ IDEA instance to hit the breakpoints and start debugging.
    Use the debugging features provided by IntelliJ IDEA, such as stepping through the code, inspecting variables, and evaluating expressions, to analyze and debug your plugin.
    By attaching the debugger to the running IntelliJ IDEA process, you can debug your plugin code even if it doesn't have a specific main class or entry point.

https://plugins.jetbrains.com/docs/intellij/syntax-highlighter-and-color-settings-page.html#define-a-color-settings-page

  https://plugins.jetbrains.com/docs/intellij/lexer-and-parser-definition.html#define-a-parser

https://plugins.jetbrains.com/docs/intellij/controlling-highlighting.html