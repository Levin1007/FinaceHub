package ui

import model.{RenditeConfig, RenditeErgebnis, VerzinsungsIntervall, JahresStatus}
import scala.io.StdIn

object RenditeUI {

  def printRenditeHeader(): Unit = {
    println("\n" + "=" * 70)
    println("                    RENDITERECHNER")
    println("=" * 70)
  }

  def printRenditeMenu(): Unit = {
    println("\n=== RENDITERECHNER OPTIONEN ===")
    println("1. Einmalige Investition berechnen")
    println("2. Sparplan mit monatlichen Einzahlungen berechnen")
    println("3. Szenarien vergleichen")
    println("4. Benötigtes Startkapital für Ziel berechnen")
    println("5. Zurück zum Hauptmenü")
  }

  def readRenditeConfig(mitMonatlicherEinzahlung: Boolean): RenditeConfig = {
    println("\n--- Eingabe der Parameter ---")
    
    val anfangsKapital = readPositiveDouble("Anfangskapital (€): ")
    
    val monatlicheEinzahlung = if (mitMonatlicherEinzahlung) {
      Some(readPositiveDouble("Monatliche Einzahlung (€): "))
    } else {
      None
    }
    
    val laufzeit = readPositiveInt("Laufzeit (Jahre): ")
    val rendite = readDouble("Erwartete Rendite pro Jahr (% z.B. 7 für 7%): ") / 100.0
    
    println("\nVerzinsungsintervall:")
    println("1. Monatlich")
    println("2. Jährlich")
    val intervallChoice = readInt("Wähle (1 oder 2): ")
    val intervall = if (intervallChoice == 1) VerzinsungsIntervall.Monatlich else VerzinsungsIntervall.Jaehrlich
    
    val zinseszins = readYesNo("Gewinne reinvestieren (Zinseszins)? (j/n): ")
    
    RenditeConfig(
      anfangsKapital = anfangsKapital,
      monatlicheEinzahlung = monatlicheEinzahlung,
      laufzeitInJahren = laufzeit,
      renditeProJahr = rendite,
      verzinsungsIntervall = intervall,
      zinseszins = zinseszins
    )
  }

  def printErgebnis(ergebnis: RenditeErgebnis): Unit = {
    println("\n" + "=" * 70)
    println("                    ERGEBNIS")
    println("=" * 70)
    
    println(f"\n💰 Endkapital:                    €${ergebnis.endkapital}%,.2f")
    println(f"📊 Gesamtinvestition:             €${ergebnis.gesamtInvestition}%,.2f")
    println(f"📈 Gewinn:                        €${ergebnis.gewinn}%,.2f")
    println(f"📉 Durchschn. Jahresrendite:      ${ergebnis.renditeInProzent}%.2f%%")
    println(f"⚡ Effektive Jahresrendite (APY): ${ergebnis.effektivRenditeInProzent}%.2f%%")
    
    ergebnis.breakEvenPeriode match {
      case Some(periode) => println(f"✅ Break-Even erreicht in Jahr:   $periode")
      case None => println("❌ Break-Even nicht erreicht")
    }
    
    println("\n" + "=" * 70)
  }

  def printJahresVerlauf(verlauf: List[JahresStatus]): Unit = {
    println("\n" + "=" * 110)
    println("                                      JAHRESVERLAUF")
    println("=" * 110)
    
    println(f"${"Jahr"}%5s | ${"Start (€)"}%12s | ${"Einzahlungen (€)"}%17s | ${"Zinsen (€)"}%12s | ${"Ende (€)"}%12s | ${"Investiert (€)"}%15s | ${"Gewinn (€)"}%12s")
    println("-" * 110)
    
    verlauf.foreach { jahr =>
      val gewinnSymbol = if (jahr.istBreakEven) "✓" else " "
      println(f"${jahr.jahr}%5d | ${jahr.startKapital}%12.2f | ${jahr.einzahlungen}%17.2f | ${jahr.zinsen}%12.2f | ${jahr.endKapital}%12.2f | ${jahr.gesamtInvestiert}%15.2f | ${jahr.gewinnBisJetzt}%12.2f $gewinnSymbol")
    }
    
    println("=" * 110)
  }

