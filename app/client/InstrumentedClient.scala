package client

import com.typesafe.scalalogging.LazyLogging
import play.api.libs.ws.{WSRequest, WSResponse}
import scala.concurrent.{ExecutionContext, Future}

trait InstrumentedClient extends LazyLogging {

  def instrumentedClientName: String

  implicit class InstrumentedRequest(request: WSRequest)(implicit ec: ExecutionContext) {
    def instrumentAndExecute: Future[WSResponse] = {
      val start = System.currentTimeMillis()
      val response = request.execute()

      response onSuccess {
        case _ =>
          val elapsed = System.currentTimeMillis() - start

          logger.info(s"$instrumentedClientName: request [$request] sent with response time: $elapsed ms")
      }

      response
    }
  }

  implicit class InstrumentedResponse(response: WSResponse) {
    def instrument = {
      logger.info(s"$instrumentedClientName: successful response status [${response.status}]")

      response
    }
  }
}
