import com.intellij.notification.*
import com.intellij.openapi.components.Service
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

@Service(Service.Level.PROJECT)
class ProgressPanelFactory {
    private var timer: Timer? = null

    fun getProgressBarPanel(panel: JPanel) {
        val progressBar = createProgressBar(0, 100, 0)
        panel.add(progressBar)
    }

    private fun createProgressBar(min: Int, max: Int, initial: Int): JProgressBar {
        val MAX_PROGRESS = 100
        val DELAY = 50
        val progressBar = JProgressBar(min, max)
        progressBar.value = initial
        progressBar.isStringPainted = true
        progressBar.string = "Waiting..."
        timer = Timer(DELAY, object : ActionListener {
            var progress = 0

            override fun actionPerformed(e: ActionEvent?) {
                progress++
                progressBar.value = progress
                if (progress >= MAX_PROGRESS) {
                    timer?.stop()
                    progressBar.isVisible = false
                }
            }
        })
        timer?.start()
        return progressBar
    }
}
