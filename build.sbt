ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "FinanceHub"
  )

libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % "3.5.6"
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.36"
