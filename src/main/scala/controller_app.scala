package controller

import model.{User, FinanzplanConfig, FinanceCalculator, AuthService}
import repository.{UserRepository, SessionRepository}
import ui.{ConsoleUI, ChartUI}
import java.sql.Connection

class AppController(connection: Connection) {
  private val userRepo = new UserRepository(connection)
  private val sessionRepo = new SessionRepository()

  def start(): Unit = {
    ConsoleUI.printHeader()

    sessionRepo.load() match {
      case Some(user) =>
        ConsoleUI.printWelcomeMessage(user)
        if (ConsoleUI.readYesNo("Möchtest du fortfahren? (j/n): ")) {
          showMainMenu(user)
        } else {
          sessionRepo.clear()
          showLoginMenu()
        }
      case None =>
        showLoginMenu()
    }
  }

  private def showLoginMenu(): Unit = {
    var loggedIn = false

    while (!loggedIn) {
      ConsoleUI.printLoginMenu()
      val choice = ConsoleUI.readString("\nBitte wähle eine Option: ")

      choice match {
        case "1" => loggedIn = handleLogin()
        case "2" => handleRegister()
        case "3" =>
          println("\nAuf Wiedersehen!")
          System.exit(0)
        case _ =>
          ConsoleUI.printError("Ungültige Auswahl. Bitte versuche es erneut.")
      }
    }
  }

  private def handleLogin(): Boolean = {
    val username = ConsoleUI.readString("Benutzername: ")
    val password = ConsoleUI.readString("Passwort: ")

    val hashedPassword = AuthService.hashPassword(password)
    
    userRepo.findByUsernameAndPassword(username, hashedPassword) match {
      case Some(user) =>
        ConsoleUI.printSuccess(s"Login erfolgreich! Willkommen zurück, ${user.name}!")
        sessionRepo.save(user)
        showMainMenu(user)
        true

      case None =>
        ConsoleUI.printError("Login fehlgeschlagen! Falscher Benutzername oder Passwort.")
        false
    }
  }

  private def handleRegister(): Unit = {
    val username = ConsoleUI.readString("Benutzername: ")
    val password = ConsoleUI.readString("Passwort: ")
    val passwordConfirm = ConsoleUI.readString("Passwort bestätigen: ")

    if (password != passwordConfirm) {
      ConsoleUI.printError("Passwörter stimmen nicht überein!")
    } else if (!AuthService.isValidUsername(username)) {
      ConsoleUI.printError("Ungültiger Benutzername! Mindestens 3 Zeichen, nur Buchstaben, Zahlen und _")
    } else if (!AuthService.isValidPasswordStrength(password)) {
      ConsoleUI.printError("Passwort zu schwach! Mindestens 4 Zeichen erforderlich.")
    } else if (userRepo.userExists(username)) {
      ConsoleUI.printError("Benutzername existiert bereits!")
    } else {
      val hashedPassword = AuthService.hashPassword(password)
      userRepo.create(username, hashedPassword) match {
        case Some(_) =>
          ConsoleUI.printSuccess(s"Benutzer '$username' erfolgreich registriert! Bitte logge dich jetzt ein.")
        case None =>
          ConsoleUI.printError("Fehler bei der Registrierung.")
      }
    }
  }

  private def showMainMenu(user: User): Unit = {
    var continue = true

    while (continue) {
      ConsoleUI.printMainMenu(user)
      val choice = ConsoleUI.readString("\nBitte wähle eine Option: ")

      choice match {
        case "1" => handleFinanceCalculation()
        case "2" => handleShowBalance(user)
        case "3" =>
          sessionRepo.clear()
          ConsoleUI.printSuccess("Erfolgreich ausgeloggt!")
          continue = false
        case _ =>
          ConsoleUI.printError("Ungültige Auswahl. Bitte versuche es erneut.")
      }
    }
  }

  private def handleFinanceCalculation(): Unit = {
    ConsoleUI.printFinanceHeader()

    val startKapital = ConsoleUI.readDouble("Startkapital: ")
    val monatlicheEinzahlung = ConsoleUI.readDouble("Monatliche Einzahlung: ")
    val zinsSatzProJahr = ConsoleUI.readDouble("Zinssatz pro Jahr (z.B. 0.05 für 5 %): ")
    val laufzeit = ConsoleUI.readInt("Laufzeit in Monaten: ")
    val steuerSatz = ConsoleUI.readDouble("Steuersatz auf Zinserträge (z.B. 0.25 für 25 %): ")
    val sonderZahlungen = ConsoleUI.readSonderzahlungen()

    val config = FinanzplanConfig(
      startKapital = startKapital,
      zinsSatzProJahr = zinsSatzProJahr,
      laufzeitInMonaten = laufzeit,
      steuerSatz = steuerSatz,
      monatlicheEinzahlung = monatlicheEinzahlung,
      sonderZahlungen = sonderZahlungen
    )

    val plan = FinanceCalculator.berechneMonatlichenFinanzplan(config)
    ConsoleUI.printFinancePlan(plan)

    if (ConsoleUI.readYesNo("\nMöchtest du ein Diagramm anzeigen? (j/n): ")) {
      ChartUI.showChart(plan)
    }
  }

  private def handleShowBalance(user: User): Unit = {
    ConsoleUI.printBalance(user)
    ConsoleUI.waitForEnter()
  }
}