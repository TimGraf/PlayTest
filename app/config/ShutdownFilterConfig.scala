package config

import config.ShutdownFilterConfig.ShutdownConfig

trait ShutdownFilterConfig {
  def config = new ShutdownConfig(
    play.Play.application.configuration.getLong("shutdown.grace-period-seconds"),
    play.Play.application.configuration.getLong("shutdown.try-lock-timeout-seconds")
  )
}

object ShutdownFilterConfig {
  case class ShutdownConfig(gracePeriod: Long, tryLockTimeout: Long)
}