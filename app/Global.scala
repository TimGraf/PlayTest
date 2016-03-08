import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import filters.ShutDownFilter
import play.api._
import play.api.mvc._
import play.api.Logger

object Global extends GlobalSettings {
  val lock = new ReentrantReadWriteLock(false)

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), new ShutDownFilter(lock))
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Set lock before shutting down ... ")

    lock.writeLock().tryLock(10, TimeUnit.SECONDS)

    Logger.info("Lock set, shutting down gracefully ... ")

    Thread.sleep(60000)

    Logger.info("Shutting down now ... ")
  }
}