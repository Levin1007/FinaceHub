package controller

import model.{RenditeConfig, RenditeCalculator, VerzinsungsIntervall}
import ui.{RenditeUI, ConsoleUI}

class RenditeController {

  def start(): Unit = {
    var continue = true

    while (continue) {
      RenditeUI.printRenditeHeader()
      RenditeUI.printRenditeMenu()
      
      val choice = ConsoleUI.readString("\nBitte wähle eine Option: ")

      choice match {
        case "1" => handleEinmaligeInvestition()
        case "2" => handleSparplan()
        case "3" => handleSzenarienVergleich()
        case "4" => handleStartkapitalBerechnung()
        case "5" =>
          println("\n↩ Zurück zum Hauptmenü...")
          continue = false
        case _ =>
          ConsoleUI.printError("Ungültige Auswahl. Bitte versuche es erneut.")
      }
    }
  }

  private def handleEinmaligeInvestition(): Unit = {
    val config = RenditeUI.readRenditeConfig(mitMonatlicherEinzahlung = false)
    berechneUndZeige(config, "Einmalige Investition")
  }

  private def handleSparplan(): Unit = {
    val config = RenditeUI.readRenditeConfig(mitMonatlicherEinzahlung = true)
    berechneUndZeige(config, "Sparplan")
  }

  private def berechneUndZeige(config: RenditeConfig, titel: String): Unit = {
    println("\n⏳ Berechne...")
    val ergebnis = RenditeCalculator.berechneRendite(config)
    
    RenditeUI.printErgebnis(ergebnis)
    
    if (ConsoleUI.readYesNo("\nJahresverlauf anzeigen? (j/n): ")) {
      RenditeUI.printJahresVerlauf(ergebnis.jahresVerlauf)
    }
    
    if (ConsoleUI.readYesNo("\nDiagramm anzeigen? (j/n): ")) {
      RenditeUI.printAsciiChart(ergebnis.jahresVerlauf)
    }
    
    ConsoleUI.waitForEnter()
  }

  private def handleSzenarienVergleich(): Unit = {
    println("\n=== SZENARIEN-VERGLEICH ===")
    println("Erstelle mehrere Szenarien zum Vergleich\n")
    
    var szenarien = List.empty[(String, RenditeConfig)]
    var continue = true
    var counter = 1
    
    while (continue && counter <= 5) {
      val name = RenditeUI.readString(s"\nName für Szenario $counter (oder Enter zum Beenden): ")
      
      if (name.isEmpty && counter > 1) {
        continue = false
      } else if (name.isEmpty) {
        println("Mindestens ein Szenario erforderlich!")
      } else {
        println(s"\n--- Konfiguration für Szenario: $name ---")
        val mitEinzahlung = ConsoleUI.readYesNo("Mit monatlichen Einzahlungen? (j/n): ")
        val config = RenditeUI.readRenditeConfig(mitEinzahlung)
        szenarien = szenarien :+ (name, config)
        counter += 1
        
        if (counter <= 5 && !ConsoleUI.readYesNo("\nWeiteres Szenario hinzufügen? (j/n): ")) {
          continue = false
        }
      }
    }
    
    if (szenarien.nonEmpty) {
      println("\n⏳ Berechne alle Szenarien...")
      val ergebnisse = RenditeCalculator.vergleicheSzenarien(szenarien)
      
      RenditeUI.printSzenarienVergleich(ergebnisse)
      
    }
    
    ConsoleUI.waitForEnter()
  }

  private def handleStartkapitalBerechnung(): Unit = {
    println("\n=== STARTKAPITAL FÜR ZIEL BERECHNEN ===")
    println("Berechne, wie viel Startkapital du brauchst, um ein Ziel zu erreichen\n")
    
    val zielKapital = RenditeUI.readString("Gewünschtes Endkapital (€): ").toDouble
    val laufzeit = RenditeUI.readString("Laufzeit (Jahre): ").toInt
    val rendite = RenditeUI.readString("Erwartete Rendite p.a. (% z.B. 7 für 7%): ").toDouble / 100.0
    
    val mitEinzahlung = ConsoleUI.readYesNo("Mit monatlichen Einzahlungen? (j/n): ")
    val monatlicheEinzahlung = if (mitEinzahlung) {
      Some(RenditeUI.readString("Monatliche Einzahlung (€): ").toDouble)
    } else {
      None
    }
    
    val benoetigtesStartkapital = RenditeCalculator.berechneStartkapitalFuerZiel(
      zielKapital,
      laufzeit,
      rendite,
      monatlicheEinzahlung
    )
    
    println("\n" + "=" * 60)
    println("                    ERGEBNIS")
    println("=" * 60)
    println(f"\n🎯 Zielkapital:              €${zielKapital}%,.2f")
    println(f"💰 Benötigtes Startkapital:  €${benoetigtesStartkapital}%,.2f")
    println(f"📅 Laufzeit:                 $laufzeit Jahre")
    println(f"📈 Rendite p.a.:             ${rendite * 100}%.2f%%")
    
    monatlicheEinzahlung.foreach { einzahlung =>
      val gesamtEinzahlungen = einzahlung * 12 * laufzeit
      println(f"💵 Monatliche Einzahlung:    €${einzahlung}%,.2f")
      println(f"📊 Gesamt-Einzahlungen:      €${gesamtEinzahlungen}%,.2f")
    }
    
    println("=" * 60)
    
    ConsoleUI.waitForEnter()
  }
}