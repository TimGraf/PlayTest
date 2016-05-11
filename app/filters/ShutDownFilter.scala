package filters

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.StampedLock

import config.ShutdownFilterConfig
import play.api.Logger
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
  * Graceful shutdown filter for Play based on the following topic.
  * https://github.com/playframework/playframework/issues/1789
  *
  * Used StampLock instead of ReentrantReadWriteLock because
  * body.onDoneEnumerating occurs on another thread and will throw
  * java.lang.IllegalMonitorStateException
  * "attempt to unlock read lock, not locked by current thread"
  *
  * See also:
  * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/StampedLock.html
  * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html
  *
  */
object ShutdownFilter extends Filter with ShutdownFilterConfig {
  val lock = new StampedLock()

  def prepareForShutdown() = {
    Logger.debug("Set lock before shutting down ... ")

    lock.tryWriteLock(config.tryLockTimeout, TimeUnit.SECONDS)

    Logger.debug(s"Lock set, shutting down gracefully in ${ShutdownFilter.config.gracePeriod} seconds ... ")

    Thread.sleep(ShutdownFilter.config.gracePeriod * 1000)

    Logger.info("Shutting down now ... ")
  }

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    Logger.debug("Inside ShutdownFilter")
    Logger.debug("Getting read lock ...")

    val stamp = lock.tryReadLock()

    if (isReadLockAcquired(stamp)) {
      nextFilter(requestHeader).map(result =>
        result.copy(body = result.body.onDoneEnumerating({
          Logger.debug("Releasing read lock ...")

          lock.unlock(stamp)
        }))
      )
    } else {
      Logger.info("ServiceUnavailable: graceful shutdown ... ")

      Future { ServiceUnavailable.copy(connection = HttpConnection.Close) }
    }
  }

  private def isReadLockAcquired(stamp: Long): Boolean = stamp > 0
}