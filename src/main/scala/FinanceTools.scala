class FinanceTools(
                    startKapital: Double,
                    zinsSatzProJahr: Double,
                    laufzeitInMonaten: Int,
                    steuerSatz: Double,
                    monatlicheEinzahlung: Double,
                    sonderZahlungen: Map[Int, Double]
                  ) {

  def berechneMonatlichenFinanzplan(): List[MonatsStatus] = {
    val zinssatzProMonat = zinsSatzProJahr / 12

    (1 to laufzeitInMonaten).foldLeft((startKapital, List.empty[MonatsStatus])) {
      case ((aktuellesKapital, statusListe), monat) =>
        val sonderzahlung = sonderZahlungen.getOrElse(monat, 0.0)

        val bruttoZinsen = aktuellesKapital * zinssatzProMonat
        val steuer = bruttoZinsen * steuerSatz
        val nettoZinsen = bruttoZinsen - steuer

        val endKapital = aktuellesKapital + nettoZinsen + monatlicheEinzahlung + sonderzahlung

        val status = MonatsStatus(
          monat,
          startKapital = aktuellesKapital,
          bruttoZinsen = bruttoZinsen,
          steuer = steuer,
          nettoZinsen = nettoZinsen,
          einzahlung = monatlicheEinzahlung,
          sonderzahlung = sonderzahlung,
          endKapital = endKapital
        )

        (endKapital, statusListe :+ status)
    }._2
  }
}
