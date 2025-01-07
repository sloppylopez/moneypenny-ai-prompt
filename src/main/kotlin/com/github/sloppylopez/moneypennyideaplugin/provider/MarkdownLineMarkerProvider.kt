package com.github.sloppylopez.moneypennyideaplugin.provider

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import org.intellij.markdown.MarkdownTokenTypes
import javax.swing.Icon

internal class MarkdownLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        println("Processing element: ${element.text}")
        val node = element.node
        val icon = loadIcon("icons/ic_linemarkerprovider.svg")

        return when (node.elementType) {
            MarkdownTokenTypes.URL,
            MarkdownTokenTypes.AUTOLINK -> {
                println("Adding line marker to: ${element.text}")
                createLineMarkerInfo(element, icon)
            }
            else -> null
        }
    }

    private fun createLineMarkerInfo(element: PsiElement, icon: Icon): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            element,
            element.textRange,
            icon,
            { "Go to link" }, // Tooltip text for the icon
            null,
            GutterIconRenderer.Alignment.CENTER,
            { "Markdown Inline Link" } // Accessible name for the icon
        )
    }

    private fun loadIcon(path: String): Icon {
        return IconLoader.getIcon(path, this::class.java.classLoader)
    }
}
