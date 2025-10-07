package repository

import model.User
import java.sql.Statement
import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

class UserRepository(connection: Connection) {
  
  def findByUsername(username: String): Option[User] = {
    try {
      val query = "SELECT * FROM users WHERE name = ?"
      val pstmt: PreparedStatement = connection.prepareStatement(query)
      pstmt.setString(1, username)
      
      val rs: ResultSet = pstmt.executeQuery()
      
      val user = if (rs.next()) {
        Some(User(
          rs.getInt("id"),
          rs.getString("name"),
          rs.getString("password"),
          rs.getDouble("balance")
        ))
      } else {
        None
      }
      
      rs.close()
      pstmt.close()
      user
      
    } catch {
      case e: SQLException =>
        println(s"Fehler beim Abrufen des Benutzers: ${e.getMessage}")
        None
    }
  }

  def findByUsernameAndPassword(username: String, hashedPassword: String): Option[User] = {
    try {
      val query = "SELECT * FROM users WHERE name = ? AND password = ?"
      val pstmt: PreparedStatement = connection.prepareStatement(query)
      
      pstmt.setString(1, username)
      pstmt.setString(2, hashedPassword)
      
      val rs: ResultSet = pstmt.executeQuery()
      
      val user = if (rs.next()) {
        Some(User(
          rs.getInt("id"),
          rs.getString("name"),
          rs.getString("password"),
          rs.getDouble("balance")
        ))
      } else {
        None
      }
      
      rs.close()
      pstmt.close()
      user
      
    } catch {
      case e: SQLException =>
        println(s"Fehler beim Login: ${e.getMessage}")
        None
    }
  }

  def create(username: String, hashedPassword: String, balance: String): Option[User] = {
    try {
      val query = "INSERT INTO users (name, password, balance) VALUES (?, ?, ?)"
      val pstmt: PreparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)

      pstmt.setString(1, username)
      pstmt.setString(2, hashedPassword)
      pstmt.setString(3, balance)
      
      val result = pstmt.executeUpdate()
      
      val user = if (result > 0) {
        val keys = pstmt.getGeneratedKeys
        if (keys.next()) {
          Some(User(keys.getInt(1), username, hashedPassword, balance.toDouble))
        } else {
          None
        }
      } else {
        None
      }
      
      pstmt.close()
      user
      
    } catch {
      case e: SQLException =>
        println(s"Fehler bei der Registrierung: ${e.getMessage}")
        None
    }
  }

  def userExists(username: String): Boolean = {
    findByUsername(username).isDefined
  }
}