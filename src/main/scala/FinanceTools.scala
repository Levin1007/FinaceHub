class FinanceTools(startKapital: Double, zinsSatz: Double, duration: Int) {

  def calculateMonthlyCapitalsSimpleInterest(): List[Double] = {
    val zinsenProMonat = startKapital * zinsSatz * 30 / 36000
    (1 to duration).map(month => startKapital + zinsenProMonat * month).toList
  }
}
