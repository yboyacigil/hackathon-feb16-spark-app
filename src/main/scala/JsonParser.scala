import java.io.InputStream
import spray.json._
import DefaultJsonProtocol._

/** BEFORE UNIFICATION 
case class Message(method: String, executionTime: Int, request: MessageRequest, response: MessageResponse)
case class MessageRequest(payload: Option[String], credentials: MessageRequestCredentials)
case class MessageRequestCredentials(provider: String, token: String, playerId: Int, gameCode: String)
// 'balanceInfo' in authenticate, lookup-balance and 'balanceInformation' in deposit & withdraw
case class MessageResponse(status: String, routingInformation: MessageResponseRoutingInfo, balanceInfo: Option[MessageResponseBalanceInfo], balanceInformation: Option[MessageResponseBalanceInfo], playerInformation: MessageResponsePlayerInformation)
case class MessageResponseRoutingInfo(wasHandledLocally: Boolean)
case class MessageResponseBalanceInfo(currency: String, realMoneyBalance: Double)
case class MessageResponsePlayerInformation(playerReference: String, alias: String, currency: String, country: String, birthDate: Long, registrationDate: Long, siteCode: String, gender: String, isTest: Boolean)

object MyJsonProtocol extends DefaultJsonProtocol {
	
	implicit val messageResponsePlayerInformationFormat = jsonFormat9(MessageResponsePlayerInformation)
	implicit val messageResponseBalanceInfoFormat = jsonFormat2(MessageResponseBalanceInfo)
	implicit val messageResponseRoutingInfoFormat = jsonFormat1(MessageResponseRoutingInfo)
	implicit val messageResponseFormat = jsonFormat5(MessageResponse)
	
	implicit val messageRequestCredentialsFormat = jsonFormat4(MessageRequestCredentials)
	implicit val messageRequestFormat = jsonFormat2(MessageRequest)

  implicit val messageFormat = jsonFormat4(Message)
}
*/

case class Message(method: String, executionTime: Int, request: MessageRequest, response: MessageResponse)

case class MessageRequest(amount: Option[Double], currency: Option[String], credentials: MessageRequestCredentials, transactionInfo: MessageRequestTransactionInfo)
case class MessageRequestCredentials(provider: String, token: String, playerId: Int, gameCode: String)
case class MessageRequestTransactionInfo(transactionReference: String, wagerReference: String)

case class MessageResponse(status: String, statusMessage: Option[String], balanceInfo: MessageResponseBalanceInfo, playerInfo: MessageResponsePlayerInfo)
case class MessageResponseBalanceInfo(currency: String, realMoneyBalance: Double)
case class MessageResponsePlayerInfo(playerReference: String, alias: String, currency: String, country: String, birthDate: Long, registrationDate: Long, siteCode: String, gender: String, isTest: Boolean)

object MyJsonProtocol extends DefaultJsonProtocol {
	
	implicit val messageResponsePlayerInforFormat = jsonFormat9(MessageResponsePlayerInfo)
	implicit val messageResponseBalanceInfoFormat = jsonFormat2(MessageResponseBalanceInfo)
	implicit val messageResponseFormat = jsonFormat4(MessageResponse)
	
	implicit val messageRequestTransactionInfoFormat = jsonFormat2(MessageRequestTransactionInfo)
	implicit val messageRequestCredentialsFormat = jsonFormat4(MessageRequestCredentials)
	implicit val messageRequestFormat = jsonFormat4(MessageRequest)

  implicit val messageFormat = jsonFormat4(Message)
}


import MyJsonProtocol._

object JsonParserTest {
  
	def main(args: Array[String]) {
	
		// val resources = List("/authenticate.json", "/deposit.json", "/lookup-balance.json", "/withdraw.json")
		val resources = List("/message-unified.json")
	
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