import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.elasticsearch.spark._ 

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

		val messages = stream.map(JsonParser(_).convertTo[Message])
		messages.foreachRDD(rdd => {
			rdd.saveToEs("spark/messages")
		})
		
		val methodCounts = messages.map(_.method).map(m => (m, 1L)).reduceByKey(_ + _)
		methodCounts.foreachRDD(rdd => {
			rdd.saveToEs("spark/method-counts")
		})

    val providerCounts = messages.map(m => (m.request.credentials.provider, 1)).reduceByKey(_ + _)
    providerCounts.foreachRDD(rdd => {
      rdd.saveToEs("spark/provider-counts")
    })

		/* */
				
		/** JSON4S
		import org.json4s._
		import org.json4s.jackson.JsonMethods._
		
		val messages = stream.map(parse(_))
		
		val methodCount = messages.map(json => compact(json \ "method")).map(m => (m, 1L)).reduceByKey(_ + _);
		methodCount.print() 
		*/
		
		ssc.start()
		ssc.awaitTermination()
  }
	
}