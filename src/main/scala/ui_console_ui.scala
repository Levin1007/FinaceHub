package ui

import model.{User, MonatsStatus, FinanzplanConfig}
import scala.io.StdIn

object ConsoleUI {

  def printHeader(): Unit = {
    println("=" * 50)
    println("      FINANCE HUB - Willkommen!")
    println("=" * 50)
  }

  def printWelcomeMessage(user: User): Unit = {
    println(s"\nDu bist bereits eingeloggt als: ${user.name}")
  }

  def printMainMenu(user: User): Unit = {
    println("\n" + "=" * 50)
    println(s"Willkommen, ${user.name}!")
    println(f"Aktuelles Guthaben: €${user.balance}%.2f")
    println("=" * 50)
    println("\n=== HAUPTMENÜ ===")
    println("1. Zinsberechnung")
    println("2. Kontostand anzeigen")
    println("3. Logout")
  }

  def printLoginMenu(): Unit = {
    println("\n=== LOGIN / REGISTRIERUNG ===")
    println("1. Login")
    println("2. Registrieren")
    println("3. Beenden")
  }

  def printFinanceHeader(): Unit = {
    println("\n=== Finanzplan-Rechner ===")
  }

  def printFinancePlan(plan: List[MonatsStatus]): Unit = {
    println(f"\n${"Monat"}%5s | ${"Startkapital"}%13s | ${"Brutto-Zinsen"}%14s | ${"Steuer"}%8s | ${"Netto-Zinsen"}%14s | ${"Einzahlung"}%11s | ${"Sonderz."}%10s | ${"Endkapital"}%12s")
    println("-" * 120)

    plan.foreach { s =>
      println(f"${s.monat}%5d | ${s.startKapital}%13.2f | ${s.bruttoZinsen}%14.2f | ${s.steuer}%8.2f | ${s.nettoZinsen}%14.2f | ${s.einzahlung}%11.2f | ${s.sonderzahlung}%10.2f | ${s.endKapital}%12.2f")
    }
  }

  def printBalance(user: User): Unit = {
    println("\n=== KONTOSTAND ===")
    println(s"Benutzer: ${user.name}")
    println(f"Guthaben: €${user.balance}%.2f")
    println("=" * 30)
  }

  def readString(prompt: String): String = {
    print(prompt)
    StdIn.readLine().trim
  }

  def readDouble(prompt: String): Double = {
    print(prompt)
    StdIn.readLine().toDouble
  }

  def readInt(prompt: String): Int = {
    print(prompt)
    StdIn.readLine().toInt
  }

  def readYesNo(prompt: String): Boolean = {
    print(prompt)
    val response = StdIn.readLine().trim.toLowerCase
    response == "j" || response == "ja" || response == "y" || response == "yes"
  }

  def readSonderzahlungen(): Map[Int, Double] = {
    println("\nGib deine Sonderzahlungen ein. Für keine Eingabe einfach Enter drücken.")
    println("Format pro Zeile: monat=betrag (z.B. 3=150), Ende mit leerer Zeile")

    var sonderZahlungen = Map.empty[Int, Double]
    var input = readString("Sonderzahlung: ")

    while (input.nonEmpty) {
      val parts = input.split("=")
      if (parts.length == 2) {
        try {
          val monat = parts(0).toInt
          val betrag = parts(1).toDouble
          if (monat >= 1) {
            sonderZahlungen += (monat -> betrag)
            println(s"✓ Sonderzahlung für Monat $monat: €$betrag hinzugefügt")
          } else {
            println("✗ Monat muss mindestens 1 sein.")
          }
        } catch {
          case _: NumberFormatException =>
            println("✗ Ungültiges Format: Monat und Betrag müssen Zahlen sein.")
        }
      } else {
        println("✗ Ungültiges Format, bitte mit '=' trennen (z.B. 3=150).")
      }
      input = readString("Sonderzahlung: ")
    }

    sonderZahlungen
  }

  def printSuccess(message: String): Unit = {
    println(s"\n✓ $message")
  }

  def printError(message: String): Unit = {
    println(s"\n✗ $message")
  }

  def waitForEnter(): Unit = {
    print("\nDrücke Enter um fortzufahren...")
    StdIn.readLine()
  }
}