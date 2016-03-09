import filters.ShutdownFilter
import play.api._
import play.api.mvc._

object Global extends GlobalSettings {

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), ShutdownFilter)
  }

  override def onStop(app: Application): Unit = {
    ShutdownFilter.prepareForShutdown()
  }
}