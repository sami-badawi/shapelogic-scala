enablePlugins(JavaAppPackaging)

name := "shapelogic"

organization := "org.shapelogicscala"

version := "0.8.0"

// Tested with both 2.11.8 and 2.12.1
scalaVersion := "2.11.8"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

mainClass in Compile := Some("org.shapelogic.sc.javafx.ViewGui")

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.5.0",
  "org.scalanlp" %% "breeze" % "0.12",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test" withSources() withJavadoc(),
  "org.spire-math" %% "spire" % "0.13.0"
)

initialCommands := "import org.shapelogic.sc._"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://shapelogicscala.org/</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:sami-badawi/shapelogic-scala.git</url>
    <connection>scm:git:git@github.com:sami-badawi/shapelogic-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>sami-badawi</id>
      <name>Sami Badawi</name>
      <url>https://github.com/sami-badawi</url>
    </developer>
  </developers>
)