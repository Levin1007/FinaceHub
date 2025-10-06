package model

object FinanceCalculator {
  
  /**
   * Berechnet monatlichen Finanzplan - Pure Function
   */
  def berechneMonatlichenFinanzplan(config: FinanzplanConfig): List[MonatsStatus] = {
    val zinssatzProMonat = config.zinsSatzProJahr / 12

    (1 to config.laufzeitInMonaten).foldLeft((config.startKapital, List.empty[MonatsStatus])) {
      case ((aktuellesKapital, statusListe), monat) =>
        val status = berechneMonat(
          monat,
          aktuellesKapital,
          zinssatzProMonat,
          config.steuerSatz,
          config.monatlicheEinzahlung,
          config.sonderZahlungen.getOrElse(monat, 0.0)
        )
        (status.endKapital, statusListe :+ status)
    }._2
  }

  /**
   * Berechnet einen einzelnen Monat - Pure Function
   */
  private def berechneMonat(
    monat: Int,
    startKapital: Double,
    zinssatzProMonat: Double,
    steuerSatz: Double,
    monatlicheEinzahlung: Double,
    sonderzahlung: Double
  ): MonatsStatus = {
    val bruttoZinsen = startKapital * zinssatzProMonat
    val steuer = bruttoZinsen * steuerSatz
    val nettoZinsen = bruttoZinsen - steuer
    val endKapital = startKapital + nettoZinsen + monatlicheEinzahlung + sonderzahlung

    MonatsStatus(
      monat = monat,
      startKapital = startKapital,
      bruttoZinsen = bruttoZinsen,
      steuer = steuer,
      nettoZinsen = nettoZinsen,
      einzahlung = monatlicheEinzahlung,
      sonderzahlung = sonderzahlung,
      endKapital = endKapital
    )
  }

  /**
   * Berechnet Endbetrag - Pure Function
   */
  def berechneEndkapital(config: FinanzplanConfig): Double = {
    berechneMonatlichenFinanzplan(config).lastOption.map(_.endKapital).getOrElse(config.startKapital)
  }

  /**
   * Berechnet Gesamtzinsen - Pure Function
   */
  def berechneGesamtZinsen(config: FinanzplanConfig): Double = {
    val plan = berechneMonatlichenFinanzplan(config)
    plan.map(_.nettoZinsen).sum
  }
}