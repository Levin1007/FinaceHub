package model

/**
 * Ergebnis einer Renditeberechnung
 */
case class RenditeErgebnis(
  endkapital: Double,
  gesamtInvestition: Double,
  gewinn: Double,
  durchschnittlicheJahresRendite: Double,
  effektiveJahresRendite: Double,
  breakEvenPeriode: Option[Int], // In welcher Periode wird Break-Even erreicht?
  jahresVerlauf: List[JahresStatus]
) {
  def renditeInProzent: Double = durchschnittlicheJahresRendite * 100
  def effektivRenditeInProzent: Double = effektiveJahresRendite * 100
}

/**
 * Status eines einzelnen Jahres
 */
case class JahresStatus(
  jahr: Int,
  startKapital: Double,
  einzahlungen: Double,
  zinsen: Double,
  endKapital: Double,
  gesamtInvestiert: Double,
  gewinnBisJetzt: Double
) {
  def istBreakEven: Boolean = gewinnBisJetzt >= 0
}