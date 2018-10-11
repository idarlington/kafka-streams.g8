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
    library.log4jScala,
    library.scalaTest
  )
)

lazy val library = new {

  val version = new {
    val kafkaVersion = "2.0.0"
    val scalaTest    = "3.0.5"
    val log4jScala   = "11.0"
  }

  val kafkaClients = "org.apache.kafka"         % "kafka-clients"        % version.kafkaVersion
  val kafkaStreams = "org.apache.kafka"         %% "kafka-streams-scala" % version.kafkaVersion
  val scalaTest    = "org.scalatest"            %% "scalatest"           % version.scalaTest % "test"
  val log4jScala   = "org.apache.logging.log4j" % "log4j-api-scala_2.11" % version.log4jScala

}
