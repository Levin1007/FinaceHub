package model

case class FinanzplanConfig(
  startKapital: Double,
  zinsSatzProJahr: Double,
  laufzeitInMonaten: Int,
  steuerSatz: Double,
  monatlicheEinzahlung: Double,
  sonderZahlungen: Map[Int, Double]
)