package model

import scala.annotation.tailrec
import scala.math.pow

/**
 * Renditerechner - Pure Functional mit Rekursion
 */
object RenditeCalculator {

  /**
   * Hauptberechnung der Rendite
   */
  def berechneRendite(config: RenditeConfig): RenditeErgebnis = {
    val jahresVerlauf = berechneJahresVerlaufRekursiv(config)
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

    val breakEven = findeBreakEvenRekursiv(jahresVerlauf)

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
   * REKURSIVE FUNKTION: Berechnet den Verlauf Jahr für Jahr
   * Tail-recursive für Effizienz
   */
  @tailrec
  private def berechneJahresVerlaufRekursiv(
                                             config: RenditeConfig,
                                             aktuellesJahr: Int = 1,
                                             aktuellesKapital: Double = 0.0,
                                             gesamtInvestiert: Double = 0.0,
                                             akkumulator: List[JahresStatus] = List.empty
                                           ): List[JahresStatus] = {
    // Initialisierung beim ersten Aufruf
    val startKapital = if (aktuellesJahr == 1) config.anfangsKapital else aktuellesKapital
    val startInvestiert = if (aktuellesJahr == 1) config.anfangsKapital else gesamtInvestiert

    // Basisfall: Alle Jahre berechnet
    if (aktuellesJahr > config.laufzeitInJahren) {
      akkumulator
    } else {
      // Berechne aktuelles Jahr
      val periodenProJahr = config.verzinsungsIntervall.periodenProJahr
      val renditeProPeriode = config.renditeProJahr / periodenProJahr
      val einzahlungProPeriode = config.monatlicheEinzahlung.getOrElse(0.0)

      val jahresErgebnis = berechneEinJahrRekursiv(
        startKapital = startKapital,
        einzahlungProPeriode = einzahlungProPeriode,
        renditeProPeriode = renditeProPeriode,
        periodenProJahr = periodenProJahr,
        zinseszins = config.zinseszins,
        gesamtInvestiert = startInvestiert,
        jahr = aktuellesJahr
      )

      // Rekursiver Aufruf für nächstes Jahr
      berechneJahresVerlaufRekursiv(
        config = config,
        aktuellesJahr = aktuellesJahr + 1,
        aktuellesKapital = jahresErgebnis.endKapital,
        gesamtInvestiert = jahresErgebnis.gesamtInvestiert,
        akkumulator = akkumulator :+ jahresErgebnis
      )
    }
  }

  /**
   * REKURSIVE FUNKTION: Berechnet ein Jahr mit Perioden rekursiv
   */
  @tailrec
  private def berechnePeriodenRekursiv(
                                        kapital: Double,
                                        einzahlungProPeriode: Double,
                                        renditeProPeriode: Double,
                                        verbleibendeperioden: Int
                                      ): Double = {
    // Basisfall: Alle Perioden berechnet
    if (verbleibendeperioden == 0) {
      kapital
    } else {
      // Berechne eine Periode
      val neuesKapital = kapital * (1 + renditeProPeriode) + einzahlungProPeriode

      // Rekursiver Aufruf für nächste Periode
      berechnePeriodenRekursiv(
        kapital = neuesKapital,
        einzahlungProPeriode = einzahlungProPeriode,
        renditeProPeriode = renditeProPeriode,
        verbleibendeperioden = verbleibendeperioden - 1
      )
    }
  }

  /**
   * Berechnet ein einzelnes Jahr (nutzt rekursive Periodenberechnung)
   */
  private def berechneEinJahrRekursiv(
                                       startKapital: Double,
                                       einzahlungProPeriode: Double,
                                       renditeProPeriode: Double,
                                       periodenProJahr: Int,
                                       zinseszins: Boolean,
                                       gesamtInvestiert: Double,
                                       jahr: Int
                                     ): JahresStatus = {
    val einzahlungenImJahr = einzahlungProPeriode * periodenProJahr

    // Berechne Kapital am Ende des Jahres mit Rekursion
    val endKapital = if (zinseszins) {
      // REKURSIVE Berechnung mit Zinseszins
      berechnePeriodenRekursiv(
        kapital = startKapital,
        einzahlungProPeriode = einzahlungProPeriode,
        renditeProPeriode = renditeProPeriode,
        verbleibendeperioden = periodenProJahr
      )
    } else {
      // Ohne Zinseszins: Nur auf Anfangskapital
      val zinsen = startKapital * renditeProPeriode * periodenProJahr
      startKapital + zinsen + einzahlungenImJahr
    }

    val zinsen = endKapital - startKapital - einzahlungenImJahr
    val neuesGesamtInvestiert = gesamtInvestiert + einzahlungenImJahr
    val gewinn = endKapital - neuesGesamtInvestiert

    JahresStatus(
      jahr = jahr,
      startKapital = startKapital,
      einzahlungen = einzahlungenImJahr,
      zinsen = zinsen,
      endKapital = endKapital,
      gesamtInvestiert = neuesGesamtInvestiert,
      gewinnBisJetzt = gewinn
    )
  }

  /**
   * REKURSIVE FUNKTION: Findet Break-Even Periode
   * Tail-recursive
   */
  @tailrec
  private def findeBreakEvenRekursiv(
                                      verlauf: List[JahresStatus],
                                      index: Int = 0
                                    ): Option[Int] = {
    // Basisfall: Liste leer oder Ende erreicht
    if (verlauf.isEmpty || index >= verlauf.length) {
      None
    } else {
      val aktuellerStatus = verlauf(index)
      // Prüfe Break-Even
      if (aktuellerStatus.istBreakEven) {
        Some(aktuellerStatus.jahr)
      } else {
        // Rekursiver Aufruf für nächsten Status
        findeBreakEvenRekursiv(verlauf, index + 1)
      }
    }
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
   * REKURSIVE FUNKTION: Vergleicht mehrere Szenarien rekursiv
   */
  def vergleicheSzenarienRekursiv(
                                   szenarien: List[(String, RenditeConfig)],
                                   ergebnisse: List[(String, RenditeErgebnis)] = List.empty
                                 ): List[(String, RenditeErgebnis)] = {
    szenarien match {
      case Nil => ergebnisse // Basisfall: Keine Szenarien mehr
      case (name, config) :: rest =>
        val ergebnis = berechneRendite(config)
        // Rekursiver Aufruf mit restlichen Szenarien
        vergleicheSzenarienRekursiv(rest, ergebnisse :+ (name, ergebnis))
    }
  }

  /**
   * Wrapper für Rückwärtskompatibilität
   */
  def vergleicheSzenarien(szenarien: List[(String, RenditeConfig)]): List[(String, RenditeErgebnis)] = {
    vergleicheSzenarienRekursiv(szenarien)
  }

  /**
   * REKURSIVE FUNKTION: Berechnet benötigtes Startkapital iterativ
   */
  @tailrec
  def berechneStartkapitalRekursiv(
                                    zielKapital: Double,
                                    laufzeitInJahren: Int,
                                    renditeProJahr: Double,
                                    aktuelleSchaetzung: Double,
                                    genauigkeit: Double = 0.01,
                                    maxIterationen: Int = 100,
                                    iteration: Int = 0
                                  ): Double = {
    // Basisfall: Maximale Iterationen erreicht oder genau genug
    if (iteration >= maxIterationen) {
      aktuelleSchaetzung
    } else {
      // Berechne Endkapital mit aktueller Schätzung
      val berechnetesEndkapital = aktuelleSchaetzung * pow(1 + renditeProJahr, laufzeitInJahren)
      val differenz = berechnetesEndkapital - zielKapital

      // Prüfe ob genau genug
      if (Math.abs(differenz) < genauigkeit) {
        aktuelleSchaetzung
      } else {
        // Verbessere Schätzung (Newton-Raphson)
        val neueSchaetzung = aktuelleSchaetzung - (differenz / pow(1 + renditeProJahr, laufzeitInJahren))

        // Rekursiver Aufruf mit verbesserter Schätzung
        berechneStartkapitalRekursiv(
          zielKapital,
          laufzeitInJahren,
          renditeProJahr,
          neueSchaetzung,
          genauigkeit,
          maxIterationen,
          iteration + 1
        )
      }
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
      // Nutze rekursive Berechnung
      val erstSchaetzung = zielKapital / pow(1 + renditeProJahr, laufzeitInJahren)
      berechneStartkapitalRekursiv(zielKapital, laufzeitInJahren, renditeProJahr, erstSchaetzung)
    } else {
      // Mit Einzahlungen komplexer - direkte Formel
      val einzahlungProMonat = monatlicheEinzahlung.get
      val gesamtEinzahlungen = einzahlungProMonat * 12 * laufzeitInJahren

      // Grobe Schätzung
      (zielKapital - gesamtEinzahlungen) / pow(1 + renditeProJahr, laufzeitInJahren)
    }
  }
}