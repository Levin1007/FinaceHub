import scala.io.StdIn

@main
def main(): Unit = {
  println("=== Finanzplan-Rechner ===")

  print("Startkapital: ")
  val startKapital = StdIn.readLine().toDouble

  print("Monatliche Einzahlung: ")
  val monatlicheEinzahlung = StdIn.readLine().toDouble

  print("Zinssatz pro Jahr (z.B. 0.05 für 5 %): ")
  val zinsSatzProJahr = StdIn.readLine().toDouble

  print("Laufzeit in Monaten: ")
  val laufzeit = StdIn.readLine().toInt

  print("Steuersatz auf Zinserträge (z.B. 0.25 für 25 %): ")
  val steuerSatz = StdIn.readLine().toDouble

  val sonderZahlungen = leseSonderzahlungen()

  val finance = new FinanceTools(
    startKapital,
    zinsSatzProJahr,
    laufzeit,
    steuerSatz,
    monatlicheEinzahlung,
    sonderZahlungen
  )

  val plan = finance.berechneMonatlichenFinanzplan()

  println(f"\n${"Monat"}%5s | ${"Startkapital"}%13s | ${"Brutto-Zinsen"}%14s | ${"Steuer"}%8s | ${"Netto-Zinsen"}%14s | ${"Einzahlung"}%11s | ${"Sonderz."}%10s | ${"Endkapital"}%12s")
  println("-" * 105)

  plan.foreach { s =>
    println(f"${s.monat}%5d | ${s.startKapital}%.2f        | ${s.bruttoZinsen}%.2f           | ${s.steuer}%.2f   | ${s.nettoZinsen}%.2f         | ${s.einzahlung}%.2f     | ${s.sonderzahlung}%.2f     | ${s.endKapital}%.2f")
  }

  // Optional: Diagramm anzeigen
  ChartExample.showZinsChart(plan.map(p => (p.monat, p.endKapital)).toList)

}

def leseSonderzahlungen(): Map[Int, Double] = {
  println("\nGib deine Sonderzahlungen ein. Für keine Eingabe einfach Enter drücken.")
  println("Format pro Zeile: monat=betrag (z.B. 3=150), Ende mit leerer Zeile")

  var sonderZahlungen = Map.empty[Int, Double]
  var input = ""

  print("Sonderzahlung: ")
  input = scala.io.StdIn.readLine().trim

  while (input.nonEmpty) {
    val parts = input.split("=")
    if (parts.length == 2) {
      try {
        val monat = parts(0).toInt
        val betrag = parts(1).toDouble
        if (monat >= 1) {
          sonderZahlungen += (monat -> betrag)
        } else {
          println("Monat muss mindestens 1 sein.")
        }
      } catch {
        case _: NumberFormatException =>
          println("Ungültiges Format: Monat und Betrag müssen Zahlen sein.")
      }
    } else {
      println("Ungültiges Format, bitte mit '=' trennen (z.B. 3=150).")
    }
    print("Sonderzahlung: ")
    input = scala.io.StdIn.readLine().trim
  }

  sonderZahlungen
}
