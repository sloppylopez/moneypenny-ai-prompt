package com.github.sloppylopez.moneypennyideaplugin.provider

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.intellij.plugins.markdown.lang.MarkdownElementTypes

internal class MarkdownLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {

        val node = element.node
        val tokenSet = TokenSet.create(MarkdownElementTypes.INLINE_LINK)
        val classLoader = Thread.currentThread().contextClassLoader
        val icon = IconLoader.getIcon(
            "C:\\Users\\sergi\\PycharmProjects2\\moneypenny-idea-plugin\\src\\main\\resources\\icons\\ic_linemarkerprovider.svg",
            classLoader
        )

        if (tokenSet.contains(node.elementType))
            return LineMarkerInfo(
                element,
                element.textRange,
                icon,
                null,   
                null,
                GutterIconRenderer.Alignment.CENTER
            )

        return null
    }
}
