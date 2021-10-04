package io.trojan.socket_service.config

import scala.concurrent.duration.FiniteDuration

import io.trojan.common.utils.WithConfigCodec

case class UserSocketConfig(
  readInterval: FiniteDuration,
  readRecoverInterval: FiniteDuration,
  concurrent: Int
)

object UserSocketConfig extends WithConfigCodec[UserSocketConfig]

