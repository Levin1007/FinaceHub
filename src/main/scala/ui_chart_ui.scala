package ui

import model.MonatsStatus
import org.jfree.chart.{ChartFactory, ChartPanel}
import org.jfree.chart.ui.ApplicationFrame
import org.jfree.data.category.DefaultCategoryDataset

object ChartUI {
  
  def showChart(plan: List[MonatsStatus]): Unit = {
    val dataset = new DefaultCategoryDataset()

    plan.foreach { status =>
      dataset.addValue(status.endKapital, "Kapital", status.monat.toString)
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