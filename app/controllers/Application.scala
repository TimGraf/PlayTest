package controllers

import client.TestClient
import com.google.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject() (ws: WSClient) extends Controller {
  val client = new TestClient(ws)

  def index() = Action.async {
    for {
      res <- client.sedRequest
    } yield Ok(res)
  }
}