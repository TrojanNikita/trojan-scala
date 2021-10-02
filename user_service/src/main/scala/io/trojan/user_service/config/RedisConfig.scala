package io.trojan.user_service.config

import scala.concurrent.duration.FiniteDuration

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class RedisConfig(
  hosts: Seq[String],

  dataBus: String,
  recoverCount: Int,
  claimIdleTime: FiniteDuration,
  batchSize: Int,
  group: String
)

object RedisConfig {
  implicit val codec: ConfigReader[RedisConfig] = deriveReader[RedisConfig]
}
