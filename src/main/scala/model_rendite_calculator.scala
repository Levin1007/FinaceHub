package model

import scala.math.pow

/**
 * Renditerechner - Pure Functional
 */
object RenditeCalculator {

  /**
   * Hauptberechnung der Rendite
   */
  def berechneRendite(config: RenditeConfig): RenditeErgebnis = {
    val jahresVerlauf = berechneJahresVerlauf(config)
    val letzterStatus = jahresVerlauf.last
    
    val gesamtInvestition = config.anfangsKapital + 
      config.monatlicheEinzahlung.getOrElse(0.0) * 12 * config.laufzeitInJahren
    
    val gewinn = letzterStatus.endKapital - gesamtInvestition
    
    val durchschnittlicheRendite = berechneDurchschnittlicheRendite(
      config.anfangsKapital,
      letzterStatus.endKapital,
      config.laufzeitInJahren
    )
    
    val effektiveRendite = berechneEffektiveRendite(config)
    
    val breakEven = findeBreakEvenPeriode(jahresVerlauf)
    
    RenditeErgebnis(
      endkapital = letzterStatus.endKapital,
      gesamtInvestition = gesamtInvestition,
      gewinn = gewinn,
      durchschnittlicheJahresRendite = durchschnittlicheRendite,
      effektiveJahresRendite = effektiveRendite,
      breakEvenPeriode = breakEven,
      jahresVerlauf = jahresVerlauf
    )
  }

  /**
   * Berechnet den Verlauf Jahr für Jahr
   */
  private def berechneJahresVerlauf(config: RenditeConfig): List[JahresStatus] = {
    val periodenProJahr = config.verzinsungsIntervall.periodenProJahr
    val renditeProPeriode = config.renditeProJahr / periodenProJahr
    val einzahlungProPeriode = config.monatlicheEinzahlung.getOrElse(0.0)
    
    (1 to config.laufzeitInJahren).foldLeft((config.anfangsKapital, config.anfangsKapital, List.empty[JahresStatus])) {
      case ((kapital, gesamtInvestiert, statusListe), jahr) =>
        val jahresErgebnis = berechneEinJahr(
          kapital,
          einzahlungProPeriode,
          renditeProPeriode,
          periodenProJahr,
          config.zinseszins,
          gesamtInvestiert,
          jahr
        )
        
        (jahresErgebnis.endKapital, jahresErgebnis.gesamtInvestiert, statusListe :+ jahresErgebnis)
    }._3
  }

  /**
   * Berechnet ein einzelnes Jahr
   */
  private def berechneEinJahr(
    startKapital: Double,
    einzahlungProPeriode: Double,
    renditeProPeriode: Double,
    periodenProJahr: Int,
    zinseszins: Boolean,
    bisherInvestiert: Double,
    jahr: Int
  ): JahresStatus = {
    val einzahlungenImJahr = einzahlungProPeriode * periodenProJahr
    
    // Berechne Kapital am Ende des Jahres
    val endKapital = if (zinseszins) {
      // Mit Zinseszins: Jede Periode wird verzinst
      (1 to periodenProJahr).foldLeft(startKapital) { (kapital, _) =>
        val neuesKapital = kapital * (1 + renditeProPeriode) + einzahlungProPeriode
        neuesKapital
      }
    } else {
      // Ohne Zinseszins: Nur auf Anfangskapital
      val zinsen = startKapital * renditeProPeriode * periodenProJahr
      startKapital + zinsen + einzahlungenImJahr
    }
    
    val zinsen = endKapital - startKapital - einzahlungenImJahr
    val gesamtInvestiert = bisherInvestiert + einzahlungenImJahr
    val gewinn = endKapital - gesamtInvestiert
    
    JahresStatus(
      jahr = jahr,
      startKapital = startKapital,
      einzahlungen = einzahlungenImJahr,
      zinsen = zinsen,
      endKapital = endKapital,
      gesamtInvestiert = gesamtInvestiert,
      gewinnBisJetzt = gewinn
    )
  }

  /**
   * Berechnet durchschnittliche jährliche Rendite (CAGR)
   * CAGR = (Endwert / Anfangswert)^(1/Jahre) - 1
   */
  private def berechneDurchschnittlicheRendite(
    anfangsKapital: Double,
    endKapital: Double,
    jahre: Int
  ): Double = {
    if (jahre == 0 || anfangsKapital == 0) return 0.0
    pow(endKapital / anfangsKapital, 1.0 / jahre) - 1.0
  }

  /**
   * Berechnet effektive jährliche Rendite (APY)
   * Bei monatlicher Verzinsung: (1 + r/12)^12 - 1
   */
  private def berechneEffektiveRendite(config: RenditeConfig): Double = {
    val periodenProJahr = config.verzinsungsIntervall.periodenProJahr
    val renditeProPeriode = config.renditeProJahr / periodenProJahr
    
    if (config.zinseszins) {
      pow(1 + renditeProPeriode, periodenProJahr) - 1.0
    } else {
      config.renditeProJahr // Ohne Zinseszins ist effektive = nominale Rendite
    }
  }

  /**
   * Findet die Periode, in der Break-Even erreicht wird
   */
  private def findeBreakEvenPeriode(verlauf: List[JahresStatus]): Option[Int] = {
    verlauf.find(_.istBreakEven).map(_.jahr)
  }

  /**
   * Vergleicht mehrere Szenarien
   */
  def vergleicheSzenarien(szenarien: List[(String, RenditeConfig)]): List[(String, RenditeErgebnis)] = {
    szenarien.map { case (name, config) =>
      (name, berechneRendite(config))
    }
  }

  /**
   * Berechnet benötigtes Startkapital für gewünschtes Endkapital
   */
  def berechneStartkapitalFuerZiel(
    zielKapital: Double,
    laufzeitInJahren: Int,
    renditeProJahr: Double,
    monatlicheEinzahlung: Option[Double] = None
  ): Double = {
    // Vereinfachte Berechnung ohne Einzahlungen
    if (monatlicheEinzahlung.isEmpty) {
      zielKapital / pow(1 + renditeProJahr, laufzeitInJahren)
    } else {
      // Mit Einzahlungen komplexer - iterative Näherung
      val einzahlungProMonat = monatlicheEinzahlung.get
      val gesamtEinzahlungen = einzahlungProMonat * 12 * laufzeitInJahren
      
      // Grobe Schätzung
      (zielKapital - gesamtEinzahlungen) / pow(1 + renditeProJahr, laufzeitInJahren)
    }
  }
}