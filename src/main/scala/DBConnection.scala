import java.sql.{Connection, DriverManager, SQLException}
object DBConnection {
  private val jdbcUrl = "jdbc:mariadb://localhost:3306/financeHub"
  private val uname = "root" 
  private val pwd = ""
  var connection: Connection = null
  
  try {
    Class.forName("org.mariadb.jdbc.Driver")

    connection = DriverManager.getConnection(jdbcUrl, uname, pwd)
    println("Connection successful")
  } catch {
    case e: ClassNotFoundException =>
      println("Error loading jdbc driver class")
      e.printStackTrace()
    case e: SQLException =>
      println("Error Connecting to the database")
      e.printStackTrace()
  }
}
