package $package$

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{ KStream, KTable }
import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig }

/**
  * A kafka streams application that reads records words from an input topic and counts the occurrence of each word
  * and outputs this count to a different topic
  *
  * Before running this application,
  * start your kafka cluster and create the required topics
  *
  * kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic input-topic
  * kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streams-wordcount-output
  *
  */
object WordCount extends App {

  val config = new Properties()
  // setting offset reset to earliest so that we can re-run the app with same data
  config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, StreamSettings.autoResetConfig)
  config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, StreamSettings.bootstrapServers)
  config.put(StreamsConfig.APPLICATION_ID_CONFIG, StreamSettings.appID)

  def topology(): StreamsBuilder = {
    val builder: StreamsBuilder = new StreamsBuilder
    val textLines: KStream[String, String] =
      builder.stream[String, String](StreamSettings.inputTopic)

    val wordCount: KTable[String, Long] = textLines
      .flatMapValues(words => words.split("\\\\W+"))
      .groupBy((_, word) => word)
      .count()

    wordCount.toStream.print(Printed.toSysOut[String, Long])
    wordCount.toStream.to(StreamSettings.outputTopic)

    builder
  }

  val wordStream = new KafkaStreams(topology().build(), config)
  wordStream.start()

  // attach shutdown handler to catch control-c
  sys.ShutdownHookThread {
    wordStream.close(10, TimeUnit.SECONDS)
  }
}
