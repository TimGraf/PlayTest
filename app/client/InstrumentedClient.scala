package client

import play.api.Logger
import play.api.libs.ws.{WSRequest, WSResponse}
import scala.concurrent.{ExecutionContext, Future}

trait InstrumentedClient {

  def instrumentedClientName: String

  implicit class InstrumentedRequest(request: WSRequest)(implicit ec: ExecutionContext) {
    def instrumentAndExecute: Future[WSResponse] = {
      val start = System.currentTimeMillis()
      val response = request.execute()

      response onSuccess {
        case _ =>
          val elapsed = System.currentTimeMillis() - start

          Logger.info(s"$instrumentedClientName: request [$request] sent with response time: $elapsed ms")
      }

      response
    }
  }

  implicit class InstrumentedResponse(response: WSResponse) {
    def instrument = {
      Logger.info(s"$instrumentedClientName: successful response status [${response.status}]")

      response
    }
  }
}
