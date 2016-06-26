name := "misser"

organization := "org.shapelogic.sc"

version := "0.0.1"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.12.1" % "test" withSources() withJavadoc(),
  "org.spire-math" %% "spire" % "0.11.0"
)

initialCommands := "import org.shapelogic.sc.misser._"

