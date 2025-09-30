import java.sql.{ResultSet, SQLException, Statement}

object ExampleDbConnection:
  def testDb(): Unit =
    val conn = DBConnection.connection;
    try {
      val stmt: Statement = conn.createStatement()

      val rs: ResultSet = stmt.executeQuery("SELECT * FROM users WHERE name = 'robin'")

      while (rs.next()) {
        val id = rs.getInt("id")
        val name = rs.getString("name")
        val balance = rs.getDouble("balance")
        println(s"User: $id, $name, Balance: $balance")
      }

      rs.close()
      stmt.close()
    } catch {
      case e: SQLException =>
        println("SQL Error:")
        e.printStackTrace()
    }