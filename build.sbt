import AssemblyKeys._

name := "hackathon_feb16_sparkapp"

version := "1.0"

scalaVersion := "2.10.4"

mainClass in (Compile, run) := Some("SparkApp")

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.6.0", //sbt % "provided",
    "org.apache.spark" %% "spark-streaming" % "1.6.0", //% "provided",
    "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0",
		"io.spray" %%  "spray-json" % "1.3.2",
    "org.elasticsearch" % "elasticsearch-spark_2.10" % "2.2.0"
)

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

assemblySettings

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}