package client

import com.typesafe.scalalogging.LazyLogging
import play.api.libs.ws._
import play.api.http.Status._

import scala.concurrent.{ExecutionContext, Future}

class TestClient(ws: WSClient) extends InstrumentedClient with LazyLogging {

  def sedRequest(implicit ec: ExecutionContext): Future[String] = {
    val request = ws.url("http://www.google.com").withMethod("GET")

    request.instrumentAndExecute.map {
      case response if response.status == OK => response.instrument.body
      case response                          =>
        logger.error(s"Client error - status code: ${response.status}")

        s"Client error - status code: ${response.status}"
    }
  }

  override def instrumentedClientName: String = "TestClient"
}
