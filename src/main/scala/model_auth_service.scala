package model

import java.security.MessageDigest

object AuthService {
  
  /**
   * Hash-Funktion - Pure Function
   */
  def hashPassword(password: String): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.getBytes("UTF-8"))
    hash.map("%02x".format(_)).mkString
  }

  /**
   * Validiert Passwort - Pure Function
   */
  def validatePassword(password: String, hashedPassword: String): Boolean = {
    hashPassword(password) == hashedPassword
  }

  /**
   * Validiert Benutzername - Pure Function
   */
  def isValidUsername(username: String): Boolean = {
    username.nonEmpty && username.length >= 3 && username.matches("^[a-zA-Z0-9_]+$")
  }

  /**
   * Validiert Passwort Stärke - Pure Function
   */
  def isValidPasswordStrength(password: String): Boolean = {
    password.length >= 4
  }
}