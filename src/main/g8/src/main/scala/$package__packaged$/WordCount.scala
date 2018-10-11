package $package$

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.logging.log4j.scala.Logging

object WordCount extends Logging {

  val props = new Properties()
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "")

  val builder = new StreamsBuilder()

  val textLines: KStream[String, String] = builder.stream[String, String]("")

  val count: KTable[String, Long] = textLines
    .flatMapValues(words => words.split("\\\\W+"))
    .groupBy((_, word) => word)
    .count()

  count.toStream.to("")

  val wordStream = new KafkaStreams(builder.build(), props)
  wordStream.start()

  wordStream.state()

  sys.ShutdownHookThread {
    wordStream.close(10, TimeUnit.SECONDS)
  }
}