  def printAsciiChart(verlauf: List[JahresStatus]): Unit = {
    println("\n" + "=" * 70)
    println("                    KAPITALVERLAUF (Diagramm)")
    println("=" * 70)
    
    val maxKapital = verlauf.map(_.endKapital).max
    val breite = 60
    
    verlauf.foreach { jahr =>
      val balkenLaenge = ((jahr.endKapital / maxKapital) * breite).toInt
      val balken = "█" * balkenLaenge
      println(f"Jahr ${jahr.jahr}%2d: $balken ${jahr.endKapital}%,.0f €")
    }
    
    println("=" * 70)
  }

  def printSzenarienVergleich(szenarien: List[(String, RenditeErgebnis)]): Unit = {
    println("\n" + "=" * 90)
    println("                           SZENARIEN-VERGLEICH")
    println("=" * 90)
    
    println(f"${"Szenario"}%-20s | ${"Endkapital (€)"}%15s | ${"Gewinn (€)"}%12s | ${"Rendite (%)"}%12s")
    println("-" * 90)
    
    szenarien.foreach { case (name, ergebnis) =>
      println(f"$name%-20s | ${ergebnis.endkapital}%15.2f | ${ergebnis.gewinn}%12.2f | ${ergebnis.renditeInProzent}%12.2f")
    }
    
    println("=" * 90)
    
    // Bestes Szenario hervorheben
    val bestes = szenarien.maxBy(_._2.gewinn)
    println(f"\n🏆 Bestes Szenario: ${bestes._1} mit €${bestes._2.gewinn}%,.2f Gewinn")
  }

  private def readPositiveDouble(prompt: String): Double = {
    var valid = false
    var value = 0.0
    while (!valid) {
      print(prompt)
      try {
        value = StdIn.readLine().toDouble
        if (value >= 0) {
          valid = true
        } else {
          println("✗ Bitte eine positive Zahl eingeben!")
        }
      } catch {
        case _: NumberFormatException => println("✗ Ungültige Eingabe! Bitte eine Zahl eingeben.")
      }
    }
    value
  }

  private def readPositiveInt(prompt: String): Int = {
    var valid = false
    var value = 0
    while (!valid) {
      print(prompt)
      try {
        value = StdIn.readLine().toInt
        if (value > 0) {
          valid = true
        } else {
          println("✗ Bitte eine positive Zahl größer 0 eingeben!")
        }
      } catch {
        case _: NumberFormatException => println("✗ Ungültige Eingabe! Bitte eine ganze Zahl eingeben.")
      }
    }
    value
  }

  private def readDouble(prompt: String): Double = {
    var valid = false
    var value = 0.0
    while (!valid) {
      print(prompt)
      try {
        value = StdIn.readLine().toDouble
        valid = true
      } catch {
        case _: NumberFormatException => println("✗ Ungültige Eingabe! Bitte eine Zahl eingeben.")
      }
    }
    value
  }

  private def readInt(prompt: String): Int = {
    var valid = false
    var value = 0
    while (!valid) {
      print(prompt)
      try {
        value = StdIn.readLine().toInt
        valid = true
      } catch {
        case _: NumberFormatException => println("✗ Ungültige Eingabe! Bitte eine ganze Zahl eingeben.")
      }
    }
    value
  }

  private def readYesNo(prompt: String): Boolean = {
    print(prompt)
    val response = StdIn.readLine().trim.toLowerCase
    response == "j" || response == "ja" || response == "y" || response == "yes"
  }

  def readString(prompt: String): String = {
    print(prompt)
    StdIn.readLine().trim
  }
}