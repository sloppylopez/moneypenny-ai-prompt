import com.github.sloppylopez.moneypennyideaplugin.highlighters.MyLexerAdapter
import com.intellij.codeHighlighting.*
import com.intellij.lang.Language
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.*
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType

class KotlinCodeHighlighter : SyntaxHighlighter {
    override fun getHighlightingLexer(): Lexer {
        return MyLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            MyTokenTypes.IDENTIFIER -> arrayOf(MyHighlighterColors.IDENTIFIER)
            MyTokenTypes.STRING -> arrayOf(MyHighlighterColors.STRING)
            else -> emptyArray()
        }
    }
}

object MyTokenTypes {
    val IDENTIFIER: IElementType = IElementType("MY_IDENTIFIER", Language.ANY)
    val STRING: IElementType = IElementType("MY_STRING", Language.ANY)
}

object MyHighlighterColors {
    val IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MY_IDENTIFIER")
    val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MY_STRING")
}
