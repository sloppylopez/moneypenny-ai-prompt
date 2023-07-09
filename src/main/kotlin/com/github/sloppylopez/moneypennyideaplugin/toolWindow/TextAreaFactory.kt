import com.github.sloppylopez.moneypennyideaplugin.components.MoneyPennyTextArea
import com.intellij.notification.*
import com.intellij.openapi.components.Service
import com.intellij.ui.JBColor
import javax.swing.BorderFactory
import javax.swing.JTextArea

@Service(Service.Level.PROJECT)
class TextAreaFactory {
    fun createTextArea(text: String, rows: Int, columns: Int): JTextArea {
        return MoneyPennyTextArea().apply {
            this.text = text
            lineWrap = true
            wrapStyleWord = true
            this.rows = rows
            this.columns = columns
            border = BorderFactory.createLineBorder(JBColor.LIGHT_GRAY) // Set red border
        }
    }
}
