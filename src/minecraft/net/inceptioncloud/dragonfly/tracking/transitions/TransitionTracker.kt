package net.inceptioncloud.dragonfly.tracking.transitions

import org.jfree.chart.*
import org.jfree.chart.block.BlockBorder
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.title.TextTitle
import org.jfree.data.xy.*
import java.awt.*
import java.io.File
import java.io.PrintWriter
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import javax.swing.BorderFactory
import javax.swing.JFrame

/**
 * Tracks the amount of transition by origin and generates a .csv file if needed.
 */
object TransitionTracker
{
    /**
     * List that contains all data.
     */
    val data = mutableListOf<TrackingData>()

    /**
     * Frame in which the live transition tracking chart is displayed.
     */
    val frame = JFrame()

    /**
     * Panel that contains the chart.
     */
    private var chartPanel: ChartPanel? = null

    /**
     * Generates a .csv file with the tracked data.
     */
    fun generateFile()
    {
        val folder = File("transition-tracker")
        val file = File("transition-tracker/${SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(System.currentTimeMillis())}.csv")

        folder.mkdir()
        file.createNewFile()

        val printWriter = PrintWriter(file)
        val allOrigins = data.map { it.groupedAmounts }.flatMap { it.keys }.distinct()
        var header = "trackingPoint,total"

        allOrigins.forEach { header += ",$it" }
        printWriter.println(header)

        data.forEach {
            var line = "${it.trackingPoint},${it.total()}"
            allOrigins.forEach { origin -> line += ",${it.groupedAmounts.getOrDefault(origin, 0)}" }

            printWriter.println(line)
        }

        printWriter.flush()
        printWriter.close()
    }

    /**
     * Builds the UI for the live transition tracking.
     */
    fun initUI()
    {
        val dataset = loadDataset()
        val chart = buildChart(dataset)

        chartPanel = ChartPanel(chart)
        chartPanel?.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        chartPanel?.background = Color.white

        frame.add(chartPanel)
        frame.pack()
        frame.setLocationRelativeTo(null)

        frame.isResizable = false
        frame.title = "Transition Tracker"
        frame.iconImage = ImageIO.read(File("dragonfly/assets/img/icon_32x.png"))
    }

    fun toggle ()
    {
        frame.isVisible = !frame.isVisible
    }

    /**
     * Updates the UI when receiving new data.
     */
    fun updateUI()
    {
        if (!frame.isVisible)
            return

        val dataset = loadDataset()
        val chart = buildChart(dataset)

        chartPanel?.chart = chart
    }

    /**
     * Loads the available data into a dataset for the chart.
     */
    private fun loadDataset(): XYDataset
    {
        val dataset = XYSeriesCollection()
        val total = XYSeries("total")
        val allOrigins = data.map { it.groupedAmounts }.flatMap { it.keys }.distinct()

        data.forEach { total.add(it.trackingPoint, it.total()) }
        dataset.addSeries(total)

        allOrigins.forEach {
            val series = XYSeries(it)

            data.forEach { data -> series.add(data.trackingPoint, data.groupedAmounts.getOrDefault(it, 0)) }
            dataset.addSeries(series)
        }

        return dataset
    }

    /**
     * Finally builds the chart with the dataset.
     */
    private fun buildChart(dataset: XYDataset): JFreeChart
    {
        val chart = ChartFactory.createXYLineChart(
                "Transitions by origin during playtime",
                "Tracking Point (^= 5 seconds)",
                "Amount of Transitions",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        )

        val plot = chart.xyPlot
        val renderer = XYLineAndShapeRenderer()
        renderer.defaultLegendTextFont = Font("Product Sans Light", Font.PLAIN, 15)
        renderer.defaultShapesVisible = false
        renderer.setSeriesStroke(0, BasicStroke(3F))

        for (i in 1 until dataset.seriesCount)
            renderer.setSeriesStroke(i, BasicStroke(2F))

        plot.renderer = renderer
        plot.backgroundPaint = Color.white
        plot.isRangeGridlinesVisible = true
        plot.rangeGridlinePaint = Color.BLACK
        plot.isDomainGridlinesVisible = true
        plot.domainGridlinePaint = Color.BLACK

        chart.legend.frame = BlockBorder.NONE
        chart.title = TextTitle("Transitions by origin during playtime", Font("Product Sans Medium", Font.PLAIN, 20))
        chart.antiAlias = true

        return chart
    }
}

/**
 * Represents data that has been tracked over the time.
 */
class TrackingData(val trackingPoint: Long, val groupedAmounts: Map<String, Int>)
{
    /**
     * Quick function to access the total amount of transitions.
     */
    fun total(): Int = groupedAmounts.values.sum()
}