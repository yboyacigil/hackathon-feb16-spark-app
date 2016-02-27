import java.util.Date

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.elasticsearch.spark._
import org.apache.spark.SparkContext
import org.elasticsearch.spark.rdd.EsSpark

import MyJsonProtocol._

object SparkApp {
	

  def main(args: Array[String]) {

		if (args.length < 5) {
			System.err.println("Usage: SparkApp <zkQuorum> <group> <topics> <esNodes> <esPort>")
		  System.exit(0)
		}
		val Array(zkQuorum, group, topics, esNodes, esPort) = args


    val conf = new SparkConf().setAppName("Hackathon-Feb16-SparkApp").setMaster("local[2]")
		conf.set("es.index.auto.create", "true")
		conf.set("es.nodes", esNodes)
		conf.set("es.port", esPort)

    val ssc = new StreamingContext(conf, Seconds(10))

    val topicMap = topics.split(",").map((_, 1)).toMap
    val stream = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)

		/** SPRAY	*/
		import spray.json._

		val messages = stream.map(m => {
      println(m)
      val message = JsonParser(m).convertTo[Message]
      message.copy(eventTime = Some(new Date()))
    })
		messages.foreachRDD(rdd => {
			rdd.saveToEs("spark/messages")
		})

		val methodCounts = messages.map(_.method).map(m => (m, 1L)).reduceByKey(_ + _)
    methodCounts.print()
//		methodCounts.foreachRDD(rdd => {
//			rdd.saveToEs("spark/method-counts")
//		})

    val providerCounts = messages.map(m => (m.request.credentials.provider, 1)).reduceByKey(_ + _)
    providerCounts.print()
//    providerCounts.foreachRDD(rdd => {
//      rdd.saveToEs("spark/provider-counts")
//    })

    val gameCounts = messages.map(m => (m.request.credentials.gameCode, 1)).reduceByKey(_ + _)
    gameCounts.print()
//    providerCounts.foreachRDD(rdd => {
//      rdd.saveToEs("spark/game-counts")
//    })
    /* */

    ssc.start()
		ssc.awaitTermination()
  }

}