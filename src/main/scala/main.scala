import scala.io.StdIn
@main
def main(): Unit = {

  //Input Values
  print("Gebe dein Startkapital an: ")
  val startKapital = StdIn.readLine().toInt
  print("Gebe deinen Zinssatz an:")
  val zinsSatz = StdIn.readLine().toInt
  print("Gebe Laufzeit in Monaten an:")
  val duration = StdIn.readLine().toInt

  // Output Values
  // todo calculate final capital
  println(s"Startkapital: $startKapital")
  println(s"Zinssatz: $zinsSatz" )
  println(s"Laufzeit: $duration")
}

