package filters

import java.util.concurrent.locks.ReentrantReadWriteLock
import play.api.Logger
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ShutDownFilter(lock: ReentrantReadWriteLock) extends EssentialFilter {

  def apply(nextFilter: EssentialAction) = new EssentialAction {
    Logger.info("Inside ShutDownFilter")

    def apply(requestHeader: RequestHeader) = {

      nextFilter(requestHeader).map { result =>
        Logger.info("Getting read lock ...")

        val read = lock.readLock()

        if (read.tryLock()) {
          result.body.andThen {
            Logger.info("Releasing read lock ...")

            read.unlock()

            result.body
          }

          result
        } else {
          Logger.info("ServiceUnavailable: Shutting down ... ")

          ServiceUnavailable.copy(connection = HttpConnection.Close)
        }
      }
    }
  }
}