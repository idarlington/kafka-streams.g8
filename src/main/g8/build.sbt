// give the user a nice default project!
lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "$organization$",
      scalaVersion := "2.11.12"
    )
  ),
  name := "$name$",
  libraryDependencies := Seq(
    library.kafkaClients,
    library.kafkaStreams,
    library.log4jCore,
    library.kafkaTest,
    library.scalaTest
  )
)

lazy val library = new {

  val version = new {
    val kafkaVersion = "2.0.0"
    val scalaTest    = "3.0.5"
    val log4jCore    = "2.11.1"
  }

  val kafkaClients = "org.apache.kafka"         % "kafka-clients"        % version.kafkaVersion
  val kafkaStreams = "org.apache.kafka"         %% "kafka-streams-scala" % version.kafkaVersion
  val log4jCore    = "org.apache.logging.log4j" % "log4j-core"           % version.log4jCore

  val kafkaTest = "org.apache.kafka" % "kafka-streams-test-utils" % version.kafkaVersion
  val scalaTest = "org.scalatest"    %% "scalatest"               % version.scalaTest % "test"
}
