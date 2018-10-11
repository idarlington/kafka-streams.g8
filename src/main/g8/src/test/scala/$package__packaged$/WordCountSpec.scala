package $package$

import java.lang.Long
import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.scala.Serdes
import org.apache.kafka.streams.test.{ ConsumerRecordFactory, OutputVerifier }
import org.apache.kafka.streams.{ StreamsConfig, Topology, TopologyTestDriver }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

class WordCountSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private val stringDeserializer = Serdes.String.deserializer()
  private val longDeserializer   = new LongDeserializer

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

  val recordFactory = new ConsumerRecordFactory[String, String]("input-topic",
                                                                new StringSerializer,
                                                                new StringSerializer)

  val topology: Topology             = WordCount.topology().build()
  val testDriver: TopologyTestDriver = new TopologyTestDriver(topology, props)

  "WordCount" should {
    "count words correctly" in {
      testDriver.pipeInput(recordFactory.create("input-topic", "", "kafka kafka"))
      OutputVerifier.compareKeyValue(
        testDriver.readOutput("streams-wordcount-output", stringDeserializer, longDeserializer),
        "kafka",
        1L: Long
      )
      OutputVerifier.compareKeyValue(
        testDriver.readOutput("streams-wordcount-output", stringDeserializer, longDeserializer),
        "kafka",
        2L: Long
      )
    }
  }

  override def afterAll(): Unit = {
    testDriver.close()
  }

}
