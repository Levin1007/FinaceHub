import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.chart.ui.ApplicationFrame
import org.jfree.data.category.DefaultCategoryDataset

object ChartExample {
  def showZinsChart(monatsDaten: List[(Int, Double)]): Unit = {
    val dataset = new DefaultCategoryDataset()

    monatsDaten.foreach { case (monat, kapital) =>
      dataset.addValue(kapital, "Kapital", monat.toString)
    }

    val chart = ChartFactory.createLineChart(
      "Kapitalentwicklung",
      "Monat",
      "Kapital (€)",
      dataset
    )

    val frame = new ApplicationFrame("Kapitalentwicklung")
    frame.setContentPane(new ChartPanel(chart))
    frame.pack()
    frame.setVisible(true)
  }
}
