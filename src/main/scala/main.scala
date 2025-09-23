import scala.io.StdIn

@main
def main(): Unit = {
  print("Gebe dein Startkapital an: ")
  val startKapital = StdIn.readLine().toDouble
  print("Gebe deinen Zinssatz an (in %): ")
  val zinsSatz = StdIn.readLine().toDouble
  print("Gebe Laufzeit in Monaten an: ")
  val duration = StdIn.readLine().toInt

  val finance = new FinanceTools(startKapital, zinsSatz, duration)
  val capitals = finance.calculateMonthlyCapitalsSimpleInterest()

  println(f"Startkapital: $startKapital%.2f")
  capitals.zipWithIndex.foreach { case (capital, month) =>
    println(f"Monat ${month + 1}: Kapital = $capital%.2f")
  }
}
