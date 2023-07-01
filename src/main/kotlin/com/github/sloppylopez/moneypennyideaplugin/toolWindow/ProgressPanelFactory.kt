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
        val maxProgress = 100
        val delay = 50
        val progressBar = JProgressBar(min, max)
        progressBar.value = initial
        progressBar.isStringPainted = true
        progressBar.string = "Waiting..."
        setTimer(delay, progressBar, maxProgress)
        return progressBar
    }

    private fun setTimer(delay: Int, progressBar: JProgressBar, maxProgress: Int) {
        timer = Timer(delay, object : ActionListener {
            var progress = 0

            override fun actionPerformed(e: ActionEvent?) {
                progress++
                progressBar.value = progress
                if (progress >= maxProgress) {
                    timer?.stop()
                    val container = progressBar.parent
                    container?.remove(progressBar)
                    container?.revalidate()
                    container?.repaint()
                }
            }
        })
        timer?.start()
    }
}
