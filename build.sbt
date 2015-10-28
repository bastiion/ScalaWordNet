name          := "ScalaWordNet"

organization  := "de.sciss"

version       := "0.1.0-SNAPSHOT"

scalaVersion  := "2.11.7"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xfuture")

licenses      := Seq("GPL v2+" -> url("https://www.gnu.org/licenses/gpl-2.0.txt"))

libraryDependencies ++= Seq(
  "de.sciss"        % "ws4j"            % "0.1.0-SNAPSHOT",
  "net.sf.jwordnet" % "jwnl"            % "1.4_rc3",
  "com.novocode"    % "junit-interface" % "0.11"     % "test"
)

// cf. http://www.scala-sbt.org/0.13.5/docs/Detailed-Topics/Classpaths.html
unmanagedClasspath in Runtime += baseDirectory.value / "config"
unmanagedClasspath in Test    += baseDirectory.value / "config"
