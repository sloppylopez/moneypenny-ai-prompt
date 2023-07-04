<!DOCTYPE html>
<html>
<head>
  <title>MoneyPenny AI Prompt</title>
  <style>
    .panel {
      display: flex;
      align-items: center;
    }

    .panel img {
      width: 300px;
      height: 200px;
      margin-right: 20px;
    }

    .panel .content {
      flex-grow: 1;
    }

    .panel h1 {
      font-size: 24px;
      margin-top: 0;
    }

    .panel p {
      font-size: 16px;
    }
  </style>
</head>
<body>
  <div class="panel">
    <img src="src/main/resources/images/moneypenny4.jpg" alt="Prompt">
    <div class="content">
      <h1>MoneyPenny AI Prompt</h1>
      <p>
        Welcome to MoneyPenny AI Prompt, the powerful Kotlin IntelliJ IDEA plugin that revolutionizes
        code refactoring! With MoneyPenny AI Prompt, you can easily improve, refactor, and test your
        codebase with just a few clicks. Seamlessly integrated with IntelliJ IDEA, this plugin provides
        a user-friendly interface to simplify your development workflow.
      </p>
    </div>
  </div>
</body>
</html>



### Refactor Your Codebase with Ease at Full Speed!

![Prompt](src/main/resources/images/MoneyPenny_ToolWindow_Prompt.png)

![Build](https://github.com/sloppylopez/moneypenny-idea-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

## Template ToDo list

- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml)
  and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review
  the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate)
  for the first time.
- [ ] Set the `PLUGIN_ID` in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate)
  related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set
  the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified
  about releases containing new features and fixes.

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be
extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections.
<!-- Plugin description end -->

## MoneyPenny AI Prompt - Refactor Your Codebase with Ease

MoneyPenny AI Prompt is a powerful IntelliJ IDEA plugin designed to supercharge your code refactoring process. With this plugin, you can easily refactor any codebase by simply dragging and dropping files into the tool window text area prompt. MoneyPenny AI Prompt leverages the capabilities of ChatGPT to parallelize all requests, ensuring fast and efficient code transformations.

## Features

- Drag and drop files to the tool window text area prompt for quick and easy refactoring.
- Parallelize all requests to ChatGPT for optimal performance and speed.
- Seamlessly integrate with IntelliJ IDEA for a smooth and intuitive user experience.
- Choose files and select the desired operation to apply, such as correcting syntax errors, improving code style, adding unit tests, or performing end-to-end testing.
- Click "Run" to initiate the code refactoring process.
- Utilize IntelliJ IDEA's file comparison feature to check the differences and verify the changes made by MoneyPenny AI Prompt.

## Installation

To install MoneyPenny AI Prompt, follow these simple steps:

- Using IDE built-in plugin system:
  1. Go to **Settings/Preferences** > **Plugins** > **Marketplace**.
  2. Search for "MoneyPenny AI Prompt".
  3. Click **Install Plugin**.

