package database

import java.sql.{Connection, DriverManager, SQLException}

object DBConnection {
  private val jdbcUrl = "jdbc:mariadb://localhost:3306/financeHub"
  private val uname = "root"
  private val pwd = ""

  def getConnection: Option[Connection] = {
    try {
      Class.forName("org.mariadb.jdbc.Driver")
      val conn = DriverManager.getConnection(jdbcUrl, uname, pwd)
      println("✓ Datenbankverbindung erfolgreich hergestellt")
      Some(conn)
    } catch {
      case e: ClassNotFoundException =>
        println("✗ FEHLER: MariaDB JDBC Driver nicht gefunden!")
        println("\nStelle sicher, dass die mariadb-java-client Dependency in build.sbt vorhanden ist.")
        println("Führe 'sbt clean compile' aus.")
        e.printStackTrace()
        None
      case e: SQLException =>
        println("✗ FEHLER: Verbindung zur Datenbank fehlgeschlagen!")
        println("\nPrüfe:")
        println("1. Läuft MariaDB/MySQL?")
        println("2. Existiert die Datenbank 'financeHub'?")
        println("3. Sind die Zugangsdaten korrekt?")
        println(s"\nJDBC URL: $jdbcUrl")
        e.printStackTrace()
        None
    }
  }

  def close(connection: Connection): Unit = {
    if (connection != null && !connection.isClosed) {
      connection.close()
      println("✓ Datenbankverbindung geschlossen")
    }
  }
}