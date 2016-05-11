package client

import play.api.libs.ws._
import play.api.http.Status._

import scala.concurrent.{Future, ExecutionContext}

class TestClient(ws: WSClient) extends InstrumentedClient {

  def sedRequest(implicit ec: ExecutionContext): Future[String] = {
    val request = ws.url("http://www.google.com").withMethod("GET")

    request.instrumentAndExecute.map {
      case response if response.status == OK => response.instrument.body
      case _                                 => "Boo"
    }
  }

  override def instrumentedClientName: String = "TestClient"
}
