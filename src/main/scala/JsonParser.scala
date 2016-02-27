import java.io.InputStream
import java.util.Date
import spray.json._

case class Message(method: String, executionTime: Int, request: MessageRequest, response: MessageResponse, eventTime: Option[Date])

case class MessageRequest(amount: Option[Double], currency: Option[String], credentials: MessageRequestCredentials, transactionInfo: Option[MessageRequestTransactionInfo])
case class MessageRequestCredentials(provider: String, token: String, playerId: Int, gameCode: String)
case class MessageRequestTransactionInfo(transactionReference: Option[String], wagerReference: Option[String])

case class MessageResponse(status: String, statusMessage: Option[String], balanceInfo: MessageResponseBalanceInfo, playerInfo: MessageResponsePlayerInfo)
case class MessageResponseBalanceInfo(currency: Option[String], realMoneyBalance: Double)
case class MessageResponsePlayerInfo(playerReference: Option[String], alias: String, currency: Option[String], country: String, birthDate: Date, registrationDate: Date, siteCode: String, gender: String, isTest: Boolean)


object MyJsonProtocol extends DefaultJsonProtocol {

  implicit object DateJsonFormat extends RootJsonFormat[Date] {

		override def write(obj: Date) = JsString(String.valueOf(obj.getTime()))

		override def read(json: JsValue): Date = {
      json match {
        case JsString(s) => new Date(s.toLong)
        case JsNumber(n) => new Date(n.toLong)
        case _ => throw new DeserializationException("Error info you want here ...")
      }
    }
	}

	implicit val messageResponsePlayerInforFormat = jsonFormat9(MessageResponsePlayerInfo)
	implicit val messageResponseBalanceInfoFormat = jsonFormat2(MessageResponseBalanceInfo)
	implicit val messageResponseFormat = jsonFormat4(MessageResponse)
	
	implicit val messageRequestTransactionInfoFormat = jsonFormat2(MessageRequestTransactionInfo)
	implicit val messageRequestCredentialsFormat = jsonFormat4(MessageRequestCredentials)
	implicit val messageRequestFormat = jsonFormat4(MessageRequest)

  implicit val messageFormat = jsonFormat5(Message)
}

import MyJsonProtocol._

object JsonParserTest {
  
	def main(args: Array[String]) {
	
		// val resources = List("/authenticate.json", "/deposit.json", "/lookup-balance.json", "/withdraw.json")
		val resources = List("/message-sample-from-kafka.json")
	
		for (resource <- resources) {
			println(s"--- $resource")
			var stream: InputStream = getClass.getResourceAsStream(resource)
			val source = scala.io.Source.fromInputStream(stream).mkString
			val jsonAst = JsonParser(source)
			val message = jsonAst.convertTo[Message]
			println(message)			
		}
	
	}
	
}