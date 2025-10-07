import controller.AppController
import database.DBConnection

@main
def main(): Unit = {
  DBConnection.getConnection match {
    case Some(connection) =>
      try {
        val app = new AppController(connection)
        app.start()
      } catch {
        case ex: Exception =>
          println(s"Ein Fehler ist aufgetreten: ${ex.getMessage}")
      }  finally {
        DBConnection.close(connection)
      }
      
    case None =>
      println("\n✗ Anwendung kann ohne Datenbankverbindung nicht gestartet werden.")
      println("Bitte behebe die oben genannten Fehler und versuche es erneut.")
      System.exit(1)
  }
}