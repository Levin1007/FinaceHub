package repository

import model.User
import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.{Try, Using}

class SessionRepository {
  private val sessionFile = "session.dat"

  def save(user: User): Boolean = {
    Try {
      val writer = new PrintWriter(new File(sessionFile))
      try {
        writer.write(s"${user.id}|${user.name}|${user.balance}")
        true
      } finally {
        writer.close()
      }
    }.getOrElse {
      println("Fehler beim Speichern der Session")
      false
    }
  }

  def load(): Option[User] = {
    val file = new File(sessionFile)
    
    if (!file.exists()) {
      return None
    }

    Using(Source.fromFile(file)) { source =>
      val content = source.mkString.trim
      val parts = content.split("\\|")
      
      if (parts.length == 3) {
        User(parts(0).toInt, parts(1), "", parts(2).toDouble)
      } else {
        throw new Exception("Ungültiges Session-Format")
      }
    }.toOption.orElse {
      clear()
      None
    }
  }

  def clear(): Boolean = {
    val file = new File(sessionFile)
    if (file.exists()) {
      file.delete()
    } else {
      true
    }
  }

  def exists(): Boolean = {
    load().isDefined
  }
}