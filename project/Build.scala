import sbt._
import sbt.Keys._

object Tvint extends Build {

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1",
    organization := "com.evst",
    scalaVersion := "2.10.2",

      libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "1.9.1" % "test",
      "junit" % "junit" % "4.8.1" % "test",
      "org.mockito" % "mockito-all" % "1.9.0" % "test"
      )
  )

  lazy val tvintCore = Project(
    id = "tvint",
    base = file("."),
    settings = Project.defaultSettings ++ buildSettings
  )
}