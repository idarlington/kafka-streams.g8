package $package$

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig }
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{ KStream, KTable }
import org.apache.logging.log4j.scala.Logging

/**
  * Before running this application,
  * start your kafka broker(s) and create the required topics
  *
  * kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streams-plaintext-input
  * kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streams-wordcount-output
  *
  */
object WordCount extends App with Logging {

  val config = new Properties()

  // setting offset reset to earliest so that we can re-run the app with same data
  config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount")

  val builder = new StreamsBuilder()

  val textLines: KStream[String, String] =
    builder.stream[String, String]("streams-plaintext-input")

  val wordCount: KTable[String, Long] = textLines
    .flatMapValues(words => words.split("\\\\W+"))
    .groupBy((_, word) => word)
    .count()

  wordCount.toStream.to("streams-wordcount-output")

  val wordStream = new KafkaStreams(builder.build(), config)
  wordStream.start()

  sys.ShutdownHookThread {
    wordStream.close(10, TimeUnit.SECONDS)
  }
}