- Manual installation:
  1. Download the [latest release](https://github.com/sloppylopez/moneypenny-idea-plugin/releases/latest) of MoneyPenny AI Prompt.
  2. In IntelliJ IDEA, go to **Settings/Preferences** > **Plugins** > ⚙️ > **Install plugin from disk...**.
  3. Select the downloaded JAR file of MoneyPenny AI Prompt and click **OK**.

---

Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template

## Debugging the Plugin

If your IntelliJ IDEA plugin doesn't have a main class or an entry point that you can directly run and debug, you can still debug it by attaching the debugger to the running IntelliJ IDEA instance. Here's how you can do it:

1. Build and package your plugin into a JAR or an IntelliJ IDEA plugin format (such as a ZIP or JAR with specific plugin structure).
2. Install or enable your plugin in IntelliJ IDEA. You can do this by going to "File" -> "Settings" -> "Plugins" and selecting "Install Plugin from Disk" or "Enable" if your plugin is already installed.
3. Run the IntelliJ IDEA instance that has your plugin enabled.
4. In your IntelliJ IDEA instance, go to "Run" -> "Attach to Process" to open the "Attach to Process" dialog.
5. In the "Attach to Process" dialog, select the IntelliJ IDEA process that you want to attach the debugger to. Make sure you choose the correct process if multiple instances are running.
6. Click the "OK" button to attach the debugger to the selected process.
7. IntelliJ IDEA will connect the debugger to the running instance, and you can now set breakpoints in your plugin code.
8. Trigger the functionality of your plugin within the running IntelliJ IDEA instance to hit the breakpoints and start debugging.
9. Use the debugging features provided by IntelliJ IDEA, such as stepping through the code, inspecting variables, and evaluating expressions, to analyze and debug your plugin.
10. By attaching the debugger to the running IntelliJ IDEA process, you can debug your plugin code even if it doesn't have a specific main class or entry point.

## Useful Links

Here are some useful links to help you make the most of MoneyPenny AI Prompt and IntelliJ IDEA:

- [Syntax Highlighter and Color Settings Page](https://plugins.jetbrains.com/docs/intellij/syntax-highlighter-and-color-settings-page.html#define-a-color-settings-page)
- [Lexer and Parser Definition](https://plugins.jetbrains.com/docs/intellij/lexer-and-parser-definition.html#define-a-parser)
- [Controlling Highlighting](https://plugins.jetbrains.com/docs/intellij/controlling-highlighting.html)
- [Java Swing Components](https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html)
- [Java Swing Tabbed Pane](https://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html)
- [Java Swing Examples](https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#TabbedPaneDemo)
- [Debugging Your IntelliJ IDEA Instance](https://medium.com/agorapulse-stories/how-to-debug-your-own-intellij-idea-instance-7d7df185a48d)
- [JavaFX Layout Tutorial](https://docs.oracle.com/javase/8/javafx/layout-tutorial/index.html)
- [NetBeans Matisse GUI Builder](https://netbeans.apache.org/kb/docs/matisse.html)
- [Debugging Your IntelliJ IDEA Plugin](https://www.youtube.com/watch?v=YSpqHOwYrk4)

## Disclaimer

**Disclaimer: This plugin, MoneyPenny AI Prompt, is an open-source project created for educational and informational purposes only.**

MoneyPenny AI Prompt does not promote, encourage, or support any form of copyright infringement or unauthorized use of intellectual property. The plugin should not be used to modify or refactor code without proper authorization or ownership rights.

The developers of MoneyPenny AI Prompt are not responsible for any misuse or illegal activities conducted using the plugin. Users are solely responsible for ensuring that they have the necessary rights and permissions to refactor or modify code.

Please use this plugin responsibly and in compliance with applicable laws and regulations. The developers disclaim any liability for any damages or consequences resulting from the use of MoneyPenny AI Prompt.

---

**Note: This disclaimer section is an essential part of the MoneyPenny AI Prompt README and must not be removed or modified.**

## Current State Of Affairs

Please note that MoneyPenny AI Prompt is currently in development and is in an early stage. As of now, only the graphical user interface (GUI) functionality is implemented. While the GUI provides a seamless and intuitive experience for selecting files, choosing operations, and running refactorings, other advanced features are still under development.

The core functionality of automatically refactoring, correcting, and improving code, as well as unit testing and E2E testing capabilities, are planned for future updates. We are actively working on adding these features to make MoneyPenny AI Prompt a comprehensive and powerful tool for codebase optimization.

We appreciate your understanding and patience as we continue to enhance the plugin and bring more functionality to it. Your feedback and suggestions are valuable to us as we strive to make MoneyPenny AI Prompt the ultimate code refactoring solution.

Stay tuned for updates and new releases as we progress towards a more feature-rich version of MoneyPenny AI Prompt!

