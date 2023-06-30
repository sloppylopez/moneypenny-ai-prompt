package com.github.sloppylopez.moneypennyideaplugin.highlighters

import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerPosition
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType

class MyLexerAdapter : Lexer() {
    private var text: CharSequence = ""
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var currentState: Int = 0

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.text = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.currentState = initialState
    }

    override fun getState(): Int {
        return currentState
    }

    override fun getTokenType(): IElementType? {
        if (startOffset >= endOffset) {
            return null
        }

        val char = text[startOffset]
        return when (char) {
            in 'a'..'z', in 'A'..'Z' -> MyTokenTypes.IDENTIFIER
            '"' -> MyTokenTypes.STRING
            else -> null
        }
    }

    override fun getTokenStart(): Int {
        return startOffset
    }

    override fun getTokenEnd(): Int {
        return startOffset + 1
    }

    override fun advance() {
        startOffset++
    }

    override fun getCurrentPosition(): LexerPosition {
        TODO("Not yet implemented")
    }

    override fun restore(position: LexerPosition) {
        startOffset = position.offset
        currentState = position.state
    }

    override fun getBufferSequence(): CharSequence {
        return text
    }

    override fun getBufferEnd(): Int {
        return endOffset
    }
}

