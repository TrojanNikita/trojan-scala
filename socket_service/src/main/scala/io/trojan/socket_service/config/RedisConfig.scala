package io.trojan.socket_service.config

import scala.concurrent.duration.FiniteDuration

import io.trojan.utils.WithConfigCodec

case class RedisConfig(
  hosts: Seq[String],
  dataBus: String,
  recoverCount: Int,
  claimIdleTime: FiniteDuration,
  batchSize: Int,
  group: String
)

object RedisConfig extends WithConfigCodec[RedisConfig]
