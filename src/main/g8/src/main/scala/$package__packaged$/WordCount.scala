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

object WordCount extends App with Logging {

  val config = new Properties()
  config.put(
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
    "localhost:9092"
  )
  config.put(
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
    "earliest"
  )
  config.put(
    StreamsConfig.APPLICATION_ID_CONFIG,
    "streams-wordcount"
  )

  val builder = new StreamsBuilder()

  val textLines: KStream[String, String] = builder.stream[String, String]("streams-plaintext-input")

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
