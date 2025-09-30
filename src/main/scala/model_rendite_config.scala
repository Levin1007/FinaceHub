package model

/**
 * Konfiguration für Renditeberechnung
 * @param anfangsKapital Startkapital in Euro
 * @param monatlicheEinzahlung Optional monatlicher Sparbetrag
 * @param laufzeitInJahren Laufzeit der Investition
 * @param renditeProJahr Erwartete jährliche Rendite (z.B. 0.07 für 7%)
 * @param verzinsungsIntervall Wie oft wird verzinst (Monatlich, Jährlich)
 * @param zinseszins Werden Gewinne reinvestiert?
 */
case class RenditeConfig(
                          anfangsKapital: Double,
                          monatlicheEinzahlung: Option[Double],
                          laufzeitInJahren: Int,
                          renditeProJahr: Double,
                          verzinsungsIntervall: VerzinsungsIntervall,
                          zinseszins: Boolean
                        )

/**
 * Intervall für Verzinsung
 */
enum VerzinsungsIntervall:
  case Monatlich, Jaehrlich

  // Berechnet, wie oft pro Jahr verzinst wird
  def periodenProJahr: Int = this match {
    case Monatlich => 12
    case Jaehrlich => 1
  }

  // Überschreiben der toString-Methode, um den passenden String zurückzugeben
  override def toString: String = this match {
    case Monatlich => "Monatlich"
    case Jaehrlich => "Jährlich"
  }
